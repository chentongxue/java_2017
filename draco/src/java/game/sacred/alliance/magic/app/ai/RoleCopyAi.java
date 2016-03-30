package sacred.alliance.magic.app.ai;

import java.util.Collection;

import sacred.alliance.magic.base.SkillApplyResult;

import com.game.draco.GameContext;
import com.game.draco.app.npc.domain.NpcInstance;
import com.game.draco.app.skill.domain.RoleSkillStat;
import com.game.draco.app.skill.vo.Skill;

public class RoleCopyAi extends ConfigurableAi {
	public RoleCopyAi() {
		super("");
	}
	
	public RoleCopyAi(String aiId) {
		super(aiId);
	}
	
	@Override
	public SkillSelectResult selectSkill() {
		NpcInstance npcRole = (NpcInstance) role ;
    	SkillSelectResult result = new SkillSelectResult();
    	Collection<RoleSkillStat> skillList = npcRole.getSkillMap().values();
    	for(RoleSkillStat skillStat : skillList) {
    		if(null == skillStat) {
    			continue;
    		}
    		Skill skill = GameContext.getSkillApp().getSkill(skillStat.getSkillId());
    		if(null == skill) {
    			continue;
    		}
    		if(!skill.isActiveSkill()) {
    			continue;
    		}
    		SkillApplyResult con = skill.condition(role);
    		if(SkillApplyResult.SUCCESS == con){
				result.setSuccessSkill(skill);
				return result;
			}
    	}
    	//无可用技能
    	return null;
	}
}
