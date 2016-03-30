package sacred.alliance.magic.app.hint;

public enum HintId {
	
	Degree((byte)1),
	Benefit((byte)2),
	VIP_Reward((byte)3),
	Faction_Salary((byte)4),
	Discount((byte)5),
	First_Recharge((byte)6),
	;
	
	private final byte id;

	HintId(byte id){
		this.id = id;
	}
	
	public byte getId() {
		return id;
	}
	
	public static HintId getCopyType(byte id){
		for(HintId item : HintId.values()){
			if(item.getId() == id){
				return item;
			}
		}
		return null;
	}
}
