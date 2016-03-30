package sacred.alliance.magic.app.active.worldboss;

import com.game.draco.message.request.C0611_BossListReqMessage;

import sacred.alliance.magic.app.active.ActiveSupport;
import sacred.alliance.magic.app.active.vo.Active;
import sacred.alliance.magic.base.ActiveStatus;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.vo.RoleInstance;

public class ActiveWorldBossAppImpl implements ActiveSupport {

	@Override
	public void checkReset(RoleInstance role, Active active) {
	}

	@Override
	public Message getActiveDetail(RoleInstance role, Active active) {
		C0611_BossListReqMessage message = new C0611_BossListReqMessage();
		role.getBehavior().addCumulateEvent(message);
		return null;
	}

	@Override
	public boolean getActiveHint(RoleInstance role, Active active) {
		return false;
	}

	@Override
	public ActiveStatus getActiveStatus(RoleInstance role, Active active) {
		return ActiveStatus.Underway;
	}

	@Override
	public boolean isOutDate(Active active) {
		return false;
	}

}
