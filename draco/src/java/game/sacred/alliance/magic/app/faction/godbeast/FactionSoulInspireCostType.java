package sacred.alliance.magic.app.faction.godbeast;

import sacred.alliance.magic.base.AttributeType;


public enum FactionSoulInspireCostType {
	
	GameMoney((byte)0, "游戏币", AttributeType.gameMoney),
	GoldMoney((byte)1, "元宝", AttributeType.goldMoney),
	factionMoney((byte)3, "门派资金", AttributeType.factionMoney),
		
	;
	
	private final byte type;
	private final String name;
	private final AttributeType attrType;
	
	FactionSoulInspireCostType(byte type, String name, AttributeType attrType){
		this.type = type;
		this.name = name;
		this.attrType = attrType;
	}
	
	public byte getType(){
		return type;
	}
	
    public String getName() {
		return name;
	}
    
	public AttributeType getAttrType() {
		return attrType;
	}

	public static FactionSoulInspireCostType get(byte type){
		for(FactionSoulInspireCostType item : FactionSoulInspireCostType.values()){
			if(item.getType() == type){
				return item;
			}
		}
		return null;
    }
	
}
