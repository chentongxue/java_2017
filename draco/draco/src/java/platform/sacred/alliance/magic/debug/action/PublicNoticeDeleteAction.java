package sacred.alliance.magic.debug.action;

import com.game.draco.GameContext;
import com.game.draco.debug.message.request.C10054_PublicNoticeDeleteReqMessage;
import com.game.draco.debug.message.response.C10000_StateRespMessage;

import sacred.alliance.magic.base.PublicNoticeType;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.core.action.ActionSupport;

public class PublicNoticeDeleteAction extends ActionSupport<C10054_PublicNoticeDeleteReqMessage>{
	
	@Override
	public Message execute(ActionContext arg0, C10054_PublicNoticeDeleteReqMessage reqMsg) {
		C10000_StateRespMessage resp = new C10000_StateRespMessage();
		resp.setInfo(GameContext.getI18n().getText(TextId.PULIC_NOTICE_DELETE_FAIL));
		try{
			boolean result = GameContext.getPublicNoticeApp().deleteNotice(PublicNoticeType.System_Notice);
			if(result){
				resp.setType((byte)1);
				resp.setInfo(GameContext.getI18n().getText(TextId.PULIC_NOTICE_DELETE_SUCCESS));
			}
			return resp;
		}catch(Exception e){
			logger.error("debug public notice delete error: ",e);
			return resp;
		}
	}

}
