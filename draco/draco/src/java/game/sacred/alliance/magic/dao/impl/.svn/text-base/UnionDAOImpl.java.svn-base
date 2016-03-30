package sacred.alliance.magic.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.game.draco.app.union.domain.Union;
import com.game.draco.app.union.domain.UnionRecord;

public class UnionDAOImpl extends BaseDAOImpl{
	
	/**
	 * 查找门派
	 * @param parameter 模糊的门派名称
	 * @return
	 */
	public List<Union> getUnionByName(String parameter){
		Map map = new HashMap();
		map.put("parameter", parameter);
		return this.getSqlMapClientTemplate().queryForList("Faction.searchFactionByName", map);
	}
	
	/**
	 * 查询门派记录
	 * @param factionId 门派ID
	 * @param start 开始记录行
	 * @param end 记录条数
	 * @return
	 */
	public List<UnionRecord> getUnionRecordList(String factionId, int start, int end) {
		Map map = new HashMap();
		map.put("factionId", factionId);
		map.put("start", start);
		map.put("end", end);
		return this.getSqlMapClientTemplate().queryForList("FactionRecord.getRecordList", map);
	}
	
	/**
	 * 删除一个月之前的门派记录
	 * @return
	 */
	public int deleteRecordBeforeOneMonth(){
		return this.getSqlMapClientTemplate().delete("FactionRecord.deleteBeforeOneMonth");
	} 
	
}
