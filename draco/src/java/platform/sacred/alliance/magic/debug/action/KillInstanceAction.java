package sacred.alliance.magic.debug.action;

import com.game.draco.GameContext;
import com.game.draco.app.npc.domain.NpcInstance;
import com.game.draco.debug.message.request.C10008_KillInstanceReqMessage;
import com.game.draco.debug.message.response.C10000_StateRespMessage;

import sacred.alliance.magic.app.onlinecenter.OnlineCenter;
import sacred.alliance.magic.base.RoleType;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.constant.RespTypeStatus;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.core.action.ActionSupport;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.RoleInstance;

/*杀死实例*/
public class KillInstanceAction extends ActionSupport<C10008_KillInstanceReqMessage>{
	
	@Override
	public Message execute(ActionContext context, C10008_KillInstanceReqMessage reqMsg) {
		C10000_StateRespMessage resp = new C10000_StateRespMessage();
		resp.setType((byte)RespTypeStatus.FAILURE);
		try{
			String attackInstanceId = reqMsg.getAttackInstanceId();
			String killedInstanceId = reqMsg.getKilledInstanceId();
			OnlineCenter onlineCenter = GameContext.getOnlineCenter();
			RoleInstance roleInstance = onlineCenter.getRoleInstanceByRoleId(attackInstanceId);
			if(roleInstance == null) {
				roleInstance = onlineCenter.getRoleInstanceByRoleId(killedInstanceId);
			}
			if(roleInstance == null) {
				resp.setType((byte)RespTypeStatus.FAILURE);
				resp.setInfo(GameContext.getI18n().getText(TextId.ROLE_OFFLINE_FAIL));
				return resp;
			}
			AbstractRole attackRole = roleInstance.getMapInstance().getAbstractRole(attackInstanceId);
			AbstractRole killedRole = roleInstance.getMapInstance().getAbstractRole(killedInstanceId);
			
			if (killedRole.getRoleType() == RoleType.NPC && ((NpcInstance)killedRole).getOwnerInstance()==null) {
    			RoleInstance instance = onlineCenter.getRoleInstanceByRoleId(attackInstanceId);
        		if(instance != null) {
        			((NpcInstance)killedRole).setOwnerInstance(instance);
        		}
    		}
			killedRole.setCurHP(0);
			GameContext.getBattleApp().killedRole(attackRole,killedRole);
			
			resp.setType((byte)RespTypeStatus.SUCCESS);
			return resp;
		}catch(Exception e){
			logger.error("KillInstanceAction error: ",e);
			resp.setInfo(GameContext.getI18n().getText(TextId.SYSTEM_ERROR));
			return resp;
		}
	}
}
