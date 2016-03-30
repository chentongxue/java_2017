package com.game.draco.app.exchange.consumetype;
/**
 * 与角色属性一致
 */
public enum ConsumeAttriType {
	SilverMoney(5),//1：银币
	BindingGoldMoney(3),//2：绑金
	Gold(4),//3：元宝
	UnionDkp(123),//4：DKP
	Honor(13),//5：荣誉
	Goods(6),//6：物品
	;
	private final int type;
	
	ConsumeAttriType(int type){
		this.type = type;
	}
	
	public int getType(){
		return type;
	}
	
	public static ConsumeAttriType get(int type){
		for(ConsumeAttriType v : values()){
			if(type == v.getType()){
				return v;
			}
		}
		return null;
	}
	
	public ConsumeLogic createConsumeLogic() {
		switch(this) {
		case SilverMoney:
			return new ConsumeSilverMoney();
		case BindingGoldMoney:
			return new ConsumeBindingGold();
		case Gold:
			return new ConsumeGold();
		case UnionDkp:
			return new ConsumeDkp();
		case Honor:
			return new ConsumeHonor();
		default:
			return null;
		}
	}
}
