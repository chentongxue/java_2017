/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sacred.alliance.magic.app.ai.state;

import com.game.draco.app.npc.domain.NpcInstance;

import sacred.alliance.magic.app.ai.StateType;
import sacred.alliance.magic.app.ai.Telegram;

/**
 *
 * @author tiefengKuang
 * @time 2010-4-21
 */
public class StateGlobal extends NpcState {

    @Override
    public StateType getStateType() {
        return StateType.State_Global ;
    }

    @Override
    public void enter(NpcInstance entity) {
    }

    @Override
    public void doExecute(NpcInstance entity) {
    }

    @Override
    public void exit(NpcInstance entity) {
    }

    @Override
    public void onMessage(NpcInstance entity, Telegram telegram) {
        switch(telegram.getType()){
           case JUSTDIE:
               //从仇恨列表中清除死亡的目标
        	   entity.getHatredTarget().removeHateTarget(telegram.getExtraInfo().toString());
            break ;
        }
    }

}
