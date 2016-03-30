package sacred.alliance.magic.dao.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import sacred.alliance.magic.dao.RoleDAO;
import sacred.alliance.magic.domain.ChargeRecord;
import sacred.alliance.magic.domain.RoleLevelDistribution;
import sacred.alliance.magic.domain.RolePayRecord;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.app.camp.balance.domain.CampLevel;

public class RoleDAOImpl extends SqlMapClientDaoSupport implements RoleDAO{


	/** 获得被隔离的玩家 **/
	public List<RoleInstance> getFrozenRoleList() {
		return this.getSqlMapClientTemplate().queryForList(
				"RoleInstance.getFrozenRoleList");
	}

	/**  根据角色名查询隔离玩家 **/
	public List<RoleInstance> getFrozenRole(String roleName) {
		Map map = new HashMap();
		map.put("rolename", roleName);
		return this.getSqlMapClientTemplate().queryForList(
				"RoleInstance.getFrozenRoleList", map);
	}

	/**  获得被禁言的玩家 **/
	public List<RoleInstance> getForbidRoleList() {
		return this.getSqlMapClientTemplate().queryForList(
				"RoleInstance.getForbidRoleList");
	}

	/**  根据角色名查询禁言玩家 **/
	public List<RoleInstance> getForbidRoleList(String roleName) {
		Map map = new HashMap();
		map.put("rolename", roleName);
		return this.getSqlMapClientTemplate().queryForList(
				"RoleInstance.getForbidRoleList", map);
	}
	
	@Override
	public List<RoleInstance> getRoleList(String userName, String roleId, String roleName, String userId, String channelUserId) {
		Map map = new HashMap();
		map.put("userName", userName);
		map.put("roleId", roleId);
		map.put("roleName", roleName);
		map.put("userId", userId);
		map.put("channelUserId", channelUserId);
		return this.getSqlMapClientTemplate().queryForList("RoleInstance.getRoleList", map);
	}
	
	/** 查看玩家充值记录 * */
	public ChargeRecord getRoleAllChargeSum(String rolename) {
		Map map = new HashMap();
		map.put("rolename", rolename);
		return (ChargeRecord) this.getSqlMapClientTemplate().queryForObject(
				"ChargeRecord.getRoleAllChargeSum", map);
	}
	
	/** 查询玩家金钱排行 **/
	public List<RoleInstance> getRoleMoneyCharts(int orderby,int start, int end){
		Map map = new HashMap();
		map.put("orderby", orderby);
		map.put("start", start);
		map.put("end", end);
		return this.getSqlMapClientTemplate().queryForList("RoleInstance.getRoleMoneyCharts", map);
	}
	
	/** 更改玩家角色名称 **/
	public int changeRoleName(int roleId,String newRoleName){
		Map map = new HashMap();
		map.put("roleId", new Integer(roleId));
		map.put("newRoleName", newRoleName);
		return (Integer)this.getSqlMapClientTemplate().queryForObject("RoleInstance.changeRoleName",map);
	}
	
	public List<Integer> getDelRoleList(){
		return this.getSqlMapClientTemplate().queryForList("RoleInstance.getDelRoleList");
	}
	
	public int delRole(int roleId){
		return (Integer)this.getSqlMapClientTemplate().queryForObject("RoleInstance.delRole",roleId);
	}

	@Override
	public int changeSilverMoney(String roleId, int changeMoney) {
		if(0 == changeMoney){
			return 0 ;
		}
		Map map = new HashMap();
		map.put("roleId", new Integer(roleId));
		map.put("silverMoney", changeMoney);
		return (Integer)this.getSqlMapClientTemplate().update("RoleInstance.changeSilverMoney", map);
	}
	
	@Override
	public List<RolePayRecord> getUserMoneyRankList(int orderby, int start, int end) {
		Map map = new HashMap();
		map.put("orderby", orderby);
		map.put("start", start);
		map.put("end", end);
		return this.getSqlMapClientTemplate().queryForList("RolePayRecord.getUserMoneyRank", map);
	}

	@Override
	public int updateFrozenAndForbid(RoleInstance role) {
		return (Integer) this.getSqlMapClientTemplate().update("RoleInstance.updateFrozenAndForbid", role);
	}

