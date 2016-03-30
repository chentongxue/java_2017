package sacred.alliance.magic.app.active.alchemy;

import sacred.alliance.magic.app.active.ActiveSupport;
import sacred.alliance.magic.app.active.vo.Active;
import sacred.alliance.magic.base.ActiveStatus;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.request.C1913_AlchemyDisplayReqMessage;

public class ActiveAlchemyAppImpl implements ActiveSupport,Service {
	
	
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
	public boolean getActiveHint(RoleInstance role, Active active) {
		if (!active.isSuitLevel(role)) {
			return false;
		}
		return GameContext.getAlchemyApp().hasFreeTimes(role);
	}

	@Override
	public Message getActiveDetail(
			RoleInstance role, Active active) {
		C1913_AlchemyDisplayReqMessage reqMsg = new C1913_AlchemyDisplayReqMessage();
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
	
}
