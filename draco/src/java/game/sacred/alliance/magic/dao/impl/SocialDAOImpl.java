package sacred.alliance.magic.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.game.draco.app.social.domain.DracoSocialRelation;


public class SocialDAOImpl extends BaseDAOImpl{
	
	@SuppressWarnings("unchecked")
	public List<DracoSocialRelation> selectFriendList(String roleId) {
		Map map = new HashMap();
		map.put("roleId", roleId);
		return this.getSqlMapClientTemplate().queryForList("DracoSocialRelation.getRelationList", map);
	}
}
