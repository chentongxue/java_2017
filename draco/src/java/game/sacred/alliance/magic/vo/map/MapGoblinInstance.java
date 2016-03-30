package sacred.alliance.magic.vo.map;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import sacred.alliance.magic.base.ForceRelation;
import sacred.alliance.magic.base.RebornType;
import sacred.alliance.magic.base.RoleType;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.exception.ServiceException;
import sacred.alliance.magic.scheduler.job.LoopCount;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.MapGoblinContainer;
import sacred.alliance.magic.vo.MapInstance;
import sacred.alliance.magic.vo.Point;
import sacred.alliance.magic.vo.RebornPointDetail;
import sacred.alliance.magic.vo.RoleBornGuide;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.goblin.config.GoblinBaseConfig;
import com.game.draco.app.goblin.config.GoblinLocationConfig;
import com.game.draco.app.goblin.config.GoblinSecretConfig;
import com.game.draco.app.goblin.vo.GoblinSecretBossTemplate;
import com.game.draco.app.npc.domain.NpcInstance;
import com.game.draco.app.npc.domain.NpcTemplate;
import com.game.draco.app.team.LeaveTeam;
import com.game.draco.app.team.Team;
import com.game.draco.message.response.C0002_ErrorRespMessage;

/**
 * 哥布林密境
 */
public class MapGoblinInstance extends MapInstance {
	private static final Logger logger = LoggerFactory.getLogger(MapGoblinInstance.class);
	/**
	 * 哥布林BOSS数目
	 */
	private static final int Goblin_Number = 4;

	private final LoopCount mapLoop = new LoopCount(10 * 1000);// 检查玩家公会状态循环（10S）
	private final LoopCount mainLoop = new LoopCount(1 * 1000);// 主循环（1S）
	private GoblinSecretConfig goblinSecretConfig = new GoblinSecretConfig();// 密境配置
	private MapState mapState = MapState.init;
	private int killGoblinNumber = 0;// 记录击杀哥布林数目，决定地图状态

	public enum MapState {
		init, // 初始化
		ready, // 战斗阶段
		chest, // 刷新宝箱
		destory, // 销毁地图
		;
	}

	public MapGoblinInstance(sacred.alliance.magic.app.map.Map map) {
		super(map);
	}

	public void setGoblinSecretConfig(GoblinSecretConfig goblinSecretConfig) {
		this.goblinSecretConfig = goblinSecretConfig;
	}

	@Override
	public boolean canDestroy() {
		if (this.hasPlayer()) {
			return false;
		}
		return true;
	}

	@Override
	public boolean canEnter(AbstractRole role) {
		RoleInstance roleInstance = (RoleInstance) role;
		if (roleInstance.hasUnion()) {
			// 拥有公会的玩家允许进入
			return true;
		}
		return false;
	}

	@Override
	protected String createInstanceId() {
		// 不使用这个ID,使用传送点所在（地图ID + 传送点坐标）作为实例ID，在创建地图实例时重新赋值，保证地图实例的单例且不重复
		return "Goblin_" + instanceIdGenerator.incrementAndGet();
	}

	@Override
	protected void npcDeathDiversity(AbstractRole attacker, AbstractRole victim) {
		if (!(victim instanceof NpcInstance)) {
			// 如果死亡的不是NPC则返回（容错）
			return;
		}
		NpcInstance goblin = (NpcInstance) victim;
		if (!this.goblinSecretConfig.isBoss(goblin.getNpcid())) {
			// 如果不是BOSS死亡
			return;
		}
		this.killGoblinNumber++;
		// 记录这个BOSS被杀，下次创建该地图实例时不刷新已死亡的BOSS
		GameContext.getGoblinApp().getGoblinTemplate(this.getKey(goblin.getNpcid())).setDeath((byte) 0);
		// 发放BOSS击杀奖励
		GameContext.getGoblinApp().giveSecretBossReward((RoleInstance) attacker, (NpcInstance) victim);
		// 如果四个哥布林BOSS全被击杀，刷新宝箱BOSS
		if (this.killGoblinNumber == Goblin_Number) {
			this.mapState = MapState.chest;// 切换地图状态到刷新宝箱
			return;
		}
		// 如果宝箱BOSS被击杀，活动结束，副本销毁
		if (this.killGoblinNumber > Goblin_Number) {
			this.mapState = MapState.destory;// 切换地图状态到销毁实例
			return;
		}
	}
	
