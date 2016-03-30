/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sacred.alliance.magic.app.ai.state;

import java.util.Random;

import com.game.draco.app.npc.domain.NpcInstance;

import sacred.alliance.magic.app.ai.StateFactory;
import sacred.alliance.magic.app.ai.StateType;
import sacred.alliance.magic.constant.AIMoveConstant;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.Point;

/**
 *
 * @author Administrator
 */
public class StateEscape extends StatePointMove{

	static Random ran = new Random();
	
    @Override
    public StateType getStateType() {
        return StateType.State_Escape ;
    }

    public StateEscape(AbstractRole role){
    	//获得逃跑点,原来是NPC出生点但那样有问题,NPC逃到此点,
    	//目标可能已经脱离视野,从仇恨中删除
    	//这样NPC逃跑后将不能再次回来(更改:到达后不进入战斗状态而进入追加状态,
    	//或者在战斗状态中中对视野做处理)
        super(role.getRebornPoint());
    	//super(getNextMovePoint(role));
    }

    /**
     * 1. 到此点不能使仇恨列表中用户脱离视野范围
     * 2. 不能离出生点太远(可以朝出生点方向逃,并且远离仇恨方向)
     * 3. 不能有阻挡
     * @param role
     * @return
     */
	private static Point getNextMovePoint(AbstractRole role) {
		int mapX = role.getMapX();
		int mapY = role.getMapY();
		int alertArea = role.getAi().getAlertArea();
		int RANDOM_MOVE_RANK = AIMoveConstant.ESCAPE_MOVE_RANK;
		if(alertArea > 0){
			RANDOM_MOVE_RANK = Math.min(alertArea, AIMoveConstant.ESCAPE_MOVE_RANK);
		}
		
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
		return new Point(role.getMapId(), destX, destY);
	}
    
    @Override
	public void enter(NpcInstance entity) {
		super.enter(entity);
        entity.getAi().enterEscapeMode();
	}
    
    @Override
    public void doExecute(NpcInstance entity){
    	super.doExecute(entity);
    }

   public void movementInform(NpcInstance entity){
         //逃跑到达后，进入战斗状态
       //1. 决定是否在逃跑中呼叫
       if(entity.getAi().escapeSummonedConditions()){
           entity.getAi().summonedRole();
       }
       entity.getAi().getStateMachine().switchState(StateFactory.getState(StateType.State_Battle, entity, null));
    }

}
