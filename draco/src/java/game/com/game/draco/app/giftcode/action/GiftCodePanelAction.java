package com.game.draco.app.giftcode.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.giftcode.config.GiftCodeConfig;
import com.game.draco.message.item.GoodsLiteNamedItem;
import com.game.draco.message.request.C2401_GiftCodePanelReqMessage;
import com.game.draco.message.response.C2401_GiftCodePanelRespMessage;

public class GiftCodePanelAction extends
		BaseAction<C2401_GiftCodePanelReqMessage> {

	@Override
	public Message execute(ActionContext context,
			C2401_GiftCodePanelReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if (null == role) {
			return null;
		}
		C2401_GiftCodePanelRespMessage respMsg = new C2401_GiftCodePanelRespMessage();
		Collection<GiftCodeConfig> all = GameContext
				.getGiftCodeApp().getAllGiftCodeConfig();
		if (null == all) {
			return respMsg;
		}
		//int channelId = role.getChannelId();
		//int serverId = GameContext.getServerId() ;
		List<GoodsLiteNamedItem> itemList = new ArrayList<GoodsLiteNamedItem>();
		/*for (GiftCodeConfig config : all) {
			try {
				if (!config.nowShow(channelId,serverId)) {
					continue;
				}
				GoodsLiteNamedItem goodsItem = config.getGoodsBase()
						.getGoodsLiteNamedItem();
				goodsItem.setBindType(BindingType.already_binding.getType());
				goodsItem.setNum((short)1);
				itemList.add(goodsItem);
			} catch (Exception ex) {
				logger.error("", ex);
			}
		}*/
		respMsg.setItemList(itemList);
		return respMsg;
	}

}
