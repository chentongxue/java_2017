package sacred.alliance.magic.action;

import com.game.draco.GameContext;
import com.game.draco.message.request.C0812_RankSelfReqMessage;
import com.game.draco.message.response.C0812_RankSelfRespMessage;

import sacred.alliance.magic.app.rank.RankInfo;
import sacred.alliance.magic.app.rank.RankLogRoleInfo;
import sacred.alliance.magic.app.rank.type.RankLogic;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

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
