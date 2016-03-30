package com.game.draco.app.nostrum;

import java.util.List;
import java.util.Map;

import sacred.alliance.magic.app.attri.AttriBuffer;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.domain.GoodsNostrum;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.app.AppSupport;
import com.game.draco.app.nostrum.config.NostrumLimitConfig;
import com.game.draco.app.nostrum.vo.NostrumRoleData;

public interface NostrumApp extends Service, AppSupport{
	
	public List<Integer> getGoodsIdList();
	
	public Map<Integer,List<NostrumLimitConfig>> getLimitConfigMap();
	
	public List<GoodsNostrum> getGoodsNostrumList();
	
	public short getMaxNumber(RoleInstance role, int goodsId);
	
	public int getCurrNumber(RoleInstance role, int goodsId);
	
	public int getAttrValue(RoleInstance role, int goodsId);
	
	public void useNostrum(RoleInstance role, int goodsId);
	
	public Result useNostrum(RoleInstance role, RoleGoods roleGoods);
	
	public AttriBuffer getAttriBuffer(RoleInstance role);
	
	public NostrumRoleData getNostrumRoleData(String roleId);
	
}
