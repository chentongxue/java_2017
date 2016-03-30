package sacred.alliance.magic.app.ai.state;

import com.game.draco.app.npc.domain.NpcInstance;

import sacred.alliance.magic.app.ai.Ai;
import sacred.alliance.magic.app.ai.State;
import sacred.alliance.magic.app.ai.StateFactory;
import sacred.alliance.magic.app.ai.StateType;
import sacred.alliance.magic.app.ai.Telegram;
import sacred.alliance.magic.app.ai.movement.DestinationHolder;
import sacred.alliance.magic.app.ai.movement.DestinationHolderImpl;
import sacred.alliance.magic.app.ai.movement.Traveller;
import sacred.alliance.magic.base.PathType;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.Path;
import sacred.alliance.magic.vo.Point;

/**
 * 路径
 * @author tiefengKuang 
 * @date 2009-12-3 
 * @version 0.0.0.1
 * @changeLog
 *  1) create: 2009-12-3
 */
public class StateWayPointMove extends NpcState{

	private Path path ;
	//private int currentNodeIndex ;
	//boolean forward = true;
    private Ai.WayPointInfo wayPointInfo ;
	DestinationHolder holder = new DestinationHolderImpl();
	
	public StateWayPointMove(Path path){
		this.path = path ;
	}
    
	@Override
	public void enter(NpcInstance entity) {
		entity.getAi().resetStateParameter();
       wayPointInfo = entity.getAi().getWapPointInfo();
		if(null != path && !Util.isEmpty(path.getPathNode())){
			Point point;
			if(wayPointInfo.getCurrentNodeIndex() >= path.getPathNode().size()-1){
				point = path.getPathNode().get(path.getPathNode().size()-1);
			}else{
				point = path.getPathNode().get(wayPointInfo.getCurrentNodeIndex());
			}
			Traveller traveller = new Traveller(entity);
			holder.setDestination(traveller, point);
		}
	}

	@Override
	public void doExecute(NpcInstance entity) {
		//有可能当前怪物中了一个buff无法行走
		if(!entity.getBehavior().canMove()){
			return;
		}
		if(null == path || path.getPathNode().isEmpty()){
			return ;
		}
		//long time_diff = entity.getMapInstance().getWorldTime().getTimeDiff();
		long time_diff = this.getTimeDiff(entity);
		Traveller traveller = new Traveller(entity);
		
		holder.updateTraveller(traveller, time_diff);
		
	    if(holder.hasArrived()){
    		Point nextMovePoint = getNextMovePoint();
    		if(nextMovePoint==null){
    			entity.getBehavior().stopMove();
    			State idleState = StateFactory.getState(StateType.State_Idle, entity, null);
    			entity.getAi().getStateMachine().switchState(idleState);
    			return ;
    		}
    		//System.out.println("~~~~~nextMovePoint.x"+nextMovePoint.getX()+" nextMovePoint.y"+nextMovePoint.getY());
    		holder.setDestination(traveller, nextMovePoint, entity.getSpeed()
    				/*SpeedType.slow.getSpeed(entity.getRoleType())*/);
	    }
	}

	@Override
	public void exit(NpcInstance entity) {
		
	}

	@Override
	public void onMessage(NpcInstance entity, Telegram telegram) {
		//TODO:处理事件
        switch(telegram.getType()){
             case SHOUT:
                //呼叫
                entity.getAi().receiveSummonMsg(telegram.getSender());
            	//看看可否帮他回血
            	entity.getAi().respondSeekRescue(telegram.getSender());
                break;
            case ATTACK:
                //处理
                //切换到战斗状态
//                AbstractRole attacker = telegram.getSender();
//                entity.getHatredTarget().addHatred(attacker.getRoleId(), (Integer)telegram.getExtraInfo());
                entity.getAi().getStateMachine().switchState(StateFactory.getState(StateType.State_Battle, entity, null));
                break;
            case INVIEW:
                //进入视野
                 entity.getAi().moveInLineOfSight(telegram.getSender());
                break;
        }
	}

	
	private Point getNextMovePoint(){
		if(path.getPathType()==PathType.CIRCLE){
			
			if(wayPointInfo.getCurrentNodeIndex() >= path.getPathNode().size()-1){
				wayPointInfo.setCurrentNodeIndex(0);
			}else{
				wayPointInfo.incrCurrentNodeIndex();
			}
			
		}else if(path.getPathType()==PathType.FORWARD){
			if(wayPointInfo.getCurrentNodeIndex() >= path.getPathNode().size()-1){
				return null;
			}
			wayPointInfo.incrCurrentNodeIndex();
		}else if(path.getPathType()==PathType.GOBACK){
			if(wayPointInfo.isForward()){
				if(wayPointInfo.getCurrentNodeIndex() >= path.getPathNode().size()-1){
					wayPointInfo.setForward(false);
					wayPointInfo.deIncrCurrentNodeIndex();
				}else{
					wayPointInfo.incrCurrentNodeIndex();
				}
			}else{
				if(wayPointInfo.getCurrentNodeIndex()<=0){
					wayPointInfo.setForward(true);
					wayPointInfo.incrCurrentNodeIndex();
				}else{
					wayPointInfo.deIncrCurrentNodeIndex();
				}
			}
		}
		return path.getPathNode().get(wayPointInfo.getCurrentNodeIndex());
	}

	@Override
	public StateType getStateType() {
		return StateType.State_WayPoint_Move ;
	}
}
