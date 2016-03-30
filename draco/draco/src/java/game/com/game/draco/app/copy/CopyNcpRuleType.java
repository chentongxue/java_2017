package com.game.draco.app.copy;

public enum CopyNcpRuleType {
	
	Without((byte)-1,"不需要刷怪"),
	Default((byte)0,"固定配置"),
	Role_Level_Auto((byte)1,"角色等级匹配"),
	Role_Choose((byte)2,"角色选择")
	
	;
	
	private final byte type;
	private final String name;

	CopyNcpRuleType(byte type, String name){
		this.type = type;
		this.name = name;
	}
	
	public byte getType() {
		return type;
	}
	
	public String getName() {
		return name;
	}

	public static CopyNcpRuleType getCopyType(byte type){
		for(CopyNcpRuleType ruleType : CopyNcpRuleType.values()){
			if(ruleType.getType() == type){
				return ruleType;
			}
		}
		return null;
	}
}
