package sacred.alliance.magic.app.ai;

import sacred.alliance.magic.app.ai.state.StateBattle;
import sacred.alliance.magic.app.ai.state.StateEscape;
import sacred.alliance.magic.app.ai.state.StateGlobal;
import sacred.alliance.magic.app.ai.state.StateHomeMove;
import sacred.alliance.magic.app.ai.state.StateIdle;
import sacred.alliance.magic.app.ai.state.StatePointMove;
import sacred.alliance.magic.app.ai.state.StateRandomMove;
import sacred.alliance.magic.app.ai.state.StateTargetedMove;
import sacred.alliance.magic.app.ai.state.StateWayPointMove;
import sacred.alliance.magic.constant.LoopConstant;
import sacred.alliance.magic.scheduler.job.LoopCount;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.Path;
import sacred.alliance.magic.vo.Point;

public class StateFactory {

	public static State getState(StateType stateType,AbstractRole role,Object extroInfo){
		if(stateType == StateType.State_Random_Move){
			StateRandomMove state = new StateRandomMove();
			state.setLoopCount(new LoopCount(LoopConstant.NPC_RANDOM_MOVE_CYCLE));
			return state ;
		}
		if(stateType == StateType.State_Targeted_Move){
			StateTargetedMove state = null ;
			//if(null == extroInfo){
				//state = new StateTargetedMove();
			//}else{
				state = new StateTargetedMove(Integer.parseInt(extroInfo.toString()));
			//}
			state.setLoopCount(new LoopCount(LoopConstant.NPC_TARGETED_MOVE_CYCLE));
			return state ;
		}
		if(stateType == StateType.State_Point_Move){
			StatePointMove state = new StatePointMove((Point)extroInfo);
			state.setLoopCount(new LoopCount(LoopConstant.NPC_DEFAULT_CYCLE));
			return state ;
		}
		if(stateType == StateType.State_WayPoint_Move){
            //这个必须要多态
			StateWayPointMove state = new StateWayPointMove((Path)extroInfo);
			state.setLoopCount(new LoopCount(LoopConstant.NPC_DEFAULT_CYCLE));
			return state ;
		}
		if(stateType == StateType.State_Home_Move){
			StateHomeMove state = new StateHomeMove();
			state.setLoopCount(new LoopCount(LoopConstant.NPC_DEFAULT_CYCLE));
			return state ;
		}
		if(stateType == StateType.State_Battle){
			//这个必须多态
			StateBattle state = new StateBattle();
			state.setLoopCount(new LoopCount(LoopConstant.NPC_DEFAULT_CYCLE));
			return state ;
		}
        if(stateType == StateType.State_Global){
        	StateGlobal state = new StateGlobal();
            state.setLoopCount(new LoopCount(LoopConstant.NPC_DEFAULT_CYCLE));
            return state ;
        }
        if(stateType == StateType.State_Escape){
        	//这个必须多态
        	StateEscape state = new StateEscape(role);
        	state.setLoopCount(new LoopCount(LoopConstant.NPC_ESCAPE_CYCLE));
        	return state ;
        }
        StateIdle state = new StateIdle();
		state.setLoopCount(new LoopCount(LoopConstant.NPC_DEFAULT_CYCLE));
		return state ;
	}
}
