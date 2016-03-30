package sacred.alliance.magic.debug.action;

import com.game.draco.GameContext;
import com.game.draco.debug.base.DebugHelper;
import com.game.draco.debug.message.request.C10023_BroadcastOperReqMessage;
import com.game.draco.debug.message.response.C10021_BroadcastListRespMessage;

import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.core.action.ActionSupport;
import sacred.alliance.magic.domain.SysAnnouncement;

public class BroadcastOperAction extends ActionSupport<C10023_BroadcastOperReqMessage>{
	
	@Override
	public Message execute(ActionContext arg0, C10023_BroadcastOperReqMessage req) {
		C10021_BroadcastListRespMessage resp = new C10021_BroadcastListRespMessage();
		try{
			int id = req.getId();
			byte state = req.getType();
			SysAnnouncement annou = GameContext.getAnnounceApp().getAnnounce(id);
			if(annou == null){
				return resp;
			}
			if(state == 1){
				GameContext.getAnnounceApp().deleAnnounce(id);
			}else{
				GameContext.getAnnounceApp().updateAccounce(id, annou.getContent(), annou.getStartTime().getTime(),annou.getEndTime().getTime(),annou.getTimeGap(), state);
			}
			resp.setType((byte) 1);
			resp.setBroadcastList(DebugHelper.getBroadcastList());
			return resp;
		}catch(Exception e){
			logger.error("debug broadcast oper error: ",e);
			return resp;
		}
	}

}
