package sacred.alliance.magic.action;

import java.util.List;

import com.game.draco.GameContext;
import com.game.draco.message.item.MenuItem;
import com.game.draco.message.request.C0150_MenuListReqMessage;
import com.game.draco.message.response.C0150_MenuListRespMessage;

import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

public class MenuListAction extends BaseAction<C0150_MenuListReqMessage>{

	@Override
	public Message execute(ActionContext context, C0150_MenuListReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null ;
		}
		List<MenuItem> items = GameContext.getMenuApp().getMenuList(role);
		C0150_MenuListRespMessage respMsg = new C0150_MenuListRespMessage();
		respMsg.setItems(items);
		return respMsg ;
	}

}
