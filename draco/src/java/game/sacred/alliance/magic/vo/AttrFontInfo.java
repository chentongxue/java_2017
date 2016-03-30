package sacred.alliance.magic.vo;

public class AttrFontInfo {
	
	private int roleId;//飘字角色id
	private int attackerId; //攻击者id size=3时客户端需要attackerId来控制方向等
	private byte size;//飘字大小[0:周期性伤害或治疗 1:正常 2:暴击 4:哪种属性攻击]
	private byte color;//飘字颜色[0:普通攻击 1:技能伤害 2:受到伤害 3:恢复HP 4:特殊状态 5:恢复气力 6:特殊状态 7:宠物工具 8:属性伤害]
	private int value;//属性值 当特殊状态时[1:闪躲 2:抵抗 3:免疫 4:吸收 5:格挡6:击飞7:击退8:属性伤害]
	
	/**
	 * 拼接飘字样式的type
	 * 高3位表示大小 低5位表示颜色
	 * @param size
	 * @param color
	 * @return
	 */
	public byte getFontType(){
		return (byte)(this.size<<5 | this.color);
	}

	public int getRoleId() {
		return roleId;
	}

	public void setRoleId(int roleId) {
		this.roleId = roleId;
	}
	
	public int getAttackerId() {
		return attackerId;
	}

	public void setAttackerId(int attackerId) {
		this.attackerId = attackerId;
	}

	public byte getSize() {
		return size;
	}

	public void setSize(byte size) {
		this.size = size;
	}

	public byte getColor() {
		return color;
	}

	public void setColor(byte color) {
		this.color = color;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}
	
}
