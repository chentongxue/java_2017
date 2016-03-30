package sacred.alliance.magic.debug.action;

import com.game.draco.GameContext;
import com.game.draco.debug.message.request.C10053_PublicNoticeUpdateReqMessage;
import com.game.draco.debug.message.response.C10000_StateRespMessage;

import sacred.alliance.magic.base.PublicNoticeType;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.core.action.ActionSupport;

public class PublicNoticeUpdateAction extends ActionSupport<C10053_PublicNoticeUpdateReqMessage>{
	
	@Override
	public Message execute(ActionContext arg0, C10053_PublicNoticeUpdateReqMessage reqMsg) {
		C10000_StateRespMessage resp = new C10000_StateRespMessage();
		resp.setType((byte) 0);
		resp.setInfo(GameContext.getI18n().getText(TextId.PULIC_NOTICE_MODIFY_FAIL));
		try{
			boolean result = GameContext.getPublicNoticeApp().modifyNotice(
					PublicNoticeType.System_Notice, reqMsg.getTitle(), reqMsg.getContent(), reqMsg.getColor());
			if(result){
				resp.setType((byte)1);
				resp.setInfo(GameContext.getI18n().getText(TextId.PULIC_NOTICE_MODIFY_SUCCESS));
			}
			return resp;
		}catch(Exception e){
			logger.error("debug public notice update error: ",e);
			return resp;
		}
	}

}
