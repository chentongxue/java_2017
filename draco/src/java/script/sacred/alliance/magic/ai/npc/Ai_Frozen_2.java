package sacred.alliance.magic.ai.npc;

import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.npc.domain.NpcInstance;
import com.game.draco.app.speciallogic.config.SpecialLogic;


public class Ai_Frozen_2 extends AbstractNpcAi{
	
	private byte forzenType = 0;
	
	public Ai_Frozen_2(){
		aiId = "01zdfb0038";
	}
	
	@Override
	public void justDied() {
		
		NpcInstance npc = (NpcInstance) this.getRole();
		
		if(Util.isEmpty(npc.getSummonRoleId())){
			return;
		}
		
		String key = forzenType + Cat.underline + npc.getMapId();
		SpecialLogic logic = GameContext.getSpecialLogicApp().getSpecialLogic(key);
		
		if(logic == null){
			return;
		}
		
		if(Util.isEmpty(logic.getBuffId())){
			return;
		}
		
		if(!npc.getNpcid().equals(logic.getNpcId()) 
				|| !npc.getMapId().equals(logic.getMapId())){
			return;
		}
		
		String roleId = npc.getSummonRoleId();
		RoleInstance r = GameContext.getOnlineCenter().getRoleInstanceByRoleId(roleId);
		String [] buffArr = logic.getBuffId().split(",");
		for(String buffId : buffArr){
			GameContext.getUserBuffApp().delBuffStat(r, Short.parseShort(buffId),false);
		}
		
		super.justDied();
		
	}
	
}
