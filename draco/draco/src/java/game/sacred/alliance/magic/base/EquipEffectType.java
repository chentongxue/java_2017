package sacred.alliance.magic.base;

/**
 * 装备特效
 * 
 * @author Wang.K
 * 
 */
public enum EquipEffectType {

	notHave(0, ""), // 没有特效
	weapon_locus(1, "weapon_locus"), // 武器轨迹
	weapon_glint(2, "weapon_glint"), // 武器发光
	equip_around(3, "equip_around"), // 身体周围
	equip_waist(4, "equip_waist"), // 腰部
	equip_foot(5, "equip_foot");// 脚下

	private int type;
	private String sheetName;// 对应加载Map中的Key

	EquipEffectType(int type, String sheetName) {
		this.type = type;
		this.sheetName = sheetName;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getSheetName() {
		return sheetName;
	}

	public void setSheetName(String sheetName) {
		this.sheetName = sheetName;
	}
}
