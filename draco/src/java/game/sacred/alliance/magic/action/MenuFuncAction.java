package sacred.alliance.magic.action;

import com.game.draco.GameContext;
import com.game.draco.message.request.C0153_MenuFuncReqMessage;
import com.game.draco.message.response.C0002_ErrorRespMessage;

import sacred.alliance.magic.app.menu.MenuConfig;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

public class MenuFuncAction extends BaseAction<C0153_MenuFuncReqMessage>{

	@Override
	public Message execute(ActionContext context, C0153_MenuFuncReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null ;
		}
		short menuId = reqMsg.getMenuId();
		MenuConfig config = GameContext.getMenuApp().getMenuConfigById(menuId);
		if(null == config){
			return new C0002_ErrorRespMessage(reqMsg.getCommandId(),this.getText(TextId.ERROR_INPUT)) ;
		}
		Message funcReqMsg = config.getMenuFunc().createFuncReqMessage(role);
		if(null == funcReqMsg){
			//logger.error("create menuFucReqMessage null,menuId=" + menuId);
			return null ;
		}
		if(funcReqMsg.getCommandId() < 0){
			role.getBehavior().sendMessage(funcReqMsg);
		}else{
			role.getBehavior().addCumulateEvent(funcReqMsg);
		}
		return null;
	}

}
