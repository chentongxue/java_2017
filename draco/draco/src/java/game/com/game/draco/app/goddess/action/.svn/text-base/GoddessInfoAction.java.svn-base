package com.game.draco.app.goddess.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.domain.GoodsGoddess;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.goddess.GoddessAppImpl;
import com.game.draco.app.goddess.config.GoddessLevelup;
import com.game.draco.app.goddess.domain.RoleGoddess;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.request.C1351_GoddessInfoReqMessage;
import com.game.draco.message.response.C1351_GoddessInfoRespMessage;

public class GoddessInfoAction extends BaseAction<C1351_GoddessInfoReqMessage> {

	@Override
	public Message execute(ActionContext context, C1351_GoddessInfoReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role) {
			return null;
		}
		int goddessId = reqMsg.getId();
		if(goddessId == -1) {
			//如果客户端请求-1,则取当前出战的女神
			RoleGoddess onBattle = GameContext.getGoddessApp().getOnBattleGoddes(role.getRoleId());
			if(null == onBattle) {
				return new C0003_TipNotifyMessage(this.getText(TextId.Sys_Param_Error));
			}
			goddessId = onBattle.getGoddessId();
		}
		GoodsGoddess goodsGoddess = GameContext.getGoodsApp().getGoodsTemplate(GoodsGoddess.class, goddessId);
		if(null == goodsGoddess) {
			return new C0003_TipNotifyMessage(this.getText(TextId.Sys_Param_Error));
		}
		C1351_GoddessInfoRespMessage respMsg = new C1351_GoddessInfoRespMessage();
		respMsg.setId(goddessId);
		respMsg.setResId((short)goodsGoddess.getResId());
		RoleGoddess roleGoddess = GameContext.getUserGoddessApp().getRoleGoddess(role.getRoleId(), goddessId);
		if(null == roleGoddess) {
			return respMsg;
		}
		GoddessLevelup levelup = GameContext.getGoddessApp().getGoddessLevelup(goddessId, roleGoddess.getLevel());
		respMsg.setHad(GoddessAppImpl.OWN_YES);
		respMsg.setCurExp(roleGoddess.getExp());
		respMsg.setMaxExp(levelup.getMaxExp());
		respMsg.setBattleScore(GameContext.getGoddessApp().getBattleScore(roleGoddess));
		respMsg.setOnBattle(roleGoddess.getOnBattle());
		return respMsg;
	}

}
