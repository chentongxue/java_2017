package sacred.alliance.magic.action;

import com.game.draco.GameContext;
import com.game.draco.message.request.C0106_RoleReplaceReqMessage;
import com.game.draco.message.response.C0106_RoleReplaceRespMessage;

import sacred.alliance.magic.constant.RespTypeStatus;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

public class RoleReplaceAction extends BaseAction<C0106_RoleReplaceReqMessage> {

	@Override
	public Message execute(ActionContext context, C0106_RoleReplaceReqMessage reqMsg) {
		C0106_RoleReplaceRespMessage respMsg = new C0106_RoleReplaceRespMessage();
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			respMsg.setType(RespTypeStatus.FAILURE);
			respMsg.setInfo(Status.FAILURE.getTips());
			return respMsg;
		}
		//蔚絞ヶ蚚誧狟盄,等悵厥蟀諉
		GameContext.getOnlineCenter().offlineWithNetIO(context.getSession());
		respMsg.setType(RespTypeStatus.SUCCESS);
		return respMsg ;
	}

}
