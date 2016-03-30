package sacred.alliance.magic.debug.action;

import com.game.draco.GameContext;
import com.game.draco.debug.message.request.C10065_DoordogRoleToVerifyReqMessage;
import com.game.draco.debug.message.response.C10000_StateRespMessage;

import sacred.alliance.magic.constant.RespTypeStatus;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.core.action.ActionSupport;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

public class DoordogRoleToVerifyAction extends ActionSupport<C10065_DoordogRoleToVerifyReqMessage>{

	@Override
	public Message execute(ActionContext context,
			C10065_DoordogRoleToVerifyReqMessage reqMsg) {
		C10000_StateRespMessage respMsg = new C10000_StateRespMessage() ;
		respMsg.setType(RespTypeStatus.FAILURE);
		String roleId = reqMsg.getRoleId() ;
		if(Util.isEmpty(roleId)){
			respMsg.setInfo("ERROR:roleId is empty");
			return respMsg ;
		}
		RoleInstance role = GameContext.getOnlineCenter().getRoleInstanceByRoleId(roleId);
		if(null == role){
			respMsg.setInfo("ERROR:role not online");
			return respMsg ;
		}
		boolean result = GameContext.getDoorDogApp().flagToVerify(role, (byte)-1);
		if(!result){
			respMsg.setInfo("FAILURE:may be gen the question error");
			return respMsg ;
		}
		respMsg.setType(RespTypeStatus.SUCCESS);
		return respMsg;
	}

}
