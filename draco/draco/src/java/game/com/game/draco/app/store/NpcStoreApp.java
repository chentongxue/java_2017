package com.game.draco.app.store;

import java.util.List;

import com.game.draco.app.store.domain.NpcStore;

import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.vo.RoleInstance;

public interface NpcStoreApp extends Service {

	/**
	 * 物品购买
	 * @param role
	 * @param npcTemplateId
	 * @param showType
	 * @param goodsTemplateId
	 * @param buyNum
	 * @return
	 */
	public String buy(RoleInstance role, String npcTemplateId, int showType,
			int goodsTemplateId, int buyNum);

	/**
	 * 出售物品给NPC
	 * @param role
	 * @param npcTemplateId
	 * @param goodsInstanceId
	 * @return
	 */
	public Status sell(RoleInstance role, String npcTemplateId, String goodsInstanceId,int sellNum);

	/**
	 * 得到NPC的买卖列表
	 * @param npcTemplateId
	 * @param showType
	 * @return
	 */
	public List<NpcStore> getNpcStoreList(String npcTemplateId, int showType);


}
