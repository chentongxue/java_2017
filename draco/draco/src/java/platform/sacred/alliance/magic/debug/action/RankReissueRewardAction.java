package sacred.alliance.magic.debug.action;

import com.game.draco.GameContext;
import com.game.draco.debug.message.request.C10058_RankReissueRewardReqMessage;
import com.game.draco.debug.message.response.C10000_StateRespMessage;

import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.core.action.ActionSupport;

public class RankReissueRewardAction extends ActionSupport<C10058_RankReissueRewardReqMessage>{
	
	@Override
	public Message execute(ActionContext context, C10058_RankReissueRewardReqMessage req) {
		C10000_StateRespMessage resp = new C10000_StateRespMessage();
		Result result = GameContext.getRankApp().rewardByMailFromGM(req.getRankId(), req.getDateStr());
		resp.setType(result.getResult());
		resp.setInfo(result.getInfo());
		return resp;
	}

}