	@Override
	public Point getRebornPoint(RoleInstance role, RebornType type) {
		if (RebornType.situ == type) {
			return role.getCurrentPoint();
		}
		Point p = role.originalCopyBeforePoint();
		//!!!!! 千万不要调用下面方法，会和此方法发生相互调用
		//Point p = role.getCopyBeforePoint() ;
		if (null == p) {
			return super.getRebornPoint(role, type);
		}
		RebornPointDetail detail = GameContext.getRoleRebornApp().getRebornPointDetail(p.getMapid(), role);
		if (null == detail) {
			return null;
		}
		return detail.createPoint();
	}

	@Override
	public void exitMap(AbstractRole role) {
		try {
			super.exitMap(role);
			this.exit(role);
		} catch (Exception ex) {
			logger.error("MapGoblinInstance.exitMap error!", ex);
		}
	}

	/**
	 * 角色退出地图，返回进入地图的默认出生点
	 * @param role
	 */
	private void exit(AbstractRole role) {
		// 重新设置用户坐标
		Point p = this.getRebornPoint((RoleInstance) role, RebornType.rebornPoint);
		role.setMapId(p.getMapid());
		role.setMapX(p.getX());
		role.setMapY(p.getY());

		// !!!! 必须先完美复活，因为删除英雄的时候会自己切换到其他英雄，导致判断失败
		this.perfectBody(role);

		// 删除buff
		RoleBornGuide guide = GameContext.getRoleBornApp().getRoleBornGuide();
		if (null == guide) {
			return ;
		}
		if (guide.getBuffId() > 0) {
			GameContext.getUserBuffApp().delBuffStat(role, guide.getBuffId(), false);
		}
		if (guide.getGiveHeroId() > 0) {
			GameContext.getHeroApp().deleteHeroBySystem((RoleInstance) role, guide.getGiveHeroId());
		}
	}

	@Override
	protected void deathLog(AbstractRole victim) {
	}

	@Override
	public void useGoods(int goodsId) {
	}

	@Override
	public void destroy() {
		this.kickAllPlayer();
		super.destroy();// 调用父类方法（从MapApp中移除MapInstance）
		// 删除进入该密境的跳转点
		GameContext.getGoblinApp().removeSignJumpPoint(this.instanceId);
		MapGoblinContainer container = (MapGoblinContainer) this.mapContainer;
		// 副本结束，删除该地图实例
		container.destroySignMap(this.instanceId);
	}

	@Override
	public void initNpc(boolean loadNpc) {
		super.initNpc(loadNpc);
		this.refreshGoblin();
	}

	/**
	 * 刷新出哥布林
	 */
	public void refreshGoblin() {
		if (null == this.goblinSecretConfig) {
			return;
		}
		for (String goblinId : this.goblinSecretConfig.getGoblinList()) {
			this.refreshSignGoblin(goblinId);
		}
		this.mapState = MapState.ready;
	}

	/**
	 * 刷新哥布林并把哥布林信息保存到模块中
	 * @param goblinId
	 */
	private void refreshSignGoblin(String goblinId) {
		GoblinLocationConfig config = GameContext.getGoblinApp().getGoblinLocationConfig(goblinId);
		if (null == config) {
			return;
		}
		NpcInstance goblin = this.summonCreateNpc(String.valueOf(goblinId), config.getMapX(), config.getMapY());
		GoblinSecretBossTemplate bossTemplate = GameContext.getGoblinApp().getGoblinTemplate(this.getKey(goblinId));
		if (null != bossTemplate) {
			if ((byte) 1 == bossTemplate.getDeath()) {
				// 如果这个BOSS已经被杀死,则不再刷新出
				return;
			}
			goblin.setNpc(bossTemplate.getNpcTemplate());
			return;
		}
		NpcTemplate npcTemplate = GameContext.getNpcApp().getNpcTemplate(String.valueOf(goblinId));
		NpcTemplate goblinRandom = new NpcTemplate();
		BeanUtils.copyProperties(npcTemplate, goblinRandom);
		// 给NPC模版赋值系数和档位
		GoblinBaseConfig baseConfig = GameContext.getGoblinApp().getGoblinBaseConfig();
		goblinRandom.setSeriesId((byte) baseConfig.getRandomSeries());
		bossTemplate = new GoblinSecretBossTemplate();
		bossTemplate.setDeath((byte) 0);
		bossTemplate.setNpcTemplate(goblinRandom);
		GameContext.getGoblinApp().setGoblinTemplate(this.getKey(goblinId), bossTemplate);
		// 更改模版
		goblin.setNpc(goblinRandom);
	}

