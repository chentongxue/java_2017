package sacred.alliance.magic.debug.action;

import com.game.draco.GameContext;
import com.game.draco.debug.message.request.C10032_MailRecoupDeleteReqMessage;
import com.game.draco.debug.message.response.C10000_StateRespMessage;

import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.constant.RespTypeStatus;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.core.action.ActionSupport;

public class MailRecoupDeleteAction extends ActionSupport<C10032_MailRecoupDeleteReqMessage>{
	
	@Override
	public Message execute(ActionContext arg0, C10032_MailRecoupDeleteReqMessage reqMsg) {
		C10000_StateRespMessage resp = new C10000_StateRespMessage();
		try{
			GameContext.getRecoupApp().deleteRecoup(reqMsg.getId());
			resp.setType((byte)RespTypeStatus.SUCCESS);
			return resp;
		}catch(Exception e){
			logger.error("MailRecoupDeleteAction error: ",e);
			resp.setType((byte)RespTypeStatus.FAILURE);
			resp.setInfo(GameContext.getI18n().getText(TextId.SYSTEM_ERROR));
			return resp;
		}
	}
	
}
