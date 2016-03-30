package sacred.alliance.magic.app.menu;

public enum MenuIdType {
	
	/**
	 *  2-挂机
		3-排行
		4-商城
		5-领奖
		6-运营活动
		7-日常
		8-开服嘉年华
		9-传说装备
		10-每日活动：击杀黑龙公主
		11-每日活动：击杀沃金
		12-每日活动：竞技之王
		13-每日活动：古巴拉什宝箱
		14-每日活动：阵营战
		15-每周活动：公会战
		16-每周活动：攻城战
		17-每月活动：最强王者
		18-每月活动：最强公会
		20-限时活动
		21-幸运转盘
		22-幸运宝箱
		23-炼金
		24-副本
		25-日常任务
		26-全民大富翁
		27-爬塔
		28-超神比赛
	 */
	
	HangUp(2,"挂机"),
	Rank(3,"排行"),
	Shop(4,"商城"),
	SignReward(5,"领奖"),
	Active_Discount(6,"运营活动"),
	Active(7, "日常活动"),//TODO:好像没什么用
	StorySuit(9,"传说装备"),
	Active_Dps(10,"击杀黑龙公主"),
	Active_KillWJ(11,"每日活动：击杀沃金"),//TODO:
	Active_Athletics(12,"每日活动：竞技之王"),//TODO:
	Active_Box(13,"每日活动：古巴拉什宝箱"),//TODO:
	Active_CampWar(14,"每日活动：阵营战"),
	Active_FactionWar(15,"每日活动：公会战"),
	Active_Siege(16,"每日活动：攻城战"),
	Active_SuperKing(17,"每日活动：最强王者"),//TODO:
	Active_FactionSuper(18,"每日活动：最强公会"),//TODO:
	Active_TimeLimit(20,"限时活动"),//TODO:
	LuckDial(21,"幸运转盘"),
	LuckBox(22,"幸运宝箱"),
	Alchemy(23,"炼金"),
	Copy(24,"副本"),
	QuestPoker(25,"日常任务"),
	RichMan(26,"大富翁"),
	Hero_Arena(27,"爬塔"),//TODO:
	Arena_1v1(28,"超神比赛"),
	AsyncArena(29,"异步竞技场"),
	;
	
	
	private final int type;
	private final String name;

	private MenuIdType(int type, String name) {
		this.type = type;
		this.name = name;
	}

	public int getType() {
		return type;
	}

	public String getName() {
		return name;
	}
	
	public static MenuIdType get(int type){
		for(MenuIdType mt : values()){
			if(mt.getType() == type){
				return mt ;
			}
		}
		return null ;
	}

}
