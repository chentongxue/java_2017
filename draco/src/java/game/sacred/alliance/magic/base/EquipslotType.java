package sacred.alliance.magic.base;

public enum EquipslotType {
	
	head(0, true), //头部
    accessories(1, true), //饰品
    shoe(2, true), //鞋子
    weapon(3, true), //武器
    clothes(4, true), //衣服
    ring(5, true), //戒指
    
	
	/*head(0, true), //头部
	necklace(1, true), //项链
	shoulder(2, true), //肩部
	weapon(3, true), //武器
	clothes(4, true), //衣服
	ring(5, true), //戒指
	trousers(6, true), //腿部
	waistband(7, true), //腰带
	shoe(8, true), //鞋子
	glove(9, true), //手套
	wing(10, false), //翅膀
	accessories(11, false), //饰品
*/	
	;
	
	private final int type;
	private final boolean effect; //是否产生装备特效
	
	EquipslotType(int type, boolean effect) {
		this.type = type;
		this.effect = effect;
	}
	
	public int getType() {
		return type;
	}
	
	public boolean isEffect() {
		return effect;
	}

	public static EquipslotType get(int type) {
		for (EquipslotType item : EquipslotType.values()) {
			if (item.getType() == type) {
				return item;
			}
		}
		return null;
	}
	
	public static int getEffectSlotNum(){
		int num = 0;
		for(EquipslotType item : EquipslotType.values()){
			if(item.isEffect()){
				num ++ ;
			}
		}
		return num ;
	}
	
}
