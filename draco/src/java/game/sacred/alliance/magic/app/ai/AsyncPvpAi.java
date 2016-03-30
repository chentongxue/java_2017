package sacred.alliance.magic.app.ai;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.ai.config.AsyncPvpAiConfig;
import sacred.alliance.magic.base.ForceRelation;
import sacred.alliance.magic.base.SkillApplyResult;
import sacred.alliance.magic.constant.AIMoveConstant;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.MapInstance;

import com.game.draco.GameContext;
import com.game.draco.app.npc.domain.NpcInstance;
import com.game.draco.app.npc.type.NpcActionType;
import com.game.draco.app.skill.domain.RoleSkillStat;
import com.game.draco.app.skill.vo.Skill;

public class AsyncPvpAi extends DefaultAi {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
    public AsyncPvpAi() {
        super("");
    }

    public AsyncPvpAi(String aiId) {
        super(aiId);
    }
    private AsyncPvpAiConfig aiConfig;

    public void init() {
        //相关状态变量reset
        this.resetStateParameter();
        if(null == aiConfig){
        	logger.error("aiConfig template not config ,aiId=" + aiId + " pls check it");
        	return ;
        }
        //添加技能
        role.getSkillMap().clear();
        for (RoleSkillStat skillStat : aiConfig.getRoleSkillList()) {
        	short skillId = skillStat.getSkillId();
            Skill skill = GameContext.getSkillApp().getSkill(skillId);
            if (null == skill) {
                continue;
            }
            role.getSkillMap().put((short)skillId, skillStat);
        }
        //设置全局状态
        role.getAi().getStateMachine().setGlobalState(StateFactory.getState(StateType.State_Global, role, null));
        //actionId 由Ai决定
        NpcInstance npcRole = (NpcInstance) role ;
        npcRole.setNpcActionType(NpcActionType.getType(aiConfig.getActionId()));
        //设置默认状态
        State defaultState = GameContext.getAiApp().getNpcDefaultState(npcRole);
        role.getAi().getStateMachine().switchState(defaultState);
        //设置攻击距离
        npcRole.setThinkArea(aiConfig.getThinkArea());
    }

    @Override
    public boolean isInView(AbstractRole target) {
        if (null == target) {
            return false;
        }
        MapInstance targetMapInstance = target.getMapInstance();
        if(null == targetMapInstance){
        	return false ;
        }
        if (!role.getMapInstance().getInstanceId().equals(targetMapInstance.getInstanceId())) {
            return false;
        }
        
        //如果目标在仇恨列表中则视为可见
        if(role.getHatredTarget().inHatredMap(target.getRoleId()))return true;
        //判断是否有碰撞
        boolean baffle = role.getMapInstance().getMap().hasBaffle(role.getMapX(), role.getMapY(), target.getMapX(), target.getMapY());
        if (!baffle) {
            return Util.inCircle(role.getMapX(), role.getMapY(), target.getMapX(), target.getMapY(), aiConfig.getAlertArea());
        }
        return false;
    }
    
    @Override
    public void chasedTarget(AbstractRole chaser) {
        //发送停止消息
        role.getBehavior().stopMove();
        //追逐上目标
        if (ForceRelation.enemy == role.getForceRelation(chaser)) {
            //如果目标是敌对关系则进入战斗模式
            this.stateMachine.switchState(StateFactory.getState(StateType.State_Battle, role, chaser));
        }
    }

    @Override
    public void canReachByRangeAttack(AbstractRole attack) {
        //判断是否追击
        if (!this.isActiveAttack()) {
            return;
        }
        //主动怪
        //TODO: 追击
    }
    
