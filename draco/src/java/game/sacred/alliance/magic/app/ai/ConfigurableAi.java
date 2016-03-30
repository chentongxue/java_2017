package sacred.alliance.magic.app.ai;

import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.ai.config.NormalAiConfig;
import sacred.alliance.magic.base.ForceRelation;
import sacred.alliance.magic.base.RoleType;
import sacred.alliance.magic.base.SkillApplyResult;
import sacred.alliance.magic.constant.AIMoveConstant;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.MapInstance;
import sacred.alliance.magic.vo.Point;

import com.game.draco.GameContext;
import com.game.draco.app.npc.domain.NpcInstance;
import com.game.draco.app.npc.type.NpcActionType;
import com.game.draco.app.skill.domain.RoleSkillStat;
import com.game.draco.app.skill.vo.Skill;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.push.C1190_NpcMusicNotifyMessage;

public class ConfigurableAi extends DefaultAi {
	private final static Logger logger = LoggerFactory
			.getLogger(ConfigurableAi.class);
	private static final int DIS_OFFSET = 15;

	public ConfigurableAi() {
		super("");
	}

	public ConfigurableAi(String aiId) {
		super(aiId);
	}

	private NormalAiConfig aiConfig;
	/** 呼叫次数(由逃跑状态转换为追击状态,或者由逃跑状态转换为战斗状态时候的呼叫不计入此数目) */
	private int shoutNumber = 0;
	private int escapeNumber = 0;
	private boolean autoMaxHp = false ;
	
	@Override
	public boolean isAutoMaxHp() {
		return this.autoMaxHp ;
	}
	
	public void init() {
		// 相关状态变量reset
		this.resetStateParameter();
		// 获得aiConfig
		// 1. 添加相关技能
		// 2. 根据npcType设置默认的状态
		aiConfig = context.getAiApp().getNormalAiConfigMap().get(aiId);
		if (null == aiConfig) {
			logger.error("aiConfig template not config ,aiId=" + aiId
					+ " pls check it");
			return;
		}
		// 添加技能
		role.getSkillMap().clear();
		for (Iterator<Map.Entry<Integer, Integer>> it = aiConfig.getSkillMap()
				.entrySet().iterator(); it.hasNext();) {
			Map.Entry<Integer, Integer> entry = it.next();
			Skill skill = context.getSkillApp().getSkill(
					entry.getKey().shortValue());
			if (null == skill) {
				continue;
			}
			int skillId = entry.getKey();
			RoleSkillStat stat = new RoleSkillStat();
			stat.setSkillId((short) skillId);
			stat.setSkillLevel(entry.getValue());
			role.getSkillMap().put((short) skillId, stat);
		}
		// 设置全局状态
		role.getAi()
				.getStateMachine()
				.setGlobalState(
						StateFactory.getState(StateType.State_Global, role,
								null));
		// actionId 由Ai决定
		NpcInstance npcRole = (NpcInstance) role;
		npcRole.setNpcActionType(NpcActionType.getType(aiConfig.getActionId()));
		// 设置默认状态
		State defaultState = context.getAiApp().getNpcDefaultState(
				npcRole);
		role.getAi().getStateMachine().switchState(defaultState);
		// 设置攻击距离
		npcRole.setThinkArea(aiConfig.getThinkArea());
		//是否自动调整maxhp
		autoMaxHp = context.getAiApp().isAutoMapHpNpc(npcRole.getNpc().getNpcid());
	}

	@Override
	public boolean isInView(AbstractRole target) {
		if (null == target) {
			return false;
		}
		MapInstance targetMapInstance = target.getMapInstance();
		if (null == targetMapInstance) {
			return false;
		}
		if (!role.getMapInstance().getInstanceId()
				.equals(targetMapInstance.getInstanceId())) {
			return false;
		}

		// 如果目标在仇恨列表中则视为可见
		if (role.getHatredTarget().inHatredMap(target.getRoleId()))
			return true;
		// 判断是否有碰撞
		boolean baffle = role
				.getMapInstance()
				.getMap()
				.hasBaffle(role.getMapX(), role.getMapY(), target.getMapX(),
						target.getMapY());
		if (!baffle) {
			return Util
					.inCircle(role.getMapX(), role.getMapY(), target.getMapX(),
							target.getMapY(), aiConfig.getAlertArea());
		}
		return false;
	}

