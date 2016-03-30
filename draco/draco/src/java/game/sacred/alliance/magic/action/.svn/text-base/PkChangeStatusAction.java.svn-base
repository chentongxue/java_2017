package sacred.alliance.magic.action;

import java.text.MessageFormat;

import com.game.draco.GameContext;
import com.game.draco.message.request.C1521_PkChangeStatusReqMessage;
import com.game.draco.message.response.C1521_PkChangeStatusRespMessage;

import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.base.RolePkStatus;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

public class PkChangeStatusAction extends BaseAction<C1521_PkChangeStatusReqMessage> {

	@Override
	public Message execute(ActionContext context, C1521_PkChangeStatusReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null;
		}
		
		byte status = reqMsg.getStatus();
		C1521_PkChangeStatusRespMessage resp = new C1521_PkChangeStatusRespMessage();
		Result result = GameContext.getPkApp().changePkStatus(role, status);
		resp.setStatus(status);
		
		if(!result.isSuccess()){
			resp.setInfo(result.getInfo());
			return resp;
		}
		resp.setType((byte) 1);
		resp.setInfo(this.messageFormat(TextId.PK_STATUS_MAP_CHANGE_MESSAGE,RolePkStatus.getRolePkStatus(status).getName()));
		return resp;
	}
}
