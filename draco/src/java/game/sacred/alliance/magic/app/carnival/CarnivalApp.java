package sacred.alliance.magic.app.carnival;

import java.util.List;
import java.util.Map;

import com.game.draco.app.AppSupport;
import com.game.draco.message.response.C1187_CarnivalDetailRespMessage;
import com.game.draco.message.response.C1186_CarnivalRespMessage;

import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.domain.CarnivalDbInfo;
import sacred.alliance.magic.domain.CarnivalRankInfo;
import sacred.alliance.magic.vo.RoleInstance;

public interface CarnivalApp extends Service, AppSupport{
	/**
	 * 定时任务，执行排行
	 */
	public void doRank();
	
	/**
	 * 角色数值统计
	 */
	public void roleDataCount(RoleInstance role, int value , int subValue, CarnivalType type);
	
	/**
	 * 获取需要记录的玩家数据
	 * @return
	 */
	public Map<String, Map<Integer, CarnivalDbInfo>> getAllRoleData();
	
	/**
	 * 根据ID获取活动的玩家数据
	 * @param itemId
	 * @return
	 */
	public List<CarnivalDbInfo> getActiveDataBySize(int itemId, int start, int end);
	
	/**
	 * 根据ID获取活动的玩家数据
	 * @param itemId
	 * @return
	 */
	public List<CarnivalDbInfo> getActiveData(int itemId, int roleValue);
	
	/**
	 * 获取数据库坐骑排名前三
	 * @param start
	 * @param end
	 * @return
	 */
	public List<CarnivalRankInfo> getRoleMonutSort(int start, int end);
	
	/**
	 * 根据阵营和ID获取活动的玩家数据
	 * @param col
	 * @param itemId
	 * @return
	 */
	public List<CarnivalDbInfo> getCampActiveDataByColumn(int itemId);
	
	/**
	 * 根据职业和ID获取活动的玩家数据
	 * @param col
	 * @param itemId
	 * @return
	 */
	public List<CarnivalDbInfo> getCareerActiveDataByColumn(int itemId);
	
	/**
	 * 获取奖励
	 * @param activeId
	 * @param rank
	 * @param career
	 * @return
	 */
	public CarnivalReward getCarnivalReward(int activeId, int rank, byte career);
	
	/**
	 * 获取活动
	 * @param activeId
	 * @return
	 */
	public C1186_CarnivalRespMessage getActiveCarnival(short activeId);
	
	/**
	 * 获取活动
	 * @param activeId
	 * @return
	 */
	public C1187_CarnivalDetailRespMessage getActiveCarnivalDetail(int itemId, byte career);
	
	/**
	 * 下线失败日志
	 * @param role
	 */
	public void offlineLog(RoleInstance role);
	
	/**
	 * 热加载
	 * @return
	 */
	public Result reload();
	
	/**
	 * 获取时间没过期并且符合服务器ID的活动
	 * @return
	 */
	public CarnivalActive getCarnivalActive();
}
