package sacred.alliance.magic.fee.action;

import com.game.draco.GameContext;

import platform.message.request.C6000_GameRolePayArgsReqMessage;
import platform.message.response.C6000_GameRolePayArgsRespMessage;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.core.action.ActionSupport;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

public class MofpaySearchRoleInfoAction extends ActionSupport<C6000_GameRolePayArgsReqMessage> {
	
	@Override
	public Message execute(ActionContext context, C6000_GameRolePayArgsReqMessage reqMsg){		
		C6000_GameRolePayArgsRespMessage resp = new C6000_GameRolePayArgsRespMessage();
		resp.setResult((byte) 0);
		try{
			String roleName = reqMsg.getRoleName();
			if(Util.isEmpty(roleName)){
				resp.setTips(Status.Sys_Param_Error.getTips());
				return resp;
			}
			RoleInstance role = GameContext.getUserRoleApp().getRoleByRoleName(roleName);
			if(null == role){
				resp.setTips(Status.Role_Not_Exist.getTips());
				return resp;
			}
			resp.setResult((byte) 1);
			resp.setUserId(role.getUserId());
			resp.setUserName(role.getUserName());
			resp.setRoleId(role.getRoleId());
			resp.setRoleName(roleName);
			resp.setChannelId(role.getChannelId());
			resp.setChannelUserId(role.getChannelUserId());
			return resp;
		}catch(Exception e){
			resp.setTips(Status.Sys_Error.getTips());
			return resp;
		}
	}
	
	
}
