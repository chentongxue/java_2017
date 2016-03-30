package sacred.alliance.magic.app.user;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import platform.message.request.C5050_RolemeetUpdateReqMessage;
import sacred.alliance.magic.app.config.ParasConfig;
import sacred.alliance.magic.app.onlinecenter.OnlineCenter;
import sacred.alliance.magic.app.set.PublicSetApp;
import sacred.alliance.magic.app.user.domain.RoleMeet;
import sacred.alliance.magic.base.ChangeNameFlag;
import sacred.alliance.magic.base.Direction;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.constant.RespTypeStatus;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.channel.ChannelSession;
import sacred.alliance.magic.core.exception.ServiceException;
import sacred.alliance.magic.dao.BaseDAO;
import sacred.alliance.magic.domain.GoodsHero;
import sacred.alliance.magic.domain.RoleLevelDistribution;
import sacred.alliance.magic.domain.RoleLevelup;
import sacred.alliance.magic.domain.RolePayRecord;
import sacred.alliance.magic.util.CheckNameUtil;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.StringUtil;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.util.Wildcard;
import sacred.alliance.magic.vo.RoleInstance;
import sacred.alliance.magic.vo.RoleInstanceBehavior;
import sacred.alliance.magic.vo.RoleShape;

import com.alibaba.fastjson.JSON;
import com.game.draco.GameContext;
import com.game.draco.app.AppSupport;
import com.game.draco.app.hero.domain.RoleHero;
import com.game.draco.app.union.domain.Union;
import com.game.draco.app.union.domain.UnionMember;
import com.game.draco.message.push.C1111_RoleShapeNotifyMessage;
import com.google.common.collect.Lists;

public class UserRoleAppImpl implements UserRoleApp {

	private BaseDAO baseDAO;
	private OnlineCenter onlineCenter;
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final List<AppSupport> appList = Lists.newArrayList();
	
	public void start() {
		// 初始化玩家的系统设置
		appList.add(GameContext.getSystemSetApp());
		//宠物
		appList.add(GameContext.getPetApp());
//		//大富翁
//		appList.add(GameContext.getRichManApp());
		// 物品
		appList.add(GameContext.getUserGoodsApp());
		//坐骑
		appList.add(GameContext.getRoleHorseApp());
		//天赋
		appList.add(GameContext.getRoleTalentApp());
		//抽卡
		appList.add(GameContext.getRoleChoiceCardApp());
		//英雄(必须放在userSkillApp前面)
		appList.add(GameContext.getHeroApp());
		//英雄试练（必须放到HeroApp之后）
		appList.add(GameContext.getHeroArenaApp());
		//初始化skill(必须放在heroApp的后面)
		appList.add(GameContext.getUserSkillApp());
		// 初始化buff
		appList.add(GameContext.getUserBuffApp());
		// 加载任务
		appList.add(GameContext.getUserQuestApp());
		// 加裁称号
		appList.add(GameContext.getTitleApp());
		// 加载VIP
		appList.add(GameContext.getVipApp());
		//秘药(暂时不需要)
		//appList.add(GameContext.getNostrumApp());
		// 加载勋章(需呀放在英雄的后面)
		appList.add(GameContext.getMedalApp());
		//目标玩法 !!!必须放在属性计算的前面,目标玩法里面有战斗力的目标
		appList.add(GameContext.getTargetApp());
		// 上线读取角色计数(需要放到属性计算前面)
		appList.add(GameContext.getCountApp());
		// 计算相关
		appList.add(GameContext.getUserAttributeApp());
		// 初始化社交关系
		appList.add(GameContext.getSocialApp());
		// 公会
		appList.add(GameContext.getUnionApp());
		// 加载队伍
		appList.add(GameContext.getTeamApp());
		//充值信息加载
		appList.add(GameContext.getChargeApp());
		//一键追回(在活动和副本之前)
		appList.add(GameContext.getRecoveryApp());
		// 加载活动信息
		appList.add(GameContext.getActiveApp());
		//竞技场
		appList.add(GameContext.getArenaApp());
		
		// 上线读取兑换相关计数
		appList.add(GameContext.getExchangeApp());
		// 加载排行榜活动
		//GameContext.getActiveRankApp()).loadRoleRank(role);
		// 加载玩家世界排行榜名次
		// GameContext.getRankApp()).updateRoleComposRank(role);
		// 排行榜走马灯
		// 在此处发送,自己是没法看到广播消息的,因为此时自己还没在onlinecenter
		// GameContext.getRankApp()).broadcastRoleLogin(role);
		// 副本
		appList.add(GameContext.getCopyLogicApp());
		appList.add(GameContext.getCopyLineApp());
		//诈金花任务
		appList.add(GameContext.getQuestPokerApp());
		// 邮件补偿
		appList.add(GameContext.getRecoupApp());
		// 上线读取召唤相关计数
		appList.add(GameContext.getSummonApp());
		//嘉年华
		appList.add(GameContext.getCarnivalApp());
		//聊天
		appList.add(GameContext.getChatApp());
		//神秘商店
		appList.add(GameContext.getShopSecretApp());
		//PK(必须在roleCount之后，因为要从roleCount中取杀人数)
		appList.add(GameContext.getPkApp());
		//处理折扣活动类型是登陆触发的
		appList.add(GameContext.getDiscountApp());
		//角色剧情相关
		appList.add(GameContext.getDramaApp());
		/*//赠送体力值
		this.loginRrewardRolePower(role);*/
		//异步竞技场
		//appList.add(GameContext.getRoleAsyncArenaApp());
		//连续登录
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
		//公会资源站（老公会战）
		appList.add(GameContext.getUnionBattleApp());
        appList.add(GameContext.getTowerApp()) ;
	}

