package sacred.alliance.magic.app.ai;

import java.util.List;

import com.game.draco.app.npc.domain.NpcInstance;
import com.game.draco.app.skill.domain.RoleSkillStat;
import com.game.draco.message.item.NpcFunctionItem;

import sacred.alliance.magic.base.SkillApplyResult;
import sacred.alliance.magic.constant.AIMoveConstant;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.AbstractRole;

public class DefaultAi extends Ai{
    
	public DefaultAi(){
		this("");
	}
	
	public DefaultAi(String aiId){
		super(aiId);
	}
	
	@Override
	public boolean isInView(AbstractRole target){
        return false ;
    }
	
	@Override
	public boolean isInSummonedView(AbstractRole target){
		return false;
	}

	@Override
	public void beSummoned(AbstractRole summonRole) {
		
	}

	@Override
	public void canReachByRangeAttack(AbstractRole attack) {
		
	}


	@Override
	public void enterEvadeMode() {
		
	}

	@Override
	public void justDied() {
		
	}

	@Override
	public void justRespawned() {
		
	}

	@Override
	public void justSummoned() {
		
	}

	@Override
	public void killedRole(AbstractRole target) {
		
	}

	@Override
	public void moveInLineOfSight(AbstractRole target) {
		
	}

	@Override
	public void movementInform() {
		
	}

	@Override
	public void summonedRole() {
		
	}

	@Override
	public List<NpcFunctionItem> talkTo(AbstractRole target) {
		return null ;
	}

	@Override
	public void updateAI() {
		if(null == this.getStateMachine()){
			return ;
		}
		getStateMachine().update();
	}

	@Override
	public void chasedTarget(AbstractRole chaser) {
		
	}


	@Override
	public void attackStart(AbstractRole attacker, int hatred) {
		
	}

    @Override
    public SkillSelectResult selectSkill() {
        return null ;
    }



	@Override
	public void init() {

	}

    @Override
    public void enterEscapeMode() {
    }

    @Override
    public boolean escapeConditions() {
        return false ;
    }

    @Override
    public boolean summonedConditions() {
        return false ;
    }
    
    @Override
    public boolean escapeSummonedConditions(){
    	return false;
    }

    @Override
    public void receiveSummonMsg(AbstractRole summonRole) {
    }

    @Override
    public void resetStateParameter() {
        
    }

    @Override
    public boolean inAttackRange(AbstractRole target) {
    	int attackRange = AIMoveConstant.ATTACK_RANGE ;
        return Util.inCircle(role.getMapX(), role.getMapY(),target.getMapX(), target.getMapY(), attackRange);
    }

    @Override
    public boolean isActiveAttack() {
        return false ;
    }

    @Override
    public boolean isOutOfView(AbstractRole target) {
        return false ;
    }

     @Override
     public boolean tooFarFromHome() {
       return false ;
    }
     
     @Override
     public boolean tooFarFromHome(AbstractRole target) {
       return false ;
    }
     
     @Override
     public boolean nearFromHome() {
    		// TODO Auto-generated method stub
    		return false;
    	}

	@Override
	public int selectRescueSkill() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void respondSeekRescue(AbstractRole seeker) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<NpcFunctionItem> choice(AbstractRole target, int index) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public int getAlertArea() {
		return 0 ;
	}

	@Override
	public int getThinkArea() {
		return 0;
	}

	@Override
	public SkillApplyResult useSkill(NpcInstance entity,int skillId) {
		return null;
	}

	@Override
	public String afterContent(AbstractRole target) {
		return null;
	}

	@Override
	public void npcDiedEncouragement() {
		
	}

	@Override
	public void damageTaken(AbstractRole attacker, int hurt) {
		
	}

	@Override
	public RoleSkillStat getNormalSkill() {
		return null;
	}


}
