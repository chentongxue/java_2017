package com.game.draco.app.skill;

import java.util.List;

import sacred.alliance.magic.app.attri.AttriBuffer;
import sacred.alliance.magic.base.SkillApplyResult;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.app.AppSupport;
import com.game.draco.app.npc.domain.NpcInstance;
import com.game.draco.app.skill.config.SkillSourceType;
import com.game.draco.app.skill.domain.RoleSkillStat;
import com.game.draco.app.skill.func.SkillLearnFunc;
import com.game.draco.message.item.RoleSkillItem;

public interface UserSkillApp extends AppSupport{
	
	public SkillApplyResult useSkill(AbstractRole role, short skillId);
	
	public SkillApplyResult useSkillCondition(AbstractRole role, short skillId,boolean systemTrigger,boolean judgeUseCond);
	
	public SkillApplyResult deathUseSkill(AbstractRole role, short skillId);
	
	public SkillApplyResult useSkill(AbstractRole role, short skillId,int clientDelay);
	
	/**
	 * 处理客户端主动请求300协议
	 * @param role
	 * @param skillId
	 * @param clientDelay
	 * @param skillActiveApply
	 * @return
	 */
	public SkillApplyResult useSkill(AbstractRole role, short skillId,int clientDelay,
			boolean skillActiveApply);
		
	public RoleSkillStat getSkillStat(AbstractRole role, short skillId);
	
	public List<RoleSkillStat> selectRoleSkillList(String roleId);

    public void addNpcSkill(NpcInstance npcInstance,short skillId,int skillLevel);
    
    
    public SkillApplyResult bossUseSkill(AbstractRole role, short skillId);
    
    public AttriBuffer getAttriBuffer(AbstractRole role);
    
	/**
	 * 角色升级通知技能消耗发生变化
	 * @param role
	 */
	public void roleLevelup(AbstractRole role) ;
	
	/**得到角色的技能*/
	public List<RoleSkillItem> getRoleSkillItemList(RoleInstance role);
	
	public SkillLearnFunc getSkillLearnFunc(SkillSourceType skillSourceType);
	
	public long getLastProcessTimeFromCache(String roleId, short skillId);
	
	public int getSkillLevelNum(RoleInstance role,int level) ;
	
}
