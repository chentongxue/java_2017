package sacred.alliance.magic.app.ai.state;

import java.util.Random;

import com.game.draco.app.npc.domain.NpcInstance;

import sacred.alliance.magic.app.ai.StateFactory;
import sacred.alliance.magic.app.ai.StateType;
import sacred.alliance.magic.app.ai.Telegram;
import sacred.alliance.magic.app.ai.movement.DestinationHolder;
import sacred.alliance.magic.app.ai.movement.DestinationHolderImpl;
import sacred.alliance.magic.app.ai.movement.TimeTracker;
import sacred.alliance.magic.app.ai.movement.Traveller;
import sacred.alliance.magic.constant.AIMoveConstant;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.Point;


/**
 * 随机走
 * @author tiefengKuang 
 * @date 2009-11-5 
 * @version 0.0.0.1
 * @changeLog
 *  1) create: 2009-11-5
 */
public class StateRandomMove extends NpcState{
	
	TimeTracker i_nextMoveTime = new TimeTracker(10000);
	static Random ran = new Random();
	AbstractRole role;
	DestinationHolder holder = new DestinationHolderImpl();
	TimeTracker timeTracker = new TimeTracker(0);
	
	private Point getNextMovePoint(AbstractRole role) {
		int mapX = role.getMapX();
		int mapY = role.getMapY();
		int RANDOM_MOVE_RANK = AIMoveConstant.RANDOM_MOVE_RANK;
		int ranX = ran.nextInt(RANDOM_MOVE_RANK);
		int ranY = ran.nextInt(RANDOM_MOVE_RANK);
		int destX, destY;

		if (ranX >= RANDOM_MOVE_RANK / 2) {
			destX = mapX + ranX;
		} else {
			destX = mapX - ranX - 1;
		}
		if (ranY >= RANDOM_MOVE_RANK / 2) {
			destY = mapY + ranY;
		} else {
			destY = mapY - ranY - 1;
		}
		if (destX < 0) {
			destX = 0;
		}
		if (destY < 0) {
			destY = 0;
		}
		/**
		 * 如果随机走的位置有阻挡，则留在原地
		 */
		/*if(role.getMapInstance().getMap().isBlock(destX, destY)){
			System.out.println("被阻挡了!!!!!!!!!desX="+destX+" destY="+destY+" mapX="+mapX+" mapY="+mapY);
			return new Point(role.getMapId(), mapX, mapY);
		}
		*/
		return new Point(role.getMapId(), destX, destY);
	}


	@Override
	public void enter(NpcInstance entity) {
//        entity.getBehavior().resetHpMp();
//        entity.setOwnerInstance(null);
		entity.getAi().resetStateParameter();
        this.role = entity ;
        Traveller traveller = new Traveller(entity);
        holder.setDestPoint(traveller);
	}

	@Override
	public void doExecute(NpcInstance entity) {
		/**
		 * 只能在家附近走
		 */
		/*if(entity.getAi().tooFarFromHome()){
			entity.getAi().enterEvadeMode();
            return ;
		}*/
//		Point point = this.getNextMovePoint(entity);
//		entity.getBehavior().move(point.getX(),point.getY(), MovementFlagType.MOVEMENTFLAG_WALK_MODE);
		if(!entity.getBehavior().canMove()){
			return;
		}
		Traveller traveller = new Traveller(role);
		//long time_diff = role.getMapInstance().getWorldTime().getTimeDiff();
		long time_diff = this.getTimeDiff(entity);
		if(holder.updateTraveller(traveller, time_diff)){
				traveller = new Traveller(role);
				Point point;
				if(!entity.getAi().nearFromHome()){
					point = entity.getRebornPoint();
				}else{
					point = this.getNextMovePoint(entity);
				}
				long travel_time = holder.setDestination(traveller, point, role.getSpeed()
						/*SpeedType.slow.getSpeed(role.getRoleType())*/);
				timeTracker.reset(travel_time);
		}
		
	}

	@Override
	public void exit(NpcInstance entity) {
		Point p = holder.getDestination();
		entity.getBehavior().move(p.getX(), p.getY(), 0);
	}

	@Override
	public void onMessage(NpcInstance entity, Telegram telegram) {
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


	@Override
	public StateType getStateType() {
		return StateType.State_Random_Move ;
	}

}
