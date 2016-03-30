package com.game.draco.app.union.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.request.C1700_UnionCreateConditionReqMessage;
import com.game.draco.message.response.C1700_UnionCreateConditionRespMessage;

/**
 * 创建公会
 * @author mofun030602
 *
 */
public class UnionCreateConditionAction extends BaseAction<C1700_UnionCreateConditionReqMessage> {

	@Override
	public Message execute(ActionContext context, C1700_UnionCreateConditionReqMessage reqMsg) {
		C1700_UnionCreateConditionRespMessage resp = new C1700_UnionCreateConditionRespMessage();
		try {
			RoleInstance role = this.getCurrentRole(context);
			Result result = GameContext.getUnionApp().checkCreateUnionCondition(role);
			if(!result.isSuccess()){
				resp.setType((byte) 0);
				resp.setInfo(result.getInfo());
				return resp;
			}
			resp.setType((byte) 1);
			resp.setInfo(result.getInfo());
			return resp;
		} catch (Exception e) {
			this.logger.error("UnionCreateAction", e);
			resp.setInfo(this.getText(TextId.UNION_CREATE_FAIL));
			return resp;
		}
	}

}
