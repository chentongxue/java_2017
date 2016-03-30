package com.game.draco.app.goddess.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.domain.GoodsGoddess;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.goddess.domain.RoleGoddess;
import com.game.draco.app.goddess.vo.GoddessUpgradeResult;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.request.C1355_GoddessUpgradeReqMessage;
import com.game.draco.message.response.C1355_GoddessUpgradeRespMessage;

public class GoddessUpgradeAction extends BaseAction<C1355_GoddessUpgradeReqMessage> {

	@Override
	public Message execute(ActionContext context, C1355_GoddessUpgradeReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role) {
			return null;
		}
		int goddessId = reqMsg.getId();
		GoodsGoddess goodsGoddess = GameContext.getGoodsApp().getGoodsTemplate(GoodsGoddess.class, goddessId);
		if(null == goodsGoddess) {
			return new C0003_TipNotifyMessage(this.getText(TextId.Sys_Param_Error));
		}
		RoleGoddess roleGoddess = GameContext.getUserGoddessApp().getRoleGoddess(role.getRoleId(), goddessId);
		if(null == roleGoddess) {
			return new C0003_TipNotifyMessage(this.getText(TextId.Goddess_had_no));
		}
		
		C1355_GoddessUpgradeRespMessage respMsg = new C1355_GoddessUpgradeRespMessage();
		GoddessUpgradeResult result = GameContext.getGoddessApp().upgrade(role, roleGoddess);
		respMsg.setId(goddessId);
		respMsg.setResult(result.getResult());
		respMsg.setInfo(result.getInfo());
		respMsg.setGrade(result.getGrade());
		respMsg.setBless(result.getBless());
		respMsg.setMaxBless((short)result.getMaxBless());
		respMsg.setCurGradeAttriAddRate(result.getCurGradeAttriAddRate());
		respMsg.setNextGradeAttriAddRate(result.getNextGradeAttriAddRate());
		return respMsg;
	}

}
