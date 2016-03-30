package sacred.alliance.magic.action;

import com.game.draco.GameContext;
import com.game.draco.message.request.C0602_FallListReqMessage;
import com.game.draco.message.response.C0602_FallListRespMessage;

import sacred.alliance.magic.base.FallRespType;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

public class FallListAction extends BaseAction<C0602_FallListReqMessage> {

	@Override
	public Message execute(ActionContext context, C0602_FallListReqMessage reqMsg) {
		try{
			String instanceId = reqMsg.getInstanceId();
			RoleInstance role = this.getCurrentRole(context);
			GameContext.getFallApp().listEntry(instanceId, role);
			return null;
		}catch(Exception e){
			logger.error("",e);
			return new C0602_FallListRespMessage(FallRespType.error.getType(),this.getText(TextId.SYSTEM_ERROR));
		}
		
	}
}
