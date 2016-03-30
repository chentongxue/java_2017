package com.game.draco.app.union.type;

public enum UnionPowerType {
	
	Modify_Desc((byte)1,"修改公会宗旨"),
	Modify_Notice((byte)2,"修改公会公告"),
	Dispose_Apply_Join((byte)3,"审批申请"),
	Remove_Member((byte)4,"驱除帮众"),
	Invite_To_Join((byte)5,"招纳帮众"),
	Impeach((byte)6,"弹劾帮主"),
	Union_Activity((byte)7,"活动控制"),
	Upgrade((byte)8,"公会升级"),
	UnionDemise((byte)9,"禅让会长"),
	UnionSummon((byte)10,"召唤BOSS"),
	UnionSetTarget((byte)11,"设置目标"),
	;
	
	private final byte type;
	private final String name;
	
	UnionPowerType(byte type,String name){
		this.type = type;
		this.name = name;
	}
	
	public final byte getType(){
		return type;
	}
	
	public String getName(){
		return name;
	}
	
	public static UnionPowerType get(byte type){
		for(UnionPowerType power : UnionPowerType.values()){
			if(power.getType() == type){
				return power;
			}
		}
		return null;
	}

 }
