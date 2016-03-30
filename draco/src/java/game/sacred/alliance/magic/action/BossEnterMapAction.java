package sacred.alliance.magic.action;

import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.request.C0612_BossEnterMapReqMessage;
import com.game.draco.message.response.C0612_BossEnterMapRespMessage;

public class BossEnterMapAction extends BaseAction<C0612_BossEnterMapReqMessage> {

	@Override
	public Message execute(ActionContext ct, C0612_BossEnterMapReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(ct);
		if(role == null){
			return null;
		}
		Result result = GameContext.getNpcRefreshApp().enterBossMap(role, reqMsg.getId());
		C0612_BossEnterMapRespMessage rs = new C0612_BossEnterMapRespMessage();
		rs.setType(result.getResult());
		if(!result.isSuccess()){
			rs.setInfo(result.getInfo());
		}
		return rs ;
	}

}
