package com.game.draco.app.skill.vo;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
import com.game.draco.app.buff.stat.MapBuffStat;
import com.game.draco.app.skill.config.SkillApplyType;
import com.game.draco.app.skill.config.SkillAttackType;
import com.game.draco.app.skill.config.SkillDetail;
import com.game.draco.app.skill.config.SkillPassiveType;
import com.game.draco.app.skill.config.SkillSourceType;
import com.game.draco.app.skill.config.SkillTargetType;
import com.game.draco.app.skill.domain.RoleSkillStat;
import com.game.draco.app.skill.vo.SkillContext.AttrSource;
import com.game.draco.app.skill.vo.scope.TargetScope;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public abstract class SkillAdaptor extends Skill {
	public static final int CD_OFFSET = 50;
	private static final int DIS_OFFSET = 15;
	private final double NPC_SPEED_MULT_OF_LOOP =  MapLoop.multipleOfMainLoop(1000) ;

	public @Data
	class Condition {
		SkillApplyResult applyResult;
		int mustHp;
		int mustMp;
		// int angerValue ;
		boolean useGlobalCd;
	}

	public SkillAdaptor(short skillId) {
		super(skillId);
	}

	/** 技能基本配置 */
	protected Map<Integer, SkillDetail> skillDetails = Maps.newHashMap();

	/** 获得技能的目标范围(提供给策划脚本) */
	protected abstract Map<Integer, TargetScope> getTargetScope(
			SkillContext context);

	/** 获得技能的效果(提供给策划脚本) */
	protected abstract void getSkillEffect(SkillContext context);
	
	/**
	 * 给攻击者添加效果
	 * @param context
	 */
	protected abstract void getAttackerEffect(SkillContext context);

	/** 通知消息 */
	protected abstract void notifyMessage(AbstractRole role, int[] targetIds, boolean setEffectTime
			 , boolean skillActiveApply);

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
		// 调整攻击方式参数
		// this.adjustAttackType(context);
		// AbstractRole role = context.getAttacker();
		SkillDetail detail = this
				.getSkillDetail(context.getSkillLevel()/* role.getSkillLevel(this.skillId) */);
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

	private SkillApplyResult useMustTargetScopeSkill(SkillContext context) {
		AbstractRole attacker = context.getAttacker();
		int skillLv = context.getSkillLevel();
		// 获得脚本目标
		Map<Integer, TargetScope> scopes = this.getTargetScope(context);
		if (Util.isEmpty(scopes)) {
			this.notifyMessage(attacker, null, context.isSetEffectTime(), context.isSkillActiveApply());
			return SkillApplyResult.SUCCESS;
		}

		Map<Integer, Collection<AbstractRole>> targetRoles = this
				.getTargetRoles(scopes, attacker);
		if (Util.isEmpty(targetRoles)) {
			this.notifyMessage(attacker, null, context.isSetEffectTime(), context.isSkillActiveApply());
			return SkillApplyResult.SUCCESS;
		}
		//广播使用技能
		this.notifyMessage(attacker, this.getTargetRoleIds(targetRoles), context.isSetEffectTime(), context.isSkillActiveApply());
		
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
				// 防御者被动技能影响
				this.passiveEffect(context, SkillPassiveType.beforeDefend);
				// 获得攻击方式
				AttackType attackType = this.getAttackType(context);
				if (AttackType.MISS == attackType) {
					// SkillDetail detail = this.getSkillDetail(attacker);
					// 上面语句不能使用,因为在buff中使用的技能在攻击者上是不存在的
					SkillDetail detail = this.getSkillDetail(context);
					int hatred = (int) (detail.getHatredValue(0)
							* (attacker.get(AttributeType.hatredRate) / (float) SkillFormula.TEN_THOUSAND));
					GameContext.getBattleApplication().attack(attacker,
							defender, 0, 0, hatred);
					// 飘字通知
					defender.getBehavior().addSelfFont(AttrFontSizeType.Common,
							AttrFontColorType.Special_State,
							AttrFontSpecialState.Miss.getType());
					attacker.getBehavior().addTargetFont(
							AttrFontSizeType.Common,
							AttrFontColorType.Special_State,
							AttrFontSpecialState.Miss.getType(), defender);
					defender.getBehavior().notifyAttrFont();
					continue;
				}

				context.setAttackType(attackType);
				// 触发攻击方式下的被动技能
				this.passiveEffect(context, SkillPassiveType.attackType);
				// 获得效果
				this.getSkillEffect(context);

				// 计算相关伤害
				context.result();

				AttrFontSizeType fontSizeType = AttrFontSizeType.Common;
				if (AttackType.CRIT == attackType) {
					fontSizeType = AttrFontSizeType.Crit;
				}
				AttrFontColorType colorType = AttrFontColorType.Skill_Attack;
				int hurts = -context.getDefenderHpResult();

				if (hurts > 0) {
					// 考虑当前地图系数
					float mapHurtRate = 1;
					MapInstance currentMap = attacker.getMapInstance();
					if (null != currentMap) {
						mapHurtRate = currentMap.getHurtRatio(attacker);
					}
					hurts = (int) (hurts * mapHurtRate);
					// 考虑目标方吸收
					int absorbValue = GameContext.getUserBuffApp().hurtAbsorb(
							defender, hurts);
					if (absorbValue > 0) {
						// 标识吸收
						hurts -= absorbValue;
						// 飘字效果
						defender.getBehavior().addSelfFont(
								AttrFontSizeType.Common,
								AttrFontColorType.Special_State,
								AttrFontSpecialState.Absorb.getType());
						attacker.getBehavior()
								.addTargetFont(AttrFontSizeType.Common,
										AttrFontColorType.Special_State,
										AttrFontSpecialState.Absorb.getType(),
										defender);
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
				} else if (hurts < 0) {
					// 治疗系数
					int healRate = defender.get(AttributeType.healRate);
					hurts = (int) (healRate / (float) SkillFormula.TEN_THOUSAND * hurts);
				}
				

				// 伤害技能需要计算仇恨
				// 计算仇恨
				// SkillDetail detail = this.getSkillDetail(attacker);
				// 上面语句不能使用,因为在buff中使用的技能在攻击者上是不存在的
				SkillDetail detail = this.getSkillDetail(context);
				int hatred = (int) (detail.getHatredValue(hurts)
						* (attacker.get(AttributeType.hatredRate) / (float) SkillFormula.TEN_THOUSAND));

				int defenderMpResult = context.getDefenderMpResult();
				GameContext.getBattleApplication().attack(attacker, defender,
						-hurts, defenderMpResult, hatred);
				// 飘字效果
				if (hurts < 0) {
					defender.getBehavior().addSelfFont(AttrFontSizeType.Common,
							AttrFontColorType.HP_Revert, -hurts);
					attacker.getBehavior().addTargetFont(AttrFontSizeType.Cycle,
							AttrFontColorType.HP_Revert, -hurts, defender);
				} else {
					defender.getBehavior().addSelfFont(AttrFontSizeType.Common,
							AttrFontColorType.Be_Hurt, -hurts);
					attacker.getBehavior().addTargetFont(fontSizeType,
							colorType, -hurts, defender);
				}
				defender.getBehavior().addSelfFont(AttrFontSizeType.Common,
						AttrFontColorType.MP_Change, defenderMpResult);
				attacker.getBehavior().addTargetFont(fontSizeType,
						AttrFontColorType.MP_Change, defenderMpResult, defender);

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
				GameContext.getBattleApplication().attack(null, attacker,
						attackerHpResult, attackerMpResult, 0);

				attacker.getBehavior().addSelfFont(AttrFontSizeType.Common,
						attackerColorType, attackerHpResult);
				attacker.getBehavior().addSelfFont(AttrFontSizeType.Common,
						AttrFontColorType.MP_Change, attackerMpResult);
				// 处理buff
				execBuff(context);
				// 触发防御者的防御技能
				//this.passiveEffect(context, SkillPassiveType.afterDefend);
				defender.getBehavior().notifyAttrFont();
				
				if(RoleType.PLAYER == defender.getRoleType() && !defender.isDeath()){
					//触发法宝技能
					GameContext.getGoddessApp().roleGoddessUseSkill((RoleInstance)defender, attacker);
				}
			}
			attacker.getBehavior().notifyAttrFont();
		}
		// 技能触发飘字效果
		for (SkillFontType item : context.getFontList()) {
			if (null == item) {
				continue;
			}
			item.notifyAttrFont();
		}

		this.dealAttackEffect(context);
		context.release();
		// 触发被动技能点
		context.setAttacker(attacker);
		context.setDefender(attacker.getTarget());
		this.passiveEffect(context, SkillPassiveType.afterAttack);
		//攻击结束点处理二重打击
		this.trigger2Attack(context);
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
		this.use(context);
	}
	
	/**
	 * 通道技能用buff实现
	 * @param context
	 */
	private void dealAttackEffect(SkillContext context) {
		this.getAttackerEffect(context);
		execBuff(context);
	}
	
	private int[] getTargetRoleIds(Map<Integer, Collection<AbstractRole>> targetRoles) {
		List<Integer> targetIdList = Lists.newArrayList();
		for (Iterator<Map.Entry<Integer, Collection<AbstractRole>>> it = targetRoles
				.entrySet().iterator(); it.hasNext();) {
			Map.Entry<Integer, Collection<AbstractRole>> entry = it.next();
			for (AbstractRole target : entry.getValue()) {
				if(null == target) {
					continue ;
				}
				targetIdList.add(target.getIntRoleId());
			}
		}
		return Util.listToInt(targetIdList);
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
			int mustMp = cond.getMustMp();
			int mustHp = cond.getMustHp();
			boolean flag = false;
			if (mustMp > 0 || mustHp > 0) {
				flag = true;
				role.getBehavior().changeAttribute(AttributeType.curMP,
						OperatorType.Decrease, mustMp);
				role.getBehavior().changeAttribute(AttributeType.curHP,
						OperatorType.Decrease, mustHp);
			}
		
			if (flag) {
				role.getBehavior().notifyAttribute();
			}
		}
		
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
		context.setSkillLevel(role.getSkillLevel(this.skillId));
		context.setClientDelay(delayTime);
		context.setDefender(role.getTarget());
		return this.use(context);
	}

	private static void execBuff(SkillContext context) {
		execRoleBuff(context, context.getDefender(), context
				.getDefenderBuffEffects());
		execRoleBuff(context, context.getAttacker(), context
				.getAttackerBuffEffects());
		// 处理地图buff
		execMapBuff(context);
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
			Buff buff = GameContext.getBuffApplication().getBuff(
					effect.getBuffId());
			if (null == buff) {
				continue;
			}

			MapBuffStat mapBuffStat = new MapBuffStat(buff, effect.getBuffLv(),
					buff.getIntervalTime(effect.getBuffLv()), new Point(
							mapInstance.getInstanceId(), effect.getX(), effect
									.getY()));
			// 设置参数
			Date now = new Date();
			mapBuffStat.setBuffId(buff.getBuffId());
			mapBuffStat.setCreateTime(now);
			mapBuffStat.setLastExecuteTime(now.getTime());
			mapBuffStat.setRemainTime(buff.getPersistTime(effect.getBuffLv()));
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
						context.getAttacker(), effect.getBuffId(),
						effect.getBuffLv());
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
		return this.skillDetails.get(role.getSkillLevel(this.skillId));
	}

	private SkillDetail getSkillDetail(SkillContext context) {
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
			int skillCd;// 人的公共CD是1秒，怪的公共CD是2秒
			if (player.getRoleType() == RoleType.PLAYER 
					|| player.getRoleType() == RoleType.GODDESS) {
				skillCd = GameContext.getSkillConfig().getRoleSkillCd();
			} else {
				skillCd = GameContext.getSkillConfig().getMonsterSkillCd();
			}
			long t = now - player.getLastSkillProcessTime() ;
			if ( t >= 0 && t< skillCd - CD_OFFSET) {
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

			int mp = this.getMp(player);
			if (player.getCurMP() < mp) {
				// mp不够
				cond.setApplyResult(SkillApplyResult.MP_NOT_ENOUGH);
				return cond;
			}
			cond.setMustMp(mp);
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
}
