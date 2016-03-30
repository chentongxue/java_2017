package sacred.alliance.magic.base;

public enum ConsumeNumberType {
	
	Silver((byte)1,"银币"),
	BindingGold((byte)2,"绑定元宝"),
	Gold((byte)3,"元宝"),
	Honor((byte)4,"荣誉"),
	FactionMoney((byte)5,"门派资金"),
	Contribute((byte)6,"门派贡献度"),
	;
	
	private final byte type;
	private final String name;
	
	ConsumeNumberType(byte type, String name){
		this.type = type;
		this.name = name;
	}
	
	public final byte getType(){
		return type;
	}
	public String getName(){
		return name;
	}
	
	public static ConsumeNumberType getPosition(byte type){
		for(ConsumeNumberType item : ConsumeNumberType.values()){
			if(item.getType() == type){
				return item;
			}
		}
		return null;
	}
}
