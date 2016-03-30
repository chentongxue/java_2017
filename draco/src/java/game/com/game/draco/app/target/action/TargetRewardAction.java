package com.game.draco.app.target.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.hint.vo.HintType;
import com.game.draco.app.target.cond.TargetLogic;
import com.game.draco.app.target.config.TargetCond;
import com.game.draco.app.target.config.TargetConfig;
import com.game.draco.app.target.domain.RoleTarget;
import com.game.draco.app.target.vo.TargetRewardResult;
import com.game.draco.message.item.TargetDetailItem;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.request.C1122_TargetRewardReqMessage;
import com.game.draco.message.response.C1122_TargetRewardRespMessage;

public class TargetRewardAction extends BaseAction<C1122_TargetRewardReqMessage> {

	@Override
	public Message execute(ActionContext context, C1122_TargetRewardReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role) {
			return null;
		}
		TargetRewardResult result = GameContext.getTargetApp().reward(role, reqMsg.getTargetId());
		if(!result.isSuccess()) {
			C0003_TipNotifyMessage tipMsg = new C0003_TipNotifyMessage();
			tipMsg.setMsgContext(result.getInfo());
			return tipMsg;
		}
		//领奖成功
		//如果是A线则刷新左边hint
		byte line = result.getLine();
		
		//更新当前线目标信息
		TargetConfig config = result.getTargetConfig();
		TargetConfig nextTarget = config.getNextTarget();
		RoleTarget roleTarget = result.getRoleTarget();
		C1122_TargetRewardRespMessage respMsg = new C1122_TargetRewardRespMessage();
		respMsg.setResult(Result.SUCCESS);
		respMsg.setLine(line);
		if(null == nextTarget) {
			//领奖的是最后一个目标
			if(line == TargetConfig.line1) {
				GameContext.getTargetApp().pushTargetHintMessage(role);
			}
			TargetDetailItem item = GameContext.getTargetApp().createDefaultTargetDetailItem(line);
			respMsg.setTargetDetailItem(item);
			// 判断红点提示
			if (!GameContext.getTargetApp().isHavaHint(role)) {
				GameContext.getHintApp().hintChange(role, HintType.target, false);
			}
			return respMsg;
		}
		//有下一个目标
		this.updateLine(role, roleTarget, nextTarget);
		if(line == TargetConfig.line1) {
			GameContext.getTargetApp().pushTargetHintMessage(role);
		}
		respMsg.setTargetDetailItem(GameContext.getTargetApp()
				.createTargetDetailItem(role, roleTarget, nextTarget.getTargetId()));
		// 判断红点提示
		if (!GameContext.getTargetApp().isHavaHint(role)) {
			GameContext.getHintApp().hintChange(role, HintType.target, false);
		}
		return respMsg;
	}
	
	private void updateLine(RoleInstance role, RoleTarget roleTarget, TargetConfig config) {
		byte lineType = config.getLine();
		short targetId = config.getTargetId();
		TargetCond cond = config.getTargetCond();
		TargetLogic logic = GameContext.getTargetApp().getTargetLogic(cond.getCondType());
		byte status = logic.isMeetCond(role, cond) ? RoleTarget.STATUS_ACHIEVE_YES : RoleTarget.STATUS_ACHIEVE_NO;
		roleTarget.updateLine(lineType, targetId, status);
	}

}
