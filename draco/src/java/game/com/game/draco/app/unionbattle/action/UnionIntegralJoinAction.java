package com.game.draco.app.unionbattle.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.app.goods.Util;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.team.LeaveTeam;
import com.game.draco.app.unionbattle.domain.UnionIntegralState;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.request.C2546_UnionIntegralJoinMessage;

/**
 * 进入公会积分战
 */
public class UnionIntegralJoinAction extends BaseAction<C2546_UnionIntegralJoinMessage> {

	@Override
	public Message execute(ActionContext context, C2546_UnionIntegralJoinMessage req) {
		RoleInstance role = this.getCurrentRole(context);
		if(role == null){
			return null;
		}
		
		Result result = new Result();
		
		if(!role.hasUnion()){
			result.setInfo(GameContext.getI18n().getText(TextId.UNION_NOT));
			return new C0003_TipNotifyMessage(result.getInfo());
		}
		
		UnionIntegralState integralState = GameContext.getUnionIntegralBattleApp().getIntegralState(role);
		if(integralState == null){
			return new C0003_TipNotifyMessage(GameContext.getI18n().getText(TextId.UNION_INTEGRAL_JOIN_ERR));
		}
		
		result = GameContext.getUnionIntegralBattleApp().joinIntegralBattle(role);
		if(!result.isSuccess()){
			if(Util.isEmpty(result.getInfo())){
				return null;
			}
			return new C0003_TipNotifyMessage(result.getInfo());
		}
		
		return null;
	}

}
