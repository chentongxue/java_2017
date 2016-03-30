package sacred.alliance.magic.debug.action;

import com.game.draco.GameContext;
import com.game.draco.debug.base.DebugHelper;
import com.game.draco.debug.message.request.C10049_RateUpdateReqMessage;
import com.game.draco.debug.message.response.C10051_RateListRespMessage;

import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.core.action.ActionSupport;

public class RateUpdateAction extends ActionSupport<C10049_RateUpdateReqMessage>{
	
	@Override
	public Message execute(ActionContext context, C10049_RateUpdateReqMessage req) {
		C10051_RateListRespMessage resp = new C10051_RateListRespMessage();
		try{
			Result result = GameContext.getRateApp().updateRate(req.getType(), req.getStartTime(), req.getEndTime(), req.getRate(), req.getRate1());
			resp.setType(result.getResult());
			resp.setInfo(result.getInfo());
			if(result.isSuccess()){
				resp.setType((byte) 1);
				resp.setRateList(DebugHelper.getRateList());
			}
			return resp;
		}catch(Exception e){
			this.logger.error("debug rate update error:",e);
			resp.setInfo(GameContext.getI18n().getText(TextId.RATE_UPDATE_ERROR));
			return resp;
		}
	}

}
