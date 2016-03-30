package sacred.alliance.magic.debug.action;

import com.game.draco.GameContext;
import com.game.draco.debug.message.request.C10055_PublicNoticeListReqMessage;
import com.game.draco.debug.message.response.C10055_PublicNoticeListRespMessage;

import sacred.alliance.magic.base.PublicNoticeType;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.core.action.ActionSupport;
import sacred.alliance.magic.domain.PublicNotice;

public class PublicNoticeListAction extends ActionSupport<C10055_PublicNoticeListReqMessage>{
	
	@Override
	public Message execute(ActionContext arg0, C10055_PublicNoticeListReqMessage reqMessage) {
		C10055_PublicNoticeListRespMessage resp = new C10055_PublicNoticeListRespMessage();
		try{
			PublicNotice notice = GameContext.getPublicNoticeApp().getNotice(PublicNoticeType.System_Notice);
			if(null == notice){
				resp.setType((byte) 0);
				resp.setInfo(GameContext.getI18n().getText(TextId.PULIC_NOTICE_NO_EXSIT));
				return resp;
			}
			resp.setType((byte) 1);
			resp.setTitle(notice.getTitle());
			resp.setContent(notice.getContent());
			resp.setUpdateTime(notice.getUpdateTime());
			resp.setColor(notice.getColor());
			return resp;
		}catch(Exception e){
			logger.error("debug public notice list error: ",e);
			return resp;
		}
	}

}
