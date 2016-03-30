package sacred.alliance.magic.action;

import java.util.List;

import sacred.alliance.magic.constant.RespTypeStatus;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.request.C0112_RoleDeleteReqMessage;
import com.game.draco.message.response.C0002_ErrorRespMessage;
import com.game.draco.message.response.C4999_UserLoginSafeRespMessage;

public class RoleDeleteAction extends BaseAction<C0112_RoleDeleteReqMessage>{

	@Override
	public Message execute(ActionContext context, C0112_RoleDeleteReqMessage req) {
		int roleId = req.getRoleId();
		String userId = req.getUserId();
		if(roleId <= 0 || Util.isEmpty(userId)){
			return null;
		}
		
		try{
			boolean bool = GameContext.getRoleService().deleteRole(roleId);
			//获得角色相关信息
			C4999_UserLoginSafeRespMessage respMsg = new C4999_UserLoginSafeRespMessage();
			List<RoleInstance> roleList = GameContext.getBaseDAO().selectList(RoleInstance.class, "userId", userId);
			short time = (short)GameContext.getHeartBeatConfig().getHeartBeat();
			//respMsg.setTime(time);
			respMsg.setType((byte)RespTypeStatus.SUCCESS);
			//session.write(respMsg);
			context.getSession().write(respMsg);
			
			if(bool){
				return new C0002_ErrorRespMessage(req.getCommandId(), Status.Role_Delete_Success.getTips());
			}
			return new C0002_ErrorRespMessage(req.getCommandId(), Status.Role_Delete_Fail.getTips());
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}

}