	public void setOnlineCenter(OnlineCenter onlineCenter) {
		this.onlineCenter = onlineCenter;
	}

	public RoleInstance getOnlineRoleByRoleName(String roleName)
			throws ServiceException {
		return onlineCenter.getRoleInstanceByRoleName(roleName);
	}

	public RoleInstance getRoleByRoleName(String roleName)
			throws ServiceException {
		RoleInstance roleInstance = onlineCenter
				.getRoleInstanceByRoleName(roleName);
		if (roleInstance != null) {
			return roleInstance;
		}
		return baseDAO.selectEntity(RoleInstance.class, "roleName", roleName);
	}

	private void initUserAttribute(RoleInstance roleInstance) {

		//roleInstance.setStatus(RoleDeathStateType.ALIVE);

		//roleInstance.setCareer(CareerType.getType(roleInstance.getCareer()).getType());

		roleInstance.setDir(Direction.DOWN.getType());

		// 设置用户行为
		// roleInstance.setBehavior(new RoleInstanceBehavior(roleInstance));
		// 设置AI
		/*Ai ai = GameContext.getGameContext().getAiApplication().getAi(
				roleInstance);
		roleInstance.setAi(ai);*/


		// 添加最大经验值
		RoleLevelup roleLevelup = GameContext.getAttriApp().getLevelup(roleInstance.getLevel());
		if (null == roleLevelup) {
			throw new java.lang.RuntimeException("roleLevelup = null ,level="
					+ roleInstance.getLevel() + ", career="
					+ roleInstance.getCareer());
		}
		// 最大经验
		int maxExp = roleLevelup.getMaxExp();
		roleInstance.setMaxExp(maxExp);
		// 当前经验值容错
		int exp = roleInstance.getExp();
		if (exp > maxExp) {
			int changeNum = exp - maxExp;
			roleInstance.setExp(maxExp);
			GameContext.getStatLogApp().roleExpLog(roleInstance, changeNum,
					"currExp-maxExp", OutputConsumeType.role_login_check);
		}
		
	}


	private void init4log(RoleInstance role, ChannelSession session) {
		role.setLoginLevel(role.getLevel());
	}

