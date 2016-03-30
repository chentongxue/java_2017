package sacred.alliance.magic.app.active.arena;

import com.game.draco.GameContext;
import com.game.draco.message.item.ActivePanelDetailArenaNvnItem;
import com.game.draco.message.response.C2301_ActivePanelDetailRespMessage;

import sacred.alliance.magic.app.active.ActiveSupport;
import sacred.alliance.magic.app.active.vo.Active;
import sacred.alliance.magic.app.arena.ApplyInfo;
import sacred.alliance.magic.app.arena.ArenaMatch;
import sacred.alliance.magic.base.ActiveStatus;
import sacred.alliance.magic.base.ActiveType;
import sacred.alliance.magic.vo.RoleInstance;

public class ActiveArenaSupport implements ActiveSupport{

	@Override
	public void checkReset(RoleInstance role, Active active) {
		
	}
	
	private C2301_ActivePanelDetailRespMessage buildNvnDetail(RoleInstance role,
			Active active){
		ActivePanelDetailArenaNvnItem detailItem = new ActivePanelDetailArenaNvnItem();
		detailItem.setType(ActiveType.Areannvn.getType());
		//公用赋值
		GameContext.getActiveApp().buildActivePanelDetailBaseItem(detailItem, active);
		ApplyInfo info = GameContext.getArenaApp().getApplyInfo(role.getRoleId());
		if(null == info || info.getActiveId() != active.getId()){
			//可报名
			detailItem.setStatus(ActiveArenaStatus.CanApply.getStatus());
		}else{
			detailItem.setStatus(ActiveArenaStatus.CanCancel.getStatus());
			//获得报名时间
			detailItem.setTime((short)((System.currentTimeMillis()-info.getCreateDate())/1000));
			detailItem.setStatus(ActiveArenaStatus.CanCancel.getStatus());
		}
		C2301_ActivePanelDetailRespMessage respMsg = new C2301_ActivePanelDetailRespMessage();
		respMsg.setDetailItem(detailItem);
		return respMsg;
	}
	
	private C2301_ActivePanelDetailRespMessage build1v1Detail(RoleInstance role,
			Active active){
		return active.getDefaultPanelDetailMessage(role);
	}
	

	@Override
	public C2301_ActivePanelDetailRespMessage getActiveDetail(RoleInstance role,
			Active active) {
		if(active.getType() == ActiveType.Arean1v1.getType()){
			return this.build1v1Detail(role, active);
		}
		return this.buildNvnDetail(role, active);
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
	public boolean isOutDate(Active active) {
		return active.isOutDate();
	}

}