	@Override
	public boolean isInSummonedView(AbstractRole target) {
		if (null == target) {
			return false;
		}
		if (!role.getMapInstance().getInstanceId()
				.equals(target.getMapInstance().getInstanceId())) {
			return false;
		}
		return Util.inCircle(role.getMapX(), role.getMapY(), target.getMapX(),
				target.getMapY(), aiConfig.getShoutArea());
	}

	@Override
	public void chasedTarget(AbstractRole chaser) {
		// 发送停止消息
		role.getBehavior().stopMove();
		// 追逐上目标
		if (ForceRelation.enemy == role.getForceRelation(chaser)) {
			// 如果目标是敌对关系则进入战斗模式
			this.stateMachine.switchState(StateFactory.getState(
					StateType.State_Battle, role, chaser));
		}

	}

	@Override
	public void canReachByRangeAttack(AbstractRole attack) {
		// 判断是否追击
		if (!this.isActiveAttack()) {
			return;
		}
		// 主动怪
		// TODO: 追击
	}

	@Override
	public void respondSeekRescue(AbstractRole seeker) {
		if (seeker == null)
			return;
		// 并且在自己的帮助列表的
		NpcInstance npc = (NpcInstance) seeker;
		if (!aiConfig.getHatredMonsterSet().contains(npc.getNpc().getNpcid())) {
			return;
		}
		// 判断范围
		// 判断视野,不在视野中不出援手
		if (!this.isInView(seeker)) {
			return;
		}
		int skillId = selectRescueSkill();
		if (skillId <= 0) {
			return;
		}
		Skill skill = context.getSkillApp().getSkill((short) skillId);
		if (SkillApplyResult.SUCCESS != skill.condition(role)) {
			return;
		}
		role.setTarget(seeker);
		GameContext.getUserSkillApp().useSkill(role, (short) skillId);
	}

	@Override
	public int selectRescueSkill() {
		return aiConfig.getSkill3();
	}

	@Override
	public SkillSelectResult selectSkill() {
		SkillSelectResult result = new SkillSelectResult();
		if (null != aiConfig.getBossActions()) {
			result.setBossActions(aiConfig.getBossActions());
			return result;
		}
		Skill skill = null;
		AiSkillConfig[] cfgs = aiConfig.getSkillConfigs();
		for (AiSkillConfig cfg : cfgs) {
			if (this.currentHpRate() >= cfg.getHpRate()) {
				break;
			}
			skill = context.getSkillApp().getSkill((short) cfg.getSkill());
			if (null != skill) {
				SkillApplyResult con = skill.condition(role);
				if (SkillApplyResult.SUCCESS == con) {
					result.setSuccessSkill(skill);
					result.setSkillDialogue(cfg.getShout());
					return result;
				}
				result.addFailureSkill(skill, con);
			}
		}
		skill = context.getSkillApp().getSkill(
				(short) aiConfig.getNormalSkill());
		if (null != skill) {
			SkillApplyResult con = skill.condition(role);
			if (SkillApplyResult.SUCCESS == con) {
				result.setSuccessSkill(skill);
				return result;
			}
			result.addFailureSkill(skill, con);
		} else {
			// 这是不能行动的NPC，策划没有配置可以使用的技能
			return null;
		}
		return result;
	}

	@Override
	public boolean isActiveAttack() {
		return aiConfig.isInitiative();
	}

	@Override
	public void summonedRole() {
		this.shoutNumber++;
		// 召唤同伴
		/**
		 * 从地图取npc实例
		 */
		role.getMapInstance().getMessageDispatcher()
				.dispatch(new Telegram(role, null, MessageType.SHOUT));
	}

	@Override
	public void receiveSummonMsg(AbstractRole summonRole) {
		// 刚接收到召唤消息
		if (summonRole.getRoleType() != RoleType.NPC) {
			return;
		}
		if (!this.isActiveAttack()) {
			return;
		}
		// 并且在自己的帮助列表的
		NpcInstance npc = (NpcInstance) summonRole;
		if (!aiConfig.getHatredMonsterSet().contains(npc.getNpc().getNpcid())) {
			return;
		}
		// 判断视野
		/*
		 * if (!this.isInView(summonRole)) { return; }
		 */
		// 判断呼救范围
		if (!isInSummonedView(summonRole)) {
			return;
		}

		if (this.stateMachine.isInState(StateType.State_Battle)) {
			// 在战斗状态
			return;
		}
		if (this.stateMachine.isInState(StateType.State_Home_Move)) {
			// 回家状态
			return;
		}
		// 召唤成功
		this.beSummoned(summonRole);
	}

