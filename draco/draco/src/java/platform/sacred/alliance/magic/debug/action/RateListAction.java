package sacred.alliance.magic.debug.action;

import com.game.draco.debug.base.DebugHelper;
import com.game.draco.debug.message.request.C10051_RateListReqMessage;
import com.game.draco.debug.message.response.C10051_RateListRespMessage;

import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.core.action.ActionSupport;

public class RateListAction extends ActionSupport<C10051_RateListReqMessage>{
	
	@Override
	public Message execute(ActionContext context, C10051_RateListReqMessage req) {
		C10051_RateListRespMessage resp = new C10051_RateListRespMessage();
		try{
			resp.setRateList(DebugHelper.getRateList());
			return resp;
		}catch(Exception e){
			this.logger.error("debug rate list error:",e);
			return resp;
		}
	}

}
