package com.game.draco.app.skill.config;

import lombok.Data;

public @Data
class CopyOfSkillBase {

	// 技能ID
	private short skillId;

	// 技能名称
	private String name;

	// 系统来源
	private byte sourceType;

	// 图标ID
	private byte iconId;

	// 效果ID
	private short effectId;

	// 动作ID
	private byte actionId;

	// 声音ID
	private byte musicId;

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
	private String desc;

	// 最大等级
	private short maxLevle;

}
