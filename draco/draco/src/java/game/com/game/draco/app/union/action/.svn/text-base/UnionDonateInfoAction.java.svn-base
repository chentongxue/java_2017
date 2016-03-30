//package com.game.draco.app.union.action;
//
//import sacred.alliance.magic.action.BaseAction;
//import sacred.alliance.magic.base.Result;
//import sacred.alliance.magic.constant.TextId;
//import sacred.alliance.magic.core.Message;
//import sacred.alliance.magic.core.action.ActionContext;
//import sacred.alliance.magic.vo.RoleInstance;
//
//import com.game.draco.GameContext;
//import com.game.draco.message.request.C1727_FactionDonateInfoReqMessage;
//import com.game.draco.message.response.C1727_UnionDonateInfoRespMessage;
//
///**
// * 获取捐献消息
// * @author mofun030602
// *
// */
//public class UnionDonateInfoAction extends BaseAction<C1727_FactionDonateInfoReqMessage> {
//
//	@Override
//	public Message execute(ActionContext context, C1727_FactionDonateInfoReqMessage reqMsg) {
//		C1727_UnionDonateInfoRespMessage resp = new C1727_UnionDonateInfoRespMessage();
//		resp.setSuccess(Result.FAIL);
//		try {
//			RoleInstance role = this.getCurrentRole(context);
//			resp = GameContext.getUnionApp().getUnionDonateInfo(role);
//			return resp;
//		} catch (Exception e) {
//			this.logger.error("UnionDonateInfoAction", e);
//			resp.setInfo(GameContext.getI18n().getText(TextId.UNION_FAILURE));
//			return resp;
//		}
//	}
//}