	@Override
	public int onLogin(RoleInstance role, Object context) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public int onLogout(RoleInstance role, Object context) {
		try {
			this.updateRole(role);
			sendRolemeet(role);
		}catch(Exception ex){
			this.roleOfflineLog(role);
			Log4jManager.OFFLINE_ERROR_LOG.error(
					"save role to db error: roleId=" + role.getRoleId() + ",userId="
							+ role.getUserId(), ex);
			return 0;
		}
		
		return 1;
	}
	
	private void sendRolemeet(RoleInstance role){
		if(null == role){
			return ;
		}
		C5050_RolemeetUpdateReqMessage reqMsg = new C5050_RolemeetUpdateReqMessage();
		try {
			reqMsg.setAppId(GameContext.getAppId());
			//放入createServerId,不是当前serverId
			reqMsg.setServerId(String.valueOf(role.getCreateServerId()));
			reqMsg.setUserId(role.getUserId());
			RoleMeet meet = new RoleMeet();
			meet.setL(role.getLevel());
			meet.setH(role.getHeroHeadId());
			String info = JSON.toJSONString(meet);
			reqMsg.setData(info);
			GameContext.getRolemeetClient().sendMessage(reqMsg);
		} catch (Exception e) {
			logger.error("sendRolemeet error",e) ;
		}
	}
	
	@Override
	public int onCleanup(String roleId, Object context) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	private void updateRole(RoleInstance role) {
		try {
			role.syncOutwardRes();
			//下线合并捐献信息
//			GameContext.getFactionFuncApp().roleOffLineUniteDonate(player);
		}catch(Exception ex){
			logger.error("",ex);
		}
		// 用户下线打日志
		Log4jManager.USER_LINE.info(role.toString("-"));
		this.baseDAO.update(role);
	}

	@Override
	public void roleOfflineLog(RoleInstance player) {
		player.offlineLog();
	}

	public void setBaseDAO(BaseDAO baseDAO) {
		this.baseDAO = baseDAO;
	}

	@Override
	public RoleInstance getRoleByRoleId(String roleId) throws ServiceException {
		RoleInstance roleInstance = onlineCenter
				.getRoleInstanceByRoleId(roleId);
		if (roleInstance != null) {
			return roleInstance;
		}
		return baseDAO.selectEntity(RoleInstance.class, "roleId", roleId);
	}

	@Override
	public RoleInstance roleLogin(RoleInstance role, ChannelSession session)
			throws Exception {
		// 角色实例初始化
		this.initUserAttribute(role);
		// 设置用户行为
		role.setBehavior(new RoleInstanceBehavior(role, session));

		Object context = null;
		for(AppSupport app : appList) {
			app.onLogin(role, context);
		}
		
		// 设置上次上线时间
		role.setLastLoginTime(new Date());
		// 打印用户上线日志
		Log4jManager.USER_LINE.info(role.toString("+"));
		// 日志需要的内容设置
		this.init4log(role, session);

		return role;
	}

	@Override
	public int handUpLevel(RoleInstance role) {
		synchronized (role) {
			// 是否达到最高等级
			if (role.getLevel() >= GameContext.getAreaServerNotifyApp()
					.getMaxLevel()) {
				return RespTypeStatus.HANDUP_TOP_LEVEL;
			}
			// 是否够升级需要的足够经验
			RoleLevelup roleLevelup = GameContext.getAttriApp().getLevelup(role.getLevel());
			if (null == roleLevelup) {
				return RespTypeStatus.HANDUP_FAIL;
			}
			int upgNeedExp = 0;
			upgNeedExp = roleLevelup.getUpExp();

			if (role.getExp() < upgNeedExp) {
				return RespTypeStatus.HANDUP_EXP_LACK;
			}
			// 调用升级方法
			GameContext.getUserAttributeApp().levelUp(role);
		}
		return RespTypeStatus.HANDUP_SUCCESS;
	}

	// 返回被禁言的角色
	public List<RoleInstance> getForbidRoleList() {
		return GameContext.getRoleDAO().getForbidRoleList();
	}

	// 根据角色名查询禁言玩家
	public List<RoleInstance> getForbidRoleList(String roleName) {
		return GameContext.getRoleDAO().getForbidRoleList(roleName);
	}

