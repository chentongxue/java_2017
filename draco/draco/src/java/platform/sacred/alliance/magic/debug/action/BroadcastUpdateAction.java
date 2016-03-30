package sacred.alliance.magic.debug.action;

import com.game.draco.GameContext;
import com.game.draco.debug.base.DebugHelper;
import com.game.draco.debug.message.request.C10024_BroadcastUpdateReqMessage;
import com.game.draco.debug.message.response.C10021_BroadcastListRespMessage;

import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.core.action.ActionSupport;

public class BroadcastUpdateAction extends ActionSupport<C10024_BroadcastUpdateReqMessage>{
	
	@Override
	public Message execute(ActionContext arg0, C10024_BroadcastUpdateReqMessage req) {
		C10021_BroadcastListRespMessage resp = new C10021_BroadcastListRespMessage();
		try{
			int id = req.getId();
			long beginTime = req.getBeginTime();
			long endTime = req.getEndTime();
			String content = req.getContent();
			int gapTime = req.getTimeGap();
			byte state = req.getState();
			GameContext.getAnnounceApp().updateAccounce(id,content,beginTime,endTime,gapTime, state);
			resp.setType((byte) 1);
			resp.setBroadcastList(DebugHelper.getBroadcastList());
			return resp;
		}catch(Exception e){
			logger.error("debug broadcast update error: ",e);
			return resp;
		}
	}

}
