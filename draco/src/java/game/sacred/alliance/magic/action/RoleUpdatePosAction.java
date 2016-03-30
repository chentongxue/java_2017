package sacred.alliance.magic.action;

import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.MapInstance;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.request.C0323_RoleUpdatePosReqMessage;
import com.game.draco.message.response.C0323_RoleUpdatePosRespMessage;

public class RoleUpdatePosAction extends BaseAction<C0323_RoleUpdatePosReqMessage> {

	@Override
	public Message execute(ActionContext context, C0323_RoleUpdatePosReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null;
		}
		//判断是不是同一个地图不处理
		MapInstance roleMapIns = role.getMapInstance();
		if(null == roleMapIns){
			return null ;
		}
		int  roleId = reqMsg.getRoleId() ;
		AbstractRole target = null ;
		if(roleId > 0){
			target = roleMapIns.getRoleInstance(String.valueOf(roleId));
		}else {
			target = roleMapIns.getNpcInstance(String.valueOf(roleId));
		}
		if(null == target){
			return null;
		}
		
		MapInstance attackerMapIns = target.getMapInstance();
		if(attackerMapIns == null 
				|| !(roleMapIns.getInstanceId().equals(attackerMapIns.getInstanceId()))){
			return null;
		}
		C0323_RoleUpdatePosRespMessage respMsg = new C0323_RoleUpdatePosRespMessage();
		respMsg.setRoleId(target.getIntRoleId());
		respMsg.setMapX((short)target.getMapX());
		respMsg.setMapY((short)target.getMapY());
		return respMsg;
	}

}
