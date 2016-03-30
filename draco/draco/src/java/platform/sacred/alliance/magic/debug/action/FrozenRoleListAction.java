package sacred.alliance.magic.debug.action;

import java.util.ArrayList;
import java.util.List;

import com.game.draco.GameContext;
import com.game.draco.debug.message.item.FrozenRoleItem;
import com.game.draco.debug.message.request.C10014_FrozenRoleListReqMessage;
import com.game.draco.debug.message.response.C10014_FrozenRoleListRespMessage;

import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.core.action.ActionSupport;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

/**
 * 查看隔离角色列表
 */
public class FrozenRoleListAction extends ActionSupport<C10014_FrozenRoleListReqMessage>{
	
	@Override
	public Message execute(ActionContext context, C10014_FrozenRoleListReqMessage reqMsg) {
		String roleName = reqMsg.getRoleName();
		List<RoleInstance> list = null;
		if (Util.isEmpty(roleName)) {
			list = GameContext.getUserRoleApp().getFrozenRoleList();
		}else{
			list = GameContext.getUserRoleApp().getFrozenRole(roleName);
		}
		C10014_FrozenRoleListRespMessage resp = new C10014_FrozenRoleListRespMessage();
		if (Util.isEmpty(list)) {
			return resp;
		}
		List<FrozenRoleItem> items = new ArrayList<FrozenRoleItem>();
		for(RoleInstance role : list){
			FrozenRoleItem item = new FrozenRoleItem();
			item.setRoleId(Integer.parseInt(role.getRoleId()));
			item.setRolename(role.getRoleName());
			item.setFrozenBeginTime(role.getFrozenBeginTime());
			item.setFrozenEndTime(role.getFrozenEndTime());
			item.setMemo(role.getFrozenMemo());
			items.add(item);
		}
		resp.setFrozenRoleList(items);
		return resp;
	}
}
