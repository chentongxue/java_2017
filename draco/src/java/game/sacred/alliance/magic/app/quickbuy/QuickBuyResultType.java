package sacred.alliance.magic.app.quickbuy;

public enum QuickBuyResultType {
	
	Not_Support((byte)0,"不支持快速购买"),
	Goods_Enough((byte)1,"物品数量足够"),
	Send_Buy_Message((byte)2,"发送快速购买消息"),
	Pay_Failure((byte)3,"快速购买支付失败"),
	Pay_Success((byte)4,"快速购买支付成功"),
	
	;
	
	private final byte type;
	private final String name;
	
	private QuickBuyResultType(byte type, String name) {
		this.type = type;
		this.name = name;
	}

	public byte getType() {
		return type;
	}

	public String getName() {
		return name;
	}
	
	public static QuickBuyResultType getActiveStatus(byte type){
		for(QuickBuyResultType item : QuickBuyResultType.values()){
			if(item.getType() == type){
				return item;
			}
		}
		return null;
	}
	
}