	// 返回被隔离的角色
	public List<RoleInstance> getFrozenRoleList() {
		return GameContext.getRoleDAO().getFrozenRoleList();
	}

	// 根据角色名查询隔离玩家
	public List<RoleInstance> getFrozenRole(String roleName) {
		return GameContext.getRoleDAO().getFrozenRole(roleName);
	}

	@Override
	public List<RoleInstance> getRoleList(String userName, String roleId,
			String roleName, String userId, String channelUserId) {
		return GameContext.getRoleDAO().getRoleList(userName, roleId, roleName, userId, channelUserId);
	}
	
	@Override
	public List<RoleInstance> getRoleList(String userId) {
		return GameContext.getBaseDAO().selectList(RoleInstance.class, "userId", userId);
	}

	/** 查询玩家金钱排行 * */
	public List<RoleInstance> getRoleMoneyCharts(int orderby, int start, int end) {
		return GameContext.getRoleDAO().getRoleMoneyCharts(orderby, start, end);
	}

	@Override
	public List<RolePayRecord> getUserMoneyRankList(int orderby, int start,
			int end) {
		return GameContext.getRoleDAO().getUserMoneyRankList(orderby, start,
				end);
	}

	@Override
	public int updateFrozenAndForbid(RoleInstance role) {
		return GameContext.getRoleDAO().updateFrozenAndForbid(role);
	}

	@Override
	public List<RoleInstance> getUserRoles(String userName, String userId, String channelUserId) {
		return GameContext.getRoleDAO().getUserRoles(userName, userId, channelUserId);
	}

	@Override
	public Result modifyRoleName(String userId, String roleId, String newName)
			throws ServiceException {
		Result result = new Result();
		try {
			RoleInstance role = this.getRoleByRoleId(roleId);
			if (null == role) {
				return result.setInfo(GameContext.getI18n().getText(TextId.Role_Not_Exist));
			}
			if (!role.getUserId().equals(userId)) {
				return result.setInfo(GameContext.getI18n().getText(TextId.USER_ROLE_MODIFY_ROLE_NAME_NOT_USER));
			}
			//判断当前角色是否允许改名
			ChangeNameFlag flag = this.getChangeNameFlag(role);
			if(ChangeNameFlag.canot == flag){
				return result.setInfo(GameContext.getI18n().getText(TextId.USER_ROLE_MODIFY_ROLE_NAME_CANOT));
			}
			// 验证角色名称是否合法
			String roleName = newName.trim();
			if (GameContext.getIllegalWordsService().isNullOrEmpty(roleName)) {
				return result.setInfo(Status.Role_Name_Null.getTips());
			}
			ParasConfig parasConfig = GameContext.getParasConfig();
			PublicSetApp cpbs = GameContext.getPublicSetApp();
			String info = Status.Role_Create_Name_In_Char.getTips()
			.replace(Wildcard.MinNum, String.valueOf(parasConfig.getMinRoleName()))
			.replace(Wildcard.MaxNum, String.valueOf(cpbs.getMaxRoleNameSize()));
			if (GameContext.getIllegalWordsService().isLow(roleName,
					parasConfig.getMinRoleName())) {
				return result.setInfo(info);
			}
			if (GameContext.getIllegalWordsService().isExceed(roleName,
					cpbs.getMaxRoleNameSize())) {
				return result.setInfo(info);
			}
			if (!GameContext.getIllegalWordsService()
					.isCNorENorFigure(roleName)) {
				return result.setInfo(Status.Role_Create_Name_Char_Info.getTips());
			}
			String illegalChar = GameContext.getIllegalWordsService()
					.findIllegalChar(roleName);
			if (null != illegalChar) {
				return result.setInfo(Status.Role_Create_Name_Illegal_Char.getTips() + illegalChar);
			}
			String forbidChar = GameContext.getIllegalWordsService()
					.findForbiddenChar(roleName);
			if (null != forbidChar) {
				return result.setInfo(Status.Role_Create_Name_Forbid_Char.getTips() + forbidChar);
			}
			// 判断是否有@#号等特殊字符
			if (StringUtil.haveSpecialChar(roleName)) {
				return result.setInfo(Status.Role_Create_Name_Has_Illegal_Char.getTips());
			}
			// 判断是否有以s/S+数字以尾
			if(CheckNameUtil.isMatchChangeName(roleName)){
				return result.setInfo(Status.Role_Create_Name_Has_Illegal_Char.getTips());
			}
			int count = GameContext.getRoleService().sameName(roleName);
			if (1 <= count) {
				return result.setInfo(Status.Role_Exist.getTips());
			}
			// 修改角色名
			role.setRoleName(roleName);
			this.baseDAO.update(role);
			// 拥有门派的角色，修改门派成员的角色名称
			String unionId = role.getUnionId();
			if (!Util.isEmpty(unionId)) {
				Union union = GameContext.getUnionApp().getUnion(role);
				// 如果是门派的帮主，修改帮主名称
				if (union != null
						&& union.getLeaderId() == Integer.valueOf(roleId)) {
					// 修改数据库
					union.setLeaderName(roleName);
					GameContext.getBaseDAO().saveOrUpdate(union);
				
					UnionMember member = union.getUnionMember(role.getIntRoleId());
					member.setRoleName(roleName);
					GameContext.getUnionApp().saveOrUpdUnionMember( member);
				}
			}
			return result.success();
		} catch (Exception e) {
			this.logger
					.error("UserRoleApplication.modifyRoleName exception", e);
			throw new ServiceException(
					"UserRoleApplication.modifyRoleName exception", e);
		}
	}
	
