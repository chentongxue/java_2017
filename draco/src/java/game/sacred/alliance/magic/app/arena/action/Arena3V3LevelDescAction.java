package sacred.alliance.magic.app.arena.action;

import com.game.draco.GameContext;
import com.game.draco.message.request.C3863_Arena3V3DetailReqMessage;
import com.game.draco.message.request.C3866_Arena3V3LevelDescReqMessage;
import com.game.draco.message.response.C3866_Arena3V3LevelDescRespMessage;
import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

public class Arena3V3LevelDescAction extends BaseAction<C3866_Arena3V3LevelDescReqMessage>{

	@Override
	public Message execute(ActionContext context, C3866_Arena3V3LevelDescReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return  null ;
		}
        return GameContext.getArena3V3App().getArena3V3LevelDescMessage(role);
	}
}
