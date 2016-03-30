package sacred.alliance.magic.app.active.richman;

import sacred.alliance.magic.app.active.ActiveSupport;
import sacred.alliance.magic.app.active.vo.Active;
import sacred.alliance.magic.base.ActiveStatus;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.message.request.C2650_RichManMapEnterReqMessage;

public class ActiveRichmanAppImpl implements ActiveSupport,Service {
	
	
	@Override
	public void setArgs(Object arg0) {
		
	}

	@Override
	public void start() {
	}

	@Override
	public void stop() {
		
	}
	
	@Override
	public Message getActiveDetail(
			RoleInstance role, Active active) {
		C2650_RichManMapEnterReqMessage reqMsg = new C2650_RichManMapEnterReqMessage();
		role.getBehavior().addCumulateEvent(reqMsg);
		return null;
	}

	@Override
	public ActiveStatus getActiveStatus(RoleInstance role, Active active) {
		if(!active.isTimeOpen() || !active.isSuitLevel(role)){
			return ActiveStatus.NotOpen;
		}
		return ActiveStatus.CanAccept;
	}

	@Override
	public void checkReset(RoleInstance role, Active active) {
		
	}

	@Override
	public boolean isOutDate(Active active) {
		return false;
	}

	@Override
	public boolean getActiveHint(RoleInstance role, Active active) {
		return false;
	}
	
}
