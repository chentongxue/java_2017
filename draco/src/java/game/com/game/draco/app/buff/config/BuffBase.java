package com.game.draco.app.buff.config;

import lombok.Data;
import sacred.alliance.magic.util.KeySupport;

public @Data
class BuffBase implements KeySupport<Short>{

	// BUFFID
	private short buffId;

	// BUFF名称
	private String name;
	
	// BUFF状态
	private byte stateType;
	
	// BUFF类型
	private byte buffType;

	// 替换类型
	private byte replaceType;

	// 图标ID
	private byte iconId;

	// 效果ID
	private short effectId;

	// buff时间类型
	private byte timeType;

	// buff分类
	private byte categoryType;

	// 变身是否清除
	private boolean transNoClean;

	// 间隔时间
	private int intervalTime;

	// 持续时间
	private int persistTime;

	// 死亡后是否消失
	private boolean dieLost;

	// 下线是否消失
	private boolean offlineLost;

	// 下线是否计时
	private boolean offlineTiming;
	
	// 过图是否消失
	private boolean transLost;
	
	// 退出副本是否消失
	private boolean exitInsLost;

	// 切换英雄是否保留
	private boolean switchOn;

	// buff类型
	private byte beingType;

	// 互斥组
	private int groupId;

	// 替换不能时说明
	private String notReplaceDesc;

	// 伤血造成仇恨百分比
	private int hatredPercent;

	// 额外附加仇恨
	private int hatredAdd;

	// 抗体类型
	private byte hurtType;

	// 外形缩放比例
	private byte zoom;

	// 颜色变化值
	private String discolor;

	// 描述
	private String des;
	
	//效果类型
	private byte effectType;
	
	// 最大等级
	private int maxLevel;
	
	//持续攻击技能ID
	private short skillContinue;
	
	//是否击飞
	private boolean blowfly;
	
	//是否随机
	private boolean random;
	
	//是否叠加伤害
	private boolean stack;
	
	//最大叠层数
	private int maxLayer;
	
	@Override
	public Short getKey() {
		return getBuffId();
	}

}
