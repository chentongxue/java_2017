package sacred.alliance.magic.action;

import com.game.draco.GameContext;
import com.game.draco.message.request.C3301_PublicNoticeReqMessage;
import com.game.draco.message.response.C3301_PublicNoticeRespMessage;

import sacred.alliance.magic.base.PublicNoticeType;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.domain.PublicNotice;
import sacred.alliance.magic.vo.RoleInstance;

public class PublicNoticeAction extends BaseAction<C3301_PublicNoticeReqMessage> {
	
	@Override
	public Message execute(ActionContext context, C3301_PublicNoticeReqMessage req) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null ;
		}
		PublicNotice sysNotice = GameContext.getPublicNoticeApp().getNotice(PublicNoticeType.System_Notice);
		C3301_PublicNoticeRespMessage resp = new C3301_PublicNoticeRespMessage();
		if(null != sysNotice){
			resp.setSystemNotice(sysNotice.getColorContent());
		}
		return resp;
	}
}