	@Override
	public void beSummoned(AbstractRole summonRole) {
		// 召唤成功
		// 将召唤着仇恨列表设置为自己仇恨,并且进入战斗状态
		/*
		 * for(Object haredRoleId :
		 * summonRole.getHatredTarget().getHatredMap().keySet()){ String strHR =
		 * haredRoleId.toString();
		 * this.role.getHatredTarget().getHatredMap().put(strHR,
		 * summonRole.getHatredTarget().getHatredMap().get(strHR)); }
		 */
		summonRole.getHatredTarget().copyHatredMap(role.getHatredTarget());
		role.getAi()
				.getStateMachine()
				.switchState(
						StateFactory.getState(StateType.State_Battle, role,
								null));
	}

	@Override
	public boolean escapeConditions() {
		if (this.escapeNumber >= 1) {
			return false;
		}
		if (aiConfig.getActionId() == NpcActionType.ROOT.getType()) {
			return false;
		}
		if (role.getAi().getStateMachine().isInState(StateType.State_Escape)) {
			// 当前已经是逃跑状态
			return false;
		}

		if (!hpConditionsForEscape()) {
			return false;
		}
		return true;
	}

	private boolean hpConditionsForEscape() {
		// 逃跑hp条件
		boolean value = (aiConfig.getFleeWorth() > 0 && role.getCurHP() < aiConfig
				.getFleeWorth())
				|| (this.currentHpRate() < aiConfig.getFleeRate());
		return value;
	}

	private boolean hpConditionsForSummoned() {
		// 呼叫hp条件
		boolean value = (aiConfig.getShoutArea() > 0 && role.getCurHP() < aiConfig
				.getShoutWorth())
				|| (this.currentHpRate() < aiConfig.getShoutRate());
		return value;
	}

	/*
	 * private boolean hpConditions(){ boolean value = (aiConfig.getFleeWorth()
	 * > 0 && role.getCurHP() < aiConfig.getFleeWorth()) || (
	 * this.currentHpRate()< aiConfig.getFleeRate()); return value ; }
	 */

	private float currentHpRate() {
		return 100 * role.getCurHP() / (float) role.getMaxHP();
	}

	@Override
	public void enterEscapeMode() {
		escapeNumber++;
	}

	@Override
	public boolean escapeSummonedConditions() {
		return summonedConditions(2);
	}

	@Override
	public boolean summonedConditions() {
		return summonedConditions(1);
	}

	private boolean summonedConditions(int maxShoutNumer) {
		// 召唤条件
		/*
		 * if (!this.isActiveAttack()) { //非主动怪不能召唤 return false; }
		 */
		// 配置了呼叫半径即可
		if (aiConfig.getShoutArea() <= 0) {
			// 呼叫半径<0,其他人无法听到
			return false;
		}
		if (0 == aiConfig.getHatredMonsterSet().size()) {
			// 没有仇恨链接怪,其他怪物听到也不会做出任务响应
			return false;
		}

		// hp条件
		if (!hpConditionsForSummoned()) {
			return false;
		}
		// 判断是否已经呼叫过
		if (this.shoutNumber >= maxShoutNumer) {
			return false;
		}
		// TODO: 逃跑状态到追击状态或者战斗状态条件的考虑
		return true;
	}

	@Override
	public void resetStateParameter() {
		this.shoutNumber = 0;
		this.escapeNumber = 0;
		NpcInstance npc = (NpcInstance) role;
		//清除攻击者信息
		npc.resetAttackerInfo();
		// 清除buff
		npc.clearState();
		npc.getReceiveBuffCopy().clear();
		GameContext.getUserBuffApp().recoverNpcShape(npc);
		// 重算属性
		GameContext.getUserAttributeApp().reCalct(npc);
		role.getBehavior().notifyAttribute();
		// role.getBehavior().resetHpMp();
		role.getHatredTarget().clearHatredMap();
		npc.setOwnerInstance(null);
		npc.setTarget(null);
		npc.setBossState(null);
		npc.setTimeRecords(null);
		// 重置技能CD
		for (RoleSkillStat stat : npc.getSkillMap().values()) {
			stat.setLastProcessTime(0);
		}
	}

