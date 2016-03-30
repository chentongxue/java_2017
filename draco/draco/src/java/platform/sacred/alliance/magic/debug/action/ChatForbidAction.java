package sacred.alliance.magic.debug.action;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.game.draco.GameContext;
import com.game.draco.debug.message.item.ChatForbidItem;
import com.game.draco.debug.message.request.C10016_ChatForbidReqMessage;
import com.game.draco.debug.message.response.C10016_ChatForbidRespMessage;

import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.core.action.ActionSupport;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

public class ChatForbidAction extends ActionSupport<C10016_ChatForbidReqMessage>{
	
	@Override
	public Message execute(ActionContext session, C10016_ChatForbidReqMessage req) {
		byte type = req.getType();
		C10016_ChatForbidRespMessage resp = new C10016_ChatForbidRespMessage();
		// 查看列表
		if (type == 0) {
			List<RoleInstance> list = null;
			if (Util.isEmpty(req.getRolename())) {
				list = GameContext.getUserRoleApp().getForbidRoleList();
			}else{
				list = GameContext.getUserRoleApp().getForbidRoleList(req.getRolename());
			}
			if (Util.isEmpty(list)) {
				return resp;
			}
			List<ChatForbidItem> items = new ArrayList<ChatForbidItem>();
			for(RoleInstance role : list){
				ChatForbidItem item = new ChatForbidItem();
				item.setRoleId(Integer.parseInt(role.getRoleId()));
				item.setRolename(role.getRoleName());
				item.setFrozenBeginTime(role.getForbidBeginTime());
				item.setFrozenEndTime(role.getForbidEndTime());
				item.setType((byte)role.getForbidType());
				item.setMemo(role.getForbidMemo());
				items.add(item);
			}
			resp.setItems(items);
			return resp;
		}
		//禁言角色
		String rolename = req.getRolename();
		if(Util.isEmpty(rolename)){
			return resp;
		}
		try {
			RoleInstance role = GameContext.getUserRoleApp().getRoleByRoleName(rolename);
			if(role == null){
				return resp;
			}
			Date forbidBeginTime = new Date();
			Date forbidEndTime = req.getFrozenEndTime();
			if(forbidEndTime == null){
				Calendar calendar = Calendar.getInstance();
				calendar.add(Calendar.YEAR, 1);
				forbidEndTime = calendar.getTime();
			}
			role.setForbidBeginTime(forbidBeginTime);
			role.setForbidEndTime(forbidEndTime);
			role.setForbidMemo(req.getMemo());
			role.setForbidType(type);
			GameContext.getBaseDAO().update(role);
			List<ChatForbidItem> items = new ArrayList<ChatForbidItem>();
			ChatForbidItem item = new ChatForbidItem();
			item.setRoleId(Integer.parseInt(role.getRoleId()));
			item.setRolename(role.getRoleName());
			item.setFrozenBeginTime(forbidBeginTime);
			item.setFrozenEndTime(forbidEndTime);
			item.setType(type);
			item.setMemo(req.getMemo());
			items.add(item);
			resp.setItems(items);
			return resp;
		} catch (Exception e) {
			this.logger.error("ChatForbidAction error: ", e);
			return resp;
		}
	}
	
}
