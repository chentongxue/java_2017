package sacred.alliance.magic.debug.action;

import com.game.draco.GameContext;
import com.game.draco.debug.message.request.C10059_UserIoSessionReqMessage;
import com.game.draco.debug.message.response.C10059_UserIoSessionRespMessage;

import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.core.action.ActionSupport;
import sacred.alliance.magic.vo.RoleInstance;

public class UserIoSessionAction extends ActionSupport<C10059_UserIoSessionReqMessage> {

	@Override
	public Message execute(ActionContext context,
			C10059_UserIoSessionReqMessage reqMsg) {
		C10059_UserIoSessionRespMessage respMsg = new C10059_UserIoSessionRespMessage();
		String userId = reqMsg.getUserId();
		if(null == userId){
			return respMsg ; 
		}
		Long ioId = GameContext.getOnlineCenter().getIoId(userId);
		RoleInstance role = GameContext.getOnlineCenter().getRoleInstanceByUserId(userId);
		
		respMsg.setUserId(userId);
		respMsg.setIoId(-1);
		if(null != ioId){
			respMsg.setIoId(ioId.longValue());
		}
		if(null != role){
			respMsg.setRoleId(role.getIntRoleId());
			respMsg.setRoleName(role.getRoleName());
			respMsg.setUserName(role.getUserName());
		}
		return respMsg;
	}

}
