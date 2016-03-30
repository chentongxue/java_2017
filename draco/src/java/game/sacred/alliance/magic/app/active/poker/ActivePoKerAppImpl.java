package sacred.alliance.magic.app.active.poker;

import sacred.alliance.magic.app.active.ActiveSupport;
import sacred.alliance.magic.app.active.vo.Active;
import sacred.alliance.magic.base.ActiveStatus;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.request.C0712_QuestPokerPanelReqMessage;

public class ActivePoKerAppImpl implements ActiveSupport,Service {
	
	
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
		C0712_QuestPokerPanelReqMessage reqMsg = new C0712_QuestPokerPanelReqMessage();
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
		int currCount = GameContext.getQuestPokerApp().getCurrCount(role);
		int maxCount = GameContext.getQuestPokerApp().getTotalTime(role);
		return maxCount > currCount;
	}

}
