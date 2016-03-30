package com.game.draco.app.rank.type;

import java.util.Arrays;

import com.game.draco.app.rank.domain.RankInfo;
import com.game.draco.app.rank.logic.*;

/**
 * 
 */
public enum RankType {
	ROLE_SCORE(1,"rankRoleScore",RankFilterType.CAMP,RankCycleType.Forever,RankActorType.ROLE),
	ROLE_LEVEL(2,"rankRoleLevel",RankFilterType.CAMP,RankCycleType.Forever,RankActorType.ROLE),
	ROLE_HERO(3,"rankRoleHero",RankFilterType.CAMP,RankCycleType.Forever,RankActorType.ROLE),
	ROLE_EQUIP(4,"rankRoleEquip",RankFilterType.CAMP,RankCycleType.Forever,RankActorType.ROLE),
	ROLE_HORSE(5,"rankRoleHorse",RankFilterType.CAMP,RankCycleType.Forever,RankActorType.ROLE),
	ROLE_PET(6,"rankRolePet",RankFilterType.CAMP,RankCycleType.Forever,RankActorType.ROLE),
	UNION_LEVEL(7,"rankUnionLevel",RankFilterType.CAMP,RankCycleType.Forever,RankActorType.UNION),
	
	//玩法排名
	ROLE_ASYNC_ARENA(8,"rankRoleAsyncArena",RankFilterType.NONE,RankCycleType.Forever,RankActorType.ROLE),
	ROLE_RICHMAN(9,"rankRoleRichman",RankFilterType.NONE,RankCycleType.Forever,RankActorType.ROLE),
	//鲜花排行榜
	Role_Flower_All(10,"rankRoleFlowerAll",RankFilterType.GENDER,RankCycleType.Forever,RankActorType.ROLE),
	Role_Flower_Day(11,"rankRoleFlowerDay",RankFilterType.GENDER,RankCycleType.Day,RankActorType.ROLE),
	Role_Donate(12,"rankRoleDonate",RankFilterType.NONE,RankCycleType.Forever,RankActorType.ROLE),
	Role_Arena_3v3(13,"rankRoleArena3v3",RankFilterType.NONE,RankCycleType.Forever,RankActorType.ROLE),
	Role_Taobao(14,"rankRoleTaobao",RankFilterType.NONE,RankCycleType.Day,RankActorType.ROLE),
	//公会积分战
	Union_Integral(15,"rankUnionLevel",RankFilterType.NONE, RankCycleType.Forever, RankActorType.UNION),
	//爬塔
	Tower(16, "rankRoleTower", RankFilterType.NONE, RankCycleType.Forever, RankActorType.ROLE),
	;
	
	
	private final int type;
	/**
	 * name中不要出现下划线(_)
	 */
	private final String name ;
	private final RankFilterType filter ;
	private final RankCycleType rankCycle ;
	private final RankActorType actorType ;
	
	RankType(int type,String name,RankFilterType filter,
			RankCycleType rankCycle,RankActorType actorType){
		this.type = type;
		this.name = name ;
		this.filter = filter ;
		this.rankCycle = rankCycle ;
		this.actorType = actorType ;
	}
	
	
	public RankCycleType getRankCycle() {
		return rankCycle;
	}


	public int getType(){
		return type;
	}
	
	
	public RankFilterType getFilter() {
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
			case ROLE_SCORE:
				return RankRoleScoreLogic.getInstance() ;
			case ROLE_LEVEL:
				return RankRoleLevelLogic.getInstance() ;
			case ROLE_HERO:
				return RanKRoleHeroLogic.getInstance();
			case ROLE_EQUIP:
				return RankRoleEquipLogic.getInstance();
			case ROLE_HORSE:
				return RankRoleHorseLogic.getInstance();
			case ROLE_PET://6
				return RankRolePetLogic.getInstance();
			case UNION_LEVEL:
				return RankUnionLevelLogic.getInstance();
			//case ROLE_ASYNC_ARENA:
			//	return RankRoleAsyncArenaLogic.getInstance();
			case ROLE_RICHMAN:
				return RankRoleRichManLogic.getInstance();
			case Role_Flower_All:
				return RankRoleFlowerAllLogic.getInstance();
			case Role_Flower_Day:
				return RankRoleFlowerDayLogic.getInstance();
			case Role_Donate :
				return RankRoleDonateLogic.getInstance() ;
			case Role_Arena_3v3 :
				return RankRoleArena3V3.getInstance() ;
			case Role_Taobao:
				return RankRoleTaobaoDayLogic.getInstance();
			case Tower:
				return RankRoleTowerLogic.getInstance();
			case Union_Integral:
				return RankUnionIntegralLogic.getInstance();
			default:
				return RankNoneLogic.getInstance() ;
		}
		
	}
	public static void main(String[] args){
		System.out.println(Arrays.toString(RankType.values()));
	}
	
}
