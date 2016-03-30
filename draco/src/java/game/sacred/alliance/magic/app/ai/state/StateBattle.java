package sacred.alliance.magic.app.ai.state;

import java.util.Iterator;
import java.util.Map;

import sacred.alliance.magic.app.ai.BossAction;
import sacred.alliance.magic.app.ai.SkillSelectResult;
import sacred.alliance.magic.app.ai.StateFactory;
import sacred.alliance.magic.app.ai.StateType;
import sacred.alliance.magic.app.ai.Telegram;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.Direction;
import sacred.alliance.magic.base.OperatorType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.SkillApplyResult;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.AbstractRole;

import com.game.draco.GameContext;
import com.game.draco.app.npc.domain.NpcInstance;
import com.game.draco.app.npc.domain.NpcTemplate;
import com.game.draco.app.npc.type.NpcActionType;
import com.game.draco.app.skill.vo.Skill;

/**
 * NPC战斗模式
 */
public class StateBattle extends NpcState{
	private final int ADJUST_MAXHP_MAX_COUNT = 3 ;
	private int adjustMaxHpCount = 0 ;
	
	@Override
	public void enter(NpcInstance entity) {
	}

	private void adjustMaxHp(NpcInstance entity) {
		if (!entity.getAi().isAutoMaxHp()) {
			return;
		}
		if (adjustMaxHpCount < ADJUST_MAXHP_MAX_COUNT) {
			adjustMaxHpCount++;
			return;
		}
		adjustMaxHpCount = 0;
		int attackerNum = (null == entity.getAllAttacker()) ? 0 : entity
				.getAllAttacker().size();
		if (attackerNum <= 0) {
			return;
		}
		int effectAttackerNum = entity.getEffectAttackerNum();
		if (effectAttackerNum >= attackerNum) {
			return;
		}
		String npcId = entity.getNpc().getNpcid();
		Map<Integer, Float> configMap = GameContext.getAiApp()
				.getAutoMaxHpConfig(npcId);
		if (Util.isEmpty(configMap)) {
			return;
		}
		attackerNum = Math.min(attackerNum, configMap.size() - 1);
		
		Float nowValue = configMap.get(attackerNum);
		if (null == nowValue) {
			return;
		}
		Float effectValue = configMap.get(effectAttackerNum);
		if (null == effectValue) {
			// 其实不会发生
			return;
		}
		if (effectValue.compareTo(nowValue) == 0) {
			return;
		}
		NpcTemplate npcTemplate = GameContext.getNpcApp().getNpcTemplate(npcId);
		if (null == npcTemplate) {
			return;
		}
		int add = (int) (nowValue - effectValue) * npcTemplate.getMaxHP();
		if (add <= 0) {
			return;
		}
		GameContext.getUserAttributeApp().changeAttribute(entity,
				AttributeType.maxHP, OperatorType.Add, add,
				OutputConsumeType.npc_auto_max_hp);
		GameContext.getUserAttributeApp().changeAttribute(entity,
				AttributeType.curHP, OperatorType.Add, add,
				OutputConsumeType.npc_auto_max_hp);
		//甚至npc的有效攻击者数目
		entity.setEffectAttackerNum(attackerNum);
		entity.getBehavior().notifyAttribute();
		
	}
	
	@Override
	public void doExecute(NpcInstance entity) {
        //如果仇恨列表为空 回到默认状态
        //如果仇恨列表不为空,仇恨列表中对象没在自己攻击范围内，进入追击状态
        if(entity.getHatredTarget().isEmptyHatredMap()){
            entity.getAi().enterEvadeMode();
            return ;
        }
        String targetNameId = entity.getHatredTarget().getFirstHateTarget();
        AbstractRole target = entity.getMapInstance().getAbstractRole(targetNameId);
        //!entity.getAi().inHatredMap(targetNameId),不添加上有可能会出现逃跑不再回来
        if(target==null || target.isDeath() || (/*entity.getAi().isOutOfView(target) && */!entity.getHatredTarget().inHatredMap(targetNameId))){
        	entity.getHatredTarget().removeHateTarget(targetNameId);
        	return;
        }
        if(entity.getAi().tooFarFromHome(target)){
        	entity.getHatredTarget().removeHateTarget(targetNameId);
        	entity.getAi().enterEvadeMode();
        	return;
        }
        entity.setTarget(target);
        
        //获得仇恨值第一位是否在攻击范围内
        if(!entity.getAi().inAttackRange(target)){
        	//当角色不能追击，则放弃该目标
        	if(entity.getNpcActionType()==NpcActionType.ROOT){
    			entity.getHatredTarget().removeHateTarget(targetNameId);
            	return;
    		}
        	//当角色因为技能不能移动时，不能移除目标，可能会脱站
        	if(!entity.getBehavior().canMove()){
        		return;
        	}
            //进入追击状态
            entity.getAi().getStateMachine().switchState(StateFactory.getState(StateType.State_Targeted_Move, entity, 
            		entity.getAi().getThinkArea()));
            return ;
        }
        //1. 决定是否呼叫
        if(entity.getAi().summonedConditions()){
            entity.getAi().summonedRole();
        }
        //2. 决定是否逃跑
        if(entity.getBehavior().canMove()&&entity.getAi().escapeConditions()){
            entity.getAi().getStateMachine().switchState(StateFactory.getState(StateType.State_Escape, entity, null));
            return;
        }
        //动态调整maxhp
        this.adjustMaxHp(entity);
        
        if(!entity.getBehavior().canUseSkill() || !entity.getBehavior().canUseCommonSkill()){
        	//当前在不可使用技能状态
        	return ;
        }
        //3. 使用相关技能
        SkillSelectResult result = entity.getAi().selectSkill();
        if(null == result) {
        	return;
        }
        BossAction[][] bas = result.getBossActions();
        if(null == bas){
        	this.doNormalPloy(entity, result);
        }else{
        	//boss
        	this.doBossPloy(entity, result);
        }
	}
	
