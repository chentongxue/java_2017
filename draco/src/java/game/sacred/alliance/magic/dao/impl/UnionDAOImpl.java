package sacred.alliance.magic.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.game.draco.app.union.domain.Union;

public class UnionDAOImpl extends BaseDAOImpl{
	
	/**
	 * 查找门派
	 * @param parameter 模糊的门派名称
	 * @return
	 */
	public List<Union> getUnionByName(String parameter){
		Map map = new HashMap();
		map.put("parameter", parameter);
		return this.getSqlMapClientTemplate().queryForList("Union.searchUnionByName", map);
	}
	
	/**
	 * 删除公会捐赠数据
	 * @return
	 */
	public void resetDonate(){
		this.getSqlMapClientTemplate().update("UnionMemberDonate.updateDonate");
	} 
	
}
