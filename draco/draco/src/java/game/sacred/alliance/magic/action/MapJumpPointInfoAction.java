package sacred.alliance.magic.action;

import com.game.draco.GameContext;
import com.game.draco.message.request.C0207_MapJumpPointInfoReqMessage;

import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;

public class MapJumpPointInfoAction extends BaseAction<C0207_MapJumpPointInfoReqMessage>{

	@Override
	public Message execute(ActionContext context, 
				C0207_MapJumpPointInfoReqMessage reqMsg) {
		byte[] data = GameContext.getMapApp().getMapJumpPointData();
		if(null != data){
			context.getSession().write(data);
		}
		return null ;
		//return GameContext.getMapApp().getMapJumpPointDataMessage();
	}

}
