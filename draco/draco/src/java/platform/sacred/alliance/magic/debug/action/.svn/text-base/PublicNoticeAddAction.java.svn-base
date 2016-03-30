package sacred.alliance.magic.debug.action;

import com.game.draco.GameContext;
import com.game.draco.debug.message.request.C10052_PublicNoticeAddReqMessage;
import com.game.draco.debug.message.response.C10000_StateRespMessage;

import sacred.alliance.magic.base.PublicNoticeType;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.core.action.ActionSupport;

public class PublicNoticeAddAction extends ActionSupport<C10052_PublicNoticeAddReqMessage>{
	
	@Override
	public Message execute(ActionContext context, C10052_PublicNoticeAddReqMessage reqMsg) {
		C10000_StateRespMessage resp = new C10000_StateRespMessage();
		resp.setType((byte) 0);
		resp.setInfo(GameContext.getI18n().getText(TextId.PULIC_NOTICE_ADD_FAIL));
		try{
			boolean result = GameContext.getPublicNoticeApp().addNotice(
					PublicNoticeType.System_Notice, reqMsg.getTitle(), reqMsg.getContent(), reqMsg.getColor());
			if(result){
				resp.setType((byte)1);
				resp.setInfo(GameContext.getI18n().getText(TextId.PULIC_NOTICE_ADD_SUCCESS));
			}
			return resp;
		}catch(Exception e){
			logger.error("debug public notice add error: ",e);
			return resp;
		}
	}

}
