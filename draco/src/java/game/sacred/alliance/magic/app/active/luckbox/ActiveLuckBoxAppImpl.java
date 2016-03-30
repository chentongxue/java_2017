package sacred.alliance.magic.app.active.luckbox;

import sacred.alliance.magic.app.active.ActiveSupport;
import sacred.alliance.magic.app.active.vo.Active;
import sacred.alliance.magic.base.ActiveStatus;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.luckybox.LuckyBoxAppImpl;
import com.game.draco.message.request.C1915_LuckyBoxDisplayReqMessage;

public class ActiveLuckBoxAppImpl implements ActiveSupport,Service {
	
	
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
		C1915_LuckyBoxDisplayReqMessage reqMsg = new C1915_LuckyBoxDisplayReqMessage();
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
		if (!active.isSuitLevel(role)) {
			return false;
		}
		// 如果有剩余重置次数
		if (GameContext.getLuckyBoxApp().getRefreshTimes(role, role.getRoleCount()) > 0) {
			return true;
		}
		// 如果有剩余免费次数
		return GameContext.getLuckyBoxApp().getRemainTimes(role) > LuckyBoxAppImpl.NORMAL_AWARD_POOL_SIZE;
	}
	
}
