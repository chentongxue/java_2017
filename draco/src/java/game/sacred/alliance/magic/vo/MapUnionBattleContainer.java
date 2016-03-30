package sacred.alliance.magic.vo;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.map.Map;
import sacred.alliance.magic.core.Message;

import com.game.draco.GameContext;

/**
 * 公会战地图容器
 */
public class MapUnionBattleContainer extends MapDefaultContainer {
	private final static Logger logger = LoggerFactory.getLogger(MapUnionBattleContainer.class);
	private Lock creatLock = new ReentrantLock();//防止创建同样的MapInstance
	private Lock bossKillLock = new ReentrantLock();
	
	private final static MapUnionBattleContainer instance = new MapUnionBattleContainer();
	
	//boss 被杀死BOSS的公会战ID
	private Set<Integer> battleBossKillset = new HashSet<Integer>();
	private MapUnionBattleContainer() {
		super();
	}
	public static MapUnionBattleContainer getMapContainer(){
		return instance;
	}
	
	public boolean isBossKilled(int unionBattleId){
		return battleBossKillset.contains(unionBattleId);
	}
	
	public boolean removeBossKill(int unionBattleId){
		this.bossKillLock.lock();
		try {
			return battleBossKillset.remove(unionBattleId);
		} finally {
			bossKillLock.unlock();
		}
	}
	
	public void addBossKill(int unionBattleId){
		this.bossKillLock.lock();
		try {
			battleBossKillset.add(unionBattleId);
		} finally {
			bossKillLock.unlock();
		}
	}
	
	@Override
	public boolean canDestroy() {
		return false;
	}

	@Override
	public void destroy() {
		
	}
	/**
	 * 返回公会战地图实例，如果不存在则创建新实例
	 */
	@Override
	public MapInstance createMapInstance(Map map, AbstractRole role) {
		// 获得公会战ID
		Integer unionBattleId = GameContext.getUnionBattleApp()
				.getUnionBattleIdByMapId(map.getMapId());
		if (unionBattleId == null) {
			logger.error("union battle not exist! battleId = " + unionBattleId);
			return null;
		}
		// 判断是否地图已创建
		MapInstance instance = subMapList.get(map.getMapId());
		if (instance != null) {
			return instance;
		}
		this.creatLock.lock();
		try {
			instance = subMapList.get(map.getMapId());
			if (instance != null) {
				return instance;
			}
			instance = new MapUnionBattleInstance(map, unionBattleId);
			addMapInstance(instance);// 加到容器中
			GameContext.getMapApp().addMapInstance(instance);
			return instance;
		} finally {
			creatLock.unlock();
		}
	}
	/*
	 * 通知所有在公会战的角色消息
	 */
	public void sendAllUnionBattleRoleMessage(Message msg){
		for(MapInstance map : this.subMapList.values()){
			for(RoleInstance r : map.getRoleList()){
				r.getBehavior().sendMessage(msg);
			}
		}
	}
}
