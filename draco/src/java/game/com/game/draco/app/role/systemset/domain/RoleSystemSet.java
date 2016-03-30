package com.game.draco.app.role.systemset.domain;

import com.game.draco.GameContext;
import com.game.draco.app.chat.ChannelType;
import com.game.draco.app.role.systemset.vo.ChatShieldType;
import com.game.draco.app.role.systemset.vo.SystemSetState;
import com.game.draco.app.role.systemset.vo.SystemSetType;
import com.game.draco.app.role.systemset.vo.TeamShieldType;

import lombok.Data;
import sacred.alliance.magic.base.SystemFuncSwitch;

public @Data class RoleSystemSet {
	
	public static final String ROLEID = "roleId";
	
	private int roleId;//角色ID
	private int synthesize;// 综合设置[0:最佳性能  1：正常体验  2：最佳画质]
	private int sound;// 声音设置[0:关闭，关闭 2： 打开，关闭 1：关闭，打开 3：打开，打开]
	private int chat;//聊天设置[0：关关关关 15：开开开开]
	private int chatVoice;//屏蔽语音设置[0:关关关 7：开开开]
	private int percentHP;// 挂机生命百分比[0-100]
	private int percentHero;// 换英雄百分比[0-100]
	
	private int fatigue;//清除疲劳度次数
	private int guide;//新手引导进度
	private int guide2;//新手引导进度2
	private int teamInvite;//组队邀请设置
	private int teamApply;//入队申请设置
	private int trade;//交易设置
	private SystemSetState sysSetState;//系统设置的状态
	
	public static RoleSystemSet getDefaultInstance(int roleId){
		RoleSystemSet sysSet = new RoleSystemSet();
		sysSet.setRoleId(roleId);
		sysSet.setFatigue(SystemSetType.Fatigue.getDefaultValue());
		sysSet.setChat(SystemSetType.Chat.getDefaultValue());
		sysSet.setGuide(SystemSetType.Guide.getDefaultValue());
		sysSet.setTeamInvite(SystemSetType.TeamInvite.getDefaultValue());
		sysSet.setTeamApply(SystemSetType.TeamApply.getDefaultValue());
		sysSet.setTrade(SystemSetType.Trade.getDefaultValue());
		sysSet.setGuide2(SystemSetType.Guide_2.getDefaultValue());
		sysSet.setChatVoice(SystemSetType.ChatVoice.getDefaultValue());
		sysSet.setSound(SystemSetType.Sound.getDefaultValue());
		sysSet.setSynthesize(SystemSetType.Synthesize.getDefaultValue());
		sysSet.setPercentHP(SystemSetType.percentHP.getDefaultValue());
		sysSet.setPercentHero(SystemSetType.percentHero.getDefaultValue());
		return sysSet;
	}
	
	public boolean isDefaultValue(){
		return this.equals(getDefaultInstance(this.roleId));
	}
	
	public int getValue(SystemSetType sysSetType){
		int value = 0;
		switch(sysSetType){
		case Fatigue:
			value = this.fatigue;
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
		case Synthesize:
			value = this.synthesize;
			break;
		case Sound:
			value = this.sound;
			break;
		case percentHP:
			value = this.percentHP;
			break;
		case percentHero:
			value = this.percentHero;
			break;
		case CleanFatigueMaxTimes :
			value = GameContext.getAttriApp().getCleanFatigueMaxTimes();
		}
		return value;
	}
	
	public void setValue(SystemSetType sysSetType, int value){
		switch(sysSetType){
		case Fatigue:
			this.fatigue = value;
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
		case Synthesize:
			this.synthesize = value;
			break;
		case Sound:
			this.sound = value;
			break;
		case percentHP:
			this.percentHP = value;
			break;
		case percentHero:
			this.percentHero = value;
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
	
	
}
