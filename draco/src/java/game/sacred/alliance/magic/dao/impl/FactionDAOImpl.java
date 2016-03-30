//package sacred.alliance.magic.dao.impl;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import sacred.alliance.magic.domain.Faction;
//import sacred.alliance.magic.domain.FactionIntegralLog;
//import sacred.alliance.magic.domain.FactionRecord;
//import sacred.alliance.magic.domain.UnionMember;
//
//public class FactionDAOImpl extends BaseDAOImpl{
//	
//	/**
//	 * 查询门派积分日志
//	 * @param factionId 门派ID
//	 * @param start 开始记录行
//	 * @param end 记录条数
//	 * @return
//	 */
//	public List<FactionIntegralLog> getIntegralLogList(String factionId, int start, int end) {
//		Map map = new HashMap();
//		map.put("factionId", factionId);
//		map.put("start", start);
//		map.put("end", end);
//		return this.getSqlMapClientTemplate().queryForList("FactionIntegralLog.getAllIntegralLogList", map);
//	}
//	
//	/**
//	 * 查询门派积分日志（消耗/收入）
//	 * @param factionId 门派ID
//	 * @param start 开始记录行
//	 * @param end 记录条数
//	 * @param operateType 消耗/收入
//	 * @return
//	 */
//	public List<FactionIntegralLog> getIntegralLogList(String factionId, int start, int end, byte operateType) {
//		Map map = new HashMap();
//		map.put("factionId", factionId);
//		map.put("operateType", operateType);
//		map.put("start", start);
//		map.put("end", end);
//		return this.getSqlMapClientTemplate().queryForList("FactionIntegralLog.getIntegralLogList", map);
//	}
//	
//	/**
//	 * 查询门派总贡献（所有帮众的贡献之和）
//	 * @param factionId
//	 * @return
//	 */
//	public int getContributionSum(String factionId){
//		Map map = new HashMap();
//		map.put("factionId", factionId);
//		Object object = this.getSqlMapClientTemplate().queryForObject("FactionRole.getContributionSum", map);
//		if(null == object) {
//			return 0;
//		}
//		return (Integer)object;
//	}
//	
//	/**
//	 * 删除一周前的积分日志
//	 * @return
//	 */
//	public int deleteIntegralLogBeforeOneWeek(){
//		return this.getSqlMapClientTemplate().delete("FactionIntegralLog.deleteBeforeOneWeek");
//	}
//	
//	/**
//	 * 修改门派成员的角色名称
//	 * @param factionId
//	 * @param roleId
//	 * @param roleName
//	 * @return
//	 */
//	public int modifyFactionRoleName(String factionId, String roleId, String roleName){
//		Map map = new HashMap();
//		map.put("factionId", factionId);
//		map.put("roleId", roleId);
//		map.put("roleName", roleName);
//		return this.getSqlMapClientTemplate().update("FactionRole.modifyFactionRoleName", map);
//	}
//	
//	/**
//	 * 修改帮主的名称
//	 * @param factionId
//	 * @param roleId
//	 * @param roleName
//	 * @return
//	 */
//	public int modifyFactionLeaderName(String factionId, String roleId, String roleName){
//		Map map = new HashMap();
//		map.put("factionId", factionId);
//		map.put("roleId", roleId);
//		map.put("roleName", roleName);
//		return this.getSqlMapClientTemplate().update("Faction.modifyLeaderName", map);
//	}
//	
//	/**
//	 * 查找门派
//	 * @param parameter 模糊的门派名称
//	 * @return
//	 */
//	public List<Faction> getFactionByName(String parameter){
//		Map map = new HashMap();
//		map.put("parameter", parameter);
//		return this.getSqlMapClientTemplate().queryForList("Faction.searchFactionByName", map);
//	}
//	
//	/**
//	 * 查询门派记录
//	 * @param factionId 门派ID
//	 * @param start 开始记录行
//	 * @param end 记录条数
//	 * @return
//	 */
//	public List<FactionRecord> getFactionRecordList(String factionId, int start, int end) {
//		Map map = new HashMap();
//		map.put("factionId", factionId);
//		map.put("start", start);
//		map.put("end", end);
//		return this.getSqlMapClientTemplate().queryForList("FactionRecord.getRecordList", map);
//	}
//	
//	/**
//	 * 删除一个月之前的门派记录
//	 * @return
//	 */
//	public int deleteRecordBeforeOneMonth(){
//		return this.getSqlMapClientTemplate().delete("FactionRecord.deleteBeforeOneMonth");
//	} 
//	
//	/**
//	 * 删除多余的门派建筑，只保留最高等级
//	 * @param factionId
//	 * @param buildId
//	 * @param maxLevel
//	 */
//	public void deleteFactionBuild(String factionId, int buildId, int maxLevel) {
//		Map map = new HashMap();
//		map.put("factionId", factionId);
//		map.put("buildId", buildId);
//		map.put("maxLevel", maxLevel);
//		this.getSqlMapClientTemplate().delete("FactionBuild.deleteFactionBuild", map);
//	}
//	
//	/**
//	 * 获取押注的总钱数
//	 * @return
//	 */
//	public int getFacrtionWarGambleMoney() {
//		Object object = this.getSqlMapClientTemplate().queryForObject("FactionWarGambleInfo.getFacrtionWarGambleMoney");
//		if(null == object) {
//			return 0;
//		}
//		return (Integer)object;
//	}
//	
//	public String getFactionIdByRoleId(String roleId) {
//		Map map = new HashMap();
//		map.put("roleId", roleId);
//		Object object = this.getSqlMapClientTemplate().queryForObject("FactionRole.getFacrtionIdByRoleId", map);
//		if(null == object) {
//			return "";
//		}
//		return ((UnionMember)object).getFactionId();
//	}
//}
