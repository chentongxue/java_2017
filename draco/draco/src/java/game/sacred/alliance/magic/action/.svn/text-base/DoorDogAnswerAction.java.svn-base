package sacred.alliance.magic.action;

import com.game.draco.GameContext;
import com.game.draco.message.request.C0012_DoorDogAnswerReqMessage;

import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

public class DoorDogAnswerAction extends BaseAction<C0012_DoorDogAnswerReqMessage>{

	@Override
	public Message execute(ActionContext context, C0012_DoorDogAnswerReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null ;
		}
		GameContext.getDoorDogApp().verifyQuestion(role, reqMsg.getAnswer());
		return null;
	}

}
