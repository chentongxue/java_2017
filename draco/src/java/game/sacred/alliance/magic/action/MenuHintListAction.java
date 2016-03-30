package sacred.alliance.magic.action;

import java.util.List;

import com.game.draco.GameContext;
import com.game.draco.message.item.MenuHintItem;
import com.game.draco.message.request.C0154_MenuHintListReqMessage;
import com.game.draco.message.response.C0154_MenuHintListRespMessage;

import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

public class MenuHintListAction extends BaseAction<C0154_MenuHintListReqMessage>{

	@Override
	public Message execute(ActionContext context, C0154_MenuHintListReqMessage reqMsg) {
		try {
			RoleInstance role = this.getCurrentRole(context);
			if(null == role){
				return null ;
			}
			List<MenuHintItem> hintList = GameContext.getMenuApp().getHintList(role);
			C0154_MenuHintListRespMessage respMsg = new C0154_MenuHintListRespMessage();
			respMsg.setHintList(hintList);
			return respMsg;
		} catch (RuntimeException e) {
			this.logger.error(this.getClass().getName() + ".execute error: ", e);
			return null;
		}
	}

}
