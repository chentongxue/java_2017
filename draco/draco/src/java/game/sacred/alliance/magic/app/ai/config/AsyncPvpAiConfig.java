
package sacred.alliance.magic.app.ai.config;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import sacred.alliance.magic.constant.SkillConstant;
import sacred.alliance.magic.util.Util;

import com.game.draco.GameContext;
import com.game.draco.app.asyncpvp.domain.AsyncPvpRoleAttr;
import com.game.draco.app.npc.type.NpcActionType;
import com.game.draco.app.skill.config.SkillDetail;
import com.game.draco.app.skill.domain.RoleSkillStat;
import com.game.draco.app.skill.vo.Skill;

public @Data class AsyncPvpAiConfig implements AiConfig {
    private String id;
    private int actionId = NpcActionType.GUARD.getType();
    /**是否主动怪物*/
    private boolean initiative = true;
    /**警戒距离(像素)*/
    private int alertArea = -1;
    /**是否追逐*/
    private boolean chase = true;
    /**追击距离(像素)*/
    private int chaseArea = -1;
    private int thinkArea ;
    private int soulThinkArea ;
    private AsyncPvpRoleAttr asyncPvpRoleAttr;
    private List<RoleSkillStat> roleSkillList = new ArrayList<RoleSkillStat>();
    
	public void init() {
		asyncPvpRoleAttr.initSkill();
		this.roleSkillList = asyncPvpRoleAttr.getSkillList();
        //最小攻击距离
        this.thinkArea = getMinRange(roleSkillList);
    }
	
	private int getMinRange(List<RoleSkillStat> skillList) {
		int minRange = Integer.MAX_VALUE;
		if(Util.isEmpty(skillList)) {
			return SkillConstant.castCloseDistanceForThink;
		}
		for(RoleSkillStat skillStat : skillList) {
			short skillId = skillStat.getSkillId();
    		int skillLevel = skillStat.getSkillLevel();
    		Skill skill = GameContext.getSkillApp().getSkill(skillId);
    		if(null == skill) {
    			continue;
    		}
    		SkillDetail sd = skill.getSkillDetail(skillLevel);
    		if(!skill.isActiveSkill()) {
    			continue;
    		}
    		int skillMinRange = sd.getMaxUseRange();
    		if(minRange < skillMinRange) {
    			continue;
    		}
    		minRange = skillMinRange;
    	}
		return minRange;
	}
}
