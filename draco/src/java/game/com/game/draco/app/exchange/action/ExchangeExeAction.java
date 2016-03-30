package com.game.draco.app.exchange.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.app.goods.behavior.result.GoodsResult;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.request.C1402_ExchangeExeReqMessage;
import com.game.draco.message.response.C1402_ExchangeExeRespMessage;

public class ExchangeExeAction extends BaseAction<C1402_ExchangeExeReqMessage> {

	@Override
	public Message execute(ActionContext context, C1402_ExchangeExeReqMessage req) {

		RoleInstance role = this.getCurrentRole(context);
		if (null == role) {
			return null;
		}
		if (Util.isEmpty(req.getParam()) || req.getParam().length() < 3) {
			return null;
		}
		String[] param = req.getParam().split(",");
		byte confirm = 0;
		if (param.length > 3) {
			confirm = 1;
		}

		C1402_ExchangeExeRespMessage resp = new C1402_ExchangeExeRespMessage();
		int id = Integer.parseInt(param[0]);
		byte enterType = Byte.parseByte(param[2]);
		try {
			short num = Short.parseShort(param[1]);
			GoodsResult goodsResult = GameContext.getExchangeApp().exchange(role, id, num, enterType, confirm);
			if (goodsResult.isIgnore()) {
				return null;
			}
			if (!goodsResult.isSuccess()) {
				resp.setType((byte) 0);
				resp.setInfo(goodsResult.getInfo());
				return resp;
			}
		} catch (Exception e) {
			logger.error(e.toString());
			return null;
		}
		// 兑换成功
		resp.setEnterType(enterType);
		resp.setType((byte) 1);
		resp.setInfo(Status.Exchange_Success.getTips());
		resp.setExchangeId(id);
		return resp;
	}
}
