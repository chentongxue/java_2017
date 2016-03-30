package sacred.alliance.magic.app.map.logic;

import com.game.draco.GameContext;

import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.Point;

public abstract class MapLogic {

	public abstract void enter(AbstractRole role);

	public abstract void exit(AbstractRole role);

	/** 最大人数 */
	public abstract int maxPlayer();

	/** 是否保存进度 */
	public abstract boolean isStore();

	/** 进入是否添加用户进入副本次数 */
	//public abstract boolean isIncrUserCopyTimes();

	/** 死亡复活点 */
	public abstract Point rebornPoint(AbstractRole role);

	/** 在副本组中的序列(只有保存进度副本才需要设置值[0-n]) */
	//public abstract int indexOfCopyCluster();

	public boolean canEnter(AbstractRole role) {
		sacred.alliance.magic.app.map.Map targetMap = GameContext
				.getMapApp().getMap(this.mapId);
		if (null == targetMap) {
			return false;
		}
		/* if(targetMap.getMapConfig().iscopymode() 
				&& targetMap.getMapLogic().isStore()
				&& GameContext.getUserMapApplication().isSysResetCopyNow()) {
			// 系统正在刷新高级副本ing
			return false;
		}*/
		// 判断副本情况下是否能进入,避免客户端通知客户端切换地图缺无法进入情况
		// 在这里判断不准确
		/*
		 * if(targetMap.getMapConfig().isIscopymode()){ //副本,判断次数限制等
		 * if(GameContext
		 * .getUserMapApplication().tooManyTimes(role,targetMap.getMapId())){
		 * return false ; } //TODO:根据用户身上进度判断是否能进入当前副本 }
		 */
		return true;
	}

	public boolean canExit(AbstractRole role) {
		return true;
	}


	public abstract void kill(AbstractRole killer, AbstractRole victim);

	public abstract void update();

	public String mapId;

	protected GameContext context = GameContext.getGameContext();

	public MapLogic(String mapId) {
		this.mapId = mapId;
	}

	public String getMapId() {
		return mapId;
	}

}
