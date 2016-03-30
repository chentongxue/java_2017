package sacred.alliance.magic.base;

public enum FactionPowerType {
	
	Modify_Desc((byte)1,"修改门派宗旨"),
	Modify_Notice((byte)2,"修改门派公告"),
	Dispose_Apply_Join((byte)3,"审批申请"),
	Remove_Member((byte)4,"驱除帮众"),
	Invite_To_Join((byte)5,"招纳帮众"),
	Impeach((byte)6,"弹劾帮主"),
	Warlords_Apply((byte)7,"报名群雄逐鹿"),
	Copy_Create((byte)8,"创建门派副本"),
	Integral_Exchange((byte)9,"积分兑换"),
	Modify_Name((byte)10,"修改门派名称"),
	Upgrade((byte)11,"门派升级"),
	UpgradeBuild((byte)12,"门派建筑升级"),
	UpgradeSkill((byte)13,"门派技能升级"),
	SoulFly((byte)14,"门派神兽飞升"),
	SummonSoul((byte)15,"召唤帮会仙兽"),
	FactionDemise((byte)16,"禅让掌门"),
	FactionInpire((byte)17,"鼓舞"),
	;
	
	private final byte type;
	private final String name;
	
	FactionPowerType(byte type,String name){
		this.type = type;
		this.name = name;
	}
	
	public final byte getType(){
		return type;
	}
	
	public String getName(){
		return name;
	}
	
	public static FactionPowerType get(byte type){
		for(FactionPowerType power : FactionPowerType.values()){
			if(power.getType() == type){
				return power;
			}
		}
		return null;
	}

 }
