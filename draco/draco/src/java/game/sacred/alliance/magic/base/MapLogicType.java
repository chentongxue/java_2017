package sacred.alliance.magic.base;

import sacred.alliance.magic.app.map.Map;
import sacred.alliance.magic.app.map.data.MapConfig;
//import sacred.alliance.magic.app.map.logic.FactionMapLogic;
import sacred.alliance.magic.app.map.logic.MapLogic;
import sacred.alliance.magic.app.map.logic.MapLogicAdaptor;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.MapContainer;
import sacred.alliance.magic.vo.MapInstance;
import sacred.alliance.magic.vo.MapMultiCopyContainer;
import sacred.alliance.magic.vo.map.MapArenaContainer;
import sacred.alliance.magic.vo.map.MapUnionContainer;
import sacred.alliance.magic.vo.map.MapUnionTeamContainer;

import com.game.draco.GameContext;
import com.game.draco.app.camp.war.map.MapCampWarContainer;

public enum MapLogicType {
	
	defaultLogic(0,"普通地图",false,true),
	copyLogic(1,"副本地图",true,true),
	arenaLogic(2,"擂台赛地图",true,false),
	unionLogic(3,"公会副本",false,true),
	factionWarLogic(4,"公会战地图",false,true),
	unionTeamLogic(5,"公会小队副本",false,true),
	battlefield(6,"战场地图",false,true),
	maincity(7,"主城",false,true),
	arenaPK(8,"切磋地图",true,false),
	siege(10,"怪物攻城地图",false,true),
	unionInstanceLogic(12,"公会副本地图",true,true),
	dps(13,"BOSSDPS",false,true),
	campWar(14,"阵营战",true,true),
	activeMap(15,"活动地图",false,true),
	angelChest(16,"神仙宝箱",false,true),
	goddess(17,"女神天梯",true,false),
	arenaTop(18,"大师赛",false,true),
	factionWar(20,"门派战",true,false),
	campWarWin(21,"阵营战皇城",false,true),
	copyLine(23,"章节副本",true,true),
	asyncArena(24,"异步竞技场",true,true),
	richman(25,"大富翁",true,false),
	heroArena(26,"英雄试练",true,false),
	;
	
	private final int type;
	private final String name;
	private final boolean copyType;
	//是否允许卡死复位
	private final boolean stuckReset ;
	
	MapLogicType(int type,String name,boolean copyType,
			boolean stuckReset){
		this.type = type;
		this.name = name;
		this.copyType = copyType;
		this.stuckReset = stuckReset ;
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
			case copyLogic:
				container = MapMultiCopyContainer.getMapContainer(role, mapId);
				break ;
			case arenaLogic:
				container = MapArenaContainer.getMapContainer(role, mapId);
				break ;
			case arenaPK:
				container = MapArenaContainer.getMapContainer(role, mapId);
				break;
			case unionLogic:
				container = MapUnionContainer.getMapContainer(role, mapId);
				break;
			case unionTeamLogic:
				container = MapUnionTeamContainer.getMapContainer(role, mapId);
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
//			case factionWar:
//				container = MapFactionWarContainer.getMapContainer(role, mapId);
//				break;
			case goddess:
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
		}
		
		if(null == container){
			container = GameContext.getMapApp().getDefaultMapContainer();
		}
		return container.createMapInstance(map, role);
	}
	
	public static MapLogic createMapLogic(String mapId,MapConfig mapConfig){
//		if (mapConfig.getLogictype() == MapLogicType.unionLogic.getType()) {
//			FactionMapLogic factionLogic = new FactionMapLogic(mapId);
//			return factionLogic;
//		}
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
