package sacred.alliance.magic.debug.action;

import com.game.draco.debug.base.DebugHelper;
import com.game.draco.debug.message.request.C10021_BroadGetListReqMessage;
import com.game.draco.debug.message.response.C10021_BroadcastListRespMessage;

import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.core.action.ActionSupport;

public class BroadGetListAction extends ActionSupport<C10021_BroadGetListReqMessage>{
	
	@Override
	public Message execute(ActionContext arg0, C10021_BroadGetListReqMessage reqMessage) {
		C10021_BroadcastListRespMessage resp = new C10021_BroadcastListRespMessage();
		try{
			resp.setBroadcastList(DebugHelper.getBroadcastList());
			return resp;
		}catch(Exception e){
			logger.error("debug broadcast list error: ",e);
			return resp;
		}
	}

}
