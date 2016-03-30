package sacred.alliance.magic.app.active.unionbattle;

import sacred.alliance.magic.app.active.ActiveSupport;
import sacred.alliance.magic.app.active.vo.Active;
import sacred.alliance.magic.base.ActiveStatus;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.message.request.C2541_UnionIntegralFightListReqMessage;
public class ActiveUnionIntegralBattleSupport implements ActiveSupport{
	
	@Override
	public void checkReset(RoleInstance role, Active active) {
	}

	@Override
	public Message getActiveDetail(RoleInstance role, Active active) {
		C2541_UnionIntegralFightListReqMessage reqMsg = new C2541_UnionIntegralFightListReqMessage();
		reqMsg.setRound((byte)-1);
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
	public boolean isOutDate(Active active) {
		return active.isOutDate();
	}

	@Override
	public boolean getActiveHint(RoleInstance role, Active active) {
		return false;
	}

}
