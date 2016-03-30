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
			byte type = reqMsg.getType();
			String rolekey = reqMsg.getRoleName();
			if(Util.isEmpty(rolekey)){
				resp.setTips(Status.Sys_Param_Error.getTips());
				return resp;
			}
			RoleInstance role = null;
			//0表示是角色名，1表示是角色ID，2表示是魔方用户ID，3表示是渠道用户ID
			switch (type) {
			case 0:
				role = GameContext.getUserRoleApp().getRoleByRoleName(rolekey);
				break;
			case 1:
				role = GameContext.getUserRoleApp().getRoleByRoleId(rolekey);
				break;
			case 2:
				role = GameContext.getOnlineCenter().getRoleInstanceByUserId(rolekey);
				break;
			case 3:
				//TODO:onlinecenter没有相关接口
				//role = GameContext.getOnlineCenter().getRoleInstanceByChannelUserId(reqMsg.getChannelId(), rolekey);
				break;
			}
			if(null == role){
				resp.setTips(Status.Role_Not_Exist.getTips());
				return resp;
			}
			resp.setResult((byte) 1);
			resp.setUserId(role.getUserId());
			resp.setUserName(role.getUserName());
			resp.setRoleId(role.getRoleId());
			resp.setRoleName(role.getRoleName());
			resp.setChannelId(role.getChannelId());
			resp.setChannelUserId(role.getChannelUserId());
			resp.setChannelAccessToken(role.getChannelAccessToken());
			resp.setChannelRefreshToken(role.getChannelRefreshToken());
			return resp;
		}catch(Exception e){
			resp.setTips(Status.Sys_Error.getTips());
			return resp;
		}
	}
	
	
}