	public void attackStart(AbstractRole attacker, int hatred) {
		if (null == attacker) {
			return;
		}
		role.getMapInstance()
				.getMessageDispatcher()
				.dispatch(
						new Telegram(attacker, (NpcInstance) role,
								MessageType.ATTACK, 0, hatred));
		if(this.isAutoMaxHp() && RoleType.PLAYER == attacker.getRoleType()){
			//自动调整mapHp
			NpcInstance npc = (NpcInstance)role ;
			npc.getAllAttacker().add(attacker.getIntRoleId()) ;
		}
	}

	public void enterEvadeMode() {
		this.resetStateParameter();
		// 离出生点太远，进入回家状态
		// 如果不是巡逻者并且离家不近，则回家

		if (aiConfig.getActionId() != NpcActionType.PATROLMAN.getType()
		/* && !role.getAi().nearFromHome() */) {
			/***
			 * !role.getAi().nearFromHome() 将注释的原因:
			 * 发现对怪放风筝的时候,怪有可能直接转到了默认状态,导致怪的位置没有同步 客户端看到的事怪不真实的位置
			 */
			role.getAi()
					.getStateMachine()
					.switchState(
							StateFactory.getState(StateType.State_Home_Move,
									role, null));
			return;
		}
		// 如果是巡逻者或者离家很近则进入默认状态，如果是ROOT则回到IDLE状态
		State defaultState = GameContext.getAiApp().getNpcDefaultState(
				(NpcInstance) role);
		role.getAi().getStateMachine().switchState(defaultState);

	}

	public void movementInform() {
		// 到达某点
		// 如果仇恨列表存在，进入战斗状态
		// 如果仇恨列表不存在,进入默认状态(默认状态满血满蓝,默认状态决定是否回家)
	}

	public void moveInLineOfSight(AbstractRole target) {
		if (ForceRelation.enemy == role.getForceRelation(target)) {
			// 如果目标是敌对关系则进入战斗模式
			// role.getHatredMap().put(target.getRoleId(),
			// AIMoveConstant.MOVE_IN_LINE_SIGHT_HATRED);
			role.getHatredTarget().addHatred(target,
					AIMoveConstant.MOVE_IN_LINE_SIGHT_HATRED);
			role.getAi()
					.getStateMachine()
					.switchState(
							StateFactory.getState(StateType.State_Battle, role,
									target));
		}
	}

	@Override
	public boolean isOutOfView(AbstractRole target) {
		if (target == null)
			return true;
		if (!role.getMapId().equals(target.getMapId())) {
			return true;
		}
		// 如果目标在仇恨列表中则视为可见
		if (role.getHatredTarget().inHatredMap(target.getRoleId()))
			return false;
		return !Util.inCircle(role.getMapX(), role.getMapY(), target.getMapX(),
				target.getMapY(), aiConfig.getAlertArea());
	}

	@Override
	public boolean tooFarFromHome() {
		if (aiConfig.getActionId() == NpcActionType.PATROLMAN.getType()) {
			return false;
		}
		Point bornPoint = role.getRebornPoint();
		return !Util.inCircle(role.getMapX(), role.getMapY(), bornPoint.getX(), bornPoint.getY(),
				aiConfig.getChaseArea());
	}

	@Override
	public boolean tooFarFromHome(AbstractRole target) {
		if (!role.getMapId().equals(target.getMapId())) {
			return true;
		}
		Point homePoint = role.getRebornPoint();
		if (aiConfig.getActionId() == NpcActionType.PATROLMAN.getType()) {
			// 巡逻NPC判断离开path点与现在的距离
			/*
			 * Path path = role.getWalkPath() ; if(null != path){ homePoint =
			 * path
			 * .getPathNode().get(role.getAi().getWapPointInfo().getCurrentNodeIndex
			 * ()); }
			 */
			homePoint = new Point(role.getMapId(), role.getMapX(),
					role.getMapY());
		}
		return !Util.inCircle(homePoint.getX(), homePoint.getY(),
				target.getMapX(), target.getMapY(), aiConfig.getChaseArea());
	}

	@Override
	public boolean nearFromHome() {
		return Util.inCircle(role.getMapX(), role.getMapY(), role
				.getRebornPoint().getX(), role.getRebornPoint().getY(),
				AIMoveConstant.NEAR_FROM_HOME_DISTANCE);
	}

	@Override
	public boolean inAttackRange(AbstractRole target) {
		// 追击思考距离,即最大攻击距离
		int attackRange = this.getThinkArea() + DIS_OFFSET;
		return Util.inCircle(role.getMapX(), role.getMapY(), target.getMapX(),
				target.getMapY(), attackRange);
	}

