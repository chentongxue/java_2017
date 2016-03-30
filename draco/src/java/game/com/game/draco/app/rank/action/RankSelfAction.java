package com.game.draco.app.rank.action;

import com.game.draco.GameContext;
import com.game.draco.app.rank.domain.RankInfo;
import com.game.draco.app.rank.domain.RankLogRoleInfo;
import com.game.draco.app.rank.logic.RankLogic;
import com.game.draco.message.request.C0812_RankSelfReqMessage;
import com.game.draco.message.response.C0812_RankSelfRespMessage;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;
/**
 * 0812
 * 获得自己所在排行榜的名次
 */
public class RankSelfAction extends BaseAction<C0812_RankSelfReqMessage>{

	@Override
	public Message execute(ActionContext context, C0812_RankSelfReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null;
		}
		RankInfo rankInfo = GameContext.getRankApp().getRankInfo(reqMsg.getRankId());
		if(null == rankInfo){
			return null;
		}
		C0812_RankSelfRespMessage respMsg = new  C0812_RankSelfRespMessage();
		RankLogRoleInfo rlRoleInfo = GameContext.getRankApp().getRoleRank(rankInfo.getId(),
				role.getRoleId());
		if(null == rlRoleInfo){
			return null;
		}
		short rank = rlRoleInfo.getRank();
		respMsg.setRank(rank);
		//客户端页码从1开始
		respMsg.setSelfPage((short)((rank - 1) / RankLogic.PRE_PAGE_COUNT + 1));
		return respMsg;
	}

}
