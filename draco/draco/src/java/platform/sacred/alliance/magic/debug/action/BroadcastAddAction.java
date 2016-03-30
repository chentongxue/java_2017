package sacred.alliance.magic.debug.action;

import java.util.Date;

import com.game.draco.GameContext;
import com.game.draco.debug.base.DebugHelper;
import com.game.draco.debug.message.request.C10022_BroadcastAddReqMessage;
import com.game.draco.debug.message.response.C10021_BroadcastListRespMessage;

import sacred.alliance.magic.base.AnnouncementType;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.core.action.ActionSupport;
import sacred.alliance.magic.domain.SysAnnouncement;

public class BroadcastAddAction extends ActionSupport<C10022_BroadcastAddReqMessage>{
	
	@Override
	public Message execute(ActionContext context, C10022_BroadcastAddReqMessage req) {
		C10021_BroadcastListRespMessage resp = new C10021_BroadcastListRespMessage();
		try{
			long beginTime = req.getBeginTime();
			long endTime = req.getEndTime();
			String content = req.getContent();
			int gapTime = req.getGapTime();
			byte state = req.getState();
			SysAnnouncement annou = new SysAnnouncement(content,new Date(beginTime),new Date(endTime),gapTime,state,AnnouncementType.GM.getType());
			SysAnnouncement annouTemp = GameContext.getAnnounceApp().insertAnnounce(annou);
			if(annouTemp != null){
				resp.setType((byte)1);
				resp.setBroadcastList(DebugHelper.getBroadcastList());
			}else{
				resp.setInfo(GameContext.getI18n().getText(TextId.BROADCAST_ADD_FAIL));
			}
			return resp;
		}catch(Exception e){
			logger.error("debug broadcast add error: ",e);
			resp.setInfo(GameContext.getI18n().getText(TextId.BROADCAST_ADD_FAIL));
			return resp;
		}
	}

}
