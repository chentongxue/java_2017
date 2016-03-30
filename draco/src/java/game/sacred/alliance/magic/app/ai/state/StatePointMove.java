package sacred.alliance.magic.app.ai.state;

import com.game.draco.app.npc.domain.NpcInstance;

import sacred.alliance.magic.app.ai.StateType;
import sacred.alliance.magic.app.ai.Telegram;
import sacred.alliance.magic.app.ai.movement.DestinationHolder;
import sacred.alliance.magic.app.ai.movement.DestinationHolderImpl;
import sacred.alliance.magic.app.ai.movement.Traveller;
import sacred.alliance.magic.vo.Point;


public class StatePointMove extends NpcState{

	private Point destPoint;
	private DestinationHolder holder = new DestinationHolderImpl();

	public StatePointMove(Point destPoint){
		this.destPoint = destPoint ;
	}
	
	@Override
	public void enter(NpcInstance entity) {
		Traveller traveller = new Traveller(entity);
		holder.setDestination(traveller, destPoint);
	}

	@Override
	public void doExecute(NpcInstance entity) {
		//有可能当前怪物中了一个buff无法行走
		if(!entity.getBehavior().canMove()){
			return;
		}
		long time_diff = this.getTimeDiff(entity);
	    Traveller traveller = new Traveller(entity);
	    holder.updateTraveller(traveller, time_diff);
	    if(holder.hasArrived()){
           movementInform(entity);
	    }
	}

    public void movementInform(NpcInstance entity){
         entity.getAi().movementInform();
	    //entity.getBehavior().stopMove();
    }
    
	@Override
	public void exit(NpcInstance entity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public StateType getStateType() {
		return StateType.State_Point_Move ;
	}

	@Override
	public void onMessage(NpcInstance entity, Telegram telegram) {
		// TODO Auto-generated method stub
		
	}

}
