package com.game.draco.app.chat;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

import com.game.draco.GameContext;
import com.game.draco.app.chat.config.ChatLimitConfig;

import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.base.RoleType;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.util.DateUtil;
import sacred.alliance.magic.util.Wildcard;
import sacred.alliance.magic.vo.RoleEntity;
import sacred.alliance.magic.vo.RoleInstance;

public abstract class Channel {
	
	/** 频道类型 */
    protected ChannelType channelType;
    /** 喊话频率的索引 */
    protected int index;
    
    public ChannelType getChannelType() {
		return channelType;
	}
    
	public Channel(ChannelType channelType){
    	this.channelType = channelType ;
    }
    
    /**
     * 聊天频道发送信息
     * @param speaker 系统发消息时为null
     * @param message
     * @param target
     * @return
     * @throws Exception
     */
    public Result send(RoleEntity speaker, Message message, Serializable target,ChatMessageType messageType) throws Exception{
    	Result cdtRes = this.condition(speaker, target,messageType);
		if(cdtRes.isIgnore()){
			return cdtRes;
		}
    	if(!cdtRes.isSuccess()){
    		return cdtRes;
    	}
    	Collection<RoleEntity> listeners = this.getListeners(speaker, target);
    	//当speaker为空时表示由系统发布信息
		if(null != speaker && RoleType.PLAYER == speaker.getRoleType()){
			for(RoleEntity listener : listeners){
				//收听者不需要接收消息，则不发
				if(!this.canReceive(speaker, listener, messageType)){
					continue;
				}
				GameContext.getMessageCenter().send(speaker, listener, message, this.isSendToSpeaker());
			}
		}else{
			GameContext.getMessageCenter().sendSysMsg(listeners, message);
		}
    	return new Result().success();
    }
    
    /**
     * 判断收听者是否能接收聊天消息
     * @param speaker
     * @param listener
     * @return
     */
    private boolean canReceive(RoleEntity speaker, RoleEntity listener, ChatMessageType messageType){
    	if(RoleType.PLAYER != listener.getRoleType()){
    		return false;
    	}
    	RoleInstance role = (RoleInstance)listener;
    	//角色未登录完成不发生聊天消息
//    	if(!role.isLoginCompleted()){
//    		return false;
//    	}
    	//如果是语音聊天的转发消息，判断是否屏蔽语音聊天;普通的聊天消息，判断是否屏蔽聊天频道
    	if(ChatMessageType.Voice == messageType && role.getSystemSet().isChatVoiceShield(this.channelType)){
    		return false;
    	}else if(ChatMessageType.Text == messageType && role.getSystemSet().isChatShield(this.channelType)){
			return false;
		}
    	//社交中设置了屏蔽
		if(this.isFilterSocialShield() && this.isShieldBySocial(speaker, role.getRoleId())){
			return false;
		}
    	return true;
    }
    
    /**
	 * 聊天条件判断，调用频道规则及喊话频率的检测
	 * @param speaker
	 * @param target
	 * @return
	 */
	private Result condition(RoleEntity speaker, Serializable target,ChatMessageType messageType){
		Result result = new Result();
		//speaker为空表示是系统发消息
		if(null == speaker){
			return result.success();
		}
		Status status = this.channelRule(speaker, target);
		if(!status.isSuccess()){
			return result.setInfo(status.getTips());
		}
		if(RoleType.PLAYER == speaker.getRoleType()){
			RoleInstance role = (RoleInstance)speaker;
			Result lmtRes = this.channelLimit(role,messageType);
			if(lmtRes.isIgnore()){
				return lmtRes;
			}
			if(!lmtRes.isSuccess()){
				return lmtRes;
			}
		}
		return result.success();
	}
    
    /**
	 * 判断是否发给喊话者
	 * @return
	 */
	protected boolean isSendToSpeaker(){
		return true ;
	}
	
	/**
	 * 频道限制条件
	 * 如：角色等级、喊话频率、喊话道具
	 * @param role
	 * @return
	 */
	protected Result channelLimit(RoleInstance role,ChatMessageType messageType){
		Result result = new Result();
		ChatLimitConfig config = GameContext.getChatApp().getChatLimitConfig(this.channelType);
		if(null == config){
			return result.success();
		}
		//角色等级限制条件
		int level = config.getRoleLevel();
		if(level > 0){
			if(role.getLevel() < level){
				return result.setInfo(Status.Chat_Role_Level_Limit.getTips().replace(Wildcard.Number, String.valueOf(level)));
			}
		}
		//说话间隔时间限制
		int spaceTime = config.getSpaceTime();
		if(spaceTime > 0){
			Date now = new Date();
			Date[] chatLastTimes = role.getChatLastSpeakTime();
			Date lastTime = chatLastTimes[this.index];
			if(null != lastTime){
				int diffTime = DateUtil.dateDiffSecond(lastTime, now);
				if(diffTime < spaceTime){
					int time = spaceTime - diffTime;//还差time秒才能说话
					return result.setInfo(Status.Chat_Speak_Time_Limit.getTips().replace(Wildcard.Number, String.valueOf(time)));
				}
			}
		}
		//喊话道具限制
		int goodsId = config.getGoodsId();
        if(ChatMessageType.Voice == messageType){
            goodsId = config.getVoiceGoodsId() ;
        }
		if(goodsId > 0){
			//----------------------------
			//快速购买
			result = GameContext.getQuickBuyApp().doQuickBuy(role, goodsId, 1, OutputConsumeType.chat_world_speak, null);
			if(result.isIgnore()){
				return result;
			}
			if(!result.isSuccess()){
				return result;
			}
			//----------------------------
		}
		//最后再设置本次说话时间
		if(spaceTime > 0){
			role.getChatLastSpeakTime()[this.index] = new Date();
		}
		return result.success();
	}
    
    /**
	 * 频道规则，不同频道喊话条件不同
	 * @param speaker
	 * @param target
	 * @return
	 */
	protected abstract Status channelRule(RoleEntity speaker, Serializable target);
	
	/**
	 * 根据喊话频道获取收听者
	 * @param speaker 系统发消息时为null
	 * @param target
	 * @return
	 */
    protected abstract Collection<RoleEntity> getListeners(RoleEntity speaker, java.io.Serializable target) throws Exception;
    
    /**
	 * 是否过滤社交屏蔽（不接收黑名单中的玩家消息）
	 * @return
	 */
	protected abstract boolean isFilterSocialShield();
    
    /**
     * 说话者是否被监听者屏蔽
     * @param speaker
     * @param listenerId
     * @return
     */
    protected boolean isShieldBySocial(RoleEntity speaker, String listenerId){
    	if(null == speaker){
    		return false;
    	}
    	if(RoleType.GM == speaker.getRoleType()){
    		return false;
    	}
    	String speakerId = speaker.getRoleId();
    	return GameContext.getSocialApp().isShieldByTarget(speakerId, listenerId);
    }

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

}
