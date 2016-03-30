package sacred.alliance.magic.dao.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class QuestDAOImpl extends BaseDAOImpl{
	
	/**
	 * 更新任务完成的日志
	 * @param roleId 角色ID
	 * @param key 列名
	 * @param value 数据值
	 * @return
	 */
	public int updateOneField(String roleId, String key, long value){
		Map map = new HashMap();
		map.put("roleId", roleId);
		map.put("key", key);
		map.put("value", value);
		return this.getSqlMapClientTemplate().update("RoleQuestFinished.updateOnlyOneField", map);
	}
	
	/**
	 * 更新日常任务的完成日志
	 * @param roleId 角色ID
	 * @param updateTime 日期
	 * @param key 列名
	 * @param value 数据值
	 * @return
	 */
	public int updateDailyOneField(String roleId, Date updateTime, String key, long value){
		Map map = new HashMap();
		map.put("roleId", roleId);
		map.put("updateTime", updateTime);
		map.put("key", key);
		map.put("value", value);
		return this.getSqlMapClientTemplate().update("RoleQuestDailyFinished.updateOnlyOneField", map);
	}
	
}
