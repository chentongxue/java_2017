package sacred.alliance.magic.app.user;

import sacred.alliance.magic.app.map.point.CollectablePoint;
import sacred.alliance.magic.base.ChangeMapResult;
import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.core.exception.ServiceException;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.MapInstance;
import sacred.alliance.magic.vo.Point;
import sacred.alliance.magic.vo.RoleInstance;

public interface UserMapApp extends Service{

	public ChangeMapResult changeMap(AbstractRole role, Point targetPoint) throws ServiceException;

	public void enter(AbstractRole role) throws ServiceException;
	
	public void exitMap(AbstractRole role);
	
	public void exitMap(AbstractRole role, MapInstance mapInstance) ;
	
	/**获得角色的副本记录*/
	//public void loadCopyProgress(AbstractRole role) ;
	
	/**获得系统中高级副本进度记录*/
	//public void loadStoreCopyProgress() ;
	
	public void roleOnEntranceWhenLogon(RoleInstance role,boolean isDieNow);
	
	//public void storeCopyProgress(AbstractRole role);
	
	//public void addCopyProgress(CopyProgress cp);
	
	/**判断单位时间内用户是否进入次数过多(副本情况)*/
//	public boolean tooManyTimes(AbstractRole role,String mapId);
	
	/**
	 * 到目标地图的防卡死点
	 * @param role
	 * @param targetMapId
	 * @return
	 * @throws ServiceException
	 */
//	public int toMapOrigin(AbstractRole role, String targetMapId) throws ServiceException;
	
	/**
	 * 重置用户所有副本
	 * @param role
	 */
	//public void resetUserCopy(AbstractRole role);
	
	/**系统重置副本*/
	//public void sysResetCopy() ;
	
	//public void sysResetAllCopy();
	
	/**
	 * 重置用户某一副本
	 * @param role
	 * @param mapId
	 */
	//public void resetUserCopy(AbstractRole role,String mapId);

	/**
	 * 将role所在地图上的采集点信息push给role
	 * @param role
	 */
	public void pushCollectPointMessage(RoleInstance role);
	
	/**
	 * 
	 * @param role
	 * @param newPointInstance
	 * @param point
	 */
	public void notifyNewCollectPoint(RoleInstance role,CollectablePoint<RoleInstance> newPointInstance,Point point);
	
	/**
	 * 将用户的宝箱在进入地图的时候push给用户
	 * @param role
	 */
	public void pushBoxMessage(RoleInstance role);
	
	/**
	 * npc死亡时候保存用户的进度
	 * @param npcInstance
	 */
	//public void storeUserCopyProcess(NpcInstance npcInstance);
	
	/**增加角色进入副本次数*/
	//public void incrUserCopyCount(RoleInstance role);
	
	/**获得角色当前进入副本的次数*/
	//public int getUserCopyCount(RoleInstance role);
	
	/**当前是否正在刷新高级副本*/
	//public boolean isSysResetCopyNow();
	
	//public CopyProgress getStoreCopyProgress(String progressId);
	
	/**获得下次重置副本时间*/
	//public Date nextResetTime(String gateId);
	
	/**将角色放置于副本入口*/
//	public void roleOnCopyEntrance(AbstractRole role,Point copyEntryPoint,String tips);
	
	/** 
	 *  传送/自动寻路到目标地图的目标点
	 *  type = 0 使用传送卷轴
	 *  type = 1 自动寻路
	 * @throws Exception 
	 */
	//public void gotoTargetMapPoint(RoleInstance role, int type, Point point) throws Exception;
	
	/** 副本入口地图信息 **/
	//public java.util.Map<String, ConveyCopyMap> getConveyCopyMap();
}
