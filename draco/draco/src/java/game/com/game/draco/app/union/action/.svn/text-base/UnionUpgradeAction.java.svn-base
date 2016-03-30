package com.game.draco.app.union.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.base.OperatorType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.request.C2752_UnionUpgradeReqMessage;
import com.game.draco.message.response.C2752_UnionUpgradeRespMessage;

/**
 * 公会升级
 * @author mofun030602
 *
 */
public class UnionUpgradeAction extends BaseAction<C2752_UnionUpgradeReqMessage> {

	@Override
	public Message execute(ActionContext context, C2752_UnionUpgradeReqMessage reqMsg) {
		C2752_UnionUpgradeRespMessage resp = new C2752_UnionUpgradeRespMessage();
		try {
			RoleInstance role = this.getCurrentRole(context);
			Result result = new Result();
			
			result = GameContext.getUnionApp().isUpgrade(role, reqMsg.getUnionId());
			if(!result.isSuccess()){
				resp.setType((byte) 0);
				resp.setInfo(result.getInfo());
				return resp;
			}
			//升级所需人气值
			int value = GameContext.getUnionApp().getUnionUpgradePopularity(reqMsg.getUnionId());
			
			result = GameContext.getUnionApp().changeUnionPopularity(reqMsg.getUnionId(), OperatorType.Decrease, value, -1);
			if(!result.isSuccess()){
				resp.setType((byte) 0);
				resp.setInfo(result.getInfo());
				return resp;
			}
			resp.setType((byte) 1);
			resp.setInfo(result.getInfo());
			return resp;
		} catch (Exception e) {
			this.logger.error("UnionUpgradeAction", e);
			resp.setInfo(this.getText(TextId.UNION_UPGRADE_FAIL));
			return resp;
		}
	}

}
