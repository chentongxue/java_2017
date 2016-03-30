package sacred.alliance.magic.debug.action;

import com.game.draco.GameContext;
import com.game.draco.debug.message.request.C10063_AdminControlReqMessage;
import com.game.draco.debug.message.response.C10063_AdminControlRespMessage;

import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.core.action.ActionSupport;

public class AdminControlAction extends ActionSupport<C10063_AdminControlReqMessage>{

	@Override
	public Message execute(ActionContext context, C10063_AdminControlReqMessage reqMsg) {
		C10063_AdminControlRespMessage respMsg = new C10063_AdminControlRespMessage();
		try {
			String result = GameContext.getAdminApp().control(reqMsg.getJarName(), reqMsg.getArgs());
			respMsg.setInfo(result);
		} catch (Exception ex) {
			logger.error("AdminControlAction error", ex);
			respMsg.setInfo("ERROR");
		}
		return respMsg;
	}

}
