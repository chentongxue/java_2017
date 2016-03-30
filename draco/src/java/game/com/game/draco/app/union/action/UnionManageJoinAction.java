package com.game.draco.app.union.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.core.exception.ServiceException;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.request.C2751_UnionManageJoinReqMessage;
import com.game.draco.message.response.C2751_UnionRecordRespMessage;

/**
 * 接受、拒绝加入公会
 * @author mofun030602
 *
 */
public class UnionManageJoinAction extends BaseAction<C2751_UnionManageJoinReqMessage> {

	@Override
	public Message execute(ActionContext context, C2751_UnionManageJoinReqMessage reqMsg) {
		C2751_UnionRecordRespMessage resp = new C2751_UnionRecordRespMessage();
		try {
			RoleInstance role = this.getCurrentRole(context);
			
			boolean isInActive = GameContext.getUnionIntegralBattleApp().inIntegtalActive(role.getUnionId());
			if(isInActive){
				return new C0003_TipNotifyMessage(GameContext.getI18n().getText(TextId.UNION_INTEGRAL_IN_ACTIVE_ERR));
			}
			
			Result result = null;
			if(1 == reqMsg.getType()){
				result = GameContext.getUnionApp().acceptApplyJoin(role, reqMsg.getRoleId());
			} else {
				result = GameContext.getUnionApp().refuseApplyJoin(role, reqMsg.getRoleId());
			}
			if(!result.isSuccess()){
				resp.setType((byte) 0);
				resp.setInfo(result.getInfo());
				return resp;
			}
			resp.setType((byte) 1);
			resp.setInfo(this.getText(TextId.SYSTEM_SUCCESS));
			return resp;
		} catch (ServiceException e) {
			this.logger.error("UnionManageJoinAction", e);
			resp.setType((byte) 0);
			resp.setInfo(this.getText(TextId.SYSTEM_ERROR));
			return resp;
		}
	}

}
