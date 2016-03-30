package sacred.alliance.magic.app.menu;

public enum MenuIdType {
	
	/**
	 *  2-挂机
		3-排行
		4-商城
		5-领奖
		6-运营活动
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
		21-Taobao
		22-幸运宝箱
		23-炼金
		24-副本
		25-日常任务
		26-全民大富翁
		27-爬塔
		28-超神比赛
		29-异步竞技场
		30-连续登录
		31-英雄活动
		32-首充
		33-哥布林
	 */
	
	//HangUp(2,"挂机"),
	Rank(3,"排行"),
	Shop(4,"商城"),
	SignReward(5,"领奖"),
	Operate_Active(6,"精彩活动"),
	Active(7,"活动"),
	Active_Dps(10,"肉山"),
	Active_AngelChest(13,"赏金宝箱"),
	Active_CampWar(14,"每日活动：阵营战"),
	Active_Siege(16,"每日活动：攻城战"),
	Active_FactionSuper(18,"每日活动：最强公会"),//TODO:
	Taobao(21,"淘宝"),
	LuckBox(22,"幸运宝箱"),
	Alchemy(23,"炼金"),
	Copy(24,"副本"),
	QuestPoker(25,"日常任务"),
	RichMan(26,"大富翁"),
	Hero_Arena(27,"英雄试炼"),
	Arena_1v1(28,"巅峰对决"),
	AsyncArena(29,"异步竞技场"),
	AccumulateLogin(30,"连续登录"),
	ChoiceCard(31,"英雄抽卡"),
	FirstPay(32,"首充"),
	Goblin(33,"哥布林"),
	UnionBattle(34,"公会战"),
	SurvivalBattle(35,"生存战场"),
	arena_3v3(36,"3v3"),
	arena_3v3_dark_door(37,"跨服3v3"),
	UnionIntegralBattle(38,"公会积分战"),
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
