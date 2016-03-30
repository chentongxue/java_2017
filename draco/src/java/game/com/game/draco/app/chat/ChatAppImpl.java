package com.game.draco.app.chat;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import lombok.Data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.onlinecenter.OnlineCenter;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.base.RoleType;
import sacred.alliance.magic.base.XlsSheetNameType;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.service.IllegalWordsService;
import sacred.alliance.magic.util.HttpUtil;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.util.Wildcard;
import sacred.alliance.magic.util.XlsPojoUtil;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.RoleEntity;
import sacred.alliance.magic.vo.RoleInstance;

import com.alibaba.fastjson.JSON;
import com.game.draco.GameContext;
import com.game.draco.app.chat.config.ChatLimitConfig;
import com.game.draco.app.npc.domain.NpcInstance;
import com.game.draco.message.item.ChatContextItem;
import com.game.draco.message.push.C1804_ChatVoicePushMessage;
import com.game.draco.message.request.C0508_GoodsInfoViewIdBindReqMessage;
import com.game.draco.message.request.C1312_TeamPanelPublishApplyReqMessage;
import com.game.draco.message.response.C1802_ChatRouteRespMessage;
import com.game.draco.message.response.C1805_ChatPrivteRespMessage;

public @Data class ChatAppImpl implements ChatApp {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private static final int VOICE_TO_TEXT_API_TIMEOUT = 2000 ;
	private static final int SystemRoleId = -1;
	private static final int GmRoleId = -2;
	private static final String GmRoleName = "GM";
	
	private OnlineCenter onlineCenter;
	private ChannelManager channelManager;
	private IllegalWordsService illegalWordsService;
	
	private java.util.Map<ChannelType,ChatLimitConfig> chatLimitMap = new HashMap<ChannelType,ChatLimitConfig>();
	private int size;//有说话间隔时间的频道数量
	
	@Override
	public Result sendSysMessage(ChatSysName chatSysName, ChannelType channelType, String message, 
			List<ChatContextItem> contextList, Serializable target){
		return this.sendMessage(null, chatSysName, channelType, message, contextList, target);
	}
	
	/** Npc喊话 */
	@Override
	public Result sendSysMessage(NpcInstance npcInstance, ChannelType channelType, String message, 
			List<ChatContextItem> contextList, Serializable target){
		Result result = new Result();
		try{
			Channel channel = this.channelManager.getChannel(channelType);
			if(null == channel){
				return result.setInfo(Status.Chat_FAILURE.getTips());
			}
			C1802_ChatRouteRespMessage routeMsg = new C1802_ChatRouteRespMessage();
			String speakerName = ChatSysName.System.getName();
			int sendRoleId = -1;
			if(npcInstance != null){
				speakerName = npcInstance.getNpcname();
				sendRoleId = npcInstance.getIntRoleId();
			}
			routeMsg.setSendRoleId(sendRoleId);
			routeMsg.setSendRoleName(speakerName);
			routeMsg.setChannelType(channelType.getType());
			routeMsg.setMessage(message);
			routeMsg.setContextList(contextList);
			return channel.send(npcInstance, routeMsg, target,ChatMessageType.Text);
		}catch(Exception e){
			this.logger.error("chat send message error:", e);
			return result.setInfo(Status.Chat_FAILURE.getTips());
		}
	}
	
	@Override
	public Result sendMessage(RoleEntity speaker, ChannelType channelType, String message, 
			List<ChatContextItem> contextList, RoleInstance targetRole){
		Result result = new Result();
		try{
			Result res = this.canSpeakByForbid(speaker, channelType);
			if(!res.isSuccess()){
				return res;
			}
			if(null == message || "".equals(message)){
				return result.setInfo(Status.Chat_Message_Null.getTips());
			}
			if(RoleType.GM == speaker.getRoleType()){
				return this.sendMessageByGm(speaker, channelType, message, contextList, targetRole);
			}
			if(RoleType.PLAYER == speaker.getRoleType()){
				int maxSize = GameContext.getParasConfig().getChatMessageSize();
				if(message.length() > maxSize){
					return result.setInfo(Status.Chat_MaxLength_Fail.getTips().replace(Wildcard.Number, String.valueOf(maxSize)));
				}
			}
			Serializable target = this.getChatTarget(speaker, channelType, targetRole);
			return this.sendMessage(speaker, null, channelType, message, contextList, target);
		}catch(Exception e){
			this.logger.error("chat send message error:", e);
			return result.setInfo(Status.Chat_FAILURE.getTips());
		}
	}
	
	/**
	 * 获取聊天频道的目标
	 * @param speaker
	 * @param channelType
	 * @param targetRole
	 * @return
	 */
	private Serializable getChatTarget(RoleEntity speaker, ChannelType channelType, RoleInstance targetRole){
		Channel channel = this.channelManager.getChannel(channelType);
		Serializable target = null;
		//私有频道，要传递收听者
		switch(channel.getChannelType()){
		case Private:
			target = targetRole;
			break;
		case Team:
			target = ((RoleInstance)speaker).getTeam();
			break;
		case Union:
			target = GameContext.getUnionApp().getUnion((RoleInstance)speaker);
			break;
		case Speak:
			target = ((AbstractRole)speaker).getMapInstance();
			break;
		case Map:
			target = ((AbstractRole)speaker).getMapInstance();
			break;
		}
		return target;
	}
	
	/**
	 * 玩家发送消息，会转发给GM
	 */
	private Result sendMessage(RoleEntity speaker, ChatSysName chatSysName, ChannelType channelType,
			String message, List<ChatContextItem> contextList, Serializable target){
		Result result = new Result();
		try{
			Channel channel = this.channelManager.getChannel(channelType);
			if(null == channel || Util.isEmpty(message)){
				return result.setInfo(Status.Chat_FAILURE.getTips());
			}
			C1802_ChatRouteRespMessage routeMsg = new C1802_ChatRouteRespMessage();
			if(null == chatSysName){
				chatSysName = ChatSysName.System;
			}
			routeMsg.setSendRoleId(SystemRoleId);
			routeMsg.setSendRoleName(chatSysName.getName());
			if(null != speaker){
				if(RoleType.PLAYER == speaker.getRoleType()){
					RoleInstance role = (RoleInstance)speaker;
					routeMsg.setSendRoleId(speaker.getIntRoleId());
					routeMsg.setSendRoleName(role.getRoleName());
					message = this.illegalWordsService.doFilter(message);
					byte vipLevel = GameContext.getVipApp().getVipLevel(role);
					routeMsg.setVipLv(vipLevel);
					routeMsg.setSex(role.getSex());
					routeMsg.setRoleLevel((byte)role.getLevel());
					routeMsg.setCampId(role.getCampId());
				}else if(RoleType.NPC == speaker.getRoleType()){
					routeMsg.setSendRoleId(speaker.getIntRoleId());
					routeMsg.setSendRoleName(((NpcInstance)speaker).getNpcname());
				}
			}
			routeMsg.setChannelType(channelType.getType());
			routeMsg.setMessage(message);
			routeMsg.setContextList(contextList);
			Result res = channel.send(speaker, routeMsg, target,ChatMessageType.Text);;
			if(res.isIgnore()){
				return res;
			}
			if(res.isSuccess()){
				//将聊天消息转发给GM
				//this.routeMessageToGm(speaker, routeMsg, channelType, target);
				if(speaker != null){
					if(channelType == ChannelType.Private && RoleType.PLAYER == speaker.getRoleType()){
						//私聊给玩家自己消息
						AbstractRole targetRole = (AbstractRole)target ;
						C1805_ChatPrivteRespMessage selfMsg = new C1805_ChatPrivteRespMessage();
						selfMsg.setMessage(message);
						selfMsg.setContextList(contextList);
						selfMsg.setTargetRoleId(targetRole.getIntRoleId());
						selfMsg.setTargetRoleName(targetRole.getRoleName());
						GameContext.getMessageCenter().sendByRoleId(null, speaker.getRoleId(), selfMsg);
					}
				}
			}
			return res;
		}catch(Exception e){
			this.logger.error("chat send message error:", e);
			return result.setInfo(Status.Chat_FAILURE.getTips());
		}
	}
	
	/**
	 * 转发聊天消息给GM
	 * @param speaker
	 * @param routeMsg
	 * @param channelType
	 * @param target
	 */
	private Result routeMessageToGm(RoleEntity speaker, C1802_ChatRouteRespMessage routeMsg,
			ChannelType channelType, Serializable target){
		return null ;
		/*
		try{
			C1805_ChatRouteToGmMessage toGmMsg = new C1805_ChatRouteToGmMessage();
			toGmMsg.setSendRoleId(String.valueOf(routeMsg.getSendRoleId()));
			toGmMsg.setSendRoleName(routeMsg.getSendRoleName());
			toGmMsg.setChannelType(channelType.getType());
			toGmMsg.setMessage(routeMsg.getMessage());
			toGmMsg.setContextList(routeMsg.getContextList());
			String receiveRoleId = "";
			String receiveRoleName = channelType.getName();
			if(ChannelType.Private == channelType){
				RoleInstance role = (RoleInstance)target;
				receiveRoleId = role.getRoleId();
				receiveRoleName = role.getRoleName();
			}else if(ChannelType.Faction == channelType){
				Faction faction = (Faction)target;
				receiveRoleId = faction.getFactionId();
				receiveRoleName = faction.getFactionName();
			}else if(ChannelType.Speak == channelType
					|| ChannelType.Map == channelType){
				MapInstance mapInstance = (MapInstance)target;
				receiveRoleId = mapInstance.getInstanceId();
				receiveRoleName = mapInstance.getMap().getMapConfig().getMapdisplayname();
			}
			toGmMsg.setReceiveRoleId(receiveRoleId);
			toGmMsg.setReceiveRoleName(receiveRoleName);
			Channel channel = this.channelManager.getChannel(ChannelType.Gm);
			return channel.send(null, toGmMsg, null);
		}catch(Exception e){
			this.logger.error("chat route message to gm error:", e);
			return new Result().setInfo(Status.Chat_FAILURE.getTips());
		}
	*/}
	
	/** GM发送聊天消息，私聊频道和世界频道 */
	private Result sendMessageByGm(RoleEntity speaker, ChannelType channelType, String message, 
			List<ChatContextItem> contextList, RoleInstance targetRole){
		Result result = new Result();
		try{
			Channel channel = this.channelManager.getChannel(channelType);
			if(ChannelType.Private != channelType && ChannelType.World != channelType){
				return result.setInfo(Status.Chat_Gm_Channel.getTips());
			}
			if(ChannelType.Private == channelType){
				if(null == targetRole){
					return result.setInfo(Status.Chat_Role_Offilne.getTips());
				}
			}
			C1802_ChatRouteRespMessage routeMsg = new C1802_ChatRouteRespMessage();
			routeMsg.setSendRoleId(GmRoleId);
			routeMsg.setSendRoleName(GmRoleName);
			routeMsg.setChannelType(channelType.getType());
			routeMsg.setMessage(message);
			routeMsg.setContextList(contextList);
			return channel.send(speaker, routeMsg, targetRole,ChatMessageType.Text);
		}catch(Exception e){
			this.logger.error("chat send message by gm error:", e);
			return result.setInfo(Status.Chat_FAILURE.getTips());
		}
	}
	

	@Override
	public void setArgs(Object arg0) {
		
	}

	@Override
	public void start() {
		this.loadChatLimitConfig();
	}

	@Override
	public void stop() {
		
	}
	
	private void loadChatLimitConfig(){
		String fileName = XlsSheetNameType.chat_config.getXlsName();
		String sheetName = XlsSheetNameType.chat_config.getSheetName();
		String info = "load excel error:fileName=" + fileName + ",sheetName=" + sheetName + ".";
		try {
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			List<ChatLimitConfig> list = XlsPojoUtil.sheetToList(sourceFile, sheetName, ChatLimitConfig.class);
			int index = 0;
			for(ChatLimitConfig config : list){
				if(null == config){
					continue;
				}
				//检测配置并初始化
				if(!config.checkAndInit(info)){
					continue;
				}
				//放到缓存中
				this.chatLimitMap.put(config.getChannelType(), config);
				//初始化限制频道的索引
				if(config.getSpaceTime() > 0){
					Channel channel = this.channelManager.getChannel(config.getChannelType());
					channel.setIndex(index);
					index ++;
				}
			}
			//有说话间隔时间的频道数量
			this.size = index;
		} catch (Exception e) {
			Log4jManager.CHECK.error(info, e);
			Log4jManager.checkFail();
		}
	}

	@Override
	public int onLogin(RoleInstance role, Object context) {
		try {
			Date[] chatLastSpeakTime = new Date[this.size]; 
			role.setChatLastSpeakTime(chatLastSpeakTime);
		} catch (Exception e) {
			this.logger.error("ChatApp.login error: ", e);
			return 0;
		}
		return 1;
	}
	
	@Override
	public int onLogout(RoleInstance role, Object context) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public int onCleanup(String roleId, Object context) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ChatLimitConfig getChatLimitConfig(ChannelType channelType) {
		return this.chatLimitMap.get(channelType);
	}

	@Override
	public Result sendVoiceMessage(RoleEntity speaker, ChannelType channelType, byte[] data, RoleInstance targetRole) {
		Result result = new Result();
		try{
			Result res = this.canSpeakByForbid(speaker, channelType);
			if(!res.isSuccess()){
				return res;
			}
			if(null == data || data.length == 0){
				return result.setInfo(GameContext.getI18n().getText(TextId.ERROR_INPUT));
			}
			Serializable target = this.getChatTarget(speaker, channelType, targetRole);
			return this.sendVoiceMessage(speaker, null, channelType, data, target);
		}catch(Exception e){
			this.logger.error(this.getClass().getName() + ".message error: ", e);
			return result.setInfo(Status.Chat_FAILURE.getTips());
		}
	}
	
	private String getVoiceText(byte[] voiceData){
		try{
			//没有开启此功能
			if(!GameContext.getParasConfig().isOpenVoice2Text()){
				return null ;
			}
			if(null == voiceData || 0 == voiceData.length){
				return null ;
			}
			String url = GameContext.getPlatformConfig().getVoice2TextUrl();
			if(Util.isEmpty(url)){
				return null ;
			}
			String data = HttpUtil.doPost(url, voiceData, VOICE_TO_TEXT_API_TIMEOUT) ;
			if(Util.isEmpty(data)){
				logger.error("voice to text response error, NULL ");
				return null ;
			}
			Response response = JSON.parseObject(data, Response.class);
			if(null == response || 1 != response.getStatus()){
				logger.error("voice to text response error," + data);
				return null ;
			}
			String text = response.getText() ;
			if(Util.isEmpty(text)){
				return text ;
			}
			return this.illegalWordsService.doFilter(text) ;
		}catch(Exception ex){
			logger.error("getVoiceText error",ex);
		}
		return null ;
	}
	
	/**
	 * 发送语音聊天消息
	 * @param speaker
	 * @param chatSysName
	 * @param channelType
	 * @param data
	 * @param target
	 * @return
	 */
	private Result sendVoiceMessage(RoleEntity speaker, ChatSysName chatSysName, ChannelType channelType, byte[] data, Serializable target){
		Result result = new Result();
		try{
			Channel channel = this.channelManager.getChannel(channelType);
			if(null == data || data.length == 0){
				return result.setInfo(GameContext.getI18n().getText(TextId.ERROR_INPUT));
			}
			C1804_ChatVoicePushMessage routeMsg = new C1804_ChatVoicePushMessage();
			if(null == chatSysName){
				chatSysName = ChatSysName.System;
			}
			routeMsg.setSendRoleId(SystemRoleId);
			routeMsg.setSendRoleName(chatSysName.getName());
			if(null != speaker){
				if(RoleType.PLAYER == speaker.getRoleType()){
					RoleInstance role = (RoleInstance)speaker;
					routeMsg.setSendRoleId(role.getIntRoleId());
					routeMsg.setSendRoleName(role.getRoleName());
					byte vipLevel = GameContext.getVipApp().getVipLevel(role);
					routeMsg.setVipLv(vipLevel);
					routeMsg.setSex(role.getSex());
					routeMsg.setRoleLevel((byte)role.getLevel());
					routeMsg.setCampId(role.getCampId());
				}else if(RoleType.NPC == speaker.getRoleType()){
					routeMsg.setSendRoleId(speaker.getIntRoleId());
					routeMsg.setSendRoleName(((NpcInstance)speaker).getNpcname());
				}
			}
			routeMsg.setChannelType(channelType.getType());
			routeMsg.setData(data);
			routeMsg.setVoiceText(this.getVoiceText(data));
			result = channel.send(speaker, routeMsg, target,ChatMessageType.Voice);
			if(result.isSuccess()
					&& null != speaker
					&& channelType == ChannelType.Private
					&& !channel.isSendToSpeaker()){
				GameContext.getMessageCenter().sendSysMsg((RoleInstance)speaker,routeMsg);
			}
			return result ;
		}catch(Exception e){
			this.logger.error(this.getClass().getName() + "sendVoiceMessage error: ", e);
			return result.setInfo(Status.Chat_FAILURE.getTips());
		}
	}

	@Override
	public Result sendSysVoiceMessage(ChatSysName chatSysName, ChannelType channelType, byte[] data, Serializable target) {
		return this.sendVoiceMessage(null, chatSysName, channelType, data, target);
	}
	
	/**
	 * 判断玩家能否在指定频道说话
	 * 部分玩家可能被禁言
	 * @param speaker
	 * @param channelType
	 * @return
	 */
	public Result canSpeakByForbid(RoleEntity speaker, ChannelType channelType){
		Result result = new Result();
		try {
			if(RoleType.PLAYER != speaker.getRoleType()){
				return result.success();
			}
			RoleInstance role = (RoleInstance) speaker;
			//是否全部禁言
			ChatForbidType forbidType = ChatForbidType.getChatForbidType(role.getForbidType());
			if(ChatForbidType.All == forbidType){
				Date endTime = role.getForbidEndTime();
				if(null != endTime && new Date().before(endTime)){
					return result.setInfo(Status.Chat_Forbid_All.getTips());
				}
				//如果禁言时间失效，则设置为非禁言状态
				role.setForbidType(ChatForbidType.None.getType());
				role.setForbidEndTime(null);
			}
			//是否禁言世界频道或阵营频道
			boolean isWordOrCamp = ChannelType.World == channelType || ChannelType.Camp == channelType;
			if(isWordOrCamp && ChatForbidType.WordAndCamp == forbidType){
				Date endTime = role.getForbidEndTime();
				if(null != endTime && new Date().before(role.getForbidEndTime())){
					return result.setInfo(Status.Chat_Forbid_Part.getTips());
				}
				//如果禁言时间失效，则设置为非禁言状态
				role.setForbidType(ChatForbidType.None.getType());
				role.setForbidEndTime(null);
			}
		} catch (RuntimeException e) {
			this.logger.error(this.getClass().getName() + ".canSpeakByForbid error: ", e);
		}
		return result.success();
	}

	@Override
	public Message getChatShowMessage(String param) {
		try {
			String[] params = Util.splitStr(param, Cat.colon);
			if (null == params || params.length < 1) {
				return null;
			}
			ChatShowType type = ChatShowType.getChatShowType(Short.valueOf(params[0]));
			if (null == type) {
				return null;
			}
			// 处理逻辑
			switch (type) {
			case team:
				C1312_TeamPanelPublishApplyReqMessage teamReq = new C1312_TeamPanelPublishApplyReqMessage();
				teamReq.setParam(params[1]);
				return teamReq;
			
			case good:
				C0508_GoodsInfoViewIdBindReqMessage goodReq = new C0508_GoodsInfoViewIdBindReqMessage();
				goodReq.setGoodsId(Integer.parseInt(params[1]));
				goodReq.setBindType((byte) -1);
				return goodReq;

			default:
				break;
			}
		} catch (Exception e) {
			logger.error("ChatAppImpl.getChatShowMessage error!", e);
		}
		return null;
	}
	
}
