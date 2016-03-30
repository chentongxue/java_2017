package sacred.alliance.magic.app.rank;

import sacred.alliance.magic.app.rank.type.RankLogic;
import sacred.alliance.magic.app.rank.type.RankNullLogic;
import sacred.alliance.magic.app.rank.type.RoleArenaTop;
import sacred.alliance.magic.app.rank.type.RoleAsyncArenaRanking;
import sacred.alliance.magic.app.rank.type.RoleConsume;
import sacred.alliance.magic.app.rank.type.RoleEquipScore;
import sacred.alliance.magic.app.rank.type.RoleFlowerAll;
import sacred.alliance.magic.app.rank.type.RoleFlowerDay;
import sacred.alliance.magic.app.rank.type.RoleGameMoney;
import sacred.alliance.magic.app.rank.type.RoleHonor;
import sacred.alliance.magic.app.rank.type.RoleKillCountDay;
import sacred.alliance.magic.app.rank.type.RoleLevel;
import sacred.alliance.magic.app.rank.type.RoleMount;
import sacred.alliance.magic.app.rank.type.RoleRecharge;
import sacred.alliance.magic.app.rank.type.RoleScore;
import sacred.alliance.magic.app.rank.type.RoleTaobaoDay;
import sacred.alliance.magic.app.rank.type.RoleTreasureDay;
import sacred.alliance.magic.app.rank.type.RoleWingScore;

public enum RankType {
	//战斗力
	Role_Score(1,"RoleScore",RankFilter.career,RankCycle.Forever,RankActorType.Role),
	//等级
	Role_Level(2,"RoleLevel",RankFilter.career,RankCycle.Forever,RankActorType.Role),
	//财富
	Role_Game_Money(3,"RoleGameMoney",RankFilter.career,RankCycle.Forever,RankActorType.Role),
	//坐骑战斗力
	Role_Mount(5,"MountScore",RankFilter.none,RankCycle.Forever,RankActorType.Role),
	//装备评分
	Role_Equip_Score(6,"EquipScore",RankFilter.camp,RankCycle.Forever,RankActorType.Role),
	//封神榜
	//Arena_1v1(7,"Arena1v1",RankFilter.career,RankCycle.Day,RankActorType.Role),
	//角色荣誉
	Role_Honor(8,"RoleHonor",RankFilter.career,RankCycle.Forever,RankActorType.Role),
	//帮会等级
	Union_Level(9,"UnionLevel",RankFilter.none,RankCycle.Forever,RankActorType.Union),
//	//帮会财富
//	Faction_Money(10,"FactionMoney",RankFilter.none,RankCycle.Forever,RankActorType.Union),
//	//帮会神兽
//	Faction_Soul(11,"FactionSoul",RankFilter.none,RankCycle.Forever,RankActorType.Faction),
	//帮会战
	Faction_Battle(12,"FactionBattle",RankFilter.none,RankCycle.Forever,RankActorType.Union),
	
	//副本
	//活动
	Role_Flower_Day(15,"FlowerDay",RankFilter.sex,RankCycle.Day,RankActorType.Role),
	Role_Flower_All(16,"FlowerAll",RankFilter.sex,RankCycle.Forever,RankActorType.Role),
	//淘宝
	Role_Taobao_Day(17,"TaobaoDay",RankFilter.taobao_type,RankCycle.Day,RankActorType.Role),
	//藏宝图
	Role_Treasure_Day(19,"TreasureDay",RankFilter.quality,RankCycle.Day,RankActorType.Role),
	
	//充值
	Role_Recharge(20,"RoleRecharge",RankFilter.none,RankCycle.Forever,RankActorType.Role),
	//消耗
	Role_Consume(21,"RoleConsume",RankFilter.none,RankCycle.Forever,RankActorType.Role),
	
	//大师赛
	Role_ArenaTop(22,"RoleArenaTop",RankFilter.career,RankCycle.Day,RankActorType.Role),
	//法宝
	Role_MagicWeapon(23,"MagicWeaponScore",RankFilter.none,RankCycle.Forever,RankActorType.Role),
	//翅膀评分
	Role_Wing_Score(24,"wingScore",RankFilter.camp,RankCycle.Forever,RankActorType.Role),
	//当天杀人数
	Role_KillCount_Day(25,"KillCountDay",RankFilter.camp,RankCycle.Day,RankActorType.Role),
	//异步竞技场
	Role_Async_Arena_Ranking(26,"RoleAsyncArenaRanking",RankFilter.none,RankCycle.WEEK,RankActorType.Role),
	;
	
	
	private final int type;
	/**
	 * name中不要出现下划线(_)
	 */
	private final String name ;
	private final RankFilter filter ;
	private final RankCycle rankCycle ;
	private final RankActorType actorType ;
	
	RankType(int type,String name,RankFilter filter,
			RankCycle rankCycle,RankActorType actorType){
		this.type = type;
		this.name = name ;
		this.filter = filter ;
		this.rankCycle = rankCycle ;
		this.actorType = actorType ;
	}
	
	
	public RankCycle getRankCycle() {
		return rankCycle;
	}


	public int getType(){
		return type;
	}
	
	
	public RankFilter getFilter() {
		return filter;
	}

	public RankActorType getActorType() {
		return actorType;
	}

	public static RankType get(int type){
		for(RankType v : values()){
			if(type == v.getType()){
				return v;
			}
		}
		return null;
	}
	
	public String getName() {
		return name;
	}
	

	public RankLogic createRankLogic(RankInfo rankInfo){
		switch(rankInfo.getRankType()){
			case Role_Score:
				return RoleScore.getInstance() ;
			case Role_Level:
				return RoleLevel.getInstance() ;
			case Role_Game_Money :
				return RoleGameMoney.getInstance() ;
			case Role_Honor :
				return RoleHonor.getInstance() ;
			case Role_Equip_Score :
				return RoleEquipScore.getInstance() ;
			case Role_Recharge :
				return RoleRecharge.getInstance() ;
			case Role_Consume :
				return RoleConsume.getInstance() ;
//			case Faction_Level :
//				return FactionLevel.getInstance() ;
//			case Faction_Money :
//				return FactionMoney.getInstance() ;
			case Role_Mount :
				return RoleMount.getInstance() ;
			case Role_Flower_All :
				return RoleFlowerAll.getInstance() ;
			case Role_Flower_Day :
				return RoleFlowerDay.getInstance() ;
			case Role_Treasure_Day :
				return RoleTreasureDay.getInstance() ;
			case Role_Taobao_Day :
				return RoleTaobaoDay.getInstance() ;
			case Role_ArenaTop :
				return RoleArenaTop.getInstance();
			case Faction_Battle :
				return RankNullLogic.getInstance() ;
			case Role_Wing_Score:
				return RoleWingScore.getInstance();
			case Role_KillCount_Day:
				return RoleKillCountDay.getInstance();
			case Role_Async_Arena_Ranking:
				return RoleAsyncArenaRanking.getInstance();
			default:
				return RankNullLogic.getInstance() ;
		}
		
	}
	
}
