package sacred.alliance.magic.domain;

import com.game.draco.GameContext;

import lombok.Data;
import sacred.alliance.magic.app.chat.ChannelType;
import sacred.alliance.magic.app.role.systemset.ChatShieldType;
import sacred.alliance.magic.app.role.systemset.SystemSetState;
import sacred.alliance.magic.app.role.systemset.SystemSetType;
import sacred.alliance.magic.app.role.systemset.TeamShieldType;
import sacred.alliance.magic.base.SystemFuncSwitch;

public @Data class RoleSystemSet {
	
	public static final String ROLEID = "roleId";
	
	private int roleId;//角色ID
	private int helmet;//头盔显示 [0:显示 1:隐藏]
	private int chat;//聊天设置
	private int guide;//新手引导进度
	private int teamInvite;//组队邀请设置
	private int teamApply;//入队申请设置
	private int trade;//交易设置
	private int guide2;//新手引导进度2
	private int chatVoice;//聊天语音设置
	private int fashion;//时装显示 [0:显示 1:隐藏]
	private SystemSetState sysSetState;//系统设置的状态
	
	public static RoleSystemSet getDefaultInstance(int roleId){
		RoleSystemSet sysSet = new RoleSystemSet();
		sysSet.setRoleId(roleId);
		sysSet.setHelmet(SystemSetType.Helmet.getDefaultValue());
		sysSet.setChat(SystemSetType.Chat.getDefaultValue());
		sysSet.setGuide(SystemSetType.Guide.getDefaultValue());
		sysSet.setTeamInvite(SystemSetType.TeamInvite.getDefaultValue());
		sysSet.setTeamApply(SystemSetType.TeamApply.getDefaultValue());
		sysSet.setTrade(SystemSetType.Trade.getDefaultValue());
		sysSet.setGuide2(SystemSetType.Guide_2.getDefaultValue());
		sysSet.setChatVoice(SystemSetType.ChatVoice.getDefaultValue());
		sysSet.setFashion(SystemSetType.Fashion.getDefaultValue());
		return sysSet;
	}
	
	public boolean isDefaultValue(){
		return this.equals(getDefaultInstance(this.roleId));
	}
	
	public int getValue(SystemSetType sysSetType){
		int value = 0;
		switch(sysSetType){
		case Helmet:
			value = this.helmet;
			break;
		case Chat:
			value = this.chat;
			break;
		case Guide:
			value = this.guide;
			break;
		case TeamInvite:
			value = this.teamInvite;
			break;
		case TeamApply:
			value = this.teamApply;
			break;
		case Trade:
			value = this.trade;
			break;
		case Guide_2:
			value = this.guide2;
			break;
		case SystemSwitch :
			if(GameContext.getParasConfig().isOpenChatVoice()){
				value =  value|(1 << SystemFuncSwitch.chat_voice.getType());
			}
			break ;
		case ChatVoice:
			value = this.chatVoice;
			break;
		case Fashion:
			value = this.fashion;
			break;
		}
		return value;
	}
	
	public void setValue(SystemSetType sysSetType, int value){
		switch(sysSetType){
		case Helmet:
			this.helmet = value;
			break;
		case Chat:
			this.chat = value;
			break;
		case Guide:
			this.guide = value;
			break;
		case TeamInvite:
			this.teamInvite = value;
			break;
		case TeamApply:
			this.teamApply = value;
			break;
		case Trade:
			this.trade = value;
			break;
		case Guide_2:
			this.guide2 = value;
			break;
		case ChatVoice:
			this.chatVoice = value;
			break;
		case Fashion:
			this.fashion = value;
			break;
		}
	}
	
	/**
	 * 屏蔽组队邀请
	 * 角色没有队伍时触发
	 * @return
	 */
	public boolean isShieldTeamInvite(){
		return TeamShieldType.Shield == TeamShieldType.get(this.teamInvite);
	}
	
	/**
	 * 屏蔽入队申请
	 * 角色在队伍中是队长时触发
	 * @return
	 */
	public boolean isShieldTeamApply(){
		return TeamShieldType.Shield == TeamShieldType.get(this.teamApply);
	}
	
	/** 屏蔽交易，拒绝交易邀请 */
	public boolean isTradeShield(){
		return 1 == this.trade;
	}
	
	/** 是否显示头盔外形 */
	public boolean isHelmetShow(){
		return 0 == this.helmet;
	}
	
	/**
	 * 屏蔽聊天消息
	 * 不接收该频道的聊天消息
	 * @param channelType
	 * @return
	 */
	public boolean isChatShield(ChannelType channelType){
		if(0 == this.chat){
			return false;
		}
		ChatShieldType chatShieldType = ChatShieldType.getByChannel(channelType);
		if(null == chatShieldType){
			return false;
		}
		return (this.chat & chatShieldType.getValue()) != 0;
	}
	
	/**
	 * 是否屏蔽语音聊天
	 * @param channelType
	 * @return
	 */
	public boolean isChatVoiceShield(ChannelType channelType){
		if(0 == this.chatVoice){
			return false;
		}
		ChatShieldType chatShieldType = ChatShieldType.getByChannel(channelType);
		if(null == chatShieldType){
			return false;
		}
		return (this.chatVoice & chatShieldType.getValue()) != 0;
	}
	
	/**
	 * 是否显示时装外形
	 * @return
	 */
	public boolean isFactionShow(){
		return 0 == this.fashion;
	}
	
}
