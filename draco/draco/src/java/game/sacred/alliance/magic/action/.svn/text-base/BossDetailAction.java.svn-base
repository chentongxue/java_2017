package sacred.alliance.magic.action;

import java.util.ArrayList;
import java.util.List;

import com.game.draco.GameContext;
import com.game.draco.app.npc.domain.NpcTemplate;
import com.game.draco.app.npc.refresh.BossLoot;
import com.game.draco.app.npc.refresh.NpcRefreshConfig;
import com.game.draco.message.item.GoodsLiteItem;
import com.game.draco.message.request.C0612_BossDetailReqMessage;
import com.game.draco.message.response.C0612_BossDetailRespMessage;
import com.game.draco.message.response.C0002_ErrorRespMessage;

import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.domain.GoodsBase;

public class BossDetailAction extends BaseAction<C0612_BossDetailReqMessage> {

	@Override
	public Message execute(ActionContext context, C0612_BossDetailReqMessage reqMsg) {
		
		NpcRefreshConfig config = GameContext.getNpcRefreshApp().getBossRefreshConfig(reqMsg.getId());
		if(null == config){
			return new C0002_ErrorRespMessage(reqMsg.getCommandId(),this.getText(TextId.ERROR_INPUT));
		}
		C0612_BossDetailRespMessage respMsg = new C0612_BossDetailRespMessage();
		respMsg.setId(reqMsg.getId());
		respMsg.setBossInfo(config.getBossInfo());
		
		NpcTemplate npcTemplate = GameContext.getNpcApp().getNpcTemplate(config.getNpcId());
		
		respMsg.setBossRes((short)npcTemplate.getResid());
		
		BossLoot loot = GameContext.getNpcRefreshApp().getBossLoot(config.getLootId());
		if(null == loot){
			return respMsg ;
		}
		List<GoodsLiteItem> lootItems = new ArrayList<GoodsLiteItem>();
		for(Integer goodsId : loot.getGoodsList()){
			GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(goodsId);
			if(null == gb){
				continue ;
			}
			lootItems.add(gb.getGoodsLiteItem());
		}
		respMsg.setLootItems(lootItems);
		return respMsg;
	}

}
