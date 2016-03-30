package sacred.alliance.magic.app.active.unionbattle;

import sacred.alliance.magic.app.active.ActiveSupport;
import sacred.alliance.magic.app.active.vo.Active;
import sacred.alliance.magic.base.ActiveStatus;
import sacred.alliance.magic.base.ActiveType;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.item.ActivePanelDetailBaseItem;
import com.game.draco.message.request.C2530_UnionBattlePanelReqMessage;
import com.game.draco.message.request.C2754_UnionActivityListReqMessage;
import com.game.draco.message.response.C2301_ActivePanelDetailRespMessage;
import com.game.draco.message.response.C2530_UnionBattlePanelRespMessage;
//..
public class ActiveUnionBattleSupport implements ActiveSupport{
	
	@Override
	public void checkReset(RoleInstance role, Active active) {
	}

	@Override
	public Message getActiveDetail(RoleInstance role, Active active) {
		ActivePanelDetailBaseItem detailItem = new ActivePanelDetailBaseItem();
		detailItem.setType(ActiveType.UnionBattle.getType());
		GameContext.getActiveApp().buildActivePanelDetailBaseItem(detailItem, active);
		C2530_UnionBattlePanelReqMessage reqMsg = new C2530_UnionBattlePanelReqMessage();
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
