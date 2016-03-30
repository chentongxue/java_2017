package sacred.alliance.magic.app.active.compass;

import sacred.alliance.magic.app.active.ActiveSupport;
import sacred.alliance.magic.app.active.vo.Active;
import sacred.alliance.magic.base.ActiveStatus;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.message.request.C1908_CompassDisplayReqMessage;

public class ActiveCompassAppImpl implements ActiveSupport{
	
	@Override
	public void checkReset(RoleInstance role, Active active) {
		//上古法阵是不限次数的抽奖，不需要重置
	}

	@Override
	public Message getActiveDetail(RoleInstance role, Active active) {
		C1908_CompassDisplayReqMessage message = new C1908_CompassDisplayReqMessage();
		message.setId((short)1);
		role.getBehavior().addCumulateEvent(message);
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
	public boolean isOutDate(Active active) {
		return active.isOutDate();
	}

	@Override
	public boolean getActiveHint(RoleInstance role, Active active) {
		return false;
	}
	
}
