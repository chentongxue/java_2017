package sacred.alliance.magic.action;
import com.game.draco.GameContext;
import com.game.draco.message.request.C0603_FallPickupReqMessage;
import com.game.draco.message.response.C0603_FallPickupRespMessage;

import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.constant.RespTypeStatus;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

public class FallPickupAction extends BaseAction<C0603_FallPickupReqMessage> {

	@Override
	public Message execute(ActionContext context , C0603_FallPickupReqMessage reqMsg) {
		try{
			String instanceId = reqMsg.getInstanceId();
			RoleInstance role = this.getCurrentRole(context);
			
			GameContext.getFallApp().pickupEntry(instanceId, role, reqMsg.getItemId());
			return null;
	
		}catch(Exception e){
			logger.error("",e);
			C0603_FallPickupRespMessage respMsg = new C0603_FallPickupRespMessage();
			respMsg.setInstanceId(reqMsg.getInstanceId());
			respMsg.setItemId(reqMsg.getItemId());
			respMsg.setStatus(RespTypeStatus.FAILURE);
			respMsg.setInfo(this.getText(TextId.SYSTEM_ERROR));
			return respMsg;
		}
	}
}