	@Override
	public ChangeNameFlag getChangeNameFlag(RoleInstance role) {
		String roleName = role.getRoleName() ;
		if(!CheckNameUtil.isMatchChangeName(roleName)){
			return ChangeNameFlag.canot ;
		}
		if(GameContext.getParasConfig().isMustChangeSameRoleName()){
			return ChangeNameFlag.must ;
		}
		return ChangeNameFlag.notmust ;
	}
	

	@Override
	public List<RoleLevelDistribution> getLevelDistributionList() {
		return GameContext.getRoleDAO().getLevelDistributionList();
	}


	@Override
	public RoleInstance selectMaxLevelRole(String userId) {
		return GameContext.getRoleDAO().selectMaxLevelRole(userId);
	}

	@Override
	public void pushRoleMorphNotifyMessage(RoleInstance role) {
		try {
			RoleShape info = this.getRoleShape(role.getRoleId());
			C1111_RoleShapeNotifyMessage message = new C1111_RoleShapeNotifyMessage();
			message.setRoleId(role.getIntRoleId());
			message.setClothesResId((short) info.getClothesResId());
			GameContext.getMessageCenter().sendSysMsg(role, message);
		} catch (RuntimeException e) {
			this.logger.error(this.getClass().getName() + ".pushRoleMorphNotifyMessage error: ", e);
		}
	}

	/*@Override
	public void sysRewardRolePower() {
		try {
			//没有用户在线
			if(this.onlineCenter.onlineUserSize() <= 0){
				return;
			}
			//取到当前的小时数字
			Calendar calendar = Calendar.getInstance();
			int hour = calendar.get(Calendar.HOUR_OF_DAY);
			List<Integer> hourList = GameContext.getParasConfig().getRewardPowerHourList();
			//判断这个小时是否需要增加体力值
			if(!hourList.contains(hour)){
				return;
			}
			//每次赠送的体力值
			int rewardValue = GameContext.getParasConfig().getRewarRolePowerValue();
			Date now = calendar.getTime();
			for(RoleInstance role : this.onlineCenter.getAllOnlineRole()){
				try {
					if(null == role){
						continue;
					}
					Date lastModifyTime = role.getPowerModifyTime();
					//容错
					if(null == lastModifyTime){
						//初始化体力值
						role.initRolePowerValue();
						continue;
					}
					//赠送体力值（登录的时候会根据相差的时间赠送）
					this.changeRolePowerValue(role, rewardValue, now, true);
				} catch (RuntimeException e) {
					this.logger.error("UserRoleApp.sysRewardRolePower error(for): ", e);
				}
			}
		} catch (RuntimeException e) {
			this.logger.error("UserRoleApp.sysRewardRolePower error: ", e);
		}
	}*/
	
