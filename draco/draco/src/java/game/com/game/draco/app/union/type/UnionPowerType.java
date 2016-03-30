package com.game.draco.app.union.type;

public enum UnionPowerType {
	
	Modify_Desc((byte)1,"修改门派宗旨"),
	Modify_Notice((byte)2,"修改门派公告"),
	Dispose_Apply_Join((byte)3,"审批申请"),
	Remove_Member((byte)4,"驱除帮众"),
	Invite_To_Join((byte)5,"招纳帮众"),
	Impeach((byte)6,"弹劾帮主"),
	Union_Activity((byte)7,"活动控制"),
	Copy_Create((byte)8,"创建门派副本"),
	Integral_Exchange((byte)9,"积分兑换"),
	Modify_Name((byte)10,"修改门派名称"),
	Upgrade((byte)11,"门派升级"),
	UpgradeBuild((byte)12,"门派建筑升级"),
	UpgradeSkill((byte)13,"门派技能升级"),
	UnionDemise((byte)14,"禅让掌门"),
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
