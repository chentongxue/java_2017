package sacred.alliance.magic.base;

public enum RoleInfoType {
	
	unknow(0,"未定义"),
	property(1,"属性"),
	equip(2,"装备"),
	bag(3,"背包"),
	skill(4,"技能"),
	friend(6,"好友"),
	mount(7,"坐骑"),
	task(8,"任务"),
	warehouse(9,"仓库"),
	mercury(10,"拍卖行"),
	team(11,"队伍"),
	base(12,"基本信息"),
	userpay(14,"充值情况"),
	discount(15,"充值活动"),
	mailList(16,"邮件列表"),
	
	;
	
	private final int type;
	private final String name;
	RoleInfoType(int type,String name) {
		this.type = type;
		this.name = name;
	}

	public int getType() {
		return type;
	}
	
	public String getName(){
		return name;
	}

	public static RoleInfoType get(int type) {
		for (RoleInfoType infoType : RoleInfoType.values()) {
			if (type == infoType.getType()) {
				return infoType;
			}
		}
		return unknow;
	}
}
