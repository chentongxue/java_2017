package sacred.alliance.magic.base;

public enum GoodsStrengthenType {
	//普通强化
	common(0),
	//保底强化
	no_downgrade(1),
	
	;
	private final int type;
	
	GoodsStrengthenType(int type){
		this.type = type;
	}
	
	public int getType(){
		return type;
	}
	
	public static GoodsStrengthenType get(int type) {
		for (GoodsStrengthenType gst : GoodsStrengthenType.values()) {
			if (gst.getType() == type) {
				return gst;
			}
		}
		return null;
	}
}
