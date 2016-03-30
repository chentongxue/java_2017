package sacred.alliance.magic.base;

public enum ActiveType {
	
	Common((byte)0,false,"通用显示类活动",true),
	BossDps((byte)1,true,"BossDPS",true),
	AngelChest((byte)2,true,"神仙福地",true),
	SyncArena((byte)3,true,"同步竞技场",true),
	CampWar((byte)4,true,"阵营战",true),
	UnionBattle((byte)5,true,"公会战",true),
	UnionTeamInstance((byte)6,true,"组队副本",true),
	SoloInstance((byte)7,false,"个人副本",true),
	TeamInstance((byte)8,false,"组队副本",true),
	UnionInstance((byte)9,true,"团队副本",true),	
	Richman((byte)10,true,"大富翁",true),
	AsyncArena((byte)11,true,"异步竞技场",true),

	HeroArena((byte)13,true,"英雄试炼",true),
	Alchemy((byte)14,true,"炼金",true),
	LuckBox((byte)15,true,"幸运宝箱",true),
	Poker((byte)16,true,"日常任务（扑克牌）",true),
	//此类活动，活动开启时间才容许进入，活动结束后将所有人传出
	ActiveMap((byte)17,false,"活动地图",true), 
	Compass((byte)18,true,"幸运转盘",true),
	Discount((byte)19,true,"折扣活动",true),
	Rank((byte)20,false,"排行活动",true),
	Siege((byte)21,false,"怪物攻城",true),
	Treasure((byte)22,false,"藏宝图",true),
	Arean3v3((byte)23,true,"3v3竞技场",true),
	Carnival((byte)24,true,"嘉年华",true),
	DarkDoorArean3v3((byte)25,true,"跨服3V3",true,ServerType.Dark),
	ArenaTop((byte)27,true,"大师赛",true),
	ShopSecret((byte)29,true,"神秘商店",true),
	Qualify((byte)30,true,"排位赛",true),
	Goblin((byte)31,true,"哥布林",true),
	Survival((byte)32,true,"生存战场",true),
	WorldBoss((byte)33,true,"世界BOSS",true),
	UnionIntegralBattle((byte)34,true,"公会积分战",true),
    Tower((byte)35,true,"爬塔",true),
	;
	
	private final byte type;//活动类型
	private final boolean onlyone;//活动是唯一的
	private final String name;//活动名称
	private final boolean usable;//是否支持此类活动
	private final ServerType serverType ;
	
	private ActiveType(byte type, boolean onlyone, String name, boolean usable,ServerType serverType) {
		this.type = type;
		this.onlyone = onlyone;
		this.name = name;
		this.usable = usable;
		this.serverType = serverType ;
	}
	
	private ActiveType(byte type, boolean onlyone, String name, boolean usable) {
		this(type, onlyone, name, usable, ServerType.Local) ;
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

	public ServerType getServerType() {
		return serverType;
	}
	
	
}
