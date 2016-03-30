package sacred.alliance.magic.app.fall;

import java.util.List;
import java.util.Map;

import sacred.alliance.magic.app.goods.GoodsOperateBean;
import sacred.alliance.magic.app.goods.behavior.result.AddGoodsBeanResult;
import sacred.alliance.magic.app.summon.Summon;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.PointType;
import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.app.npc.domain.NpcInstance;
import com.game.draco.message.item.FallItem;

public interface FallApp extends Service{

	public LootList getNpcLootList(String npcId) ;
	/**
	 * 获取新的宝箱ID
	 * @return
	 */
	public String getBoxId();
	
	/**
	 * NPC死亡,掉落宝箱
	 * 
	 * @param dieNpc
	 * @param role 掉落者归属者（如果是藏宝图，则归召唤者所有）
	 */
	public boolean fallBox(NpcInstance dieNpc, RoleInstance role,OutputConsumeType ocType);
	
	
	/**
	 * 拾取
	 * @param entryId
	 * @param caller
	 * @param itemId
	 */
	public void pickupEntry(String entryId,RoleInstance caller,int itemId);
	
	
	/**
	 * 查看里面的物品
	 * @param entryId
	 * @param caller
	 */
	public void listEntry(String entryId,RoleInstance caller);
	
	public PointType getPointType(String entryId) ;

	/**
	 * 掉落
	 * @param role
	 * @param itemList
	 * @return
	 */
	public boolean fallBox(RoleInstance role, List<GoodsOperateBean> itemList,
			OutputConsumeType ocType,int x,int y,boolean fullSendMail);
	

	public AddGoodsBeanResult pickupAction(String entryId, RoleInstance role,
			int itemId, List<FallItem> fallList,int outputType);
	
	/**
	 * 召唤怪物死亡掉落
	 * @param dieNpc
	 * @param summon
	 * @param role
	 * @return
	 */
	public boolean summonFallBox(NpcInstance dieNpc,Summon summon,RoleInstance role,RoleInstance ownRole);
	
	public Map<String,List<GoodsOperateBean>> roll(List<GoodsOperateBean> itemList,
			List<AbstractRole> sameMapMembers);
	
	/**
	 * 根据索引获得掉落
	 * @param lootId
	 * @return
	 */
	public LootList getLootList(String lootId);
	
}