    @Override
    public SkillSelectResult selectSkill() {
    	NpcInstance npcRole = (NpcInstance) role ;
    	SkillSelectResult result = new SkillSelectResult();
    	List<RoleSkillStat> skillList = aiConfig.getRoleSkillList();
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
    
    
    @Override
    public boolean isActiveAttack() {
        return aiConfig.isInitiative();
    }

  
    @Override
    public void resetStateParameter() {
        
        NpcInstance npc = (NpcInstance)role ;
        //清除buff
        npc.clearState();
        npc.getReceiveBuffCopy().clear();
        //重算属性
        GameContext.getUserAttributeApp().reCalct(npc);
        role.getBehavior().notifyAttribute();
       // role.getBehavior().resetHpMp();
        role.getHatredTarget().clearHatredMap();
        npc.setOwnerInstance(null);
        npc.setTarget(null);
        npc.setBossState(null);
        npc.setTimeRecords(null);
        //重置技能CD
        for(RoleSkillStat stat : npc.getSkillMap().values()){
        	stat.setLastProcessTime(0);
        }
    }

    public void attackStart(AbstractRole attacker, int hatred) {
    	if(null == attacker){
    		return ;
    	}
        role.getMapInstance().getMessageDispatcher().dispatch(new Telegram(attacker, (NpcInstance) role, MessageType.ATTACK, 0, hatred));
    }

    public void enterEvadeMode(){
        this.resetStateParameter();
        //离出生点太远，进入回家状态
        //如果不是巡逻者并且离家不近，则回家
        
        if(aiConfig.getActionId() != NpcActionType.PATROLMAN.getType()
        		/*&& !role.getAi().nearFromHome()*/){
        	/***
        	 * !role.getAi().nearFromHome() 将注释的原因:
        	 * 发现对怪放风筝的时候,怪有可能直接转到了默认状态,导致怪的位置没有同步
        	 * 客户端看到的事怪不真实的位置
        	 */
            role.getAi().getStateMachine().switchState(StateFactory.getState(StateType.State_Home_Move, role, null));
            return ;
        }
        //如果是巡逻者或者离家很近则进入默认状态，如果是ROOT则回到IDLE状态
        State defaultState = GameContext.getAiApp().getNpcDefaultState((NpcInstance)role);
        role.getAi().getStateMachine().switchState(defaultState);
        
    }

    public void moveInLineOfSight(AbstractRole target) {
        if (ForceRelation.enemy == role.getForceRelation(target)) {
            //如果目标是敌对关系则进入战斗模式
        	//role.getHatredMap().put(target.getRoleId(), AIMoveConstant.MOVE_IN_LINE_SIGHT_HATRED);
        	role.getHatredTarget().addHatred(target, AIMoveConstant.MOVE_IN_LINE_SIGHT_HATRED);
            role.getAi().getStateMachine().switchState(StateFactory.getState(StateType.State_Battle, role, target));
        }
    }

     @Override
    public boolean isOutOfView(AbstractRole target) {
    	if(target==null)return true;
        if(!role.getMapId().equals(target.getMapId())){
            return true ;
        }
        //如果目标在仇恨列表中则视为可见
        if(role.getHatredTarget().inHatredMap(target.getRoleId()))return false;
        return !Util.inCircle(role.getMapX(), role.getMapY(), target.getMapX(), target.getMapY(), aiConfig.getAlertArea());
    }

    @Override
    public boolean inAttackRange(AbstractRole target) {
    	//追击思考距离,即最大攻击距离
    	int attackRange = this.getThinkArea();
        return Util.inCircle(role.getMapX(), role.getMapY(),target.getMapX(), target.getMapY(), attackRange);
    }
    
    @Override
	public int getAlertArea() {
		return aiConfig.getAlertArea() ;
	}

    @Override
	public int getThinkArea() {
    	return ((NpcInstance)role).getThinkArea() ;
	}
    
    @Override
    public SkillApplyResult useSkill(NpcInstance entity,int skillId) {
    	return GameContext.getUserSkillApp().useSkill(entity, (short) skillId);
    }

	public AsyncPvpAiConfig getAiConfig() {
		return aiConfig;
	}

	public void setAiConfig(AsyncPvpAiConfig aiConfig) {
		this.aiConfig = aiConfig;
	}
	
}
