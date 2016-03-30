package sacred.alliance.magic.action;

import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.util.StringUtil;

import com.game.draco.GameContext;
import com.game.draco.message.request.C0113_RoleModifyNameReqMessage;
import com.game.draco.message.response.C0113_RoleModifyNameRespMessage;

public class RoleModifyNameAction extends BaseAction<C0113_RoleModifyNameReqMessage> {

	@Override
	public Message execute(ActionContext context, C0113_RoleModifyNameReqMessage reqMsg) {
		C0113_RoleModifyNameRespMessage resp = new C0113_RoleModifyNameRespMessage();
		try {
			String roleName = StringUtil.replaceNewLine(reqMsg.getRoleName());
			Result result = GameContext.getUserRoleApp().modifyRoleName(this.getUserId(context), 
					String.valueOf(reqMsg.getRoleId()), roleName);
			if(!result.isSuccess()){
				resp.setType((byte)0);
				resp.setInfo(result.getInfo());
				return resp;
			}
			resp.setType((byte)1);
			resp.setInfo(Status.Role_Modify_Success.getTips());
			resp.setRoleId(reqMsg.getRoleId());
			resp.setRoleName(roleName);
			return resp;
		} catch (Exception e) {
			logger.error("",e);
			resp.setType((byte)0);
			resp.setInfo(Status.Role_Modify_Sys_Error.getTips());
			return resp;
		}
	}


}
