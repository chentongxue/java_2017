package com.game.draco.app.goddess.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.domain.GoodsGoddess;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.goddess.GoddessAppImpl;
import com.game.draco.app.goddess.config.GoddessGrade;
import com.game.draco.app.goddess.config.GoddessLevelup;
import com.game.draco.app.goddess.domain.RoleGoddess;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.request.C1352_GoddessBasicReqMessage;
import com.game.draco.message.response.C1352_GoddessBasicRespMessage;

public class GoddessBasicAction extends BaseAction<C1352_GoddessBasicReqMessage> {

	@Override
	public Message execute(ActionContext context, C1352_GoddessBasicReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role) {
			return null;
		}
		int goddessId = reqMsg.getId();
		GoodsGoddess goodsGoddess = GameContext.getGoodsApp().getGoodsTemplate(GoodsGoddess.class, goddessId);
		if(null == goodsGoddess) {
			return new C0003_TipNotifyMessage(this.getText(TextId.Sys_Param_Error));
		}
		C1352_GoddessBasicRespMessage respMsg = new C1352_GoddessBasicRespMessage();
		respMsg.setId(goddessId);
		respMsg.setName(goodsGoddess.getName());
		respMsg.setQuality(goodsGoddess.getQualityType());
		RoleGoddess roleGoddess = GameContext.getUserGoddessApp().getRoleGoddess(role.getRoleId(), goddessId);
		int level = goodsGoddess.getStartLevel();
		GoddessLevelup lp = GameContext.getGoddessApp().getGoddessLevelup(goddessId, level);
		if(null != lp) {
			respMsg.setAttriItemList(lp.getAttriTypeValueList());
		}
		if(null == roleGoddess) {
			//未拥有女神
			respMsg.setGoodsLiteItem(goodsGoddess.getEnlistGoodsLiteNamedItem());
			return respMsg;
		}
		//拥有女神
		level = roleGoddess.getLevel();
		respMsg.setHad(GoddessAppImpl.OWN_YES);
		byte grade = roleGoddess.getGrade();
		respMsg.setGrade(grade);
		respMsg.setLevel(level);
		respMsg.setWeakTime(roleGoddess.getWeakTime());
		respMsg.setCurBless(roleGoddess.getCurBless());
		//当前阶属性加成给主角比率
		GoddessGrade curGrade = GameContext.getGoddessApp().getGoddessGrade(grade);
		respMsg.setMaxBless((short)curGrade.getBlessMax());
		respMsg.setCurGradeAttriAddRate(curGrade.getAttriAddRate());
		//下一阶属性加成给主角比率
		if(grade < GoddessGrade.getMaxGrade()) {
			GoddessGrade nextGrade = GameContext.getGoddessApp().getGoddessGrade((byte)(grade + 1));
			respMsg.setNextGradeAttriAddRate(nextGrade.getAttriAddRate());
		}
		respMsg.setGoodsLiteItem(curGrade.getUpgradeGoodsLiteNamedItem());
		return respMsg;
	}

}
