package sacred.alliance.magic.vo.map;

import java.util.Collection;

import sacred.alliance.magic.app.active.vo.Active;
import sacred.alliance.magic.app.map.Map;
import sacred.alliance.magic.base.ActiveType;
import sacred.alliance.magic.base.RoleType;
import sacred.alliance.magic.core.exception.ServiceException;
import sacred.alliance.magic.scheduler.job.LoopCount;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.MapLineInstance;
import sacred.alliance.magic.vo.Point;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;

public class MapActiveInstance extends MapLineInstance{

	private LoopCount cleanRoleCount = new LoopCount(5000);
	
	private Active active = null ;
	
	public MapActiveInstance(Map map,int lineId) {
		super(map,lineId);
		this.initActive();
	}
	
	@Override
	public void exitMap(AbstractRole role) {
		super.exitMap(role);
		if(role.getRoleType() != RoleType.PLAYER){
			return ;
		}
		Point targetPoint = ((RoleInstance)role).getCopyBeforePoint();
		role.setMapId(targetPoint.getMapid());
		role.setMapX(targetPoint.getX());
		role.setMapY(targetPoint.getY());
	}

	@Override
	protected void updateSub() throws ServiceException{
		super.updateSub();
		if(!cleanRoleCount.isReachCycle()){
			return ;
		}
		if(!this.inAcitveTime()){
			//清楚地图内所有人员
			this.clearRole() ;
		}
	}

	/**
	 * 活动是否开启
	 * @return
	 */
	private boolean inAcitveTime(){
		if(null == active){
			return false ;
		}
		return active.isTimeOpen();
	}
	
	protected void initActive(){
		Collection<Active> activeList = GameContext.getActiveApp().getAllActive();
		for(Active theActive : activeList){
			if(theActive.getType() != ActiveType.ActiveMap.getType()){
				continue ;
			}
			if(Util.isEmpty(theActive.getParameter())){
				continue ;
			}
			if(!this.map.getMapId().equals(theActive.getParameter())){
				continue ;
			}
			//获得活动
			this.active = theActive ;
			return ;
		}
	}
	
	private void clearRole(){
		// 踢人
		try {
			if (null != this.getRoleList()) {
				for (RoleInstance role : this.getRoleList()) {
					this.kickRole(role);
				}
			}
		}catch(Exception ex){
			
		}
	}
	

}
