package sacred.alliance.magic.base;

import sacred.alliance.magic.app.map.Map;
import sacred.alliance.magic.app.map.data.MapConfig;
import sacred.alliance.magic.app.map.logic.MapLogic;
import sacred.alliance.magic.app.map.logic.MapLogicAdaptor;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.MapContainer;
import sacred.alliance.magic.vo.MapInstance;
import sacred.alliance.magic.vo.MapMultiCopyContainer;
import sacred.alliance.magic.vo.MapUnionBattleContainer;
import sacred.alliance.magic.vo.map.MapArenaContainer;
import sacred.alliance.magic.vo.map.MapSurvivalBattleContainer;
import sacred.alliance.magic.vo.map.MapUnionContainer;
import sacred.alliance.magic.vo.map.MapUnionIntegralBattleContainer;
import sacred.alliance.magic.vo.map.MapUnionTerritoryContainer;

import com.game.draco.GameContext;
import com.game.draco.app.camp.war.map.MapCampWarContainer;

public enum MapLogicType {
	
	defaultLogic(0,"普通地图",false,true,false),
	copyLogic(1,"副本地图",true,true,false),
	arenaLogic(2,"擂台赛地图",true,false,false),
	unionTerritoryLogic(3,"公会领地地图",false,true,true),
	factionWarLogic(4,"公会战地图",false,true,true),
    tower(5,"爬塔",true,true,false),
	battlefield(6,"战场地图",false,true,false),
	maincity(7,"主城",false,true,false),
	arenaPK(8,"切磋地图",true,false,false),
	siege(10,"怪物攻城地图",false,true,false),
	unionInstanceLogic(12,"公会副本地图",true,true,true),
	dps(13,"BOSSDPS",false,true,false),
	campWar(14,"阵营战",true,true,false),
	activeMap(15,"活动地图",false,true,false),
	angelChest(16,"神仙宝箱",false,true,false),
	pet(17,"宠物天梯",true,false,false),
	arenaTop(18,"大师赛",false,true,false),
	unionBattle(20,"公会战",true,false,true),
	campWarWin(21,"阵营战皇城",false,true,false),
	copyLine(23,"章节副本",true,true,false),
	asyncArena(24,"旧异步竞技场(一战到底)",true,true,false),
	richman(25,"大富翁",true,false,false),
	heroArena(26,"英雄试练",true,false,false),
	roleBornGuide(27,"出生向导",true,false,false),
	qualify(28,"异步竞技场(排位赛)",true,true,false),
	goblin(29,"哥布林密境",true,true,true),
	survival(30,"生存战场",false,true,false),
	arena3V3(31,"3v3",true,false,false),
	integral(32,"公会积分战",false,true,true),
	;
	
	private final int type;
	private final String name;
	private final boolean copyType;
	//是否允许卡死复位
	private final boolean stuckReset ;
	/**
	 * 是否必须要有公会
	 */
	private final boolean mustUnion ;
	
	
	
	public boolean isMustUnion() {
		return mustUnion;
	}


	MapLogicType(int type,String name,boolean copyType,
			boolean stuckReset,boolean mustUnion){
		this.type = type;
		this.name = name;
		this.copyType = copyType;
		this.stuckReset = stuckReset ;
		this.mustUnion = mustUnion ;
	}
	
	
	public static MapInstance createMapInstance(AbstractRole role,String mapId){
		if(null == mapId){
			return null ;
		}
		Map map = GameContext.getMapApp().getMap(mapId);
		MapConfig mapConfig = map.getMapConfig();
		
		MapContainer container= null ;
		//分线地图
		if(mapConfig.isHadLineMap()){
			container = GameContext.getMapApp().getMapLineContainer(mapId); 
			return container.createMapInstance(map, role);
		}
		
		MapLogicType logicType = MapLogicType.getMapLogicType(map.getMapConfig().getLogictype());
		
		switch(logicType){
			case defaultLogic:
				container = GameContext.getMapApp().getDefaultMapContainer();
				break ;
			case roleBornGuide :
				container = GameContext.getMapApp().getMapRoleBornGuideContainer() ;
				break ;
			case copyLogic:
				container = MapMultiCopyContainer.getMapContainer(role, mapId);
				break ;
			case arenaLogic:
				container = MapArenaContainer.getMapContainer(role, mapId);
				break ;
			case arenaPK:
				container = MapArenaContainer.getMapContainer(role, mapId);
				break;
			case arena3V3:
				container = MapArenaContainer.getMapContainer(role, mapId);
				break;
			case unionInstanceLogic:
				container = MapUnionContainer.getMapContainer(role, mapId);
				break;
//			case factionLogic:
//				container = MapFactionContainer.getMapContainer(role, mapId);
//				break ;
//			case factionWarLogic:
//				container = FactionWarCopyMapContainer.getMapContainer(role, mapId);
//				break ;
			case battlefield:
				break;
			case maincity:
				break ;
//			case factionCopyLogic:
//				container = MapMultiCopyContainer.getMapContainer(role, mapId);
//				break;
			//公会战
			case unionBattle:
				container = MapUnionBattleContainer.getMapContainer();
				break;
			case pet:
				container = GameContext.getAsyncPvpApp().getMapAsyncPvpContainer();
				break;
			case asyncArena:
				container = GameContext.getAsyncPvpApp().getMapAsyncPvpContainer();
				break;
			case heroArena:
				container = GameContext.getAsyncPvpApp().getMapAsyncPvpContainer();
				break;
			case campWar :
				container = MapCampWarContainer.getMapContainer(role, mapId);
				break ;
			case qualify:
				container = GameContext.getAsyncPvpApp().getMapAsyncPvpContainer();
				break;
			case goblin:
				container = GameContext.getGoblinApp().getMapGoblinContainer();
				break;
			case survival:
				container = MapSurvivalBattleContainer.getMapContainer(role);
				break;
			case unionTerritoryLogic:
				container = MapUnionTerritoryContainer.getMapContainer(role);
				break;
			case integral:
				container = MapUnionIntegralBattleContainer.getMapContainer(role);
				break;
            case tower:
                container = GameContext.getTowerApp().getMapTowerContainer() ;
		
		}
		
		if(null == container){
			container = GameContext.getMapApp().getDefaultMapContainer();
		}
		return container.createMapInstance(map, role);
	}
	
	public static MapLogic createMapLogic(String mapId,MapConfig mapConfig){
		// 如果是普通地图返回defaultLogic
		MapLogicAdaptor defaultLogic = new MapLogicAdaptor(mapId);
		return defaultLogic;
	
	}

	/**根据类型得到是否为副本类型*/
	public static boolean isCopyType(byte type){
		MapLogicType mlt = getMapLogicType(type);
		if(null == mlt){
			return false ;
		}
		return mlt.isCopyType();
	}

	public static MapLogicType getMapLogicType(byte type){
		for(MapLogicType mlt : values()){
			if(mlt.getType() == type){
				return mlt;
			}
		}
		return null;
	}
	
	public int getType() {
		return type;
	}
	
	public String getName() {
		return name;
	}

	public boolean isCopyType() {
		return copyType;
	}

	public boolean isStuckReset() {
		return stuckReset;
	}

	
}
