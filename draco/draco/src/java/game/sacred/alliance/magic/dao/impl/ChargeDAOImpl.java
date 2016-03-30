package sacred.alliance.magic.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sacred.alliance.magic.domain.ChargeRecord;

public class ChargeDAOImpl extends BaseDAOImpl{
	
	/**
	 * 查询帐号的充值记录
	 * @param userId 
	 * @param start 开始位置
	 * @param size 查询的条数
	 * @return
	 */
	public List<ChargeRecord> getUserChargeRecord(String userId, int start, int size){
		Map map = new HashMap();
		map.put("userId", userId);
		map.put("start", start);
		map.put("size", size);
		return this.getSqlMapClientTemplate().queryForList("ChargeRecord.getUserChargeRecord", map);
	}
	
}
