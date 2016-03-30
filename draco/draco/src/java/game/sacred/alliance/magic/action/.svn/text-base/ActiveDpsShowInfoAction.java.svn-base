package sacred.alliance.magic.action;

import com.game.draco.message.request.C2364_ActiveDpsShowInfoReqMessage;
import com.game.draco.message.response.C2364_ActiveDpsShowInfoRespMessage;

import sacred.alliance.magic.base.MapLogicType;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

public class ActiveDpsShowInfoAction extends BaseAction<C2364_ActiveDpsShowInfoReqMessage> {

	@Override
	public Message execute(ActionContext context, C2364_ActiveDpsShowInfoReqMessage req) {
		RoleInstance role = this.getCurrentRole(context);
		if(role == null){
			return null;
		}
		String showInfo = this.getText(TextId.DPS_MAPLOGIC_SHOW_DEFAULT);
		MapLogicType mapLogicType = MapLogicType.getMapLogicType(req.getLogicType());
		switch(mapLogicType){
		case dps:
			showInfo = this.getText(TextId.DPS_MAPLOGIC_SHOW_HURT);
			break;
		case arenaTop:
			showInfo = this.getText(TextId.DPS_MAPLOGIC_SHOW_KILL_NUM);
			break;
		}
		C2364_ActiveDpsShowInfoRespMessage resp = new C2364_ActiveDpsShowInfoRespMessage();
		resp.setShowInfo(showInfo);
		return resp;
	}
}
