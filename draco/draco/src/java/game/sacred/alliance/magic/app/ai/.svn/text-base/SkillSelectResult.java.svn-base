package sacred.alliance.magic.app.ai;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import com.game.draco.app.skill.vo.Skill;

import sacred.alliance.magic.base.SkillApplyResult;

public class SkillSelectResult implements java.io.Serializable{

	private BossAction[][] bossActions = null ;
	/**
	 * 成功选择的技能
	 */
	private Skill successSkill ;
	private Map<Skill,SkillApplyResult> failureMap = new LinkedHashMap<Skill,SkillApplyResult>();
	private String skillDialogue;//使用技能喊话
	public Skill getSuccessSkill() {
		return successSkill;
	}
	public void setSuccessSkill(Skill successSkill) {
		this.successSkill = successSkill;
	}
	public void addFailureSkill(Skill skill,SkillApplyResult reason) {
		failureMap.put(skill, reason);
	}
	
	public void release(){
		this.successSkill = null ;
		this.failureMap.clear();
		this.failureMap = null ;
	}
	
	public Map<Skill, SkillApplyResult> getFailureMap() {
		return failureMap;
	}
	
	
	public String toString(){
		StringBuffer buffer = new StringBuffer("");
		buffer.append("successSkill=");
		if(null == successSkill){
			buffer.append("NULL");
		}else{
			buffer.append(successSkill.getSkillId());
		}
		buffer.append(" failureMap={");
		for(Iterator<Map.Entry<Skill,SkillApplyResult>> it = failureMap.entrySet().iterator();it.hasNext();){
			Map.Entry<Skill,SkillApplyResult> entry = it.next();
			buffer.append(" skillId=" + entry.getKey().getSkillId() + " reason=" + entry.getValue());
		}
		buffer.append("}");
		return buffer.toString();
	}
	public String getSkillDialogue() {
		return skillDialogue;
	}
	public void setSkillDialogue(String skillDialogue) {
		this.skillDialogue = skillDialogue;
	}
	public BossAction[][] getBossActions() {
		return bossActions;
	}
	public void setBossActions(BossAction[][] bossActions) {
		this.bossActions = bossActions;
	}
	
	
}
