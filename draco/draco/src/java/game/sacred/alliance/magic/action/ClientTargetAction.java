package sacred.alliance.magic.action;

import com.game.draco.GameContext;
import com.game.draco.message.request.C3001_ClientTargetReqMessage;

import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

public class ClientTargetAction extends BaseAction<C3001_ClientTargetReqMessage>{

	@Override
	public Message execute(ActionContext context, C3001_ClientTargetReqMessage reqMsg) {
		try{
			RoleInstance role = this.getCurrentRole(context);
			if(null == role){
				return null;
			}
			//统计日志
			GameContext.getStatLogApp().clientTargetCollect(role, reqMsg.getTargetInfo());
			return null;
		}catch(Exception e){
			logger.error("",e);
			return null;
		}
	}

}
