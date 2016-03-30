package sacred.alliance.magic.debug.action;

import com.game.draco.GameContext;
import com.game.draco.debug.base.DebugHelper;
import com.game.draco.debug.message.request.C10048_RateAddReqMessage;
import com.game.draco.debug.message.response.C10051_RateListRespMessage;

import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.core.action.ActionSupport;

public class RateAddAction extends ActionSupport<C10048_RateAddReqMessage>{
	
	@Override
	public Message execute(ActionContext context, C10048_RateAddReqMessage req) {
		C10051_RateListRespMessage resp = new C10051_RateListRespMessage();
		try{
			Result result = GameContext.getRateApp().addRate(req.getType(), req.getStartTime(), req.getEndTime(), req.getRate(), req.getRate1());
			resp.setType(result.getResult());
			resp.setInfo(result.getInfo());
			if(result.isSuccess()){
				resp.setType((byte) 1);
				resp.setRateList(DebugHelper.getRateList());
			}
			return resp;
		}catch(Exception e){
			this.logger.error("debug rate add error:",e);
			resp.setInfo(GameContext.getI18n().getText(TextId.RATE_ADD_ERROR));
			return resp;
		}
	}

}
