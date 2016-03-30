package sacred.alliance.magic.debug.action;

import java.util.ArrayList;
import java.util.List;

import com.game.draco.GameContext;
import com.game.draco.debug.message.item.RolesItem;
import com.game.draco.debug.message.request.C10037_UserRoleListReqMessage;
import com.game.draco.debug.message.response.C10037_UserRoleListRespMessage;

import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.core.action.ActionSupport;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

public class UserRoleListAction extends ActionSupport<C10037_UserRoleListReqMessage>{
	
	@Override
	public Message execute(ActionContext context, C10037_UserRoleListReqMessage reqMsg) {
		C10037_UserRoleListRespMessage resp = new C10037_UserRoleListRespMessage();
		try {
			List<RoleInstance> list = GameContext.getUserRoleApp().getRoleList(reqMsg.getUserId());
			if(Util.isEmpty(list)){
				return resp;
			}
			List<RolesItem> items = new ArrayList<RolesItem>();
			for (RoleInstance role : list) {
				RolesItem item = new RolesItem();
				item.setUserId(role.getUserId());
				item.setUserName(role.getUserName());
				item.setRoleId(role.getRoleId());
				item.setRoleName(role.getRoleName());
				item.setCareer((byte) role.getCareer());
				item.setLevel((byte) role.getLevel());
				item.setCampId(role.getCampId());
				item.setOnline((byte) 0);
				item.setExp(role.getExp());
				//item.setBindMoney(role.getBindingGoldMoney());
				//item.setConsumeBindMoney(role.getConsumeBindMoney());
				item.setSilverMoney(role.getSilverMoney());
				item.setMapId(role.getMapId());
				item.setMapX(role.getMapX());
				item.setMapY(role.getMapY());
				item.setLoginIp(role.getLoginIp());
				item.setChannelId(role.getChannelId());
				item.setChannelUserId(role.getChannelUserId());
				RoleInstance onlineRole = GameContext.getOnlineCenter().getRoleInstanceByRoleId(role.getRoleId());
				//在线角色，从内存中取值
				if(null != onlineRole){
					item.setOnline((byte) 1);
					item.setExp(onlineRole.getExp());
					//item.setBindMoney(onlineRole.getBindingGoldMoney());
					//item.setConsumeBindMoney(onlineRole.getConsumeBindMoney());
					item.setSilverMoney(onlineRole.getSilverMoney());
					item.setMapId(onlineRole.getMapId());
					item.setMapX(onlineRole.getMapX());
					item.setMapY(onlineRole.getMapY());
					item.setLoginIp(onlineRole.getLoginIp());
					item.setChannelId(role.getChannelId());
					item.setChannelUserId(role.getChannelUserId());
				}
				items.add(item);
			}
			resp.setItems(items);
			return resp;
		} catch (RuntimeException e) {
			this.logger.error("GetRoleListAction error: ", e);
			return resp;
		}
	}

}
