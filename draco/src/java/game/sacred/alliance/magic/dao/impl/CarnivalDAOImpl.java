package sacred.alliance.magic.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sacred.alliance.magic.domain.CarnivalDbInfo;
import sacred.alliance.magic.domain.CarnivalRankInfo;

public class CarnivalDAOImpl extends BaseDAOImpl{
	
	public List<CarnivalDbInfo> getActiveDataBySize(int activeId, int start, int end){
		List<CarnivalDbInfo> list = new ArrayList<CarnivalDbInfo>();
		Map map = new HashMap();
		map.put("activeId", activeId);
		map.put("start", start);
		map.put("end", end);
		list = this.getSqlMapClientTemplate().queryForList("CarnivalDbInfo.getActiveDataBySize", map);
		return list;
	}
	
	public List<CarnivalDbInfo> getActiveData(int activeId, int targetValue){
		List<CarnivalDbInfo> list = new ArrayList<CarnivalDbInfo>();
		Map map = new HashMap();
		map.put("activeId", activeId);
		map.put("targetValue", targetValue);
		list = this.getSqlMapClientTemplate().queryForList("CarnivalDbInfo.getActiveData", map);
		return list;
	}

	public List<CarnivalRankInfo> getRoleMonutSort(int start, int end) {
		Map map = new HashMap();
		map.put("start", start);
		map.put("end", end);
		return this.getSqlMapClientTemplate().queryForList("CarnivalRankInfo.getRoleMonutSort", map);
	}

	public List<CarnivalDbInfo> getCampActiveDataByColumn(int itemId){
		Map map = new HashMap();
		map.put("itemId", itemId);
		return this.getSqlMapClientTemplate().queryForList("CarnivalDbInfo.getCampActiveDataByColumn", map);
	}
	
	public List<CarnivalDbInfo> getCareerActiveDataByColumn(int itemId){
		Map map = new HashMap();
		map.put("itemId", itemId);
		return this.getSqlMapClientTemplate().queryForList("CarnivalDbInfo.getCareerActiveDataByColumn", map);
	}
}
