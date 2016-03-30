package sacred.alliance.magic.action;

import com.game.draco.GameContext;
import com.game.draco.message.request.C1520_PkStatusDetailReqMessage;
import com.game.draco.message.response.C1520_PkStatusDetailRespMessage;

import sacred.alliance.magic.app.pk.PkConfig;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

public class PkStatusDetailAction extends BaseAction<C1520_PkStatusDetailReqMessage> {

	@Override
	public Message execute(ActionContext context, C1520_PkStatusDetailReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null;
		}
		C1520_PkStatusDetailRespMessage resp = new C1520_PkStatusDetailRespMessage();
		PkConfig config = GameContext.getPkApp().getPkConfig();
		if(null == config){
			return null;
		}
		resp.setTime(config.getTime());
		resp.setDesc(config.getDesc());
		return resp;
	}
}
