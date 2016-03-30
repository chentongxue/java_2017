package com.game.draco.app.store;

import java.util.List;

import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.app.store.config.NpcStore;
import com.game.draco.app.store.config.NpcStoreAnytime;


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
	public Result buy(RoleInstance role, String npcTemplateId, int showType,
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
	
	public Message getNpcStoreMessage(String npcTemplateId, int showType) ;
	
	public NpcStoreAnytime getNpcStoreAnytime() ;


}