	private void doBossPloy(NpcInstance entity,SkillSelectResult result){
		BossAction[][] bas = result.getBossActions();
		long currentTime = System.currentTimeMillis();
		if(null == entity.getBossState()){
			//战斗状态的第周期
			entity.setBossState(new short[bas.length]);
			long[] timeRecords = new long[bas.length];
			for(int i=0;i<timeRecords.length;i++){
				timeRecords[i] = currentTime ;
			}
			entity.setTimeRecords(timeRecords);
		}
		int index = 0 ;
		for(BossAction[] ba : bas){
			this.doBossAction(entity,ba,index,currentTime);
			index ++ ;
		}
	}
	
	//return new state
	private void doBossAction(NpcInstance entity,BossAction[] ba ,int linkIndex,long currentTime){
		short currentState = entity.getBossState()[linkIndex];
		if(currentState < 0){
			//空实现,不做任何事情
			return ;
		}
		BossAction ca = ba[currentState];
		//判断当前时间是否到达
		long enterTime = entity.getTimeRecords()[linkIndex];
		if((currentTime - enterTime) < ca.getInterval()*1000){
			//未到时间
			return ;
		}
		//判断是否符合条件
		boolean ok = ca.execute(entity);
		int newState = ok?ca.getSuccessId():ca.getFailureId();
		//设置行为链中的下次操作行为
		entity.getBossState()[linkIndex] = (short)newState;
		//设置进入下行为的时间
		entity.getTimeRecords()[linkIndex] = currentTime ;
	}
	
	private void doNormalPloy(NpcInstance entity,SkillSelectResult result){
		 //3. 使用相关技能
        if(null == result){
        	return ;
        }
        if(null != result.getSuccessSkill()&&entity.getBehavior().canUseSkill()){
        	if(entity.getTarget() != null){
	        	/**
	             * role.dir要根据坐标来计算
	             */
	            int nowX = entity.getMapX();
	            int nowY = entity.getMapY();
	            entity.setDir(Direction.getDir(entity.getTarget().getMapX(), entity.getTarget().getMapY(), nowX, nowY));
        	}
        	SkillApplyResult skillResult = entity.getAi().useSkill(entity,result.getSuccessSkill().getSkillId());
        	if(skillResult == SkillApplyResult.SUCCESS) {
        		String skillDialogue = result.getSkillDialogue();
            	if(skillDialogue != null && skillDialogue.length() > 0) {
            		entity.getBehavior().notifyNpcMsg(result.getSkillDialogue());
            	}
        	}
        }
        //策略
       //根据失败情况决定切换到某个状态
        this.targetedMovePloy(entity,result);
        result.release();
        result = null ;
	}
	
	private void targetedMovePloy(AbstractRole entity,SkillSelectResult result){
		Map<Skill,SkillApplyResult> failureMap = result.getFailureMap();
		if(Util.isEmpty(failureMap)){
			return ;
		}
		for(Iterator<Map.Entry<Skill,SkillApplyResult>> it = failureMap.entrySet().iterator();it.hasNext();){
			Map.Entry<Skill,SkillApplyResult> entry = it.next();
			if(SkillApplyResult.DISTANCE_TOO_LONG != entry.getValue()){
				//非距离原因
				continue ;
			}
			Skill failureSkill = entry.getKey();
			//int thinkArea = SkillCastDistanceType.getDistanceForThink(failureSkill.getSkillCastDistanceType());
			//技能的最大施法距离
			int thinkArea = failureSkill.getMaxUseRange(entity) ;
			 //进入追击状态
			if(!entity.getBehavior().canMove()){
				return;
			}
			entity.getAi().getStateMachine().switchState(StateFactory.getState(StateType.State_Targeted_Move,
           		entity,thinkArea));
       		return ;
		}
		
	}

	@Override
	public void exit(NpcInstance entity) {
		//脱离战斗状态
		//entity.getAi().enterEvadeMode();
	}

	@Override
	public StateType getStateType() {
		return StateType.State_Battle;
	}

	@Override
	public void onMessage(NpcInstance entity, Telegram telegram) {
		switch(telegram.getType()){
            case SHOUT :
                //战斗过程中接收到其他人的呼叫不需要复制仇恨列表
            	//但需要看看可否帮他回血
            	entity.getAi().respondSeekRescue(telegram.getSender());
                break ;   
        }
		
	}

}