	@Override
	public List<RoleInstance> getUserRoles(String userName, String userId, String channelUserId) {
		Map map = new HashMap();
		map.put("userName", userName);
		map.put("userId", userId);
		map.put("channelUserId", channelUserId);
		return this.getSqlMapClientTemplate().queryForList("RoleInstance.searchUserRoles", map);
	}

	@Override
	public int timingWriteDBRole(RoleInstance role) {
		return (Integer) this.getSqlMapClientTemplate().update("RoleInstance.timingWriteDBRole", role);
	}
	
	/*@Override
	public List<RoleInstance> getCareerRoleSortByColumn(String columnName, String subColumnName) {
		Map map = new HashMap();
		map.put("columnName", columnName);
		map.put("subColumnName", subColumnName);
		return this.getSqlMapClientTemplate().queryForList("RoleInstance.getCareerRoleSortByColumn", map);
	}*/
	
	@Override
	public List<RoleInstance> getCampRoleSortByColumn(String columnName, String subColumnName) {
		Map map = new HashMap();
		map.put("columnName", columnName);
		map.put("subColumnName", subColumnName);
		return this.getSqlMapClientTemplate().queryForList("RoleInstance.getCampRoleSortByColumn", map);
	}
	
	@Override
	public List<RoleInstance> getRoleByColumn(String columnName, int value) {
		Map map = new HashMap();
		map.put("columnName", columnName);
		map.put("value", value);
		return this.getSqlMapClientTemplate().queryForList("RoleInstance.getRoleByColumn", map);
	}

	@Override
	public List<RoleInstance> getRoleMountByScore(int value) {
		Map map = new HashMap();
		map.put("value", value);
		return this.getSqlMapClientTemplate().queryForList("RoleInstance.getRoleMountByScore", map);
	}

	@Override
	public void clearAllCopyLostReLoginInfo() {
		this.getSqlMapClientTemplate().update("RoleInstance.clearAllCopyLostReLoginInfo");
	}

	@Override
	public List<RoleLevelDistribution> getLevelDistributionList() {
		return this.getSqlMapClientTemplate().queryForList("RoleInstance.getLevelDistribution");
	}
	
	@Override
	public long getGoldMoneyReamin() {
		Object object = this.getSqlMapClientTemplate().queryForObject("RolePayRecord.goldMoneyRemain");
		if(null == object) {
			return 0;
		}
		return (Long)object;
	}
	
	public int countLateLoginRole(Date date){
		Map map = new HashMap();
		map.put("lastLoginTime", date);
		Object object = this.getSqlMapClientTemplate().queryForObject("RoleInstance.countLateLoginRole",map);
		if(null == object) {
			return 0;
		}
		return (Integer)object;
	}

	@Override
	public RoleInstance selectMaxLevelRole(String userId) {
		Map map = new HashMap();
		map.put("userId", userId);
		return (RoleInstance) this.getSqlMapClientTemplate().queryForObject("RoleInstance.selectMaxLevelRole", map);
	}
	
	@Override
	public List<CampLevel> getCampLevelList(int minLevel) {
		Map map = new HashMap();
		map.put("minLevel", minLevel);
		return this.getSqlMapClientTemplate().queryForList("RoleInstance.getCampLevel",map);
	}
	
	/** 更改玩家角色阵营 **/
	@Override
	public int changeRoleCamp(String roleId,byte newCampId){
		Map map = new HashMap();
		map.put("roleId", roleId);
		map.put("newCampId", newCampId);
		return (Integer)this.getSqlMapClientTemplate().update("RoleInstance.changeRoleCamp",map);
	}
	
	@Override
	public void updateRoleLeaveFactionTime(RoleInstance role) {
		this.getSqlMapClientTemplate().update("RoleInstance.updateRoleLeaveFactionTime", role);
	}

	@Override
	public List<RoleInstance> getRoleWorldLevelList(int number) {
		Map map = new HashMap();
		map.put("number", number);
		return this.getSqlMapClientTemplate().queryForList("RoleInstance.getWorldLevelList", map);
	}
}