	/**
	 * 刷新宝箱BOSS
	 */
	private void refreshChestBoss() {
		if (null == this.goblinSecretConfig) {
			return;
		}
		this.refreshSignGoblin(this.goblinSecretConfig.getBossId9());
		this.mapState = MapState.ready;
	}

	/**
	 * 确保哥布林ID的唯一性
	 * @param goblinId
	 * @return
	 */
	private String getKey(String goblinId) {
		return this.instanceId + "_" + goblinId;
	}

	@Override
	protected void updateSub() throws ServiceException {
		try {
			super.updateSub();
			if (this.mapLoop.isReachCycle()) {
				// 检测玩家是否有公会并踢出没有公会的玩家
				this.kickNoUnionPlayer();
			}
			// 逻辑主循环
			if (this.mainLoop.isReachCycle()) {
				if (this.mapState == MapState.chest) {
					this.refreshChestBoss();
				}
				if (this.mapState == MapState.destory) {
					this.destroy();
				}
			}
		} catch (Exception e) {
			logger.error("MapGoblinInstance.updateSub error!", e);
		}
	}

	/**
	 * 踢出没有公会的玩家
	 */
	private void kickNoUnionPlayer() {
		Collection<RoleInstance> roleList = this.getRoleList();
		if (Util.isEmpty(roleList)) {
			return;
		}
		for (RoleInstance role : roleList) {
			if (null == role) {
				continue;
			}
			if (!role.hasUnion()) {
				C0002_ErrorRespMessage message = new C0002_ErrorRespMessage();
				message.setInfo(GameContext.getI18n().messageFormat(TextId.map_havenot_union_canot_enter,
						this.map.getMapConfig().getMapdisplayname()));
				GameContext.getMessageCenter().sendSysMsg(role, message);
				this.kickRole(role);
			}
		}
	}

	/**
	 * 踢出地图中的所有玩家
	 */
	private void kickAllPlayer() {
		Collection<RoleInstance> roleList = this.getRoleList();
		if (Util.isEmpty(roleList)) {
			return;
		}
		for (RoleInstance role : roleList) {
			if (null == role) {
				continue;
			}
			this.kickRole(role);
		}
	}

	@Override
	protected ForceRelation getForceRelation(RoleInstance role, RoleInstance target) {
		if (Util.isEmpty(role.getUnionId()) || Util.isEmpty(target.getUnionId())) {
			return ForceRelation.enemy;
		}
		// 如果两个玩家同一公会则关系为友好
		if (role.getUnionId().equals(target.getUnionId())) {
			return ForceRelation.friend;
		}
		return ForceRelation.enemy;
	}

	@Override
	protected ForceRelation getForceRelation(RoleInstance role, NpcInstance target) {
		return ForceRelation.enemy;
	}

	@Override
	protected ForceRelation getForceRelation(NpcInstance npc, RoleInstance target) {
		return ForceRelation.enemy;
	}

	@Override
	protected void enter(AbstractRole role) {
		if (role.getRoleType() != RoleType.PLAYER) {
			return;
		}
		super.enter(role);
		// 进入哥布林密境，自动离开队伍
		RoleInstance player = (RoleInstance) role;
		Team team = player.getTeam();
		if (null != team) {
			team.memberLeave(player, LeaveTeam.apply);
		}
	}

	@Override
	protected void deathDiversity(AbstractRole attacker, AbstractRole victim) {
	}
	
	@Override
	public Point getBeforeEnterPoint(RoleInstance role) {
		return this.getRebornPoint(role, RebornType.rebornPoint);
	}
	
}
