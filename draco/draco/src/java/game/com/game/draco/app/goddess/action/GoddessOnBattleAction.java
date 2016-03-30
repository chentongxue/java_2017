package com.game.draco.app.goddess.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.constant.RespTypeStatus;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.goddess.domain.RoleGoddess;
import com.game.draco.app.goddess.domain.RoleGoddessStatus;
import com.game.draco.app.goddess.vo.GoddessOnBattleResult;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.request.C1361_GoddessOnBattleReqMessage;
import com.game.draco.message.response.C1361_GoddessOnBattleRespMessage;

public class GoddessOnBattleAction extends BaseAction<C1361_GoddessOnBattleReqMessage> {

	@Override
	public Message execute(ActionContext context,	C1361_GoddessOnBattleReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role) {
			return null;
		}
		int goddessId = reqMsg.getGoddessId();
		RoleGoddess goddess = GameContext.getUserGoddessApp().getRoleGoddess(role.getRoleId(), goddessId);
		if(null == goddess) {
			//提示参数错误
			return new C0003_TipNotifyMessage(GameContext.getI18n().getText(TextId.ERROR_INPUT));
		}
		byte onBattle = reqMsg.getOnBattle();
		C1361_GoddessOnBattleRespMessage respMsg = new C1361_GoddessOnBattleRespMessage();
		GoddessOnBattleResult result = GameContext.getGoddessApp().onBattle(role, goddess, onBattle);
		respMsg.setInfo(result.getInfo());
		if(!result.isSuccess()) {
			return respMsg;
		}
		respMsg.setGoddessId(reqMsg.getGoddessId());
		respMsg.setState(onBattle);
		respMsg.setResult(RespTypeStatus.SUCCESS);
		return respMsg ;
	}

}
