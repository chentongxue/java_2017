package sacred.alliance.magic.base;

public enum ExchangeType {

	numerical(0),//用数值兑换的类型
	goods(1),//用物品兑换的类型
	;
	ExchangeType(int type){
		this.type = type;
	}
	
	private final int type;

	public int getType() {
		return type;
	}
	
	public static ExchangeType getExchangeType(byte type){
		for(ExchangeType item : ExchangeType.values()){
			if(item.getType() == type){
				return item;
			}
		}
		return null;
	}
}
