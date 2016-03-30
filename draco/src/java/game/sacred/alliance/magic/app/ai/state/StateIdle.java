package sacred.alliance.magic.app.ai.state;

import com.game.draco.app.npc.domain.NpcInstance;

import sacred.alliance.magic.app.ai.StateFactory;
import sacred.alliance.magic.app.ai.StateType;
import sacred.alliance.magic.app.ai.Telegram;
import sacred.alliance.magic.base.RoleType;

/**
 * 空闲状态
 * @author tiefengKuang 
 * @date 2009-11-5 
 * @version 0.0.0.1
 * @changeLog
 *  1) create: 2009-11-5
 */
public class StateIdle extends NpcState {

    @Override
    public void enter(NpcInstance entity) {
    	//进入默认装备不再进行属性重算
    	//调用方地方已经计算过，重复调用
    	//entity.getAi().resetStateParameter();
    }

    @Override
    public void doExecute(NpcInstance entity) {
    	if(entity.getRoleType() != RoleType.COPY) {
    		return ;
    	}
    	if(entity.getAi().tooFarFromHome()){
        	entity.getAi().enterEvadeMode();
        	return;
        }
    }

    @Override
    public void exit(NpcInstance entity) {
    }

    @Override
    public void onMessage(NpcInstance entity, Telegram telegram) {
        if (null == telegram || null == entity) {
            return;
        }
        switch (telegram.getType()) {
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
        return StateType.State_Idle;
    }
}
