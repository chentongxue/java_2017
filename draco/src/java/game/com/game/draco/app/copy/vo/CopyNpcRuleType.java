package com.game.draco.app.copy.vo;

public enum CopyNpcRuleType {
	
	Without((byte)-1),//不需要刷怪
	Default((byte)0),//固定配置
	Role_Level_Auto((byte)1),//角色等级匹配
	Role_Choose((byte)2)//角色选择
	
	;
	
	private final byte type;

	CopyNpcRuleType(byte type){
		this.type = type;
	}
	
	public byte getType() {
		return type;
	}
	
	public static CopyNpcRuleType getCopyType(byte type){
		for(CopyNpcRuleType ruleType : CopyNpcRuleType.values()){
			if(ruleType.getType() == type){
				return ruleType;
			}
		}
		return null;
	}
}
