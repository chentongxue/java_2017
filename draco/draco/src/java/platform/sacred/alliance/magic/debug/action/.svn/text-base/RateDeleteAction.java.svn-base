package sacred.alliance.magic.debug.action;

import com.game.draco.GameContext;
import com.game.draco.debug.base.DebugHelper;
import com.game.draco.debug.message.request.C10050_RateDeleteReqMessage;
import com.game.draco.debug.message.response.C10051_RateListRespMessage;

import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.core.action.ActionSupport;

public class RateDeleteAction extends ActionSupport<C10050_RateDeleteReqMessage>{
	
	@Override
	public Message execute(ActionContext context, C10050_RateDeleteReqMessage req) {
		C10051_RateListRespMessage resp = new C10051_RateListRespMessage();
		try{
			Result result = GameContext.getRateApp().deleteRate(req.getType());
			resp.setType(result.getResult());
			resp.setInfo(result.getInfo());
			if(result.isSuccess()){
				resp.setType((byte) 1);
				resp.setRateList(DebugHelper.getRateList());
			}
			return resp;
		}catch(Exception e){
			this.logger.error("debug rate delete error:",e);
			resp.setInfo(GameContext.getI18n().getText(TextId.RATE_DELETE_ERROR));
			return resp;
		}
	}

}
