package sacred.alliance.magic.app.attri.calct;

import java.util.HashMap;
import java.util.Map;

import sacred.alliance.magic.app.ai.config.AutoMaxHp;
import sacred.alliance.magic.app.attri.AttriBuffer;
import sacred.alliance.magic.app.attri.AttriItem;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.util.Util;

import com.game.draco.GameContext;
import com.game.draco.app.npc.domain.NpcInstance;

public class NpcCalct extends DefaultCalct<NpcInstance> {
	private NpcFormulaCalct npcFormulaCalct;
	@Override
	public void bornAtrri(NpcInstance role) {
		reCalct(role);
		role.setCurHP(role.getMaxHP());
	}
	
	
	public static int getMaxHp(NpcInstance role,int baseHp){
		Map<Integer,Float> config = GameContext.getAiApp().getAutoMaxHpConfig(
				role.getNpc().getNpcid());
		if(Util.isEmpty(config)){
			return baseHp ;
		}
		int attackerNum = Math.min(AutoMaxHp.MAX_NUM, role.getEffectAttackerNum());
		Float f = config.get(attackerNum);
		if(null == f){
			return baseHp ;
		}
		return (int)(baseHp * f) ;
	}
	

	@Override
	protected int getByLevelup(NpcInstance role, AttributeType attriType) {
		int value = role.getNpc().getAttriValue(attriType.getType());
		if(AttributeType.maxHP != attriType){
			return value ;
		}
		return getMaxHp(role,value);
	}

	@Override
	protected Map<Byte, AttriItem> getByMultAdvanced(NpcInstance role) {
		 AttriBuffer buffer = GameContext.getUserBuffApp().getAttriBuffer(role);
		 if(null == buffer){
			 return new HashMap<Byte,AttriItem>() ;
		 }
		 return buffer.getMap();
	}

	

	@Override
	protected void autoUpgrade(NpcInstance role) {
		
	}

	@Override
	protected int getFormulaCalct(NpcInstance role, AttributeType attriType) {
		return npcFormulaCalct.getBaseValue(attriType);
	}

	@Override
	protected void otherEffect(NpcInstance role, AttriBuffer buffer) {
		
	}

	@Override
	protected void changeExp(NpcInstance role, AttriItem item) {
		
	}

	public void setNpcFormulaCalct(NpcFormulaCalct npcFormulaCalct) {
		this.npcFormulaCalct = npcFormulaCalct;
	}

	@Override
	protected void changeLevel(NpcInstance role, AttriItem item) {
		
	}
	
	

	
}
