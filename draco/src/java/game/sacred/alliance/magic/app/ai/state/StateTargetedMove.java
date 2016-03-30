package sacred.alliance.magic.app.ai.state;

import sacred.alliance.magic.app.ai.StateType;
import sacred.alliance.magic.app.ai.Telegram;
import sacred.alliance.magic.app.ai.movement.DestinationHolder;
import sacred.alliance.magic.app.ai.movement.DestinationHolderImpl;
import sacred.alliance.magic.app.ai.movement.TimeTracker;
import sacred.alliance.magic.app.ai.movement.Traveller;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.Point;

import com.game.draco.app.npc.domain.NpcInstance;

/**
 * 追逐
 * @author tiefengKuang 
 * @date 2009-12-3 
 * @version 0.0.0.1
 * @changeLog
 *  1) create: 2009-12-3
 */
public class StateTargetedMove extends NpcState{
	private static final int THINK_AREA_MARGIN = 5 ;
	private static final int THINK_AREA_DEFAULT = 10 ;
	byte targetDir = 0 ;
	AbstractRole role;
	DestinationHolder holder = new DestinationHolderImpl();
	TimeTracker timeTracker = new TimeTracker(0);
	//追击最小距离,达到此距离NPC将状态切换,进行思考
	int thinkArea = -1 ;
	
	/*public StateTargetedMove(){
		
	}*/
	
	public StateTargetedMove(int thinkArea){
		if(thinkArea >=0){
			this.thinkArea = thinkArea ;
		}
	}
	
	
	@Override
	public void enter(NpcInstance entity) {
		this.role = entity ;
        //String targetNameId = Util.getFistElement(entity.getHatredMap());
		String targetNameId = entity.getHatredTarget().getFirstHateTarget();
        AbstractRole target = entity.getMapInstance().getAbstractRole(targetNameId);
        role.setTarget(target);
		if(null != target){
			Traveller traveller = new Traveller(role);
			holder.setDestination(traveller, role.getTarget().getCurrentPoint());
			this.targetDir = target.getDir();
//			if(this.inThinkRange(target)){
//				holder.setDestination(traveller, role.getTarget().getCurrentPoint());
//			}else{
//				holder.setDestination(traveller, this.getTargetPoint(
//						role.getMapX(), role.getMapY(), target.getMapX(), target.getMapY()));
//			}
		}
	}

	@Override
	public void doExecute(NpcInstance entity) {
		//有可能当前怪物中了一个buff无法行走
		//TODO:这里有问题,应该判断如果不能走不能进入此状态
		if(!entity.getBehavior().canMove()){
			return;
		}
		//System.out.println("开始~~~~~mapX"+entity.getMapX()+" mapY"+entity.getMapY());
        if(/*entity.getAi().tooFarFromHome() || */entity.getHatredTarget().isEmptyHatredMap()){
            //离开出生点太远
            //回家
            entity.getAi().enterEvadeMode();
            return ;
        }
		AbstractRole target = role.getTarget();
		/*
		 *目标不存在或者目标离家太远则放弃 
		 */
        if(null == target || 
        		entity.getAi().tooFarFromHome(target) 
        		//|| entity.getAi().isOutOfView(target)
        		){
            //将target从仇恨列表中删除
            if(null != target){
            	entity.getHatredTarget().removeHateTarget(target.getRoleId());
            }
            //获得当前仇恨值列表第一位为追击目标
            String nextTarget = entity.getHatredTarget().getFirstHateTarget();
           if(null != nextTarget){
               role.setTarget(role.getMapInstance().getAbstractRole(nextTarget));
               return ;
           }
           return ;
        }
		Traveller traveller = new Traveller(role);
		//long time_diff = role.getMapInstance().getWorldTime().getTimeDiff();
		long time_diff = this.getTimeDiff(entity);
		if(holder.updateTraveller(traveller, time_diff)){
			/**
			 * 是否进行攻击应该写在ai里
			 * 判断离目标在一定范围内停止行走
			 */
			if(this.inThinkRange(target)){
				//role.getBehavior().stopMove();
				//this.setTargetLocation(role,true);
				//已经追逐上
				role.getAi().chasedTarget(target);
				return ;
			}
			/*if(holder.getDestinationDiff(role.getCurrentPoint(), target.getCurrentPoint())
					< holder.getDestinationDiff(role.getCurrentPoint(), holder.getDestination())){
				this.setTargetLocation(role);
			}*/
			if(target.getDir() != targetDir) {
				this.setTargetLocation(role);
				targetDir = target.getDir();
			}
			if(holder.hasArrived()){
                //停止
               // role.getBehavior().stopMove();
				//this.setTargetLocation(role,true);
				//已经追逐上
				if( this.inThinkRange(target)){
					role.getAi().chasedTarget(target);
				}else{
					this.setTargetLocation(role);
				}
			}
		}
		//System.out.println("结束~~~~~mapX"+entity.getMapX()+" mapY"+entity.getMapY());
	}
	
//	private Point getTargetPoint(int roleX,int roleY,int targetX,int targetY){
//		int thinkArea = role.getAi().getThinkArea();
//		if(thinkArea <=0){
//			thinkArea = THINK_AREA_DEFAULT ;
//		}
//		thinkArea += THINK_AREA_MARGIN ;
//		
//		int distance = Util.distance(roleX, roleY, targetX, targetY);
//		float rate = thinkArea/(float)distance;
//		int x= (int)(roleX - rate*(roleX-targetX));
//		int y= (int)(roleY - rate*(roleY-targetY));	
//		return new Point("",x,y) ;
//	}
	
	private boolean inThinkRange(AbstractRole target){
		if(0 >= this.thinkArea){
			return role.getAi().inAttackRange(target);
		}
		return Util.inCircle(role.getMapX(), role.getMapY(),target.getMapX(), target.getMapY(), thinkArea);
	}

	private void setTargetLocation(AbstractRole role) {
		AbstractRole target = role.getTarget();
		if (null == target  || !target.getMapId().equals(role.getMapId())) {
			return;
		}
		Traveller traveller = new Traveller(role);
		
		Point targetPoint = new Point(role.getMapId(), target.getMapX(), target.getMapY());
		long travel_time = holder.setDestination(traveller, targetPoint);
		timeTracker.reset(travel_time);
	}
	
	@Override
	public void exit(NpcInstance entity) {
		//System.out.println("========== -------  x=" + entity.getMapX() + " y=" + entity.getMapY() );
		//Point p = holder.getDestination();
		//entity.getBehavior().move(p.getX(), p.getY(), 0);
	}

	@Override
	public void onMessage(NpcInstance entity, Telegram telegram) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public StateType getStateType() {
		return StateType.State_Targeted_Move ;
	}

}