	/**
	 * 修改体力值
	 * @param role 角色
	 * @param rewardValue 奖励体力值
	 * @param now 修改时间
	 * @param notifyAttr 是否通知属性更改
	 */
	/*private void changeRolePowerValue(RoleInstance role, int rewardValue, Date now, boolean notifyAttr){
		if(rewardValue <= 0){
			return;
		}
		//赠送体力值
		role.getBehavior().changeAttribute(AttributeType.curPower, OperatorType.Add, rewardValue);
		//必须修改赠送时间
		role.setPowerModifyTime(now);
		if(notifyAttr){
			role.getBehavior().notifyAttribute();
		}
	}*/
	
	/**
	 * 登录时赠送体力值
	 * @param role
	 */
	/*private void loginRrewardRolePower(RoleInstance role){
		try {
			Date lastModifyTime = role.getPowerModifyTime();
			Calendar calendar = Calendar.getInstance();
			Date now = calendar.getTime();
			//容错
			if(null == lastModifyTime){
				//第一次登录，需要将体力值置满
				role.initRolePowerValue();
				return;
			}
			Calendar calLast = Calendar.getInstance();
			calLast.setTime(lastModifyTime);
			int lastHour = calLast.get(Calendar.HOUR_OF_DAY);//上次奖励的小时数
			int nowHour = calendar.get(Calendar.HOUR_OF_DAY);//当前时间的小时数
			List<Integer> hourList = GameContext.getParasConfig().getRewardPowerHourList();
			int times = 0;//计算增加的次数
			int diffDay = DateUtil.dateDiffDay(lastModifyTime, now);
			if(diffDay > 0){
				//不在同一天
				int todayTimes = 0;
				int lastDayTimes = 0;
				for(int hour : hourList){
					if(hour < nowHour){
						todayTimes ++;
					}
					if(hour > lastHour){
						lastDayTimes ++;
					}
				}
				//总次数 = 最后奖励当天之后的次数 + 每天奖励的次数 + 今天已经奖励的次数
				times = lastDayTimes + ((diffDay-1)*hourList.size()) + todayTimes;
			}else{
				//同一天，总次数 = 最后奖励时间与现在之间，发奖的次数
				for(int hour : hourList){
					if(hour > lastHour && hour < nowHour){
						times ++;
					}
				}
			}
			int rewardValue = times * GameContext.getParasConfig().getRewarRolePowerValue();
			//赠送体力值
			this.changeRolePowerValue(role, rewardValue, now, false);
		} catch (Exception e) {
			this.logger.error("UserRoleApp.loginRrewardRolePower error: ", e);
		}
	}*/
	
	

	/** 返回所穿装备资源ID */
	@Override
	public RoleShape getRoleShape(String roleId) {
		RoleHero hero = GameContext.getUserHeroApp().getOnBattleRoleHero(
				roleId);
		if (null == hero) {
			return this.getDefaultRoleShape();
		}

		GoodsHero goodsHero = GameContext.getGoodsApp().getGoodsTemplate(
				GoodsHero.class, hero.getHeroId());
		if (null == goodsHero) {
			return this.getDefaultRoleShape();
		}
		RoleShape info = new RoleShape();
		info.setClothesResId(goodsHero.getResId());
		info.setEquipResId(goodsHero.getWeaponResId());
		return info;
	}
	
	@Override
	public RoleShape getDefaultRoleShape() {
		RoleShape info = new RoleShape();
		info.setClothesResId(RespTypeStatus.DEFAULT_CLOTHES_RESID);
		info.setEquipResId(RespTypeStatus.DEFAULT_EQUIP_RESID);
		return info;
	}

	@Override
	public void setArgs(Object args) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		
	}
	
}
