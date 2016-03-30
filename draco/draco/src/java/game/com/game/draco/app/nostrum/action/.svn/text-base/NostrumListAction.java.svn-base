package com.game.draco.app.nostrum.action;

import java.util.ArrayList;
import java.util.List;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.domain.GoodsNostrum;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.item.NostrumItem;
import com.game.draco.message.request.C1911_NostrumListReqMessage;
import com.game.draco.message.response.C1911_NostrumListRespMessage;

public class NostrumListAction extends BaseAction<C1911_NostrumListReqMessage> {

	@Override
	public Message execute(ActionContext context, C1911_NostrumListReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null;
		}
		C1911_NostrumListRespMessage resp = new C1911_NostrumListRespMessage();
		List<NostrumItem> nostrumList = new ArrayList<NostrumItem>();
		try {
			for(GoodsNostrum goods : GameContext.getNostrumApp().getGoodsNostrumList()){
				if(null == goods){
					continue;
				}
				int goodsId = goods.getId();
				NostrumItem item = new NostrumItem();
				item.setGoodsId(goodsId);
				item.setImageId(goods.getImageId());
				item.setName(goods.getName());
				item.setCurrNum((short) GameContext.getNostrumApp().getCurrNumber(role, goodsId));
				item.setMaxNum(GameContext.getNostrumApp().getMaxNumber(role, goodsId));
				item.setAttrType(goods.getAttrType());
				item.setAttrValue(GameContext.getNostrumApp().getAttrValue(role, goodsId));
				nostrumList.add(item);
			}
			resp.setNostrumList(nostrumList);
		} catch (Exception e) {
			this.logger.error(this.getClass().getName() + ".execute error: ", e);
		}
		return resp;
	}

}
