package sacred.alliance.magic.app.onlinecenter;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.config.HeartBeatConfig;
import sacred.alliance.magic.app.config.TimingWriteDBConfig;
import sacred.alliance.magic.app.user.UserGoodsApp;
import sacred.alliance.magic.app.user.UserRoleApp;
import sacred.alliance.magic.channel.EmptyChannelSession;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.channel.ChannelHandler;
import sacred.alliance.magic.core.channel.ChannelListener;
import sacred.alliance.magic.core.channel.ChannelSession;
import sacred.alliance.magic.core.server.MinaServer;
import sacred.alliance.magic.util.SessionUtil;
import sacred.alliance.magic.util.StringUtil;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.AppSupport;
import com.game.draco.app.buff.UserBuffApp;
import com.game.draco.app.login.UserInfo;
import com.game.draco.message.internal.C0050_RoleOfflineInternalMessage;
import com.game.draco.message.internal.C0067_TimingWriteDBReqMessage;
import com.game.draco.message.push.C0108_ForcedExitMessage;
import com.game.draco.message.response.C0001_ActiveTestRespMessage;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class OnlineCenterImpl implements OnlineCenter {
	private final static ChannelSession emptyChannelSession = new EmptyChannelSession();
	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	/** 按通行证索引的活动用户角色实例容器 */
	private Map<String, RoleInstance> passportIdMap = Maps.newConcurrentMap() ;
	/** 角色ID和通行证ID对应关系 */
	private Map<String, String> roleIdPassportMap = Maps.newConcurrentMap() ;
	private Map<String, String> roleNamePassportMap = Maps.newConcurrentMap() ;
	private Map<String, Long> passportIoMap = Maps.newConcurrentMap() ;

	private UserRoleApp userRoleApp;
	private UserBuffApp userBuffApp;
	private UserGoodsApp userGoodsApp;
	private HeartBeatConfig heartBeatConfig;
	private ChannelHandler handler;
	private ChannelSession emptySession = new EmptyChannelSession();
	private int maxOnline = 0;
	private long ioBothIdleSecond ;
	
	public void setIoBothIdleSecond(long ioBothIdleSecond) {
		this.ioBothIdleSecond = ioBothIdleSecond;
	}

	public void setHandler(ChannelHandler handler) {
		this.handler = handler;
	}


	public void addOnlineUser(RoleInstance roleInstance) {
		passportIdMap.put(roleInstance.getUserId(), roleInstance);
		roleIdPassportMap.put(roleInstance.getRoleId(), roleInstance
				.getUserId());
		roleNamePassportMap.put(roleInstance.getRoleName(), roleInstance
				.getUserId());
		//打印最高在线人数日志
		int onlineSize = this.onlineUserSize();
		if(onlineSize > maxOnline){
			maxOnline = onlineSize;
			GameContext.getStatLogApp().roleOnlineLog();
		}
	}

	private void offlineEvent(ChannelSession session) {
		if (null == session) {
			return;
		}
		// 用户下线后删除在线信息
		String userId = SessionUtil.getUserId(session) ;
		if (null == userId) {
			return;
		}
		// 保证单用户单线程允许
		C0050_RoleOfflineInternalMessage reqMsg = new C0050_RoleOfflineInternalMessage();
		reqMsg.setUserId(userId);
		reqMsg.setSession(session);
		GameContext.getUserSocketChannelEventPublisher().publish(userId,
				reqMsg, session);
	}

	private void updateOfflineTime(RoleInstance role) {
		try {
			if (null == role) {
				return;
			}
			int onlineTime = (int) ((System.currentTimeMillis() - role
					.getLastLoginTime().getTime()) / 60000);
			role.setHistoryOnlineTime(role.getHistoryOnlineTime() + onlineTime);
			// 设置下线时间
			role.setLastOffTime(new Date());
			if (StringUtil.dateFormatDay(role.getLastLoginTime()).equals(
					StringUtil.dateFormatDay(new Date()))) {
				onlineTime += role.getDayOnlineTime();
			}
			role.setDayOnlineTime(onlineTime);
		} catch (Exception ex) {
			logger.error("", ex);
		}
	}

	@Override
	public void offline(RoleInstance role, boolean withIO) {
		if (null == role) {
			return;
		}
		String roleId = role.getRoleId();
		String userId = role.getUserId();
		try {
			//将role设置为下线
			role.setOfflined(true);
			
			try {
				// 清除地图中信息
				role.getBehavior().exitMap();
			} catch (Exception ex) {
				logger.error("role exitmap error,userId=" + role.getUserId()
						+ " roleId=" + role.getRoleId(), ex);
			}
			// 取消交易
			try {
				GameContext.getTradingApp().logout(role);
			} catch (Exception ex) {
			}

			// 组队平台下线处理
			GameContext.getTeamApp().onLogout(role, null);
			
			// 更新充值表
			try {
				GameContext.getChargeApp().updateUserGold(role);
			} catch (Exception e) {
				logger.error(
						"offline update easter_user_pay table error: userId="
								+ role.getUserId() + " userName="
								+ role.getUserName() + " roleId="
								+ role.getRoleId(), e);
			} finally {
				GameContext.getChargeApp().printRolePayLog(
						role.getRolePayRecord(), "-");
			}
			this.updateOfflineTime(role);
			role.updateLevelUpTime();
			// 邮件补偿
			GameContext.getRecoupApp().onLogout(role, null);
			// 保存数据
			this.saveOnline(role);
			this.offlineSuccess(userId, roleId, withIO);
			//看门狗
			GameContext.getDoorDogApp().roleLogout(role);
			// 下线物品日志
			GameContext.getStatLogApp().logoutGoodsInfoLog(role);
			// 下线日志
			GameContext.getStatLogApp().roleLogoutLog(role);
			// 每日在线时长日志
			GameContext.getStatLogApp().roleOnlineTimeLog(role, new Date());
		} catch (Exception ex) {
			logger.error(
					"offline error,userId=" + userId + " roleId=" + roleId, ex);
		}
	}

	private void offline(ChannelSession session, boolean withIO) {
		if (null == session) {
			logger.error("null_sessin 3");
			return;
		}
		// 用户下线后删除在线信息
		String userId = SessionUtil.getUserId(session);
		if (null == userId) {
			return;
		}
		RoleInstance role = this.getRoleInstanceByUserId(userId);
		if (null == role) {
			// 不要忘记这里,否则用户在角色列表界面断网就悲剧了
			this.offlineSuccess(userId, "", withIO);
			return;
		}
		this.offline(role, withIO);
	}

	@Override
	public void offlineWithNetIO(ChannelSession session) {
		this.offline(session, true);
		UserInfo userInfo = SessionUtil.getUserInfo(session);
		if(null != userInfo){
			userInfo.setCurrRoleId(0);
		}
	}

	@Override
	public void offline(ChannelSession session) {
		this.offline(session, false);
	}

	private void offlineSuccess(String userId, String roleId, boolean withIO) {
		// 删除在线中心数据
		removeOnlineUser(userId);
		if (!withIO) {
			this.removeIoId(userId);
		}
		logger.info("offline success,userId=" + userId + " roleId=" + roleId
				+ " withIO=" + withIO);
	}


	public int onlineUserSize() {
		return passportIdMap.size();
	}

	@Override
	public void stop() {
		// 角色数据入库
		for (RoleInstance role :this.getAllOnlineRole()) {
			//GameContext.getOnlineCenter().offline(role);
			try{
				role.getBehavior().closeNetLink();
			}catch(Exception ex){
				logger.error("",ex);
			}
		}
		try{
			//因为closeNetLink是异步操作,这里必须等所有入库操作完成
			while(true){
				int onlineSize = 0 ;
				try{
					onlineSize = GameContext.getOnlineCenter().onlineUserSize();
					logger.info("onlineCenter stop: onlineSize=" + onlineSize);
					if(onlineSize>0){
						Thread.sleep(3000);
					}else{
						break ;
					}
				}catch(Exception ex){
					logger.error("",ex);
					try {
						Thread.sleep(2000);
					} catch (Exception e) {
					}
				}
			}
		}catch(Exception ex){
			logger.error("onlineCenter stop error",ex);
		}
	}
	
	private void tickOffline(RoleInstance role){
		C0050_RoleOfflineInternalMessage reqMsg = new C0050_RoleOfflineInternalMessage();
		reqMsg.setUserId(role.getUserId());
		reqMsg.setSession(emptySession);
		reqMsg.setRole(role);
		GameContext
				.getUserSocketChannelEventPublisher()
				.publish(role.getUserId(), reqMsg,
						emptySession);
		role.setOfflined(true);
		logger.error("[heartTime out] roleId="
				+ role.getRoleId() + " userId="
				+ role.getUserId());
	}

	@Override
	public void start() {
		Thread heartTest = new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						Thread.sleep(1 * 60 * 1000);
					} catch (Exception e) {
					}
					try {
						if (!heartBeatConfig.getHeartBeatOffline()) {
							continue;
						}
						//未收到心跳时间
						int time2 = GameContext.getHeartBeatConfig().getHeartBeat() * 1000 * 2;
						int time4 = GameContext.getHeartBeatConfig().getHeartBeat() * 1000 * 4;
						int clientHomebackMillistime = GameContext.getHeartBeatConfig().getClientHomebackMillistime();
						for (RoleInstance role : passportIdMap.values()) {
							try {
								if (role == null || role.isOfflined()) {
									continue;
								}
								long now = System.currentTimeMillis();
								if(now - role.getLastHeartNumber() >= clientHomebackMillistime){
									//已达切换到后台进程最大时间
									tickOffline(role);
									continue ;
								}
								//System.out.println("==================lastHeart=" + role.getLastHeartNumber() + " lastRead=" +
								//		role.getBehavior().getLastIoReadTime() + " lastWrite=" + role.getBehavior().getLastIoWriteTime() + " now=" + now);
								
								long roleHeart2 = role.getLastHeartNumber() + time2 ;
								long roleHeart4 = role.getLastHeartNumber() + time4 ;
								long writeInernal = now-role.getBehavior().getLastIoWriteTime();
								//System.out.println("============= writeInernal=" + writeInernal);
								if(now >= roleHeart4 && writeInernal >= ioBothIdleSecond*1000){
									//4次未收到心跳
									//System.out.println("======================================== 1");
									tickOffline(role);
									continue ;
								}
								if(now >= roleHeart2){
									//2次未收到心跳
									//主动push心跳返回
									//System.out.println("======================================== 2");
									role.getBehavior().sendMessage(new C0001_ActiveTestRespMessage());
									continue ;
								}
								//System.out.println("======================================== 3");
							}catch(Exception ex){
								logger.error("",ex);
							}
						}
					} catch (Exception ex) {
						logger.error("", ex);
					}
				}
			}
		});
		heartTest.start();

		handler.addListener(new ChannelListener() {
			@Override
			public void fireMessageSent(ChannelSession arg0, Object arg1)
					throws Exception {
			}

			@Override
			public void fireSessionClosed(ChannelSession session)
					throws Exception {
				// synchronized (session) {
				// offline(session);
				// }
				offlineEvent(session);
			}

			@Override
			public void messageReceived(ChannelSession arg0, Object arg1)
					throws Exception {
			}
		});

		this.initAppList();
	}

	@Override
	public RoleInstance getRoleInstanceByRoleId(String roleId) {
		String passportId = this.roleIdPassportMap.get(roleId);
		if (null == passportId) {
			return null;
		}
		return this.getRoleInstanceByUserId(passportId);
	}

	@Override
	public RoleInstance getRoleInstanceByRoleName(String roleName) {
		String passportId = this.roleNamePassportMap.get(roleName);
		if (null == passportId) {
			return null;
		}
		return this.getRoleInstanceByUserId(passportId);
	}

	@Override
	public RoleInstance getRoleInstanceByUserId(String userId) {
		return this.passportIdMap.get(userId);
	}

	@Override
	public boolean isOnlineByUserId(String userId) {
		if (null == userId || 0 == userId.trim().length()) {
			return false;
		}
		return this.passportIdMap.containsKey(userId);
	}

	@Override
	public boolean isOnlineByRoleId(String roleId) {
		if (null == roleId || 0 == roleId.trim().length()) {
			return false;
		}
		return this.roleIdPassportMap.containsKey(roleId);
	}

	@Override
	public void removeOnlineUser(String userId) {
		RoleInstance roleInstance = this.passportIdMap.remove(userId);
		if (null == roleInstance) {
			return;
		}
		this.roleIdPassportMap.remove(roleInstance.getRoleId());
		this.roleNamePassportMap.remove(roleInstance.getRoleName());
	}

	public void setUserRoleApp(UserRoleApp userRoleApp) {
		this.userRoleApp = userRoleApp;
	}

	public void setUserBuffApp(UserBuffApp userBuffApp) {
		this.userBuffApp = userBuffApp;
	}

	public void setUserGoodsApp(UserGoodsApp userGoodsApp) {
		this.userGoodsApp = userGoodsApp;
	}

	@Override
	public Collection<RoleInstance> getAllOnlineRole() {
		return passportIdMap.values();
	}

	private final List<AppSupport> appList = Lists.newArrayList();
	/**
	 * 初始化app列表
	 */
	private void initAppList() {
		//属性
		appList.add(GameContext.getAttriApp());
		//保存角色
		appList.add(userRoleApp);
		// 保存goods信息
		appList.add(userGoodsApp);
		// 保存仓库信息
		appList.add(GameContext.getUserWarehouseApp());
		// 保存技能
		appList.add(GameContext.getUserSkillApp());
		// 保存buff信息
		appList.add(userBuffApp);
		//战斗
		appList.add(GameContext.getBattleApp());
		// 保存玩家系统设置
		appList.add(GameContext.getSystemSetApp());
		//一键追回(在活动和副本之前)
		appList.add(GameContext.getRecoveryApp());
		// 保存活动信息
		appList.add(GameContext.getActiveApp());
		// 擂台赛
		appList.add(GameContext.getArenaApp());
		// 社交关系
		appList.add(GameContext.getSocialApp());
		// 任务
		appList.add(GameContext.getUserQuestApp());
		//淘宝--上古法阵
		appList.add(GameContext.getCompassApp());
		// 兑换信息
		appList.add(GameContext.getExchangeApp());
		// 排行榜
		appList.add(GameContext.getCountApp());
		//折扣活动
		appList.add(GameContext.getDiscountApp());
		// 玩家下线排行榜日志
		appList.add(GameContext.getRankApp());
		// VIP new
		appList.add(GameContext.getVipApp());
		// 召唤信息
		appList.add(GameContext.getSummonApp());
		//嘉年华
		appList.add(GameContext.getCarnivalApp());
		//神秘商店
		appList.add(GameContext.getShopSecretApp());
		//异步pvp数据 必须放在女神app下线之前
		appList.add(GameContext.getAsyncPvpApp());
		//宠物
		appList.add(GameContext.getPetApp());
//		//大富翁
//		appList.add(GameContext.getRichManApp());
		//英雄试练（必须放到HeroApp之前）
		appList.add(GameContext.getHeroArenaApp());
		//英雄
		appList.add(GameContext.getHeroApp());
		//称号
		appList.add(GameContext.getTitleApp());
		//剧情
		appList.add(GameContext.getDramaApp());
		// 副本
		appList.add(GameContext.getCopyLogicApp());
		//章节副本
		appList.add(GameContext.getCopyLineApp());
		//炸金花任务
		appList.add(GameContext.getQuestPokerApp());
		//秘药(暂时不需要)
		//appList.add(GameContext.getNostrumApp());
		//勋章
		appList.add(GameContext.getMedalApp());
		//坐骑
		appList.add(GameContext.getRoleHorseApp());
		//天赋
		appList.add(GameContext.getRoleTalentApp());
		//幸运宝箱
		appList.add(GameContext.getLuckyBoxApp());
		//目标玩法
		appList.add(GameContext.getTargetApp());
		//异步竞技场
		//appList.add(GameContext.getRoleAsyncArenaApp());
		appList.add(GameContext.getCampWarApp());
		//连续登陆
		appList.add(GameContext.getAccumulateLoginApp());
		//活跃度
		appList.add(GameContext.getDailyPlayApp());
		// 排行榜
		appList.add(GameContext.getQualifyApp());
		// 月卡
		appList.add(GameContext.getMonthCardApp());
		// 首冲
		appList.add(GameContext.getFirstPayApp());
		// 成长基金
		appList.add(GameContext.getGrowFundApp());
		// 充值额外
		appList.add(GameContext.getPayExtraApp());
		//乐翻天
		appList.add(GameContext.getDonateApp());
		//商场
		appList.add(GameContext.getShopApp());
	}

	@Override
	public void saveOnline(RoleInstance role) {
		// 线程安全
		// 保存角色信息
		if (null == role) {
			return;
		}
		Object context = null;
		long now = 0;
		for(AppSupport app : appList) {
			now = System.currentTimeMillis();
			app.onLogout(role, context);
			logger.debug(app.getClass() + ": " + (System.currentTimeMillis() - now));
		}
	}

	@Override
	public void setArgs(Object args) {
	}

	@Override
	public Long removeIoId(String passportId) {
		if (StringUtil.nullOrEmpty(passportId)) {
			return null;
		}
		return passportIoMap.remove(passportId);
	}

	@Override
	public Long getIoId(String passportId) {
		if (StringUtil.nullOrEmpty(passportId)) {
			return null;
		}
		return passportIoMap.get(passportId);
	}

	@Override
	public void addIoId(String passportId, long ioId) {
		if (StringUtil.nullOrEmpty(passportId)) {
			return;
		}
		passportIoMap.put(passportId, ioId);
	}

	public HeartBeatConfig getHeartBeatConfig() {
		return heartBeatConfig;
	}

	public void setHeartBeatConfig(HeartBeatConfig heartBeatConfig) {
		this.heartBeatConfig = heartBeatConfig;
	}

	@Override
	public void timingWriteDBSendMsg(RoleInstance role) {
		if (null == role) {
			return;
		}
		TimingWriteDBConfig config = GameContext.getTimingWriteDBConfig();
		if (null == config) {
			return;
		}
		if (!config.getTimingWriteDBOn()) {
			return;
		}
		long now = System.currentTimeMillis();
		if (now - role.getLastWriteDBTime() < config.getTimingWriteDBTime()) {
			return;
		}
		// 发送定时入库消息67
		C0067_TimingWriteDBReqMessage reqMsg = new C0067_TimingWriteDBReqMessage();
		reqMsg.setRole(role);
		GameContext.getUserSocketChannelEventPublisher().publish(
				role.getUserId(), reqMsg, emptyChannelSession);
		role.setLastWriteDBTime(now);
	}

	@Override
	public void timingWriteDB(RoleInstance role) {
		String roleId = role.getRoleId();
		if (!GameContext.getOnlineCenter().isOnlineByRoleId(roleId)) {
			return;
		}
		// 更新充值表
		try {
			GameContext.getChargeApp().updateUserGold(role);
		} catch (Exception e) {
			logger.error(
					"timingWriteDB update easter_user_pay table error: userId="
							+ role.getUserId() + " roleId=" + role.getRoleId(),
					e);
		}
		// role对象（顺带时长奖励，邮件补偿）
		try {
			GameContext.getRecoupApp().onLogout(role, null);
		} catch (Exception e) {
			logger.error("timingWriteDB  recoup error,roleId=" + roleId, e);
		}
		
		try {
			GameContext.getRoleDAO().timingWriteDBRole(role);
		} catch (Exception e) {
			logger.error(
					"timingWriteDB update role except offline time, roleId= "
							+ roleId, e);
		}
		try {
			// goods入库
			role.getRoleBackpack().timingWriteDB();
		} catch (Exception e) {
			logger.error(
					"timingWriteDB goods writeDB error, roleId= " + roleId, e);
		}

		// 折扣活动
		try {
			GameContext.getDiscountApp().saveRoleActiveDiscount(
					role.getRoleId());
		} catch (Exception e) {
			logger.error("timingWriteDB activeDiscount error,roleId=" + roleId,
					e);
		}
	}

	@Override
	public void closeIoSession(long oldIoId) {
		// 已经存在此通行证的IO，判断IO id是否和当前currentSession的Io id是否相同
		// 如果不相同则表明是重复登陆,如果相同说明是自己的当前链接不需要处理
		MinaServer minaServer = GameContext.getMinaServer();
		IoSession oldIo = minaServer.getAcceptor().getManagedSessions().get(
				oldIoId);
		if (null == oldIo || oldIo.isClosing()) {
			// null == oldIo 不能返回false,mina是先remove session,
			// 再触发 session.getFilterChain().fireSessionClosed();
			return;
		}
		// 通知原来链接
		try {
			C0108_ForcedExitMessage pushMsg = new C0108_ForcedExitMessage();
			pushMsg.setInfo(GameContext.getI18n().getText(TextId.FORCED_EXIT_BY_DUP_LOGIN_TIPS));
			oldIo.write(pushMsg).awaitUninterruptibly();
		} catch (Exception ex) {
			logger.error("", ex);
		}
		try {
			// 入库操作让io.close触发
			oldIo.close(true);
		} catch (Exception ex) {
			logger.error("", ex);
		}
	}

	@Override
	public void kickAllRole() {
		try {
			// 角色数据入库
			for (RoleInstance role : passportIdMap.values()) {
				try {
					role.getBehavior().closeNetLink();
				} catch (Exception ex) {
					logger.error("kickAllRole:", ex);
				}
			}
		} catch (Exception ex) {
			logger.error("kickAllRole:", ex);
		}
	}
}
