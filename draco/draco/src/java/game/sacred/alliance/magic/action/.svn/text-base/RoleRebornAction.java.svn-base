package sacred.alliance.magic.action;

import com.game.draco.GameContext;
import com.game.draco.message.request.C2001_RoleRebornReqMessage;
import com.game.draco.message.response.C0002_ErrorRespMessage;
import com.game.draco.message.response.C2001_RoleRebornRespMessage;

import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.MapInstance;
import sacred.alliance.magic.vo.RoleInstance;

public class RoleRebornAction extends BaseAction<C2001_RoleRebornReqMessage> {
	
	@Override
	public Message execute(ActionContext context, C2001_RoleRebornReqMessage req) {
		C2001_RoleRebornRespMessage resp = new C2001_RoleRebornRespMessage();
		C0002_ErrorRespMessage erm = new C0002_ErrorRespMessage();
		erm.setReqCmdId(req.getCommandId());
		try {
			RoleInstance role = this.getCurrentRole(context);
			if(!role.isDeath()){
				resp.setType((byte) 1);
				role.getHasSendDeathMsg().compareAndSet(true, false);
				return resp;
			}
			Result result = GameContext.getRoleRebornApp().roleReborn(role, req.getType());
			resp.setType(result.getResult());
			resp.setInfo(result.getInfo());
			MapInstance map = role.getMapInstance();
			if(null != map){
				map.roleReborn(role);
			}
			return resp;
		} catch (Exception e) {
			logger.error("",e);
			erm.setInfo(this.getText(TextId.SYSTEM_ERROR));
			return erm;
		}
	}
}
