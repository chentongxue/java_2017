package sacred.alliance.magic.action;

import com.game.draco.GameContext;
import com.game.draco.message.request.C2343_TitleCancelReqMessage;
import com.game.draco.message.response.C2343_TitleCancelRespMessage;

import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;
public class TitleCancelAction extends BaseAction<C2343_TitleCancelReqMessage>{

	@Override
	public Message execute(ActionContext context, C2343_TitleCancelReqMessage req) {
		C2343_TitleCancelRespMessage resp = new C2343_TitleCancelRespMessage();
		try{
			RoleInstance role = this.getCurrentRole(context);
			if(null == role){
				return null;
			}
			Status status = GameContext.getTitleApp().cancelTitle(role, req.getTitleId());
			if(!status.isSuccess()){
				resp.setType(Status.FAILURE.getInnerCode());
				resp.setInfo(status.getTips());
				return resp;
			}
			resp.setTitleId(req.getTitleId());
			resp.setType(Status.SUCCESS.getInnerCode());
			
		}catch(Exception e){
			logger.error("",e);
			resp.setInfo(this.getText(TextId.SYSTEM_ERROR));
		}
		return resp;
	}
}
