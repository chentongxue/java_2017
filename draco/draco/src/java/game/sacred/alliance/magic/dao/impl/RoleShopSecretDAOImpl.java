package sacred.alliance.magic.dao.impl;


public class RoleShopSecretDAOImpl extends BaseDAOImpl{
	/**
	 * 删除不是同一天的记录
	 * @return
	 */
	public int deleteBeforeOneDay(){
		return this.getSqlMapClientTemplate().delete("RoleSecretShop.deleteBeforeOneDay");
	}
}
