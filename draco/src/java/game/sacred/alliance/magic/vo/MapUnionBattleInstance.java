package sacred.alliance.magic.vo;

import java.text.MessageFormat;
import java.util.List;

import sacred.alliance.magic.app.map.Map;
import sacred.alliance.magic.base.ForceRelation;
import sacred.alliance.magic.base.RoleType;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.exception.ServiceException;
import sacred.alliance.magic.util.Util;

import com.game.draco.GameContext;
import com.game.draco.app.chat.ChannelType;
import com.game.draco.app.chat.ChatSysName;
import com.game.draco.app.npc.domain.NpcInstance;
import com.game.draco.app.npc.domain.NpcTemplate;
import com.game.draco.app.union.battle.UnionBattleMapBuffParam;
import com.game.draco.app.union.battle.config.UnionBattleKillMsgConfig;
import com.game.draco.app.union.battle.config.UnionBattleKilledMsgConfig;
import com.game.draco.app.union.battle.domain.UnionBattle;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.google.common.collect.Lists;

/**
 * 公会战地图实例
 */
public class MapUnionBattleInstance extends MapInstance {

	private long overTime = -1;
	// 战斗结束2秒后踢人
	private final static int KICK_ROLE_WHEN_OVER_TIME = 2 * 1000;

	private UionBattleMapState mapState = UionBattleMapState.create;
	private int unionBattleId = 0;
	//地图BUFF，防守方，连胜n次，则攻击方加成10% ^ n
	private UnionBattleMapBuffParam unionBattleMapParam;
	private String originDefenderUnionId;
	protected static enum UionBattleMapState {
		create, 
		normal, 
		kick_role, // 正在清人
		boss_killed, // BOSS被击杀
		destory, // 关闭中
		;
	}

	public MapUnionBattleInstance(Map map, Integer unionBattleId) {
		super(map);
		this.unionBattleId = unionBattleId;
		unionBattleMapParam = GameContext.getUnionBattleApp().getunionBattleMapParam(unionBattleId);
		originDefenderUnionId = GameContext.getUnionBattleApp().getOriginDefenderUnionId(unionBattleId);
	}

	/**
	 * 进入公会战地图
	 */
	@Override
	protected void enter(AbstractRole player) {
		if (!(player instanceof RoleInstance)) {
			return;
		}
		if (this.mapState == UionBattleMapState.kick_role) {
			return;
		}
		RoleInstance role = (RoleInstance) player;
		// 在角色进行公会战期间，角色退出公会的情况
		if (!role.hasUnion()) {
			String context = GameContext.getI18n().getText(
					TextId.UNION_BATTLE_NOT_MEMBER);
			notifyRole(role, context);
			return;
		}
		super.enter(player);
		if(unionBattleMapParam == null || unionBattleMapParam.getMapbuffLevel() == 0){
			return;
		}
		if(!role.getUnionId().equals(originDefenderUnionId)){
			//是攻击方增加BUFF
			for(Short buffId : unionBattleMapParam.getBuffIds()){
				GameContext.getUserBuffApp().addBuffStat(role, role, buffId, unionBattleMapParam.getMapbuffLevel());
			}
		}
	}

	private boolean hasBoss() {
		return GameContext.getUnionBattleApp().hasBoss(this.unionBattleId,
				this.getMap().getMapId());
	}

	private void updateBoss() {
		String bossId = GameContext.getUnionBattleApp().getBossId(
				this.unionBattleId);
		Point point = GameContext.getUnionBattleApp().getBossPoint(
				this.unionBattleId);
		NpcTemplate npcTemplate = GameContext.getNpcApp()
				.getNpcTemplate(bossId);
		if (npcTemplate == null) {
			logger.error("mapUnionBattleInstance.initMapBoss() fail: bossId = "
					+ bossId + "getNpcTemplate  fail");
			return;
		}
		summonCreateNpc(bossId, point.getX(), point.getY());
	}

	@Override
	public boolean canDestroy() {
		if (mapState == UionBattleMapState.destory && !hasPlayer()) {
			return true;
		}
		return false;
	}

	@Override
	protected String createInstanceId() {
		return "union_battle" + unionBattleId + "_" + map.getMapId();
	}

	@Override
	protected void deathLog(AbstractRole victim) {

	}

	/**
	 * 退出地图，返回上之前的地点
	 */
	@Override
	public void exitMap(AbstractRole role) {
		super.exitMap(role);
		if (role.getRoleType() != RoleType.PLAYER) {
			return;
		}
		Point targetPoint = ((RoleInstance) role).getCopyBeforePoint();
		role.setMapId(targetPoint.getMapid());
		role.setMapX(targetPoint.getX());
		role.setMapY(targetPoint.getY());
		this.perfectBody(role);
	}

