package sacred.alliance.magic.debug.action;

import java.util.ArrayList;
import java.util.List;

import com.game.draco.GameContext;
import com.game.draco.debug.message.request.C10015_FrozenRoleReqMessage;
import com.game.draco.debug.message.response.C10015_FrozenRoleRespMessage;
import com.game.draco.message.internal.C0057_RoleFrozenInternalMessage;

import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.core.action.ActionSupport;
import sacred.alliance.magic.core.exception.ServiceException;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

/**
 * 隔离角色功能
 *
 */
public class FrozenRoleAction extends ActionSupport<C10015_FrozenRoleReqMessage>{
	
	@Override
	public Message execute(ActionContext context, C10015_FrozenRoleReqMessage req) {
		C10015_FrozenRoleRespMessage resp = new C10015_FrozenRoleRespMessage();
		String[] roleInfos = req.getRoleInfos();
		if(null == roleInfos || 0 == roleInfos.length){
			return resp;
		}
		List<String> failedList = new ArrayList<String>();
		int infoType = req.getInfoType();
		for(String roleInfo : roleInfos){
			if(Util.isEmpty(roleInfo)){
				continue;
			}
			RoleInstance role = null;
			try {
				if(0 == infoType){
					role = GameContext.getUserRoleApp().getRoleByRoleId(roleInfo);
				}else if(1 == infoType){
					role = GameContext.getUserRoleApp().getRoleByRoleName(roleInfo);
				}
			} catch (ServiceException e) {
				this.logger.error("FrozenRoleAction error: ", e);
			}
			if(null == role){
				failedList.add(roleInfo);
				continue;
			}
			//放到单用户单线程执行
			C0057_RoleFrozenInternalMessage reqMsg = new C0057_RoleFrozenInternalMessage();
			reqMsg.setRole(role);
			reqMsg.setAdminReqMsg(req);
			GameContext.getUserSocketChannelEventPublisher().publish(role.getUserId(),reqMsg,context.getSession());
		}
		int size = failedList.size();
		if(size > 0){
			String[] failedRoleInfos = new String[size];
			for(int i=0; i<size; i++){
				failedRoleInfos[i] = failedList.get(i);
			}
			resp.setFailedRoleInfos(failedRoleInfos);
		}
		return resp;
	}
}
