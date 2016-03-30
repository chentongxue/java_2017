package sacred.alliance.magic.base;

public enum StorageType {
	bag((byte)1,null), // 背包
	warehouse((byte)2,null), // 仓库
	//equip((byte)3,GoodsType.GoodsEquHuman), // 装备
	mail((byte)4,null),//邮件
	hero((byte)5,GoodsType.GoodsEquHuman),//英雄
	trading((byte)6,null),//交易 
	;
	
	private final byte type;
	/**
	 * 要求的物品类型
	 * null 为没有限制
	 */
	private final GoodsType onGoodsType ;

	StorageType(byte type,GoodsType goodsType) {
		this.type = type;
		this.onGoodsType = goodsType ;
	}

	public byte getType() {
		return type;
	}
	

	public boolean isCanOnEquip() {
		return (null != this.onGoodsType);
	}

	public GoodsType getOnGoodsType() {
		return onGoodsType;
	}

	public static StorageType get(byte type) {
		for(StorageType st : StorageType.values()){
			if(st.getType() == type){
				return st;
			}
			
		}
		return null;
	}
}
