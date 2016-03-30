package sacred.alliance.magic.app.active.dps;

import java.util.List;

import sacred.alliance.magic.app.active.ActiveSupport;
import sacred.alliance.magic.app.active.vo.Active;
import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.vo.Point;
import sacred.alliance.magic.vo.RoleInstance;

public interface ActiveDpsApp extends ActiveSupport,Service {
	
	/**
	 * 根据地图ID获取活动对象
	 * @param mapId
	 * @return
	 */
	public Active getActiveByMapId(String mapId);
	
	/**
	 * 获取地图配置信息
	 * @param mapId
	 * @return
	 */
	public DpsMapConfig getDpsMapConfig(String mapId);
	
	/**
	 * 伤害突破点列表
	 * @return
	 */
	public List<DpsHurtPoint> getHurtPointList();
	
	/**
	 * 活动奖励
	 * @param activeId 活动ID
	 * @param role 角色
	 * @param index 排名
	 * @param hurt 伤害
	 */
	public void sendReward(short activeId, RoleInstance role, int index, long hurt,int npcLevel);
	
	public Point getEnterMapPoint(short activeId);
	
}