	/**
	 * 踢人所有角色回到上给自之前的传送点
	 */
	private void kickRole() {
		try {
			if (Util.isEmpty(this.getRoleList())) {
				return;
			}
			for (RoleInstance role : this.getRoleList()) {
				this.kickRole(role);
			}
		} catch (Exception ex) {
			logger.error("mapUnionBattleInstance.kickRole() err:", ex);
		}
	}
	/**
	 * 清除【公会战地图】中，退出公会的角色
	 */
	private void filteRoleNotUnionMember() {
		try {
			if (Util.isEmpty(this.getRoleList())) {
				return;
			}
			for (RoleInstance role : this.getRoleList()) {
				if(!role.hasUnion()){
					this.kickRole(role);
				}
			}
		} catch (Exception ex) {
			logger.error("mapUnionBattleInstance.kickRole() err:", ex);
		}
	}

	/**
	 * 角色死亡 清空被击杀者
	 */
	@Override
	protected void deathDiversity(AbstractRole attacker, AbstractRole victim) {
		// 连杀数，DKP奖励
		addKilled(attacker, victim);
		//累计角色击杀数
		GameContext.getUnionBattleApp().deathRecord(attacker, victim);
	}

	@Override
	public void broadcastScreenMap(AbstractRole role, Message message) {
		super.broadcastMap(role, message, 0);
	}

	@Override
	public void broadcastScreenMap(AbstractRole role, Message message, int expireTime) {
		super.broadcastMap(role, message, expireTime);
	}
	
	/**
	 * NPC死亡
	 */
	@Override
	protected void npcDeathDiversity(AbstractRole attacker, AbstractRole victim) {
		if (victim.getRoleType() != RoleType.NPC) {
			return;
		}
		//累计角色击杀数
		GameContext.getUnionBattleApp().deathRecord(attacker, victim);
		NpcInstance npc = (NpcInstance) victim;
		// 指挥官被击杀
		boolean isBoss = GameContext.getUnionBattleApp().isUnionBattleBoss(
				npc.getNpcid(), this.map.getMapId());
		if (!isBoss) {
			return;
		}
		RoleInstance role = (RoleInstance) attacker.getMasterRole();
		GameContext.getUnionBattleApp().bossKilled(unionBattleId, role, victim);
		
		this.mapState = UionBattleMapState.boss_killed;
		MapUnionBattleContainer ct = (MapUnionBattleContainer) this.getMapContainer();
		ct.addBossKill(unionBattleId);
	}


	private void clearUnionBattleMap() {
		kickRole();
		clearNpc();
	}

	/**
	 * 增加杀人数&增加积分
	 * @param role
	 * @param point
	 */
	private void addKilled(AbstractRole attacker, AbstractRole victim) {
		try {
			if (null == attacker || victim == null) {
				return;
			}
			if (RoleType.PLAYER != attacker.getRoleType()) {//被NPC等击杀
				if (RoleType.PLAYER != victim.getRoleType()) {
					return;
				}
				GameContext.getUnionBattleApp().removeFromKillMap(
						unionBattleId, victim.getRoleId());
				return;
			}
			RoleInstance role = (RoleInstance) attacker;
			String roleId = role.getRoleId();
			int dkp = GameContext.getUnionBattleApp().getKillDkpAward();
			addDkp(role, dkp);
			// dkp
			int killNum = getKillNum(roleId);
			GameContext.getUnionBattleApp().putKillMap(unionBattleId, roleId,
					++killNum);
			// 连续击杀的DKP
			addKillDkp(role, killNum);

			if (victim.getRoleType() != RoleType.PLAYER) {
				return;
			}
			// 连杀
			addKilledDkp(role, victim);
			// 清除死亡者的击杀数
			GameContext.getUnionBattleApp().removeFromKillMap(unionBattleId,
					victim.getRoleId());
		} catch (Exception e) {
			logger.error("", e);
		}
	}

	/**
	 * 打断连续击杀所获得额外DKP
	 */
	private void addKilledDkp(RoleInstance role, AbstractRole victim) {
		String victimRoleId = victim.getRoleId();
		int vKillNum = getKillNum(victimRoleId);
		UnionBattleKilledMsgConfig cf = GameContext.getUnionBattleApp()
				.getUnionBattleKilledMsgConfig(vKillNum);
		if (cf != null) {
			addDkp(role, cf.getDkp());

			String content = MessageFormat.format(cf.getContent(),
					role.getRoleName(), victim.getRoleName(),
					victim.getRoleName(), vKillNum, cf.getDkp());
			broadCast(content, cf.getChannelType());
		}
	}

