package com.game.draco.app.skill.config;

import java.util.List;

import com.google.common.collect.Lists;
import com.google.common.primitives.Shorts;

import sacred.alliance.magic.util.Initable;
import sacred.alliance.magic.util.KeySupport;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;
import lombok.Data;

public @Data
class SkillBase implements KeySupport<Short>,Initable{

	// 技能ID
	private short skillId;

	// 技能名称
	private String name;

	// 系统来源
	private byte sourceType;

	// 图标ID
	private short iconId;

	// 效果ID
	private short effectId;

	// 动作ID
	private byte actionId;

	// 声音ID
	private short musicId;

	// 技能类型[0主|1被]
	private byte skillApplyType;

	// CD(单位毫秒)
	private int cd;

	// 最小释放距离
	private int minUseRange;

	// 最大释放距离
	private int maxUseRange;

	// 目标类型[0任意|1敌方|2己方]
	private byte serverTargetType;

	// 客户端目标类型 [0任意 1敌方 2友方 3自己]
	private byte clientTargetType;
	
	//技能效果类型
	private byte skillEffectType;

	// 是否触发被动技能 [0否 1是]
	private boolean triggerPassive;

	// 是否公用公共CD[0否 1是]
	private boolean useGlobalCd;

	// 命中修正
	private int hitChange;

	// 暴击修正
	private int critChange;

	// 攻击方式 [0默认 | 1普通攻击 | 2冲锋 | 3闪现]
	private byte attackType;

	// 预留参数
	private short prepareArg;

	// 仇恨百分比
	private int hatredPercent;

	// 额外附加仇恨
	private int hatredAdd;

	// 被动技能影响主动技能的id表(CD、耗蓝、耗HP、攻击距离、预留参数；用,分割；影响全部填-1)
	private String affectSkills;

	// 被攻击者受伤动画id
	private short targetAnimId;

	// 被攻击者受伤特效id
	private short targetEffectId;

	// 描述
	private String des;

	// 最大等级
	private short maxLevel;
	
	//是否固定坐标
	private boolean fixedXy;
	
	//是否引导技能
	private boolean guideSkill;
	
	//音乐
	private String music;
	
	private short[] musicIds ;
	
	//选取规则是客户端还是服务器端
	private boolean selectionRules;
	
	//是否蒙黑
	private boolean blackGround;
	
	//是否需要持续特效
	private boolean continueEffectId;
	
	@Override
	public void init() {
		try {
			String[] arr = Util.splitString(music);
			if (null == arr) {
				return;
			}
			List<Short> shortList = Lists.newArrayList();
			for (String s : arr) {
				shortList.add(Short.parseShort(s.trim()));
			}
			this.musicIds = Shorts.toArray(shortList);
		}catch(Exception ex){
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("skill music config error,skillId=" + skillId ,ex);
		}
	}

	@Override
	public Short getKey() {
		return getSkillId();
	}

}
