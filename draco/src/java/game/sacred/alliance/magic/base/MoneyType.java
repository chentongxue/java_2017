package sacred.alliance.magic.base;

public enum MoneyType {
	game(AttributeType.gameMoney.getType(),true,
			AttributeType.gameMoney,(short)150),
			
	rmb(AttributeType.goldMoney.getType(),true,
			AttributeType.goldMoney,(short)224),
	;
	private final byte type ;
	private final boolean exchange ;
	private final AttributeType attributeType ;
	private final short imageId ;
	MoneyType(byte type,boolean exchange,
			AttributeType attributeType,
			short imageId){
		this.type = type ;
		this.exchange = exchange ;
		this.attributeType = attributeType ;
		this.imageId = imageId ;
	}
	public byte getType() {
		return type;
	}
	
	public static MoneyType get(byte type){
		for(MoneyType mt : values()){
			if(mt.getType() == type){
				return mt ;
			}
		}
		return null ;
	}
	public boolean isExchange() {
		return exchange;
	}
	public AttributeType getAttributeType() {
		return attributeType;
	}
	
	public short getImageId() {
		return imageId;
	}
	
	
}
