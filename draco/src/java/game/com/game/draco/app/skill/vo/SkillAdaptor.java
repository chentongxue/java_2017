package com.game.draco.app.skill.vo;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.Data;
import sacred.alliance.magic.base.AttrFontColorType;
import sacred.alliance.magic.base.AttrFontSizeType;
import sacred.alliance.magic.base.AttrFontSpecialState;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.ForceRelation;
import sacred.alliance.magic.base.OperatorType;
import sacred.alliance.magic.base.RoleType;
import sacred.alliance.magic.base.SkillApplyResult;
import sacred.alliance.magic.scheduler.job.MapLoop;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.MapInstance;
import sacred.alliance.magic.vo.Point;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.buff.Buff;
import com.game.draco.app.buff.BuffEffect;
import com.game.draco.app.buff.MapBuffEffect;
import com.game.draco.app.buff.stat.BuffStat;
import com.game.draco.app.pet.domain.RolePet;
import com.game.draco.app.skill.config.SkillApplyType;
import com.game.draco.app.skill.config.SkillAttackType;
import com.game.draco.app.skill.config.SkillDetail;
import com.game.draco.app.skill.config.SkillEffectType;
import com.game.draco.app.skill.config.SkillPassiveType;
import com.game.draco.app.skill.config.SkillScope;
import com.game.draco.app.skill.config.SkillSourceType;
import com.game.draco.app.skill.config.SkillTargetType;
import com.game.draco.app.skill.domain.RoleSkillStat;
import com.game.draco.app.skill.domain.SBuffC;
import com.game.draco.app.skill.domain.SHurt;
import com.game.draco.app.skill.vo.scope.AreaType;
import com.game.draco.app.skill.vo.scope.Cross;
import com.game.draco.app.skill.vo.scope.EffectTarget;
import com.game.draco.app.skill.vo.scope.Radian;
import com.game.draco.app.skill.vo.scope.TArea;
import com.game.draco.app.skill.vo.scope.TargetScope;
import com.game.draco.app.skill.vo.scope.TargetScopeType;
import com.game.draco.message.item.SkillApplyItem;
import com.game.draco.message.item.SkillApplyTargetItem;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public abstract class SkillAdaptor extends Skill {
	public static final int CD_OFFSET = 50;
	private static final int DIS_OFFSET = 15;
	private final double NPC_SPEED_MULT_OF_LOOP =  MapLoop.multipleOfMainLoop(1000) ;

	public @Data
	class Condition {
		SkillApplyResult applyResult;
		int mustHp;
		// int angerValue ;
		boolean useGlobalCd;
	}

	public SkillAdaptor(short skillId) {
		super(skillId);
	}

	/** 技能基本配置 */
	protected Map<Integer, SkillDetail> skillDetails = Maps.newHashMap();

	/** 获得技能的目标范围(提供给策划脚本) 	*/
	protected abstract Map<Integer, TargetScope> getTargetScope(
			SkillContext context);


	/** 获得技能的效果(提供给策划脚本) */
	protected abstract void getSkillEffect(SkillContext context);
	
	/**
	 * 攻击后效果(提供给策划脚本,循环内)
	 * (例如攻击者回血值为产生伤害的A%)
	 * @param context
	 */
	protected abstract void getAfterAttackEffect(SkillContext context);
	
	/**
	 * 给攻击者添加效果(提供给策划脚本)
	 * @param context
	 */
	protected abstract void getAttackerEffect(SkillContext context);

	/** 通知消息 
	 * 
	 * @param role
	 * @param context 技能上下文
	 */
	protected abstract void notifyMessage(AbstractRole role, Set<Integer> targetIdSet, SkillContext context);

	/** 被动技能效果 */
	protected abstract void passiveEffect(SkillContext context,
			SkillPassiveType passiveType);

	/** 对攻击方式的调整(提供给策划脚本)有需求才需要在脚本里面重写此方法 */
	protected void adjustAttackType(SkillContext context) {

	}

	/** 技能的额外条件 */
	protected boolean otherCondition(SkillContext context) {
		return true;
	}

	/** 获得攻击类型 */
	private AttackType getAttackType(SkillContext context) {
		if (context.getSkillEffectType() == SkillEffectType.ASSISTANCE) {
			return AttackType.ONHIT;
		}
		// 调整攻击方式参数
		// this.adjustAttackType(context);
		// AbstractRole role = context.getAttacker();
		SkillDetail detail = null;/* role.getSkillLevel(this.skillId) );*/
		if(isNormalAttack()){
			detail = this.getSkillDetail(1);
		}else{
			detail = this.getSkillDetail(context.getSkillLevel());
		}
		
		return SkillFormula.getAttackType(context, detail);
	}
	
	private Map<Integer, Collection<AbstractRole>> getTargetRoles(
			Map<Integer, TargetScope> scopes, AbstractRole attacker) {
		Map<Integer, Collection<AbstractRole>> values = Maps.newHashMap();
		for (Iterator<Map.Entry<Integer, TargetScope>> it = scopes.entrySet()
				.iterator(); it.hasNext();) {
			Map.Entry<Integer, TargetScope> entry = it.next();
			Collection<AbstractRole> roleList = entry.getValue().getTargetRole(
					attacker);
			if (Util.isEmpty(roleList)) {
				continue;
			}
			values.put(entry.getKey(), roleList);
		}
		return values;
	}

	private SkillApplyResult useNotTargetScopeSkill(SkillContext context) {
		// 被动技能只将效果附加到context中,最后在触发被动技能的主动技能中将效果实现
		this.getSkillEffect(context);
		return SkillApplyResult.SUCCESS;
	}
	
	private void clearHitCombo(RoleInstance role){
		if(null == role){
			return ;
		}
		GameContext.getHitComboApp().clearHitCombo(role);
	}

	private SkillApplyResult useMustTargetScopeSkill(SkillContext context) {
		AbstractRole attacker = context.getAttacker();
		int skillLv = context.getSkillLevel();
		RoleInstance player = null ;
		if(attacker.getRoleType() == RoleType.PLAYER){
			player = (RoleInstance)attacker ;
		}
		// 获得脚本目标
		Map<Integer, TargetScope> scopes = this.getTargetScope(context);
		if (Util.isEmpty(scopes)) {
			//清除combo
			this.clearHitCombo(player);
			
			this.notifyMessage(attacker, null, context);
			return SkillApplyResult.SUCCESS;
		}
		Map<Integer, Collection<AbstractRole>> targetRoles = this
				.getTargetRoles(scopes, attacker);
		if (Util.isEmpty(targetRoles)) {
			//清除combo
			this.clearHitCombo(player);
			
			this.notifyMessage(attacker, null, context);
			return SkillApplyResult.SUCCESS;
		}
		
		boolean hit = false ;
		//目标roleId
		Set<Integer> targetIdSet = Sets.newHashSet();
		for (Iterator<Map.Entry<Integer, Collection<AbstractRole>>> it = targetRoles
				.entrySet().iterator(); it.hasNext();) {
			if (context.isMustLive() && attacker.isDeath()) {
				break;
			}
			Map.Entry<Integer, Collection<AbstractRole>> entry = it.next();
			int areaId = entry.getKey();
			for (AbstractRole defender : entry.getValue()) {
				if (null == defender) {
					continue;
				}
				if (context.isMustLive() && attacker.isDeath()) {
					break;
				}
				targetIdSet.add(defender.getIntRoleId());
				context.release();
				// 下面语句不能丢掉
				context.setAttacker(attacker);
				context.setSkillLevel(skillLv);
				context.setDefender(defender);
				context.setAreaId(areaId);
				context.setSkill(this);

				// 附加被动技能的影响
				this.passiveEffect(context,
								SkillPassiveType.beforeAttackTarget);
				if (this.getSkillEffectType() != SkillEffectType.ASSISTANCE) {
					// 防御者被动技能影响
					this.passiveEffect(context, SkillPassiveType.beforeDefend);
				}
				// 获得攻击方式
				AttackType attackType = this.getAttackType(context);
				if (AttackType.MISS == attackType) {
					// SkillDetail detail = this.getSkillDetail(attacker);
					// 上面语句不能使用,因为在buff中使用的技能在攻击者上是不存在的
					SkillDetail detail = this.getSkillDetail(context);
					int hatred = (int) (detail.getHatredValue(0)
							* (attacker.get(AttributeType.hatredRate) / (float) SkillFormula.TEN_THOUSAND));
					GameContext.getBattleApp().attack(attacker,
							defender, 0, 0, hatred);
					// 飘字通知
					defender.getBehavior().addSelfFont(AttrFontSizeType.Common,
							AttrFontColorType.Special_State,
							AttrFontSpecialState.Miss.getType(), context);
					attacker.getBehavior().addTargetFont(
							AttrFontSizeType.Common,
							AttrFontColorType.Special_State,
							AttrFontSpecialState.Miss.getType(), defender, context);
					//defender.getBehavior().notifyAttrFont();
					continue;
				}

				context.setAttackType(attackType);
				// 触发攻击方式下的被动技能
				this.passiveEffect(context, SkillPassiveType.attackType);
				// 获得效果
				this.getSkillEffect(context);
				// 计算相关伤害
				context.result();
				
				//攻击完成后接口
				this.getAfterAttackEffect(context);

				AttrFontSizeType fontSizeType = AttrFontSizeType.Common;
				if (AttackType.CRIT == attackType) {
					fontSizeType = AttrFontSizeType.Crit;
				}
				AttrFontColorType colorType = AttrFontColorType.Skill_Attack;
				int hurts = -context.getDefenderHpResult();
				int attrHurts = -context.getDefenderAttHurtResult();

				if (hurts > 0 || attrHurts > 0) {
					// 考虑当前地图系数
					float mapHurtRate = 1;
					MapInstance currentMap = attacker.getMapInstance();
					if (null != currentMap) {
						mapHurtRate = currentMap.getHurtRatio(attacker);
					}
					hurts = (int) (hurts * mapHurtRate);
					attrHurts = (int)(attrHurts * mapHurtRate);
					// 考虑目标方吸收
					int absorbValue = GameContext.getUserBuffApp().hurtAbsorb(
							defender, hurts);
					int  absorbAttrValue = GameContext.getUserBuffApp().hurtAbsorb(
							defender, attrHurts);
					
					if (absorbValue > 0 || absorbAttrValue > 0) {
						// 标识吸收
						if(absorbValue > 0){
							hurts -= absorbValue;
						}
						if(absorbAttrValue > 0){
							attrHurts -= absorbAttrValue;
						}
						// 飘字效果
						defender.getBehavior().addSelfFont(
								AttrFontSizeType.Common,
								AttrFontColorType.Special_State,
								AttrFontSpecialState.Absorb.getType(), context);
						attacker.getBehavior()
								.addTargetFont(AttrFontSizeType.Common,
										AttrFontColorType.Special_State,
										AttrFontSpecialState.Absorb.getType(),
										defender, context);
					}
					if (hurts > 0) {
						context.setInputHurts(hurts);
						// 攻击方触发被动技能
						/*this.passiveEffect(context,
								SkillPassiveType.beforeAttackerExertHurt);*/
						// 考虑施加伤害前触发被动技能(防御方)
						this.passiveEffect(context,
								SkillPassiveType.beforeDefenderExertHurt);
					}
					// 真正伤害
					hurts = context.getInputHurts();
				} else if (hurts < 0 || attrHurts < 0) {
					int healRate = defender.get(AttributeType.healRate);
					if(hurts < 0){
						// 治疗系数
						hurts = (int) (healRate / (float) SkillFormula.TEN_THOUSAND * hurts);
					}
					if(attrHurts < 0){
						// 治疗系数
						attrHurts = (int) (healRate / (float) SkillFormula.TEN_THOUSAND * attrHurts);
					}
				}
				

				// 伤害技能需要计算仇恨
				// 计算仇恨
				// SkillDetail detail = this.getSkillDetail(attacker);
				// 上面语句不能使用,因为在buff中使用的技能在攻击者上是不存在的
				SkillDetail detail = this.getSkillDetail(context);
				int hurt = hurts + attrHurts;
				int hatred = (int) (detail.getHatredValue(hurt)
						* (attacker.get(AttributeType.hatredRate) / (float) SkillFormula.TEN_THOUSAND));

				int defenderMpResult = context.getDefenderMpResult();
				GameContext.getBattleApp().attack(attacker, defender,
						-hurt, defenderMpResult, hatred);
				if(attacker.getRoleType() == RoleType.NPC){
					attrHurts = 0;
					hurts = hurt;
				}
				// 飘字效果
				if (hurts < 0) {
					defender.getBehavior().addSelfFont(AttrFontSizeType.Common,
							AttrFontColorType.HP_Revert, -hurts, context);
					attacker.getBehavior().addTargetFont(AttrFontSizeType.Cycle,
							AttrFontColorType.HP_Revert, -hurts, defender, context);
				} else {
					defender.getBehavior().addSelfFont(AttrFontSizeType.Common,
							AttrFontColorType.Be_Hurt, -hurts, context);
					attacker.getBehavior().addTargetFont(fontSizeType,
							colorType, -hurts, defender, context);
				}
				if(attrHurts != 0){
					// 飘字效果
					if (attrHurts < 0) {
						defender.getBehavior().addSelfFont(AttrFontSizeType.Attr,
								AttrFontColorType.HP_Revert, -attrHurts, context,attacker);
						attacker.getBehavior().addTargetFont(AttrFontSizeType.Cycle,
								AttrFontColorType.HP_Revert, -attrHurts, defender, context);
					} else {
						defender.getBehavior().addSelfFont(AttrFontSizeType.Attr,
								AttrFontColorType.Attr_Hurt, -attrHurts, context,attacker);
						attacker.getBehavior().addTargetFont(AttrFontSizeType.Attr,
								AttrFontColorType.Attr_Hurt, -attrHurts, defender, context);
					}
				}
				
				/*defender.getBehavior().addSelfFont(AttrFontSizeType.Common,
						AttrFontColorType.MP_Change, defenderMpResult, context);
				attacker.getBehavior().addTargetFont(fontSizeType,
						AttrFontColorType.MP_Change, defenderMpResult, defender, context);*/

				int attackerHpResult = context.getAttackerHpResult();
				int attackerMpResult = context.getAttackerMpResult();

				// 攻击者飘字通知
				AttrFontColorType attackerColorType = AttrFontColorType.HP_Revert;
				if (attackerHpResult < 0) {
					attackerColorType = AttrFontColorType.Be_Hurt;
				} else if (attackerHpResult > 0) {
					// 加血
					// 治疗系数
					int healRate = attacker.get(AttributeType.healRate);
					attackerHpResult = (int) (healRate
							/ (float) SkillFormula.TEN_THOUSAND * attackerHpResult);
				}
				// 计算攻击者的属性变化
				GameContext.getBattleApp().attack(null, attacker,
						attackerHpResult, attackerMpResult, 0);

				attacker.getBehavior().addSelfFont(AttrFontSizeType.Common,
						attackerColorType, attackerHpResult, context);
				/*attacker.getBehavior().addSelfFont(AttrFontSizeType.Common,
						AttrFontColorType.MP_Change, attackerMpResult, context);*/
				// 处理buff
				//在循环内不能执行地图buff
				execBuff(context,false);
				// 触发防御者的防御技能
				//this.passiveEffect(context, SkillPassiveType.afterDefend);
				//defender.getBehavior().notifyAttrFont();
				
				if(RoleType.PLAYER == defender.getRoleType() && !defender.isDeath()){
					if(defender.getIntRoleId() != attacker.getIntRoleId()) {
						GameContext.getPetApp().rolePetUseSkill((RoleInstance)defender, attacker);
						//触发角色分身技能
						GameContext.getSkillApp().roleCopyAttack((RoleInstance)defender, attacker);
					}
				}
				//添加hit
				GameContext.getHitComboApp().addHitCombo(player);
				hit = true ;
			}
			//attacker.getBehavior().notifyAttrFont();
		}
		if(!hit){
			//全部miss
			GameContext.getHitComboApp().clearHitCombo(player);
		}
		
		// 技能触发飘字效果
		for (SkillFontType item : context.getFontList()) {
			if (null == item) {
				continue;
			}
			item.notifyAttrFont();
		}
		//通知客户端
		this.notifyMessage(attacker, targetIdSet, context);
		if(context.getSkill() != null){
			short sId = context.getSkill().getSkillId();
			context.setTriggerSkillId(sId);
			context.setDefenderTempHpResult();
		}
		
		this.dealAttackEffect(context);
		//攻击结束点处理二重打击
//		this.trigger2Attack(context);
//		context.release();
		context.clearAttrFontInfo();
		// 触发被动技能点
		context.setAttacker(attacker);
		context.setDefender(attacker.getTarget());
		this.passiveEffect(context, SkillPassiveType.afterAttack);
		
		context.release();
		context = null;
		return SkillApplyResult.SUCCESS;
	}
	
	/**
	 * 触发双重打击
	 * @param context
	 */
	private void trigger2Attack(SkillContext context) {
		if(!context.isTrigger2Attack()) {
			return ;
		}
		//使用双重打击
		context.setSystemTrigger(true);
		context.setTrigger2Attack(false);
		context.setJudgeUseCond(false);
		this.use(context);
	}
	
	/**
	 * 通道技能用buff实现
	 * 循环外执行地图buff
	 * @param context
	 */
	private void dealAttackEffect(SkillContext context) {
		
		this.getAttackerEffect(context);
		execBuff(context,true);
	}

	/*
	private boolean isAssistanceSkill(SkillContext context) {
		// return this.skillEffectType == SkillEffectType.ASSISTANCE ;
		return context.getSkillEffectType() == SkillEffectType.ASSISTANCE;
	}

	private boolean isReliveSkill(SkillContext context) {
		return this.isAssistanceSkill(context)
				&& this.getSkillAttackType() == SkillAttackType.Relive;
	}*/

	private void setCooldown(AbstractRole role, boolean useGlobalCd,int delayTime) {
		// 减少Hp,Mp,重置cd
		RoleSkillStat stat = role.getSkillStat(this.getSkillId());
		// 修改最后执行时间
		long nowMillis = System.currentTimeMillis() - delayTime;
		stat.setLastProcessTime(nowMillis);
		// 主动技能设置公共CD
		if (useGlobalCd) {
			role.setLastSkillProcessTime(nowMillis);
		}
	}

	protected abstract AbstractRole getSkillOwner(SkillContext context);

	/**
	 * 处理连击逻辑
	 * @param context
	 */
	protected void hitComboLogic(SkillContext context){
	}
	
	@Override
	public SkillApplyResult use(SkillContext context) {
		if(context.isJudgeUseCond()) {
			// 是否需要判断条件
			Condition cond = this.useCondition(context);
			if (cond.getApplyResult() != SkillApplyResult.SUCCESS) {
				return cond.getApplyResult();
			}
			AbstractRole role = this.getSkillOwner(context);
			// 设置cd
			this.setCooldown(role, cond.useGlobalCd,context.getClientDelay());
			
			//!!!!!! 下面的技能将客户端延时时间清除
			context.setClientDelay(0);
			
			// 减少Mp
			int mustHp = cond.getMustHp();
			boolean flag = false;
			if ( mustHp > 0) {
				flag = true;
				role.getBehavior().changeAttribute(AttributeType.curHP,
						OperatorType.Decrease, mustHp);
			}
		
			if (flag) {
				role.getBehavior().notifyAttribute();
			}
		}
		//处理
		this.hitComboLogic(context);
		//必须要有目标域
		if (context.isMustTargetScope()) {
			return this.useMustTargetScopeSkill(context);
		}
		return this.useNotTargetScopeSkill(context);
	}

	public SkillApplyResult use(AbstractRole role) {
		return this.use(role, 0);
	}
	
	public SkillApplyResult use(AbstractRole role,int delayTime) {
		SkillContext context = new SkillContext(this);
		context.setAttacker(role);
		context.setSkillLevel(role.getSkillEffectLevel(this.skillId));
		context.setClientDelay(delayTime);
		context.setDefender(role.getTarget());
		return this.use(context);
	}
	
	public SkillApplyResult use(AbstractRole role,int delayTime,boolean systemTrigger,boolean judgeUseCond) {
		SkillContext context = new SkillContext(this);
		context.setAttacker(role);
		context.setSystemTrigger(systemTrigger);
		context.setJudgeUseCond(judgeUseCond);
		context.setSkillLevel(role.getSkillEffectLevel(this.skillId));
		context.setClientDelay(delayTime);
		context.setDefender(role.getTarget());
		return this.use(context);
	}

	private static void execBuff(SkillContext context,boolean execMapBuff) {
		execRoleBuff(context, context.getDefender(), context
				.getDefenderBuffEffects());
		execRoleBuff(context, context.getAttacker(), context
				.getAttackerBuffEffects());
		
		execSummonerBuff(context);
		if(execMapBuff){
			// 处理地图buff
			execMapBuff(context) ;
		}
	}
	
	/**
	 * 处理攻击者的召唤者的buff
	 * @param context
	 */
	private static void execSummonerBuff(SkillContext context) {
		AbstractRole attacker = context.getAttacker();
		if(null == attacker) {
			return ;
		}
		if(RoleType.PET != attacker.getRoleType()) {
			return ;
		}
		AbstractRole summoner = ((RolePet)(attacker)).getMasterRole();
		if(null == summoner) {
			return ;
		}
		execRoleBuff(context, summoner, context.getSummonerBuffEffects());
	}

	private static void execMapBuff(SkillContext context) {
		AbstractRole attacker = context.getAttacker();
		if (attacker.isDeath() || Util.isEmpty(context.getMapBuffEffects())) {
			return;
		}
		MapInstance mapInstance = attacker.getMapInstance();
		if (null == mapInstance) {
			return;
		}
		for (MapBuffEffect effect : context.getMapBuffEffects()) {
			if (effect.getBuffLv() <= 0) {
				continue;
			}
			// 处理地图buff
			Buff buff = GameContext.getBuffApp().getBuff(
					effect.getBuffId());
			if (null == buff) {
				continue;
			}

			BuffStat mapBuffStat = new BuffStat(buff, effect.getBuffLv(),
					buff.getIntervalTime(effect.getBuffLv()));
			mapBuffStat.setContextInfo(new Point(mapInstance.getInstanceId(), effect.getX(), effect
									.getY()));
			// 设置参数
			Date now = new Date();
			mapBuffStat.setBuffId(buff.getBuffId());
			mapBuffStat.setCreateTime(now);
			mapBuffStat.setLastExecuteTime(now.getTime());
			mapBuffStat.setRemainTime(buff.getPersistTime());
			mapBuffStat.setCaster(attacker);
			mapInstance.addMapInstanceBuff(mapBuffStat);
		}
	}

	private static void execRoleBuff(SkillContext context,
			AbstractRole buffOwner, List<BuffEffect> effects) {
		if (null == buffOwner || buffOwner.isDeath()) {
			return;
		}
		if (Util.isEmpty(effects)) {
			return;
		}
		
		for (BuffEffect effect : effects) {
			if (effect.getBuffId() <= 0 && effect.getBuffLv() <= 0) {
				// 删除所有的debuff
				for (BuffStat stat : buffOwner.getReceiveBuffCopy()) {
					if (!stat.getBuff().isDebuff()) {
						continue;
					}
					GameContext.getUserBuffApp().delBuffStat(buffOwner,
							stat.getBuffId(), false);
				}
				continue;
			}
			if (effect.getBuffLv() > 0) {
				// 添加buff
				GameContext.getUserBuffApp().addBuffStat(buffOwner,
						context.getAttacker().getMasterRole(), effect.getBuffId(),
						effect.getEffectTime(), effect.getBuffLv(),effect.getInfo());
			} else if (effect.getBuffLv() == 0) {
				// 删除buff
				GameContext.getUserBuffApp().delBuffStat(buffOwner,
						effect.getBuffId(), false);
			} else if (effect.getBuffLv() == -1) {
				// 删除攻击者附加的buff
				GameContext.getUserBuffApp().delBuffStat(buffOwner,
						effect.getBuffId(), false,
						context.getAttacker().getRoleId());
			}
		}
		//给role加上buff后就应该移除，否则在这个context生命期间再次执行execRoleBuff方法时
		//会走buff替换逻辑
		effects.clear();
	}

	public void putSkillDetail(Map<Integer, SkillDetail> details) {
		if (null == details || 0 == details.size()) {
			Log4jManager.CHECK.error("skill: not config detail,skillId="
					+ this.skillId);
			Log4jManager.checkFail();
			return;
		}
		this.skillDetails.clear();
		this.skillDetails.putAll(details);
		SkillDetail detail = this.getSkillDetail(1);
		if (null == detail) {
			Log4jManager.CHECK
					.error("skill: config detail error,lv=1 not exist,skillId="
							+ this.skillId);
			Log4jManager.checkFail();
			return;
		}
		this.setSkillSourceType(SkillSourceType.get(detail.getSourceType()));
		this.setName(detail.getName());
		this.setSkillApplyType(SkillApplyType.get(detail.getSkillApplyType()));
		this.setSkillEffectType(SkillEffectType
				.get(detail.getSkillEffectType()));
		this.setSkillTargetType(SkillTargetType.get(detail
				.getServerTargetType()));
		this.setSkillAttackType(SkillAttackType.get(detail.getAttackType()));
		this.setTriggerPassive(detail.isTriggerPassive());
		this.setUseGlobalCd(detail.isUseGlobalCd());
		this.setAffectSkills(detail.getAffectSkills());
		this.setIconId(detail.getIconId());
	}

	@Override
	public SkillDetail getSkillDetail(int level) {
		return this.skillDetails.get(level);
	}

	protected SkillDetail getSkillDetail(AbstractRole role) {
		int lv = role.getSkillLevel(this.skillId);
		if(isNormalAttack()){
			return this.skillDetails.get(1);
		}
		return this.skillDetails.get(lv);
	}

	private SkillDetail getSkillDetail(SkillContext context) {
		if(isNormalAttack()){
			return this.skillDetails.get(1);
		}
		return this.skillDetails.get(context.getSkillLevel());
	}

	public Map<Integer, SkillDetail> getSkillDetails() {
		return skillDetails;
	}

	private boolean targetCondition(AbstractRole player) {
		// 判断目标条件
		if (null == this.skillTargetType
				|| this.skillTargetType == SkillTargetType.all) {
			return true;
		}
		if (null == player.getTarget()) {
			return false;
		}
		ForceRelation fr = player.getForceRelation(player.getTarget());
		if (skillTargetType == SkillTargetType.enemy
				&& fr == ForceRelation.enemy) {
			return true;
		}
		if (skillTargetType == SkillTargetType.friend
				&& fr != ForceRelation.enemy) {
			return true;
		}
		return false;
	}

	protected Condition useCondition(SkillContext context) {
		Condition cond = new Condition();
		AbstractRole player = this.getSkillOwner(context);
		if(null == player || (context.isMustLive() && player.isDeath())){
			cond.setApplyResult(SkillApplyResult.ERROR);
			return cond;
		}
		
		RoleSkillStat stat = player.getSkillStat(skillId);
		if (null == stat) {
			cond.setApplyResult(SkillApplyResult.HAS_NOT_SKILL);
			return cond;
		}
		
		long now = System.currentTimeMillis() - context.getClientDelay() ;
		// 主动技能判断公共cd时间
		if (this.getSkillApplyType() == SkillApplyType.active
				&& this.useGlobalCd) {
			cond.setUseGlobalCd(true);
			long t = now - player.getLastSkillProcessTime() ;
			if ( t >= 0 && t< GameContext.getSkillConfig().getSkillGlobalCd() - CD_OFFSET) {
				cond.setApplyResult(SkillApplyResult.CD_NOT_ENOUGH);
				return cond;
			}
		}
		
		// 判断冷却时间
		long t = now - stat.getLastProcessTime() ;
		if (t >= 0 && t < this.getCd(player) - CD_OFFSET) {
			cond.setApplyResult(SkillApplyResult.CD_NOT_ENOUGH);
			return cond;
		}

		if (this.getSkillApplyType() == SkillApplyType.active) {
			if (!this.targetCondition(player)) {
				// 目标
				cond.setApplyResult(SkillApplyResult.WRONG_TARGET);
				return cond;
			}
			// 判断施放距离
			SkillApplyResult result = distanceCondition(player);
			if (result != SkillApplyResult.SUCCESS) {
				cond.setApplyResult(result);
				return cond;
			}
			
			int hp = this.getHp(player);
			if (player.getCurHP() < this.getHp(player)) {
				// hp不够
				cond.setApplyResult(SkillApplyResult.HP_NOT_ENOUGH);
				return cond;
			}
			cond.setMustHp(hp);
		}
		// 判断其他条件
		if (!this.otherCondition(context)) {
			cond.setApplyResult(SkillApplyResult.ERROR);
			return cond;
		}
		cond.setApplyResult(SkillApplyResult.SUCCESS);
		return cond;
	}

	@Override
	public SkillApplyResult condition(AbstractRole player) {
		SkillContext context = new SkillContext(this);
		context.setAttacker(player);
		return this.useCondition(context).getApplyResult();
	}

	/**
	 * 判断施放距离
	 * 
	 * @param player
	 * @return
	 */
	private SkillApplyResult distanceCondition(AbstractRole player) {
		if (this.skillTargetType == SkillTargetType.all) {
			return SkillApplyResult.SUCCESS;
		}
		int min = getMinUseRange(player);
		int max = getMaxUseRange(player);
		if (min <= 0 && max <= 0) {
			return SkillApplyResult.SUCCESS;
		}
		AbstractRole target = player.getTarget();
		int dis = Point.getTwoPointDis(player, target);
		int offset = DIS_OFFSET;
		if(null != target && target.getRoleType() == RoleType.NPC) {
			offset = (int)Math.ceil(target.getSpeed()/NPC_SPEED_MULT_OF_LOOP);
		}
		if (max > 0 && dis > (max + offset)) {
			return SkillApplyResult.DISTANCE_TOO_LONG;
		}
		if (min > 0 && dis < min) {
			return SkillApplyResult.DISTANCE_TOO_SHORT;
		}
		return SkillApplyResult.SUCCESS;
	}

	@Override
	public int getCd(AbstractRole role) {
		SkillDetail detail = this.getSkillDetail(role);
		if(null == detail){
			return 0 ;
		}
		return detail.getRealCd(role);
	}

	@Override
	public String getDesc(AbstractRole role) {
		SkillDetail detail = this.getSkillDetail(role);
		if(null == detail){
			return "" ;
		}
		return detail.getDesc();
	}

	@Override
	public byte getActionId(AbstractRole role) {
		SkillDetail detail = this.getSkillDetail(role);
		if(null == detail){
			return 0 ;
		}
		return detail.getActionId();
	}

	@Override
	public int getHp(AbstractRole role) {
		SkillDetail detail = this.getSkillDetail(role);
		if(null == detail){
			return 0 ;
		}
		return detail.getHp();
	}

	@Override
	public int getMp(AbstractRole role) {
		SkillDetail detail = this.getSkillDetail(role);
		if(null == detail){
			return 0 ;
		}
		return detail.getMp();
	}
	

	/** 变身后消耗气力值 */
	/*
	 * private int getAngerValue(AbstractRole role){ return 0; }
	 */

	/** 获得最大施法距离 */
	public int getMaxUseRange(AbstractRole role) {
		SkillDetail detail = this.getSkillDetail(role);
		if(null == detail){
			return 0 ;
		}
		return detail.getMaxUseRange();
	}

	/** 获得最小施法距离 */
	public int getMinUseRange(AbstractRole role) {
		SkillDetail detail = this.getSkillDetail(role);
		if(null == detail){
			return 0 ;
		}
		return detail.getMinUseRange();
	}

	@Override
	public void init() {

	}

	@Override
	public boolean verify() {
		return true;
	}

	@Override
	public int getMaxLevel() {
		return this.skillDetails.size();
	}
	
	/**
	 * 创建使用技能信息
	 * @param type 0：SkillApplyItem 1：SkillApplyTargetItem
	 * @param role
	 * @param detail
	 * @param targetIdSet
	 * @param noActionAndEffect
	 * @return
	 */
	protected SkillApplyItem createSkillApplyItem(int type, AbstractRole role, SkillDetail detail
			, Set<Integer> targetIdSet, boolean noActionAndEffect) {
		SkillApplyItem applyItem = null;
		if(0 == type) {
			applyItem = new SkillApplyItem();
		}else {
			applyItem = new SkillApplyTargetItem();
			((SkillApplyTargetItem)(applyItem)).setTargetIds(sacred.alliance.magic.app.goods.Util.setToIntArray(targetIdSet));
		}
		applyItem.setRoleId(role.getIntRoleId());
		if(null != role.getTarget()){
			applyItem.setTargetRoleId(role.getTarget().getIntRoleId());
		}
		applyItem.setSkillId(this.getSkillId());
		if(noActionAndEffect) {
			applyItem.setActionId((byte)-1);
			applyItem.setEffectId((short)-1);
		}else {
			applyItem.setEffectId(detail.getEffectId());
			applyItem.setActionId(detail.getActionId());
		}
		if(detail.isContinueEffectId()){
			applyItem.setEffectId(detail.getEffectId());
			applyItem.setActionId(detail.getActionId());
		}
		applyItem.setAttackType(detail.getAttackType());
		applyItem.setTargetEffectId(detail.getTargetEffectId());
		return applyItem;
	}
	
	@Override
	public int getSkillHurt(SkillContext context,int areaId) {
		if(null == this.skillDetails){
			return 0 ;
		}
		SkillDetail skillDetail = skillDetails.get(context.getSkillLevel());
		SHurt sHurt = skillDetail.getSHurtMap().get(areaId);
		if(sHurt == null){
			return 0;
		}
		int attrValue = 0;
		if(sHurt.getTargetType() == 0){
			attrValue = context.getAttacker().get(sHurt.getAttrType()) ; 
		}else{
			attrValue = context.getDefender().get(sHurt.getAttrType()) ; 
		}
		return Util.getAbc(sHurt.getA(),sHurt.getB(),sHurt.getC(),sHurt.getD(),attrValue,false,false);
	}
	
	@Override
	public int getSkillHurtProb(SkillContext context,int areaId) {
		if(null == this.skillDetails){
			return 0 ;
		}
		SkillDetail skillDetail = skillDetails.get(context.getSkillLevel());
		SHurt sHurt = skillDetail.getSHurtMap().get(areaId);
		if(sHurt == null){
			return 0;
		}
		return sHurt.getA();
	}
	
	@Override
	public int getSkillBuffProb(SkillContext context,int buffId,int areaId) {
		if(null == this.skillDetails){
			return 0 ;
		}
		
		SkillDetail skillDetail = skillDetails.get(context.getSkillLevel());
		List<SBuffC> skillBuffList = skillDetail.getSkillBuffMap().get(areaId);
		if(Util.isEmpty(skillBuffList)){
			return 0;
		}
		for(SBuffC buffC : skillBuffList){
			if(buffC.getBuffId() == buffId){
				int probability = Util.getAbc(0,buffC.getB(),buffC.getC(),buffC.getD(),0,true,true);
				return probability;
			}
		}
		return 0;
	}
	
	@Override
	public Map<Integer, TargetScope> getTargetScopeMap(SkillContext context) {
		SkillDetail detail = skillDetails.get(context.getSkillLevel());
		List<SkillScope> list = detail.getSkillScopeList();
		Map<Integer, TargetScope> targetScopeMap = Maps.newConcurrentMap();
		
		for(SkillScope scope : list){
			
			TargetScope targetScope = null;
			int mapX = 0,mapY = 0;
			
			if(detail.isFixedXy()){
				if(context.getInfo() == null){
					Object info = new Point("",context.getAttacker().getTarget().getMapX(),context.getAttacker().getTarget().getMapY());
					context.setInfo(info);
				}
			}
			
			if(scope.getTargetXY() == AreaType.self.getType()){
				mapX = context.getAttacker().getMapX();
				mapY = context.getAttacker().getMapY();
			}
			if(scope.getTargetXY() == AreaType.target.getType()){
				if(context.getInfo() != null){
					Point point = (Point)context.getInfo();
					mapX = point.getX();
					mapY = point.getY();
				}else{
					if(context.getDefender() == null){
						return targetScopeMap;
					}
					mapX = context.getDefender().getMapX();
					mapY = context.getDefender().getMapY();
				}
			}
			
			if(scope.getScopeType() == TargetScopeType.target.getType()){
				targetScope = new TargetScope(EffectTarget.getType(scope.getEffectTarget()),scope.getTargetScope());
			}
			
			if(scope.getScopeType() == TargetScopeType.circle.getType()){
				Radian radian = new Radian(mapX,mapY,scope.getRadius(),0,scope.getMaxDegrees());
				targetScope = new TargetScope(radian,EffectTarget.getType(scope.getEffectTarget()),scope.getTargetScope(),scope.getTargetNum(),scope.isPentacombo());
			}
			
			if(scope.getScopeType() == TargetScopeType.cross.getType()){
				Cross radian = new Cross(mapX,mapY,scope.getDownLength(),scope.getHight());
				targetScope = new TargetScope(radian,EffectTarget.getType(scope.getEffectTarget()),scope.getTargetScope(),scope.getTargetNum(),scope.isPentacombo());
			}
			
			if(scope.getScopeType() == TargetScopeType.tarea.getType()){
				TArea t = new TArea(mapX,mapY,scope.getDownLength(),scope.getUpLength(),scope.getHight(),context.getAttacker().getDir());
				targetScope = new TargetScope(t,EffectTarget.getType(scope.getEffectTarget()),scope.getTargetScope(),scope.getTargetNum(),scope.isPentacombo());
			}
			if(targetScope != null){
				targetScopeMap.put(scope.getAreaId(), targetScope);
			}
		}
		return targetScopeMap;
	}
	
}
