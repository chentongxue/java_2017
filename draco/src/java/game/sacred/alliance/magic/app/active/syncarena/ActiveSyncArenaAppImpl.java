package sacred.alliance.magic.app.active.syncarena;

import sacred.alliance.magic.app.active.ActiveSupport;
import sacred.alliance.magic.app.active.vo.Active;
import sacred.alliance.magic.app.arena.ApplyInfo;
import sacred.alliance.magic.app.arena.ArenaMatch;
import sacred.alliance.magic.base.ActiveStatus;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.request.C3859_Arean1V1ingDetailReqMessage;

public class ActiveSyncArenaAppImpl implements ActiveSupport,Service {
	
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
		C3859_Arean1V1ingDetailReqMessage reqMsg = new C3859_Arean1V1ingDetailReqMessage();
		role.getBehavior().addCumulateEvent(reqMsg);
		return null ;
	}

	@Override
	public ActiveStatus getActiveStatus(RoleInstance role, Active active) {
		ApplyInfo info = GameContext.getArenaApp().getApplyInfo(role.getRoleId());
		if(null != info && info.getActiveId() == active.getId()){
			ArenaMatch match = info.getMatch();
			if(null != match && match.isTimeout()){
				//!!!! 未知原因报名信息一直都存在也无法取消,故有下面代码
				//已经匹配并且已经超时
				info.setCancel(true);
				GameContext.getArenaApp().removeApplyInfo(role.getRoleId());
				return ActiveStatus.CanAccept ;
			}
			return ActiveStatus.Underway ;
		}
		if(!active.isTimeOpen()){
			return ActiveStatus.NotOpen ;
		}
		return ActiveStatus.CanAccept ;
	}

	@Override
	public void checkReset(RoleInstance role, Active active) {
		
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
