package sacred.alliance.magic.debug.action;

import java.util.List;

import com.game.draco.GameContext;
import com.game.draco.app.rank.RankInitResult;
import com.game.draco.debug.message.request.C10057_RankInitReqMessage;
import com.game.draco.debug.message.response.C10057_RankInitRespMessage;

import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.core.action.ActionSupport;
import sacred.alliance.magic.util.Util;

public class RankInitAction extends ActionSupport<C10057_RankInitReqMessage>{
	
	@Override
	public Message execute(ActionContext context, C10057_RankInitReqMessage req) {
		C10057_RankInitRespMessage resp = new C10057_RankInitRespMessage();
		RankInitResult result = GameContext.getRankApp().initLogDataFormDB(req.getRankIds());
		resp.setSucessRankIds(this.listToArr(result.getSuccessList()));
		resp.setFailureRankIds(this.listToArr(result.getFailureList()));
		return resp;
	}
	
	private int[] listToArr(List<Integer> list){
		if(Util.isEmpty(list)){
			return new int[0] ;
		}
		int[] value = new int[list.size()] ;
		int index = 0 ;
		for(Integer i : list){
			value[index ++] = i ;
		}
		return value ;
	}

}