	@Override
	public int getAlertArea() {
		return aiConfig.getAlertArea();
	}

	@Override
	public int getThinkArea() {
		return ((NpcInstance) role).getThinkArea();
	}

	@Override
	public SkillApplyResult useSkill(NpcInstance entity, int skillId) {
		return GameContext.getUserSkillApp().useSkill(entity, (short) skillId);
	}

	@Override
	public void justDied() {
		// 死亡喊话
		deathShout();
		// NPCAI刷怪
		refreshNpc();
		// 死亡音效
		deathMusic();
		// 死亡释放技能
		deathSkill();
		//向订阅者广播死亡事件
		this.postAiMessageEvent(MessageType.JUSTDIE);
	}

	@Override
	public void npcDiedEncouragement() {

	}

	@Override
	public void justRespawned() {
		// NPC出生喊话
		brithShout();
		//检查npc是否初始化buff
		/*if(aiConfig.getBornBuffId() > 0){
			brithInitBuff();
		}*/
	}

	/**
	 * 刷怪
	 */
	private void refreshNpc() {
		try {
			GameContext.getAiApp().deathRefresh(
					aiConfig.getDeathRefreshGroup(), role);
		} catch (Exception e) {
			logger.error("ConfigurableAi.refreshNpc error:", e);
		}
	}

	/**
	 * 死亡喊话
	 */
	private void deathShout() {
		try {
			String deathShout = aiConfig.getDeathShout();
			if (!Util.isEmpty(deathShout)) {
				this.getRole().getBehavior().notifyNpcMsg(deathShout);
			}
		} catch (Exception e) {
			logger.error("ConfigurableAi.deathShout error:", e);
		}
	}

	/**
	 * 死亡音效
	 */
	private void deathMusic() {
		try {
			byte musicId = aiConfig.getDeathMusicId();
			if (0 == musicId) {
				return;
			}
			C1190_NpcMusicNotifyMessage message = new C1190_NpcMusicNotifyMessage();
			message.setMusicId(musicId);
			this.getRole().getMapInstance().broadcastMap(null, message);
		} catch (Exception e) {
			logger.error("ConfigurableAi.deathShout error:", e);
		}
	}

	/**
	 * 死亡释放技能
	 */
	private void deathSkill() {
		try {
			short skillId = (short) aiConfig.getDeathSkillId();
			if (skillId != 0) {
				
				RoleSkillStat stat = new RoleSkillStat() ;
				stat.setSkillId(skillId);
				stat.setSkillLevel(1);
				stat.setRoleId(this.getRole().getRoleId());
				this.getRole().addSkillStat(stat);
				GameContext.getUserSkillApp().deathUseSkill(this.getRole(),
						skillId);
			}
		} catch (Exception e) {
			logger.error("ConfigurableAi.deathSkill error", e);
		}
	}

	private void brithShout() {
		try {
			AbstractRole role = this.getRole();
			if (role.getRoleType() != RoleType.NPC) {
				return;
			}
			String birthShoutStr = aiConfig.getBirthShout();
			if (Util.isEmpty(birthShoutStr)) {
				return;
			}
			NpcInstance npc = (NpcInstance) role;
			C0003_TipNotifyMessage tipNotifyMessage = new C0003_TipNotifyMessage();
			String msg = npc.getNpcname() + ":" + birthShoutStr;
			tipNotifyMessage.setMsgContext(msg);
			npc.getMapInstance().broadcastMap(null, tipNotifyMessage);
		} catch (Exception e) {
			logger.error("ConfigurableAi.deathShout error:", e);
		}
	}
	
	private void brithInitBuff() {
		try {
			AbstractRole role = this.getRole();
			if (role.getRoleType() != RoleType.NPC) {
				return;
			}
			short buffId = aiConfig.getBornBuffId();
			NpcInstance npc = (NpcInstance) role;
			GameContext.getUserBuffApp().addBuffStat(npc, npc, buffId, 1);
		} catch (Exception e) {
			logger.error("ConfigurableAi.deathShout error:", e);
		}
	}

	@Override
	public RoleSkillStat getNormalSkill() {
		RoleSkillStat skillStat = new RoleSkillStat();
		skillStat.setSkillId((short) aiConfig.getNormalSkill());
		skillStat.setSkillLevel(aiConfig.getNormalskilllevel());
		return skillStat;
	}
}