	/**
	 * 连续击杀获得额外DKP
	 */
	private void addKillDkp(RoleInstance role, int killNum) {
		UnionBattleKillMsgConfig cf = GameContext.getUnionBattleApp()
				.getUnionBattleKillMsgConfig(killNum);
		if (cf != null) {
			addDkp(role, cf.getDkp());
			String content = MessageFormat.format(cf.getContent(),
					role.getRoleName(), cf.getDkp());
			broadCast(content, cf.getChannelType());
		}
	}

	private int getKillNum(String roleId) {
		return GameContext.getUnionBattleApp().getUnionBattleKillNum(
				unionBattleId, roleId);
	}

	/**
	 * 添加DKP
	 * @param role
	 * @param dkp
	 */
	private boolean addDkp(RoleInstance role, int dkp) {
		if (role == null || dkp <= 0) {
			return false;
		}
		return GameContext.getUnionBattleApp().addDkp(role, dkp);
	}

	@Override
	public boolean canEnter(AbstractRole role) {
		return true;
	}

	@Override
	public void useGoods(int goodsId) {

	}

	/**
	 * 主线程 
	 * 公会战结束踢人，指挥官被击杀踢人
	 */
	@Override
	protected void updateSub() throws ServiceException {
		boolean activeOpen = GameContext.getUnionBattleApp()
				.isUnionBattleActiveTimeOpen();
		// 公会战结束,开始踢人，并设置地图为destroy状态
		if (!activeOpen) {
			GameContext.getUnionBattleApp().endUnionBattleActive();
			overTime = System.currentTimeMillis();
			if(this.mapState != UionBattleMapState.destory){
				this.mapState = UionBattleMapState.kick_role;
			}
		}
		MapUnionBattleContainer ct = (MapUnionBattleContainer) this.getMapContainer();
		if(ct.isBossKilled(unionBattleId)){
			overTime = System.currentTimeMillis();
			this.mapState = UionBattleMapState.kick_role;
		}
		
		switch (mapState) {
		case create: // 如果是创建状态，初始化
			GameContext.getUnionBattleApp().initUnionBattle(unionBattleId);
			if (hasBoss()) {
				updateBoss();
			}
			this.mapState = UionBattleMapState.normal;
			break;
		case boss_killed: // 收到指挥官被杀死的状态
			overTime = System.currentTimeMillis();
			this.mapState = UionBattleMapState.kick_role;
			break;
		case kick_role: // 立即踢人
//			if (System.currentTimeMillis() - overTime >= KICK_ROLE_WHEN_OVER_TIME) {
			clearUnionBattleMap();
			this.mapState = activeOpen?UionBattleMapState.create:UionBattleMapState.destory ;
//			}
			ct.removeBossKill(unionBattleId);
			break;
		case destory:
			super.destroy();
			break;
		default:
			break;
		}
		filteRoleNotUnionMember();
		super.updateSub();
	}


	@Override
	protected ForceRelation getForceRelation(NpcInstance npc,
			RoleInstance target) {
		return getForceRelation(target, npc);
	}

	@Override
	protected ForceRelation getForceRelation(RoleInstance role,
			RoleInstance target) {
		if (role.getUnion() != null && target.getUnion() != null) {
			if (role.getUnionId().equals(target.getUnionId())) {
				return ForceRelation.friend;
			}
		}
		return ForceRelation.enemy;
	}

	@Override
	protected ForceRelation getForceRelation(RoleInstance role,
			NpcInstance target) {
		if(role.getUnionId() == null){
			return ForceRelation.friend;
		}
		UnionBattle battle = GameContext.getUnionBattleApp()
				.getUnionBattleByMapId(this.getMap().getMapId());
		if (role.getUnionId().equals(battle.getUnionId())) {
			return ForceRelation.friend;
		}
		return ForceRelation.enemy;
	}

	@Override
	protected ForceRelation getForceRelation(NpcInstance npc, NpcInstance target) {
		return ForceRelation.enemy;
	}

	/**
	 * 地图广播
	 * 
	 * @param content
	 * @param channelType
	 */
	public void broadCast(String content, byte channelType) {
		GameContext.getChatApp().sendSysMessage(ChatSysName.System,
				ChannelType.getChannelType(channelType), content, null, null);
	}

	/**
	 * 通知玩家
	 */
	public void notifyRole(RoleInstance role, String context) {
		C0003_TipNotifyMessage msg = new C0003_TipNotifyMessage(context);
		role.getBehavior().sendMessage(msg);
	}
	
	@Override
	public boolean mustRunMapLoop(){
		//活动没开启的时候强制进入地图主循环逻辑便于自动销毁地图
		return !GameContext.getUnionBattleApp().isUnionBattleActiveTimeOpen(); 
	}
}
