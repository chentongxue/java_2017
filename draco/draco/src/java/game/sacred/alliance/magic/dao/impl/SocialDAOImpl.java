package sacred.alliance.magic.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sacred.alliance.magic.domain.RoleSocialRelation;

public class SocialDAOImpl extends BaseDAOImpl{
	
	public List<RoleSocialRelation> selectFriendList(String roleId) {
		Map map = new HashMap();
		map.put("roleId", roleId);
		return this.getSqlMapClientTemplate().queryForList("RoleSocialRelation.getRelationList", map);
	}
	
	/**
	 * 修改角色1的名称
	 * @param roleId
	 * @param roleName
	 * @return
	 */
	public int modifyRoleName1(String roleId, String roleName){
		Map map = new HashMap();
		map.put("roleId", roleId);
		map.put("roleName", roleName);
		return this.getSqlMapClientTemplate().update("RoleSocialRelation.modifyRoleName1", map);
	}
	
	/**
	 * 修改角色2的名称
	 * @param roleId
	 * @param roleName
	 * @return
	 */
	public int modifyRoleName2(String roleId, String roleName){
		Map map = new HashMap();
		map.put("roleId", roleId);
		map.put("roleName", roleName);
		return this.getSqlMapClientTemplate().update("RoleSocialRelation.modifyRoleName2", map);
	}
	
	/**
	 * 修改角色1的阵营
	 * @param roleId
	 * @param roleName
	 * @return
	 */
	public int modifyRoleCamp1(String roleId, byte campId){
		Map map = new HashMap();
		map.put("roleId", roleId);
		map.put("camp", campId);
		return this.getSqlMapClientTemplate().update("RoleSocialRelation.modifyRoleCamp1", map);
	}
	
	/**
	 * 修改角色2的阵营
	 * @param roleId
	 * @param roleName
	 * @return
	 */
	public int modifyRoleCamp2(String roleId, byte campId){
		Map map = new HashMap();
		map.put("roleId", roleId);
		map.put("camp", campId);
		return this.getSqlMapClientTemplate().update("RoleSocialRelation.modifyRoleCamp2", map);
	}
	
}
