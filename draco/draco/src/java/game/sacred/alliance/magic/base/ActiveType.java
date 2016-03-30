package sacred.alliance.magic.base;

public enum ActiveType {
	
	Common((byte)0,false,"通用显示类活动",true),
	Compass((byte)2,true,"上古法阵/淘宝",true),
	Arean1v1((byte)4,true,"1v1擂台赛",true),
	Dps((byte)6,false,"BossDPS",true),
	Discount((byte)7,true,"折扣活动",true),
	Rank((byte)8,false,"排行活动",true),
	Siege((byte)12,false,"怪物攻城",true),
	//此类活动，活动开启时间才容许进入，活动结束后将所有人传出
	ActiveMap((byte)14,false,"活动地图",true), 
	Treasure((byte)15,false,"藏宝图",true),
	CampWar((byte)16,true,"阵营战",true),
	Areannvn((byte)17,true,"nvn擂台赛",true),
	Carnival((byte)18,true,"嘉年华",true),
	Ladder((byte)19,true,"天梯",true),
	ArenaTop((byte)20,true,"大师赛",true),
	FactionWar((byte)21,true,"门派战",true),
	;
	
	private final byte type;//活动类型
	private final boolean onlyone;//活动是唯一的
	private final String name;//活动名称
	private final boolean usable;//是否支持此类活动
	
	private ActiveType(byte type, boolean onlyone, String name, boolean usable) {
		this.type = type;
		this.onlyone = onlyone;
		this.name = name;
		this.usable = usable;
	}

	public byte getType() {
		return type;
	}

	public String getName() {
		return name;
	}
	
	public boolean isOnlyone() {
		return onlyone;
	}

	public boolean isUsable() {
		return usable;
	}

	public static ActiveType get(byte type){
		for(ActiveType actType:ActiveType.values()){
			if(actType.getType()==type){
				return actType;
			}
		}
		return null;
	}
}
