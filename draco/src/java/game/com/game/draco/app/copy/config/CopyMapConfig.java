package com.game.draco.app.copy.config;

import com.game.draco.app.copy.vo.CopyNpcRuleType;

import sacred.alliance.magic.util.Util;
import lombok.Data;

public @Data class CopyMapConfig {
	private short copyId;
	private String mapId;
	private byte mapIndex;
	private String needKillNpcId;
	private boolean needKillAll;
	private String needKillAllTips;
	private String passTips;
	private byte ruleType;//刷怪规则类型[0:固定配置 1:角色等级匹配 2:角色选择]
	private String ruleId;//刷怪规则
	private int limitTime;//限制时间（过关倒计时）
	private short jumpX;//传送点X
	private short jumpY;//传送点Y
	private String toMapId;//目标地图ID
	private short toMapX;//目标X
	private short toMapY;//目标Y
	/**
	 * 地图通关后是否清除NPC
	0 不清
	1 清除
	 */
	private byte passCleanNpc = 0 ; 
	
	private byte stopRef = 0; //指定npc死亡 停止刷新
	
	private boolean firstMap = false;
	private boolean lastMap = false;
	
	/** 是否有通关条件 **/
	public boolean hasPassCondition(){
		return this.needKillAll || !Util.isEmpty(this.needKillNpcId);
	}
	
	public boolean isKillNpc(String npcId){
		if(npcId.equals(needKillNpcId)){
			return true;
		}
		return false;
	}
	
	/** 是否停止刷新 **/
	public boolean hasStopRefCondition(){
		return stopRef == (byte)1;
	}
	
	public CopyNpcRuleType getCopyNcpRuleType(){
		return CopyNpcRuleType.getCopyType(this.ruleType);
	}
	
	/**
	 * 是否有倒计时限制
	 * @return
	 */
	public boolean isTimeLimit(){
		return this.limitTime > 0;
	}
	
}
