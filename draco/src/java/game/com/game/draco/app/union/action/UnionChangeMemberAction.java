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
import com.game.draco.message.request.C1708_UnionChangeMemberReqMessage;
import com.game.draco.message.response.C1708_UnionChangeMemberRespMessage;

/**
 * 操作家族成员（踢出 升级 降级）
 * @author mofun030602
 *
 */
public class UnionChangeMemberAction extends BaseAction<C1708_UnionChangeMemberReqMessage> {

	@Override
	public Message execute(ActionContext context, C1708_UnionChangeMemberReqMessage reqMsg) {
		C1708_UnionChangeMemberRespMessage resp = new C1708_UnionChangeMemberRespMessage();
		resp.setType((byte) 0);
		try {
			RoleInstance role = this.getCurrentRole(context);
			
			boolean isInActive = GameContext.getUnionIntegralBattleApp().inIntegtalActive(role.getUnionId());
			if(isInActive){
				return new C0003_TipNotifyMessage(GameContext.getI18n().getText(TextId.UNION_INTEGRAL_IN_ACTIVE_ERR));
			}
			
			Result result = new Result();
			if(reqMsg.getType() == 0){
				//开除
				result = GameContext.getUnionApp().removeUnionMember(role, reqMsg.getRoleId());
			}else if(reqMsg.getType() == 1){
				//升级
				result = GameContext.getUnionApp().levelUpUnionMember(role, reqMsg.getRoleId());
			}else if(reqMsg.getType() == 2){
				//降级
				result = GameContext.getUnionApp().demotionUnionMember(role, reqMsg.getRoleId());
			}
			if(!result.isSuccess()){
				resp.setInfo(result.getInfo());
				return resp;
			}
			resp.setType(result.getResult());
			resp.setInfo(result.getInfo());
			return resp;
		} catch (ServiceException e) {
			this.logger.error("UnionChangeMemberAction", e);
			resp.setInfo(this.getText(TextId.SYSTEM_ERROR));
			return resp;
		}
	}

}
