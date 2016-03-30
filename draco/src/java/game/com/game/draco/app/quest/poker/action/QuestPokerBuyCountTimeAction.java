package com.game.draco.app.quest.poker.action;
/**
 * 每日任务（翻牌）购买轮次，每轮三张牌
 * 如果VIP符合等级，切购买次数未用尽，则提示
 */
import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.request.C0722_QuestPokerBuyCountTimeReqMessage;
import com.game.draco.message.response.C0722_QuestPokerBuyCountTimeRespMessage;

public class QuestPokerBuyCountTimeAction extends BaseAction<C0722_QuestPokerBuyCountTimeReqMessage>{

	@Override
	public Message execute(ActionContext context, C0722_QuestPokerBuyCountTimeReqMessage req) {
		RoleInstance role = this.getCurrentRole(context);
		if(role == null){
			return null;
		}
		String param = req.getParam();
		byte confirm = 0;
		try{
			if(!Util.isEmpty(param))
				confirm = Byte.parseByte(param);
		}catch (Exception e) {
			this.logger.error(this.getClass().getName() + ".execute action error: ", e);
			return null;
		}
		 
		
		C0722_QuestPokerBuyCountTimeRespMessage resp = new C0722_QuestPokerBuyCountTimeRespMessage();
		Result result = GameContext.getQuestPokerApp().buyCountTime(role, confirm);
		if(result.isIgnore()){
			return null;
		}
		if(!result.isSuccess()) {
			resp.setInfo(result.getInfo());
			return resp;
		}
		resp.setType(result.getResult());
		resp.setInfo(result.getInfo());
		return resp;
	}
}
