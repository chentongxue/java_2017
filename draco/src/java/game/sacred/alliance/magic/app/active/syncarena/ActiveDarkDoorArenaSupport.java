package sacred.alliance.magic.app.active.syncarena;

import sacred.alliance.magic.app.active.ActiveSupport;
import sacred.alliance.magic.app.active.vo.Active;
import sacred.alliance.magic.base.ActiveStatus;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;

public class ActiveDarkDoorArenaSupport implements ActiveSupport{

	@Override
	public void checkReset(RoleInstance role, Active active) {
	}

	@Override
	public Message getActiveDetail(RoleInstance role, Active active) {
		return GameContext.getArena3V3App().getArena3V3DetailRespMessage(role);
	}

	@Override
	public ActiveStatus getActiveStatus(RoleInstance role, Active active) {
		if(!GameContext.getArena3V3App().isOpenDarkDoor() 
				|| !active.isTimeOpen() || !active.isSuitLevel(role)){
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
