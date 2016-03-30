package sacred.alliance.magic.debug.action;

import com.game.draco.GameContext;
import com.game.draco.app.mail.type.MailFreezeType;
import com.game.draco.debug.message.request.C10039_MailModifyFreezeReqMessage;
import com.game.draco.debug.message.response.C10000_StateRespMessage;

import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.core.action.ActionSupport;

public class MailModifyFreezeAction extends ActionSupport<C10039_MailModifyFreezeReqMessage>{
	
	@Override
	public Message execute(ActionContext arg0, C10039_MailModifyFreezeReqMessage reqMsg) {
		C10000_StateRespMessage resp = new C10000_StateRespMessage();
		try{
			MailFreezeType freezeType = MailFreezeType.get(reqMsg.getFreezeStatus());
			Result result = GameContext.getMailApp().modifyMailFreeze(reqMsg.getRoleId(), reqMsg.getMailId(), freezeType);
			resp.setType(result.getResult());
			resp.setInfo(result.getInfo());
			return resp;
		}catch(Exception e){
			this.logger.error(this.getClass().getName() + ".execute error: ", e);
			return resp;
		}
	}
	
}
