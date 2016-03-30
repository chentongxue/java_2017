package sacred.alliance.magic.app.active.map;

import sacred.alliance.magic.app.active.ActiveSupport;
import sacred.alliance.magic.app.active.vo.Active;
import sacred.alliance.magic.base.ActiveStatus;
import sacred.alliance.magic.base.ActiveType;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.message.response.C2301_ActivePanelDetailRespMessage;

public class ActiveMapSupport implements ActiveSupport {

	@Override
	public void checkReset(RoleInstance role, Active active) {
		
	}

	@Override
	public Message getActiveDetail(RoleInstance role, Active active) {
		C2301_ActivePanelDetailRespMessage respMsg = active.getDefaultPanelDetailMessage(role);
		//重新设置为默认类型
		respMsg.getDetailItem().setType(ActiveType.Common.getType());
		return respMsg ;
	}
	
	@Override
	public ActiveStatus getActiveStatus(RoleInstance role, Active active) {
		if(!active.isTimeOpen() || !active.isSuitLevel(role)){
			return ActiveStatus.NotOpen;
		}
		return ActiveStatus.CanAccept;
	}

	@Override
	public boolean isOutDate(Active active) {
		return active.isOutDate();
	}

	@Override
	public boolean getActiveHint(RoleInstance role, Active active) {
		return false;
	}

}
