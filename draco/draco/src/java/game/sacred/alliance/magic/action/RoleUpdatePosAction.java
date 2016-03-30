package sacred.alliance.magic.action;

import com.game.draco.GameContext;
import com.game.draco.message.request.C0323_RoleUpdatePosReqMessage;
import com.game.draco.message.response.C0323_RoleUpdatePosRespMessage;

import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.MapInstance;
import sacred.alliance.magic.vo.RoleInstance;

public class RoleUpdatePosAction extends BaseAction<C0323_RoleUpdatePosReqMessage> {

	@Override
	public Message execute(ActionContext context, C0323_RoleUpdatePosReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null;
		}
		String roleId = String.valueOf(reqMsg.getRoleId());
		RoleInstance attacker = GameContext.getOnlineCenter().getRoleInstanceByRoleId(roleId);
		if(null == attacker){
			return null;
		}
		//判断是不是同一个地图不处理
		MapInstance roleMapIns = role.getMapInstance();
		MapInstance attackerMapIns = attacker.getMapInstance();
		if(roleMapIns == null || attackerMapIns == null 
				|| !(roleMapIns.getInstanceId().equals(attackerMapIns.getInstanceId()))){
			return null;
		}
		C0323_RoleUpdatePosRespMessage respMsg = new C0323_RoleUpdatePosRespMessage();
		respMsg.setRoleId(attacker.getIntRoleId());
		respMsg.setMapX((short)attacker.getMapX());
		respMsg.setMapY((short)attacker.getMapY());
		return respMsg;
	}

}
