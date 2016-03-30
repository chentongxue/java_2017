package com.game.draco.app.role.systemset.vo;

/**
 * 系统设置类型
 * 0 自动清除疲劳度次数
 * 1 聊天频道设置：按位取值[0:不屏蔽 1:屏蔽] 从左向右依次表示的频道[1-私聊 2-队伍 3-门派 4-世界 5-喊话 6-地图 7-系统 8-阵营 11-大喇叭]
 * 2 新手引导进度：新建角色值为0
 * 3 组队邀请设置：[0:开启-正常组队 1:屏蔽-不可被组队 2:自动-自动接受组队]
 * 4 入队申请设置：[0:开启-正常组队 1:屏蔽-拒绝入队申请 2:自动-自动接受入队申请]
 * 5 交易设置：[0:不屏蔽 1:屏蔽(不可交易)]
 * 6 新手引导进度2：默认值为0
 * 7 系统功能开启 按位取值[0:关闭 1:开启] 从右向左依次表示[0 语音聊天]
 * 8 聊天语音设置:规则同1（聊天频道设置）
 * 9 时装显示：[0:显示 1:隐藏]
 * 10 综合设置：[1:最佳性能 2:正常体验 3：最佳画质]
 * 11 音效设置：规则同1
 * 12 挂机生命设置：百分比
 * 13 换英雄百分比[0-100]
 */
public enum SystemSetType {
	
	Fatigue((byte)0,"清除疲劳度次数",0),
	Chat((byte)1,"聊天频道设置",0),
	Guide((byte)2,"新手引导进度",0),
	TeamInvite((byte)3,"组队邀请设置",0),
	TeamApply((byte)4,"入队申请设置",0),
	Trade((byte)5,"交易设置",0),
	Guide_2((byte)6,"新手引导进度2",0),
	SystemSwitch((byte)7,"系统功能开关",0),
	ChatVoice((byte)8,"语音聊天设置",0),
	CleanFatigueMaxTimes((byte)9,"清除疲劳度最大次数",0),
	Synthesize((byte)10,"综合设置",1),
	Sound((byte)11,"音效设置",3),
	percentHP((byte)12,"挂机生命设置",80),
	percentHero((byte)13,"换英雄百分比",80),
	;
	
	private final byte type;
	private final String name;
	private final int defaultValue;
	
	SystemSetType(byte type, String name, int defaultValue){
		this.type = type;
		this.name = name;
		this.defaultValue = defaultValue;
	}

	public byte getType() {
		return type;
	}

	public String getName() {
		return name;
	}
	
	public int getDefaultValue() {
		return defaultValue;
	}

	public static SystemSetType get(byte type){
		for(SystemSetType item : SystemSetType.values()){
			if(item.getType() == type){
				return item;
			}
		}
		return null;
	}
}
