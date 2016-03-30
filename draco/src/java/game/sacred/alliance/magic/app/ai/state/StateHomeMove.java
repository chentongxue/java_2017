package sacred.alliance.magic.app.ai.state;

import com.game.draco.GameContext;
import com.game.draco.app.npc.domain.NpcInstance;
import com.game.draco.app.npc.type.NpcActionType;

import sacred.alliance.magic.app.ai.State;
import sacred.alliance.magic.app.ai.StateType;
import sacred.alliance.magic.app.ai.Telegram;
import sacred.alliance.magic.app.ai.movement.DestinationHolder;
import sacred.alliance.magic.app.ai.movement.DestinationHolderImpl;
import sacred.alliance.magic.app.ai.movement.TimeTracker;
import sacred.alliance.magic.app.ai.movement.Traveller;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.Point;

public class StateHomeMove extends NpcState {

	DestinationHolder holder = new DestinationHolderImpl();

	TimeTracker timeTracker = new TimeTracker(0);

	@Override
	public void enter(NpcInstance entity) {
		this.setTargetLocation(entity);
	}

	@Override
	public void doExecute(NpcInstance entity) {
		//有可能当前怪物中了一个buff无法行走，也可以不要，怪物要回家，谁也挡不住
//		if(entity.getBehavior().canMove())return;
		//long time_diff = entity.getMapInstance().getWorldTime().getTimeDiff();
		long time_diff = this.getTimeDiff(entity);
	    Traveller traveller = new Traveller(entity);
	    holder.updateTraveller(traveller, time_diff);
        if(holder.hasArrived()){
            //回到家转换到默认状态
            //停止
        	if(!entity.getAi().nearFromHome()){
        		this.setTargetLocation(entity);
        		return;
        	}
            entity.getBehavior().resetHpMp();
            //清仇恨列表
            entity.getHatredTarget().clearHatredMap();
        	//System.out.println("到达home~~~~~mapX"+entity.getMapX()+" mapY"+entity.getMapY());
            if(entity.getNpcActionType() != NpcActionType.ROOT){
            	entity.getBehavior().stopMove();
            }
            State defaultState = GameContext.getGameContext().getAiApp().getNpcDefaultState(entity);
            entity.getAi().getStateMachine().switchState(defaultState);
            return ;
        }
	    if (timeTracker.passed()){
	        return ;
	    }
	    timeTracker.update(time_diff);
	}

	@Override
	public void exit(NpcInstance entity) {
		//Point p = holder.getDestination();
		//entity.getBehavior().move(p.getX(), p.getY(), 0);
	}

	@Override
	public StateType getStateType() {
		return StateType.State_Home_Move ;
	}

	@Override
	public void onMessage(NpcInstance entity, Telegram telegram) {
		// 返回家状态过程中NPC不理会任何消息

	}

	private void setTargetLocation(AbstractRole role){
		Point rebornPoint = role.getRebornPoint();
		Traveller traveller = new Traveller(role);
		
		long travel_time = holder.setDestination(traveller, rebornPoint);
		
		timeTracker.reset(travel_time);
		
	}
}
