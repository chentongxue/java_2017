package sacred.alliance.magic.shutdown.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.core.action.ActionSupport;

import com.game.draco.GameContext;
import com.game.draco.debug.message.request.C10072_ShutdownRejectReqMessage;
import com.game.draco.debug.message.response.C10072_ShutdownRejectRespMessage;


public class RejectAction extends ActionSupport<C10072_ShutdownRejectReqMessage>{
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Override
	public Message execute(ActionContext context, C10072_ShutdownRejectReqMessage reqMsg) {
		C10072_ShutdownRejectRespMessage respMsg = new C10072_ShutdownRejectRespMessage();
		try{
			if(1 == reqMsg.getType()){
				GameContext.getShutDownApp().setRefuseRequest();
			}else{
				GameContext.getShutDownApp().setAcceptRequest();
			}
			respMsg.setType((byte)1);
		}catch(Exception e){
			logger.error("",e);
			respMsg.setType((byte)0);
		}
		return respMsg;
	}
}
