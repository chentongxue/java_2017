package com.game.draco.app.qualify;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.attri.AttriBuffer;
import sacred.alliance.magic.app.attri.AttriItem;
import sacred.alliance.magic.app.attri.calct.RoleFormulaCalct;
import sacred.alliance.magic.app.count.type.CountType;
import sacred.alliance.magic.app.goods.GoodsOperateBean;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.ChallengeResultType;
import sacred.alliance.magic.base.MapLogicType;
import sacred.alliance.magic.base.OperatorType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.QualityType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.base.XlsSheetNameType;
import sacred.alliance.magic.component.id.IdFactory;
import sacred.alliance.magic.component.id.IdType;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.domain.GoodsHero;
import sacred.alliance.magic.domain.RoleCount;
import sacred.alliance.magic.domain.RoleLevelup;
import sacred.alliance.magic.util.DateUtil;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.RandomUtil;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.util.XlsPojoUtil;
import sacred.alliance.magic.vo.Point;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.asyncpvp.domain.AsyncPvpRoleAttr;
import com.game.draco.app.asyncpvp.vo.AsyncPvpBattleInfo;
import com.game.draco.app.chat.ChannelType;
import com.game.draco.app.chat.ChatSysName;
import com.game.draco.app.hero.domain.RoleHero;
import com.game.draco.app.mail.domain.Mail;
import com.game.draco.app.qualify.config.QualifyBaseConfig;
import com.game.draco.app.qualify.config.QualifyCostConfig;
import com.game.draco.app.qualify.config.QualifyGiftConfig;
import com.game.draco.app.qualify.config.QualifyRobotNameConfig;
import com.game.draco.app.qualify.config.RobotRulesConfig;
import com.game.draco.app.qualify.domain.ChallengeRecord;
import com.game.draco.app.qualify.domain.ChallengeRecordType;
import com.game.draco.app.qualify.domain.QualifyRank;
import com.game.draco.app.qualify.domain.QualifyRobot;
import com.game.draco.app.qualify.domain.RankStatusType;
import com.game.draco.app.qualify.domain.RoleQualifyRecord;
import com.game.draco.app.vip.type.VipPrivilegeType;
import com.game.draco.message.item.AttriTypeValueItem;
import com.game.draco.message.item.HeroBattleItem;
import com.game.draco.message.push.C0007_ConfirmationNotifyMessage;
import com.game.draco.message.push.C1759_QualifyGiftPanelRespMessage;
import com.game.draco.message.request.C1754_QualifyBuyChallengeTimesMessage;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class QualifyAppImpl implements QualifyApp {
	private static final String CONFIRM = "1";
	// 挑战对手1系数
	private static final double FIRST_COEFFICIENT1 = 0.95;
	private static final double FIRST_COEFFICIENT2 = 0.99;
	// 挑战对手2系数
	private static final double SECOND_COEFFICIENT1 = 0.6;
	private static final double SECOND_COEFFICIENT2 = 0.7;
	// 挑战对手3系数
	private static final double THIRD_COEFFICIENT1 = 0.3;
	private static final double THIRD_COEFFICIENT2 = 0.4;
	// 不记录榜单排名
	public static final int OUT_RANK = 4901;
	// 直接挑战前三
	private static final int CHALLENGE_STRONG = 10;
	// 排行前四
	private static final int FIRST = 1;
	private static final int SECOND = 2;
	private static final int THIRD = 3;
	private static final int FOURTH = 4;
	// 时间转换
	private static final int TIME_INT = 1000;
	// 挑战
	private static final short COMMANDID = 1753;
	private static final String HAS_CONFIRM = "1";
	// 排行榜每页条数
	private static final byte PAGE_NUMBER = 5;
	// 排行榜展示条数
	private static final byte RANK_NUMBER = 100;
	// 定时任务序列
	private static AtomicInteger jobIndex = new AtomicInteger(0);
	// 是否是NPC
	private static final byte PLAYER = 0;
	private static final byte ROBOT = 1;
	// 机器人最大等级
	private static final byte ROBOT_MAX_LEVEL = 81;

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	// 基本配置
	private QualifyBaseConfig qualifyBaseConfig = new QualifyBaseConfig();
	// 排名奖励
	private List<QualifyGiftConfig> rankGiftList = Lists.newArrayList();
	// 胜利奖励
	private List<QualifyGiftConfig> winGiftList = Lists.newArrayList();
	// 失败奖励
	private List<QualifyGiftConfig> loseGiftList = Lists.newArrayList();
	// 购买挑战次数消耗
	private Map<Byte, QualifyCostConfig> challengeCostMap = Maps.newHashMap();
	// 机器人生成规则
	List<RobotRulesConfig> rulesList = Lists.newArrayList();
	// 绿色品质英雄列表
	List<Integer> greenHeroList = Lists.newArrayList();
	// 蓝色品质英雄列表
	List<Integer> blueHeroList = Lists.newArrayList();
	// 紫色品质英雄列表
	List<Integer> purpleHeroList = Lists.newArrayList();
	// 橙色品质英雄列表
	List<Integer> orangeHeroList = Lists.newArrayList();
	// 机器人姓名
	List<String> firstNameList = Lists.newArrayList();
	List<String> secondNameList = Lists.newArrayList();
	// 排行榜
	private Map<Short, QualifyRank> qualifyMap = Maps.newConcurrentMap();
	// 玩家排名
	private Map<String, Short> rankMap = Maps.newConcurrentMap();
	// 机器人
	private Map<String, QualifyRobot> robotMap = Maps.newHashMap();
	// 玩家对战记录
	private Map<String, RoleQualifyRecord> recordMap = Maps.newConcurrentMap();
	// 记录玩家对手
	private Map<String, String> challengeOpponentMap = Maps.newConcurrentMap();
	// 机器人数据默认值
	private RoleFormulaCalct roleCalct = new RoleFormulaCalct();
	// 创建机器人英雄时使用
	private Map<QualityType, Integer> numberMap = Maps.newHashMap();
	// 确认购买挑战次数
	private final short BUY_COMMID = new C1754_QualifyBuyChallengeTimesMessage().getCommandId();

	@Override
	public void setArgs(Object arg0) {
	}

	@Override
	public void start() {
		String xlsPath = GameContext.getPathConfig().getXlsPath();
		this.loadQualifyBaseConfig(xlsPath);
		this.loadRankGiftConfig(xlsPath);
		this.loadWinGiftConfig(xlsPath);
		this.loadLoseGiftConfig(xlsPath);
		this.loadBuyTimesCost(xlsPath);
		this.loadQualifyRobot();
		this.loadQualifyRankInfo(xlsPath);
		this.loadTimingTask();
	}

	/**
	 * 加载定时任务
	 */
	@SuppressWarnings("unchecked")
	private void loadTimingTask() {
		List<String> ces = this.qualifyBaseConfig.getCronExpressionList();
		if (Util.isEmpty(ces)) {
			return;
		}
		for (String ce : ces) {
			if (Util.isEmpty(ce)) {
				continue;
			}
			Class clazz = QualifyRankGiveGift.class;
			try {
				int index = jobIndex.getAndDecrement();
				JobDetail jobDetail = new JobDetail("qualify_job_" + index, Scheduler.DEFAULT_GROUP, clazz);
				CronTrigger trigger = new CronTrigger("qualify_cron_" + index, null, ce);
				GameContext.getSchedulerApp().addToScheduler(jobDetail, trigger);
				logger.info("register qualify scheduler success,class=" + clazz.getName() + " cronExpression=" + ce);
			} catch (Exception ex) {
				Log4jManager.CHECK.error("register qualify scheduler success,class=" + clazz.getName() + " cronExpression=" + ce, ex);
				Log4jManager.checkFail();
			}
		}
	}

	@Override
	public void stop() {
		this.saveQualifyRankInfo() ;
	}

	@Override
	public int onCleanup(String roleId, Object context) {
		this.recordMap.remove(roleId);
		return 0;
	}

	@Override
	public int onLogin(RoleInstance role, Object context) {
		RoleQualifyRecord record = GameContext.getQualifyStorage().getRoleQualifyRecord(role.getRoleId());
		if (null == record) {
			record = new RoleQualifyRecord();
			record.setRoleId(role.getRoleId());
		}
		this.recordMap.put(role.getRoleId(), record);
		// 更新出战英雄
		return 0;
	}

	/**
	 * 更新榜单中的信息
	 * @param role
	 */
	private void updateRoleQualify(RoleInstance role) {
		QualifyRank roleQualify = this.getQualifyRank(role);
		if (null == roleQualify) {
			return;
		}
		this.updateQualifyRank(role, roleQualify);
	}

	/**
	 * 更新榜单中的信息
	 * @param role
	 * @param roleQualify
	 */
	private void updateQualifyRank(RoleInstance role, QualifyRank roleQualify) {
		if (roleQualify.getRoleLevel() != role.getLevel()) {
			roleQualify.setRoleLevel(role.getLevel());
		}
		if (roleQualify.getRoleName() != role.getRoleName()) {
			roleQualify.setRoleName(role.getRoleName());
		}
		if (roleQualify.getBattleScore() != role.getBattleScore()) {
			roleQualify.setBattleScore(role.getBattleScore());
		}
	}

	@Override
	public int onLogout(RoleInstance role, Object context) {
		this.updateRoleQualify(role);
		// 保存对战记录
		RoleQualifyRecord record = this.getRoleQualifyRecord(role.getRoleId());
		if (null != record) {
			GameContext.getQualifyStorage().saveRoleQualifyRecord(record);
		}
		return 0;
	}

	/**
	 * 加载基本配置
	 * @param xlsPath
	 */
	private void loadQualifyBaseConfig(String xlsPath) {
		String fileName = XlsSheetNameType.qualify_base_config.getXlsName();
		String sourceFile = xlsPath + fileName;
		String sheetName = XlsSheetNameType.qualify_base_config.getSheetName();
		String fileInfo = "load excel error:fileName=" + fileName + ",sheetName=" + sheetName + ".";
		try {
			this.qualifyBaseConfig = XlsPojoUtil.getEntity(sourceFile, sheetName, QualifyBaseConfig.class);
			this.qualifyBaseConfig.init(fileInfo);
			// 处理地图逻辑
			sacred.alliance.magic.app.map.Map map = GameContext.getMapApp().getMap(this.qualifyBaseConfig.getMapId());
			if (null == map) {
				Log4jManager.checkFail();
				Log4jManager.CHECK.error("QualifyAppImpl The map is not exist. mapId = " + this.qualifyBaseConfig.getMapId() + ",file=" + xlsPath + fileName + " sheet=" + sheetName);
			}
			// 将地图逻辑修改为qualify类型
			if (!map.getMapConfig().changeLogicType(MapLogicType.qualify)) {
				Log4jManager.CHECK.error("QualifyAppImpl The map logic type config error. mapId= " + this.qualifyBaseConfig.getMapId());
				Log4jManager.checkFail();
			}
		} catch (Exception ex) {
			logger.error(fileInfo, ex);
			Log4jManager.checkFail();
		}
	}

	/**
	 * 加载排名奖励配置
	 * @param xlsPath
	 */
	private void loadRankGiftConfig(String xlsPath) {
		String fileName = XlsSheetNameType.qualify_rank_gift.getXlsName();
		String sourceFile = xlsPath + fileName;
		String sheetName = XlsSheetNameType.qualify_rank_gift.getSheetName();
		String fileInfo = "load excel error:fileName=" + fileName + ",sheetName=" + sheetName + ".";
		try {
			this.rankGiftList = XlsPojoUtil.sheetToList(sourceFile, sheetName, QualifyGiftConfig.class);
			for (QualifyGiftConfig config : this.rankGiftList) {
				if (null == config) {
					continue;
				}
				config.init(fileInfo);
			}
		} catch (Exception ex) {
			logger.error(fileInfo, ex);
			Log4jManager.checkFail();
		}
	}

	/**
	 * 加载挑战胜利奖励配置
	 * @param xlsPath
	 */
	private void loadWinGiftConfig(String xlsPath) {
		String fileName = XlsSheetNameType.qualify_win_gift.getXlsName();
		String sourceFile = xlsPath + fileName;
		String sheetName = XlsSheetNameType.qualify_win_gift.getSheetName();
		String fileInfo = "load excel error:fileName=" + fileName + ",sheetName=" + sheetName + ".";
		try {
			this.winGiftList = XlsPojoUtil.sheetToList(sourceFile, sheetName, QualifyGiftConfig.class);
			for (QualifyGiftConfig config : this.winGiftList) {
				if (null == config) {
					continue;
				}
				config.init(fileInfo);
			}
		} catch (Exception ex) {
			logger.error(fileInfo, ex);
			Log4jManager.checkFail();
		}
	}

	/**
	 * 加载挑战失败奖励配置
	 * @param xlsPath
	 */
	private void loadLoseGiftConfig(String xlsPath) {
		String fileName = XlsSheetNameType.qualify_lose_gift.getXlsName();
		String sourceFile = xlsPath + fileName;
		String sheetName = XlsSheetNameType.qualify_lose_gift.getSheetName();
		String fileInfo = "load excel error:fileName=" + fileName + ",sheetName=" + sheetName + ".";
		try {
			this.loseGiftList = XlsPojoUtil.sheetToList(sourceFile, sheetName, QualifyGiftConfig.class);
			for (QualifyGiftConfig config : this.loseGiftList) {
				if (null == config) {
					continue;
				}
				config.init(fileInfo);
			}
		} catch (Exception ex) {
			logger.error(fileInfo, ex);
			Log4jManager.checkFail();
		}
	}

	/**
	 * 加载VIP购买挑战次数消耗配置
	 * @param xlsPath
	 */
	private void loadBuyTimesCost(String xlsPath) {
		String fileName = XlsSheetNameType.qualify_buy_cost.getXlsName();
		String sourceFile = xlsPath + fileName;
		String sheetName = XlsSheetNameType.qualify_buy_cost.getSheetName();
		String fileInfo = "load excel error:fileName=" + fileName + ",sheetName=" + sheetName + ".";
		try {
			this.challengeCostMap = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName, QualifyCostConfig.class);
			for (QualifyCostConfig config : this.challengeCostMap.values()) {
				if (null == config) {
					continue;
				}
				config.init(fileInfo);
			}
		} catch (Exception ex) {
			logger.error(fileInfo, ex);
			Log4jManager.checkFail();
		}
	}

	private void loadQualifyRobot() {
		// 获得机器人列表
		List<QualifyRobot> list = GameContext.getBaseDAO().selectAll(QualifyRobot.class);
		if (Util.isEmpty(list)) {
			return;
		}
		for (QualifyRobot robot : list) {
			if (null == robot) {
				continue;
			}
			this.robotMap.put(robot.getRoleId(), robot);
		}
	}

	/**
	 * 从数据库中读取排行榜信息
	 */
	private void loadQualifyRankInfo(String xlsPath) {
		List<QualifyRank> roleQualifyList = GameContext.getBaseDAO().selectAll(QualifyRank.class);
		// 如果没有排行榜信息,创建机器人填充榜单
		if (Util.isEmpty(roleQualifyList)) {
			roleQualifyList = this.createQualifyRank(xlsPath);
		}
		this.initQualifyRank(roleQualifyList);
	}

	/**
	 * 加载机器人生成规则配置
	 */
	private void loadRobotRulesConfig(String xlsPath) {
		String fileName = XlsSheetNameType.qualify_robot_rules.getXlsName();
		String sourceFile = xlsPath + fileName;
		String sheetName = XlsSheetNameType.qualify_robot_rules.getSheetName();
		String fileInfo = "load excel error:fileName=" + fileName + ",sheetName=" + sheetName + ".";

		try {
			this.rulesList = XlsPojoUtil.sheetToList(sourceFile, sheetName, RobotRulesConfig.class);
			for (RobotRulesConfig config : this.rulesList) {
				if (null == config) {
					continue;
				}
				config.init(fileInfo);
			}
		} catch (Exception ex) {
			logger.error(fileInfo, ex);
			Log4jManager.checkFail();
		}
	}

	/**
	 * 加载机器人随机英雄列表
	 * @param xlsPath
	 */
	private void loadRobotHeroList(String xlsPath) {
		String fileName = XlsSheetNameType.qualify_robot_hero.getXlsName();
		String sourceFile = xlsPath + fileName;
		String sheetName = XlsSheetNameType.qualify_robot_hero.getSheetName();
		String fileInfo = "load excel error:fileName=" + fileName + ",sheetName=" + sheetName + ".";
		try {
			List<String> heroList = XlsPojoUtil.sheetToStringList(sourceFile, sheetName);
			for (String heroId : heroList) {
				if (Util.isEmpty(heroId)) {
					continue;
				}
				GoodsHero goodsHero = GameContext.getGoodsApp().getGoodsTemplate(GoodsHero.class, Integer.parseInt(heroId));
				if (null == goodsHero) {
					continue;
				}
				this.addHeroList(goodsHero);
			}
		} catch (Exception ex) {
			logger.error(fileInfo, ex);
			Log4jManager.checkFail();
		}
	}

	/**
	 * 加载机器人姓名配置
	 * @param xlsPath
	 */
	private void loadRobotName(String xlsPath) {
		String fileName = XlsSheetNameType.qualify_robot_name.getXlsName();
		String sourceFile = xlsPath + fileName;
		String sheetName = XlsSheetNameType.qualify_robot_name.getSheetName();
		String fileInfo = "load excel error:fileName=" + fileName + ",sheetName=" + sheetName + ".";
		try {
			List<QualifyRobotNameConfig> nameList = XlsPojoUtil.sheetToList(sourceFile, sheetName, QualifyRobotNameConfig.class);
			for (QualifyRobotNameConfig config : nameList) {
				if (null == config) {
					continue;
				}
				this.firstNameList.add(config.getFirstName());
				this.secondNameList.add(config.getSecondName());
			}
		} catch (Exception ex) {
			logger.error(fileInfo, ex);
			Log4jManager.checkFail();
		}
	}

	/**
	 * 将随机英雄添加到列表
	 * @param goodsHero
	 */
	private void addHeroList(GoodsHero goodsHero) {
		switch (QualityType.get(goodsHero.getQualityType())) {
		case green:
			this.greenHeroList.add(goodsHero.getId());
			break;
		case blue:
			this.blueHeroList.add(goodsHero.getId());
			break;
		case purple:
			this.purpleHeroList.add(goodsHero.getId());
			break;
		case orange:
			this.orangeHeroList.add(goodsHero.getId());
			break;
		default:
			break;
		}
	}

	/**
	 * 创建排行榜
	 */
	private List<QualifyRank> createQualifyRank(String xlsPath) {
		// 删除数据库中所有机器人信息
		GameContext.getBaseDAO().deleteAll(QualifyRobot.class);
		List<QualifyRank> roleQualifyList = Lists.newArrayList();
		// 加载创建机器人规则
		this.loadRobotRulesConfig(xlsPath);
		this.loadRobotHeroList(xlsPath);
		this.loadRobotName(xlsPath);
		int firstNameNum = 0;
		int secondNameNum = 0;
		int laps = 0;
		for (RobotRulesConfig config : this.rulesList) {
			if (null == config) {
				continue;
			}
			for (int i = config.getUpRank(); i <= config.getDownRank(); i++) {
				try {
					String roleId = IdFactory.getInstance().nextId(IdType.NPCID);
					String roleName = this.createRobotName(firstNameNum, secondNameNum);
					firstNameNum++;
					secondNameNum++;
					if (secondNameNum >= this.secondNameList.size()) {
						firstNameNum = 0;
						secondNameNum = ++laps;
					}
					// 创建机器人
					QualifyRobot robot = new QualifyRobot();
					this.createRobotHeroList(robot, config.getGreenNum(), config.getBlueNum(), config.getPurpleNum(), config.getOrangeNum());
					int level = ROBOT_MAX_LEVEL - (int) (Math.pow(i, 0.5));
					robot.setLevel(level);
					robot.setRoleId(roleId);
					robot.setRoleName(roleName);
					robot.setBattleScore(this.getRobotBattleScore(robot));
					this.robotMap.put(roleId, robot);
					// 创建排行榜信息
					QualifyRank rank = new QualifyRank();
					rank.setBattleScore(robot.getBattleScore());
					rank.setRank((short) i);
					rank.setRobot((byte) ROBOT);
					rank.setRoleId(roleId);
					rank.setRoleLevel(robot.getLevel());
					rank.setRoleName(roleName);
					roleQualifyList.add(rank);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		List<QualifyRobot> robotList = Lists.newArrayList();
		robotList.addAll(this.robotMap.values());
		int size = robotList.size();
		int fromIndex = 0;
		int toIndex = 500;
		int num = toIndex;
		boolean isEnd = false;
		while (!isEnd) {
			if (toIndex > size) {
				toIndex = size;
				isEnd = true;
			}
			Log4jManager.LOOP_LOG.info("QualifyAppImpl.createQualifyRank save robot info star.");
			GameContext.getBaseDAO().insertBatch(robotList.subList(fromIndex, toIndex));
			Log4jManager.LOOP_LOG.info("QualifyAppImpl.createQualifyRank save robot info stop.");
			fromIndex = toIndex;
			toIndex += num;
		}
		this.clearRobotRules();
		return roleQualifyList;
	}

	/**
	 * 随机组合机器人姓名
	 * @return
	 */
	private String createRobotName(int firstNum, int secondNum) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(this.firstNameList.get(firstNum));
		buffer.append("·");
		buffer.append(this.secondNameList.get(secondNum));
		return buffer.toString();
	}

	/**
	 * 获得机器人的战力
	 * @param robot
	 * @return
	 */
	private int getRobotBattleScore(QualifyRobot robot) {
		return GameContext.getAttriApp().getAttriBattleScore(this.getRobotBattleBuffer(robot));
	}
	
	/**
	 * 获得所有英雄总属性（计算战力）
	 * @param robot
	 * @return
	 */
	private AttriBuffer getRobotBattleBuffer(QualifyRobot robot) {
		AttriBuffer buffer = AttriBuffer.createAttriBuffer();
		// 人物属性
		RoleLevelup levelConfig = GameContext.getAttriApp().getLevelup(robot.getLevel());
		if (null == levelConfig) {
			return buffer;
		}
		buffer.append(this.getRoleAttriBuffer(levelConfig));
		for (int heroId : robot.getHeroIdList()) {
			buffer.append(this.getHeroAttriBuffer(heroId, robot.getLevel()));
		}
		return buffer;
	}
	
	/**
	 * 获得机器人的属性
	 * @param robot
	 * @return
	 */
	private AttriBuffer getRobotAttriBuffer(QualifyRobot robot) {
		AttriBuffer buffer = AttriBuffer.createAttriBuffer();
		// 人物属性
		RoleLevelup levelConfig = GameContext.getAttriApp().getLevelup(robot.getLevel());
		if (null == levelConfig) {
			return buffer;
		}
		buffer.append(this.getRoleAttriBuffer(levelConfig));
		buffer.append(this.getHeroAttriBuffer(robot.getHeroId1(), robot.getLevel()));
		return buffer;
	}

	/**
	 * 获得人物属性
	 * @param config
	 * @return
	 */
	private AttriBuffer getRoleAttriBuffer(RoleLevelup config) {
		AttriBuffer buffer = AttriBuffer.createAttriBuffer();
		buffer.append(AttributeType.maxHP, config.getMaxHP());
		buffer.append(AttributeType.breakDefense, config.getBreakDefense());
		buffer.append(AttributeType.atk, config.getAtk());
		buffer.append(AttributeType.rit, config.getRit());
		buffer.append(AttributeType.critAtk, config.getCritAtk());
		buffer.append(AttributeType.critRit, config.getCritRit());
		buffer.append(AttributeType.hit, config.getHit());
		buffer.append(AttributeType.dodge, config.getDodge());
		return buffer;
	}

	/**
	 * 获得最强英雄属性
	 * @param heroId
	 * @return
	 */
	private AttriBuffer getHeroAttriBuffer(int heroId, int level) {
		AttriBuffer buffer = AttriBuffer.createAttriBuffer();
		GoodsHero goodsHero = GameContext.getGoodsApp().getGoodsTemplate(GoodsHero.class, heroId);
		if (null == goodsHero) {
			return buffer;
		}
		buffer.append(GameContext.getHeroApp().getBaseAttriBuffer(heroId, level, goodsHero.getQualityType(), goodsHero.getStar()));
//		buffer.append(this.getEquipsBuffer(heroId, level));
		return buffer;
	}

//	/**
//	 * 获得装备属性
//	 * @param robot
//	 * @return
//	 */
//	private AttriBuffer getEquipsBuffer(int heroId, int level) {
//		AttriBuffer buffer = AttriBuffer.createAttriBuffer();
//		GoodsHero goodsHero = GameContext.getGoodsApp().getGoodsTemplate(GoodsHero.class, heroId);
//		if (null == goodsHero) {
//			return buffer;
//		}
//		buffer.append(this.getSignEquipBuffer(goodsHero.getEquip0(), level));
//		buffer.append(this.getSignEquipBuffer(goodsHero.getEquip1(), level));
//		buffer.append(this.getSignEquipBuffer(goodsHero.getEquip2(), level));
//		buffer.append(this.getSignEquipBuffer(goodsHero.getEquip3(), level));
//		buffer.append(this.getSignEquipBuffer(goodsHero.getEquip4(), level));
//		buffer.append(this.getSignEquipBuffer(goodsHero.getEquip5(), level));
//		return buffer;
//	}

//	/**
//	 * 获得单个装备属性
//	 * @param equipId
//	 * @param strengthenLevel
//	 * @return
//	 */
//	private AttriBuffer getSignEquipBuffer(int equipId, int strengthenLevel) {
//		AttriBuffer buffer = AttriBuffer.createAttriBuffer();
//		GoodsEquipment goodsEquip = GameContext.getGoodsApp().getGoodsTemplate(GoodsEquipment.class, equipId);
//		if (null == goodsEquip) {
//			return buffer;
//		}
//		buffer.append(GameContext.getEquipApp().getBaseAttriBuffer(equipId, goodsEquip.getQualityType(), 
//				goodsEquip.getStar()));
//		buffer.append(GameContext.getEquipApp().getStrengthenAttriBuffer(equipId, goodsEquip.getQualityType(), 
//				goodsEquip.getStar(), strengthenLevel));
//		return buffer;
//	}

	/**
	 * 添加英雄列表
	 * @param robot
	 * @param greenNum
	 * @param blueNum
	 * @param purpleNum
	 * @param orangeNum
	 */
	private void createRobotHeroList(QualifyRobot robot, byte greenNum, byte blueNum, byte purpleNum, byte orangeNum) {
		this.createRobotQualityHero(robot, orangeNum, QualityType.orange);
		this.createRobotQualityHero(robot, purpleNum, QualityType.purple);
		this.createRobotQualityHero(robot, blueNum, QualityType.blue);
		this.createRobotQualityHero(robot, greenNum, QualityType.green);
	}
	
	private void createRobotQualityHero(QualifyRobot robot, int number, QualityType type) {
		List<Integer> allHeroList = this.getAllHeroList(type);
		if (Util.isEmpty(allHeroList)) {
			return;
		}
		int size = allHeroList.size();
		while (number > 0) {
			int index = this.getRobotHeroNumber(type);
			int heroId = allHeroList.get(index);
			this.incrRobotHeroNumber(type, size);
			robot.addHero(heroId);
			number --;
		}
	}
	
	/**
	 * 请空创建机器人规则
	 */
	private void clearRobotRules() {
		this.rulesList.clear();
		this.blueHeroList.clear();
		this.greenHeroList.clear();
		this.purpleHeroList.clear();
		this.orangeHeroList.clear();
		this.firstNameList.clear();
		this.secondNameList.clear();
	}

	/**
	 * 启服时初始化榜单信息
	 * @param roleQualifyList
	 */
	private void initQualifyRank(List<QualifyRank> roleQualifyList) {
		if (Util.isEmpty(roleQualifyList)) {
			return;
		}
		for (QualifyRank roleQualify : roleQualifyList) {
			if (null == roleQualify) {
				continue;
			}
			this.qualifyMap.put(roleQualify.getRank(), roleQualify);
			this.rankMap.put(roleQualify.getRoleId(), roleQualify.getRank());
		}
	}

	/**
	 * 将排行榜信息保存到数据库
	 */
	@Override
	public void saveQualifyRankInfo() {
		// 删除排行榜数据
		GameContext.getBaseDAO().deleteAll(QualifyRank.class);
		// 保存排行榜日志
		this.saveRankLog();
		// 排行榜数据入库
		List<QualifyRank> rankList = Lists.newArrayList();
		rankList.addAll(this.qualifyMap.values());
		int size = rankList.size();
		int fromIndex = 0;
		int toIndex = 500;
		int num = toIndex;
		boolean isEnd = false;
		while (!isEnd) {
			if (toIndex > size) {
				toIndex = size;
				isEnd = true;
			}
			GameContext.getBaseDAO().insertBatch(rankList.subList(fromIndex, toIndex));
			fromIndex = toIndex;
			toIndex += num;
		}
	}

	/**
	 * 保存排名日志
	 */
	private void saveRankLog() {
		if (Util.isEmpty(this.qualifyMap)) {
			return;
		}
		for (QualifyRank rank : this.qualifyMap.values()) {
			if (null == rank) {
				continue;
			}
			StringBuffer buffer = new StringBuffer();
			buffer.append(rank.getRoleId()).append(Cat.pound).append(rank.getRank()).append(Cat.pound).append(rank.getRobot());
			Log4jManager.QUALIFY_RANK.info(buffer.toString());
		}
	}

	/**
	 * 获取排名奖励配置
	 * @param rank
	 * @param level
	 * @return QualifyGiftConfig
	 */
	@Override
	public QualifyGiftConfig getRankQualifyGiftConfig(int rank, int level) {
		for (QualifyGiftConfig config : this.rankGiftList) {
			if (level <= config.getDownLevel() && level >= config.getUpLevel() && rank <= config.getDownRank() && rank >= config.getUpRank()) {
				return config;
			}
		}
		return null;
	}

	/**
	 * 获取挑战胜利奖励配置
	 * @param rank
	 * @param level
	 * @return QualifyGiftConfig
	 */
	private QualifyGiftConfig getWinQualifyGift(int rank, int level) {
		for (QualifyGiftConfig config : this.winGiftList) {
			if (level <= config.getDownLevel() && level >= config.getUpLevel() && rank <= config.getDownRank() && rank >= config.getUpRank()) {
				return config;
			}
		}
		return null;
	}

	/**
	 * 获取挑战失败奖励配置
	 * @param rank
	 * @param level
	 * @return QualifyGiftConfig
	 */
	private QualifyGiftConfig getLoseQualifyGift(int rank, int level) {
		for (QualifyGiftConfig config : this.loseGiftList) {
			if (level <= config.getDownLevel() && level >= config.getUpLevel() && rank <= config.getDownRank() && rank >= config.getUpRank()) {
				return config;
			}
		}
		return null;
	}

	/**
	 * 获取排名对应的信息
	 * @param rank
	 * @return RoleQualify
	 */
	private QualifyRank getQualifyRank(int rank) {
		QualifyRank qualifyRank = this.qualifyMap.get((short) rank);
		if (null == qualifyRank) {
			return null;
		}
		if (qualifyRank.getRobot() == ROBOT) {
			return qualifyRank;
		}
		RoleInstance targetRole = GameContext.getOnlineCenter().getRoleInstanceByRoleId(qualifyRank.getRoleId());
		if (null == targetRole) {
			return qualifyRank;
		}
		this.updateQualifyRank(targetRole, qualifyRank);
		return qualifyRank;
	}

	/**
	 * 获得购买挑战次数消耗
	 * @param times
	 * @return
	 */
	private QualifyCostConfig getQualifyCostConfig(byte times) {
		return this.challengeCostMap.get(times);
	}

	/**
	 * 获得玩家对战记录
	 * @param roleId
	 * @return
	 */
	private RoleQualifyRecord getRoleQualifyRecord(String roleId) {
		return this.recordMap.get(roleId);
	}

	/**
	 * 获取玩家对应的排名
	 * @param RoleInstance
	 * @return 排名
	 */
	@Override
	public short getRoleRank(RoleInstance role) {
		return this.getRoleRank(role.getRoleId());
	}

	/**
	 * 获取玩家对应的排名
	 * @param RoleInstance
	 * @return 排名
	 */
	@Override
	public short getRoleRank(String roleId) {
		Short rank = this.rankMap.get(roleId);
		if (null == rank) {
			return OUT_RANK;
		}
		return rank;
	}

	
	
	/**
	 * 根据系数获得挑战对手
	 * @param Coefficient
	 * @return RoleQualify
	 */
	private List<QualifyRank> getChallengeOpponents(int rank) {
		int firstRank = this.getRandomRank(rank, FIRST_COEFFICIENT1, FIRST_COEFFICIENT2);
		int secondRank = this.getRandomRank(rank, SECOND_COEFFICIENT1, SECOND_COEFFICIENT2);
		int thirdRank = this.getRandomRank(rank, THIRD_COEFFICIENT1, THIRD_COEFFICIENT2);
		List<QualifyRank> qualifyList = Lists.newArrayList();
		// 第一个显示最强的
		QualifyRank thirdQualify = this.getQualifyRank(thirdRank);
		if (null != thirdQualify) {
			qualifyList.add(thirdQualify);
		}
		QualifyRank secondQualify = this.getQualifyRank(secondRank);
		if (null != secondQualify) {
			qualifyList.add(secondQualify);
		}
		QualifyRank firstQualify = this.getQualifyRank(firstRank);
		if (null != firstQualify) {
			qualifyList.add(firstQualify);
		}
		return qualifyList;
	}

	/**
	 * 根据排名和系数随机获得挑战对手的排名
	 * @param rank
	 * @param coe1
	 * @param coe2
	 * @return int
	 */
	private int getRandomRank(int rank, double coe1, double coe2) {
		int rank1 = (int) (rank * coe1);
		int rank2 = (int) (rank * coe2);
		return RandomUtil.randomInt(rank1, rank2);
	}

	/**
	 * 前十的挑战特殊处理
	 * @param rank
	 * @return List<RoleQualify>
	 */
	private List<QualifyRank> getStrongChallengeOpponents(int rank) {
		List<QualifyRank> qualifyList = Lists.newArrayList();
		switch (rank) {
		case FIRST:
			qualifyList.add(this.getQualifyRank(SECOND));
			qualifyList.add(this.getQualifyRank(THIRD));
			qualifyList.add(this.getQualifyRank(FOURTH));
			break;
		case SECOND:
			qualifyList.add(this.getQualifyRank(FIRST));
			qualifyList.add(this.getQualifyRank(THIRD));
			qualifyList.add(this.getQualifyRank(FOURTH));
			break;
		case THIRD:
			qualifyList.add(this.getQualifyRank(FIRST));
			qualifyList.add(this.getQualifyRank(SECOND));
			qualifyList.add(this.getQualifyRank(FOURTH));
			break;
		default:
			qualifyList.add(this.getQualifyRank(rank - THIRD));
			qualifyList.add(this.getQualifyRank(rank - SECOND));
			qualifyList.add(this.getQualifyRank(rank - FIRST));
			break;
		}
		return qualifyList;
	}

	/**
	 * 获得挑战对手列表
	 * @param role
	 * @return List<RoleQualify>
	 */
	@Override
	public List<QualifyRank> getChallengeOpponents(RoleInstance role) {
		int rank = this.getRoleRank(role);
		if (rank <= CHALLENGE_STRONG) {
			return this.getStrongChallengeOpponents(rank);
		}
		return this.getChallengeOpponents(rank);
	}

	/**
	 * 获得下次发奖信息
	 * @return String
	 */
	@Override
	public String getNextGiveGiftStrInfo() {
		String message = GameContext.getI18n().messageFormat(TextId.Qualify_Give_Gift_Message, this.qualifyBaseConfig.getNextGiveGiftTime());
		return message;
	}

	/**
	 * 获得下次免费挑战的CD时间
	 * @param role
	 * @return
	 */
	@Override
	public int getChallengeCDTime(RoleInstance role) {
		long lastChallengeTime = role.getRoleCount().getRoleTimesToLong(CountType.ChallengeTime);
		if (lastChallengeTime <= 0) {
			return 0;
		}
		int cdTime = this.qualifyBaseConfig.getCooldownTime() - this.getIntTime(new Date().getTime() - lastChallengeTime);
		return cdTime >= 0 ? cdTime : 0;
	}

	/**
	 * 获得当前时间
	 * @return
	 */
	private int getIntTime(long date) {
		int nowTime = (int) (date / TIME_INT);
		return nowTime;
	}

	/**
	 * 每天可以挑战多少次
	 * @return
	 */
	@Override
	public byte getMaxChallengeTimes(RoleInstance role) {
		return (byte) (this.qualifyBaseConfig.getChallengeTimes() + role.getRoleCount().getRoleTimesToByte(CountType.BuyChallengeTimes));//BuyChallengeTimes());
	}

	/**
	 * 剩余挑战次数
	 * @param role
	 * @return
	 */
	@Override
	public byte getRemainChallengeTimes(RoleInstance role) {
		return (byte) (this.getMaxChallengeTimes(role) - role.getRoleCount().getRoleTimesToByte(CountType.ChallengeTimes));//.getChallengeTimes());
	}

	/**
	 * 获得荣誉商店的ID
	 * @return
	 */
	@Override
	public String getShopId() {
		return this.qualifyBaseConfig.getShopId();
	}

	/**
	 * 获得角色排行信息
	 * @param role
	 * @return
	 */
	@Override
	public QualifyRank getQualifyRank(RoleInstance role) {
		return this.getQualifyRank(role.getRoleId());
	}

	/**
	 * 获得角色排行信息
	 * @param role
	 * @return
	 */
	@Override
	public QualifyRank getQualifyRank(String roleId) {
		int rank = this.getRoleRank(roleId);
		if (rank == OUT_RANK) {
			return null;
		}
		return this.getQualifyRank(rank);
	}

	/**
	 * 购买挑战次数
	 * @param role
	 * @return
	 */
	@Override
	public Result buyChallengeTimes(RoleInstance role, String isConfirm) {
		Result result = new Result();
		int vipLevel = GameContext.getVipApp().getVipLevel(role);
		int canBuyTimes = GameContext.getVipApp().getVipPrivilegeTimes(vipLevel, VipPrivilegeType.QUALIFY_BUY_TIMES.getType(), "");
		// 获得开启该特权VIP等级
		if (canBuyTimes <= 0) {
			int openVipLevel = GameContext.getVipApp().getOpenVipLevel(VipPrivilegeType.QUALIFY_BUY_TIMES.getType(), "");
			String info = GameContext.getI18n().messageFormat(TextId.Qualify_Vip_No_Times, openVipLevel, GameContext.getVipApp().getVipPrivilegeTimes(openVipLevel, VipPrivilegeType.QUALIFY_BUY_TIMES.getType(), ""));
			result.setInfo(info);
			return result;
		}
		RoleCount count = role.getRoleCount();
		int haveBuyTimes = count.getRoleTimesToByte(CountType.BuyChallengeTimes);//getBuyChallengeTimes();
		// 如果超出购买次数
		if (canBuyTimes <= haveBuyTimes) {
			// 如果已经到达最大VIP等级
			if (GameContext.getVipApp().isFullVipLevel(role)) {
				String info = GameContext.getI18n().getText(TextId.Qualify_Challenge_No_Times);
				result.setInfo(info);
				return result;
			}
			int nextVipLevel = vipLevel + 1;
			// 下一等级VIP可购买次数
			String info = GameContext.getI18n().messageFormat(TextId.Qualify_Vip_No_Times, nextVipLevel, GameContext.getVipApp().getVipPrivilegeTimes(nextVipLevel, VipPrivilegeType.QUALIFY_BUY_TIMES.getType(), ""));
			result.setInfo(info);
			return result;
		}
		int needMoney = this.getBuyChallengeCost((byte) (haveBuyTimes + 1));
		// 判断钻石是否足够
		Result moneyResult = GameContext.getUserAttributeApp().getEnoughResult(role, AttributeType.goldMoney, needMoney);
		if (!moneyResult.isSuccess()) {
			return moneyResult;
		}
		if (!CONFIRM.equals(isConfirm)) {
			String tips = GameContext.getI18n().messageFormat(TextId.Qualify_Buy_Times_Confirm, String.valueOf(needMoney));
			C0007_ConfirmationNotifyMessage message = new C0007_ConfirmationNotifyMessage();
			message.setAffirmCmdId(BUY_COMMID);
			message.setAffirmParam(CONFIRM);
			message.setInfo(tips);
			role.getBehavior().sendMessage(message);
			result.setIgnore(true);
			return result;
		}
		// 扣除钻石
		GameContext.getUserAttributeApp().changeRoleMoney(role, AttributeType.goldMoney, OperatorType.Decrease, needMoney, OutputConsumeType.qualify_buy_cost);
		role.getBehavior().notifyAttribute();
		// 增加购买次数
		count.changeTimes(CountType.BuyChallengeTimes);//incrBuyChallengeTimes();
		GameContext.getCountApp().saveRoleCount(count);
		result.success();
		return result;
	}

	/**
	 * 获得立即挑战消耗
	 * @param role
	 * @return
	 */
	private int getChallengeCDCost(byte times) {
		QualifyCostConfig config = this.getQualifyCostConfig(times);
		if (null == config) {
			return Integer.MAX_VALUE;
		}
		return config.getCooldownCost();
	}
	
	/**
	 * 获得购买挑战次数消耗
	 * @param times
	 * @return
	 */
	private int getBuyChallengeCost(byte times) {
		QualifyCostConfig config = this.getQualifyCostConfig(times);
		if (null == config) {
			return Integer.MAX_VALUE;
		}
		return config.getCost();
	}

	/**
	 * 获得挑战CD时间
	 * @param role
	 * @return
	 */
	@SuppressWarnings("deprecation")
	@Override
	public String getChallengeCDStrTime(RoleInstance role) {
		int cdTime = this.getChallengeCDTime(role);
		return cdTime / 60 + ":" + cdTime % 60;
	}

	/**
	 * 挑战PVP
	 * @param role
	 * @return
	 */
	@Override
	public Result qualifyChallenge(RoleInstance role, String targetRoleId, boolean isConfirm) {
		Result result = this.qualifyChallengeCondition(role);
		if (!result.isSuccess()) {
			return result;
		}
		int targetRank = this.getRoleRank(targetRoleId);
		if (OUT_RANK == targetRank) {
			result.setInfo(GameContext.getI18n().getText(TextId.ERROR_INPUT));
			return result;
		}
		QualifyRank targetQualify = this.getQualifyRank(targetRank);
		if (null == targetQualify) {
			result.setInfo(GameContext.getI18n().getText(TextId.Sys_Error));
			return result;
		}
		// 二次确认
		if (!isConfirm) {
			// 判断挑战是否已CD
			if (this.getChallengeCDTime(role) <= 0) {
				String confirmInfo = GameContext.getI18n().messageFormat(TextId.Qualify_Challenge_Confirm, targetQualify.getRoleName());
				this.sendConfirmMessage(role, targetRoleId, confirmInfo);
				result.setIgnore(true);
				return result;
			}
			// 如果VIP等级不够
			int vipLevel = GameContext.getVipApp().getVipLevel(role.getRoleId());
			int canCDVipLevel = GameContext.getVipApp().getOpenVipLevel(VipPrivilegeType.QUALIFY_CD_CLEAR.getType(), "");
			if (vipLevel < canCDVipLevel) {
				String info = GameContext.getI18n().messageFormat(TextId.Qualify_Vip_Can_CD, canCDVipLevel);
				result.setInfo(info);
				return result;
			}
			int codChallengeTimes = role.getRoleCount().getRoleTimesToByte(CountType.CodChallengeTimes);//.getCodChallengeTimes();
			int needMoney = this.getChallengeCDCost((byte) (codChallengeTimes + 1));
			String confirmInfo = GameContext.getI18n().messageFormat(TextId.Qualify_Cost_Challenge_Now, this.getChallengeCDStrTime(role), needMoney);
			this.sendConfirmMessage(role, targetRoleId, confirmInfo);
			result.setIgnore(true);
			return result;
		}
		// 未冷却挑战扣费
		if (this.getChallengeCDTime(role) > 0) {
			// 判断钻石是否足够
			int codChallengeTimes = role.getRoleCount().getRoleTimesToByte(CountType.CodChallengeTimes);//.getCodChallengeTimes();
			int needMoney = this.getChallengeCDCost((byte) (codChallengeTimes + 1));
			Result moneyResult = GameContext.getUserAttributeApp().getEnoughResult(role, AttributeType.goldMoney, needMoney);
			if (!moneyResult.isSuccess()) {
				return moneyResult;
			}
			GameContext.getUserAttributeApp().changeRoleMoney(role, AttributeType.goldMoney, OperatorType.Decrease, needMoney, OutputConsumeType.qualify_challenge_output);
			// 增加付费冷却次数
			role.getRoleCount().changeTimes(CountType.CodChallengeTimes);//.incrCodChallengeTimes();
			GameContext.getCountApp().saveRoleCount(role.getRoleCount());
			role.getBehavior().notifyAttribute();
		}
		// 开始挑战
		try {
			AsyncPvpBattleInfo info = new AsyncPvpBattleInfo();
			info.setRoleId(role.getRoleId());
			info.setRoleName(role.getRoleName());
			info.setTargetRoleId(targetRoleId);
			info.setTargetRoleName(targetQualify.getRoleName());
			info.setOpType(targetQualify.getRobot());
			GameContext.getAsyncPvpApp().addAsyncPvpBattleInfo(info);
			// 切换地图
			Point point = new Point(this.qualifyBaseConfig.getMapId(), this.qualifyBaseConfig.getMapX(), this.qualifyBaseConfig.getMapY());
			GameContext.getUserMapApp().changeMap(role, point);
			result.success();
			return result;
		} catch (Exception ex) {
			logger.error("QualifyAppImpl.qualifyChallenge error!", ex);
			return result;
		}
	}

	/**
	 * 处理挑战结果
	 * @param role
	 * @param battleInfo
	 * @param type
	 */
	@Override
	public void challengeOver(RoleInstance role, AsyncPvpBattleInfo battleInfo, ChallengeResultType type, boolean sendMessage) {
		boolean isPlayer = true;
		boolean isTargOnline = true;
		if (null == role) {
			return;
		}
		try {
			RoleQualifyRecord roleRecord = this.getRoleQualifyRecord(role.getRoleId());
			if (null == roleRecord) {
				return;
			}
			QualifyRank targetQualify = this.getQualifyRank(this.getRoleRank(battleInfo.getTargetRoleId()));
			RoleQualifyRecord targRecord = null;
			if (targetQualify.getRobot() == PLAYER) {
				targRecord = this.getRoleQualifyRecord(battleInfo.getTargetRoleId());
				if (null == targRecord) {
					isTargOnline = false;
					targRecord = GameContext.getQualifyStorage().getRoleQualifyRecord(battleInfo.getTargetRoleId());
					if (null == targRecord) {
						return;
					}
				}
			} else {
				isPlayer = false;
			}
			// 挑战失败
			if (type == ChallengeResultType.Lose) {
				roleRecord.addChallengeRecord(this.createChallengeRecord(this.getRoleRank(role), battleInfo.getTargetRoleName(), ChallengeRecordType.ChallengeLose, RankStatusType.keep.getType()));
				if (isPlayer) {
					targRecord.addChallengeRecord(this.createChallengeRecord(targetQualify.getRank(), battleInfo.getRoleName(), ChallengeRecordType.BeChallengeWin, RankStatusType.keep.getType()));
					// 如果目标不在线，保存目标的对战记录
					if (!isTargOnline) {
						GameContext.getQualifyStorage().saveRoleQualifyRecord(targRecord);
					}
				}
				// 获得奖励配置
				QualifyGiftConfig config = this.getLoseQualifyGift(this.getRoleRank(role), role.getLevel());
				if (null == config) {
					return;
				}
				if (sendMessage) {
					this.pushGiveGiftMessage(role, config, type.getType());
				}
				this.giveQualifyGift(role, config);
				// 记录挑战时间
				role.getRoleCount().changeTimes(CountType.ChallengeTime, new Date().getTime());
				return;
			}
			// 挑战成功
			short roleRank = this.getRoleRank(role);
			short targetRank = this.getRoleRank(targetQualify.getRoleId());
			if (roleRank > targetRank) {
				roleRecord.addChallengeRecord(this.createChallengeRecord(targetRank, battleInfo.getTargetRoleName(), ChallengeRecordType.ChallengeWin, RankStatusType.up.getType()));
			} else {
				roleRecord.addChallengeRecord(this.createChallengeRecord(roleRank, battleInfo.getTargetRoleName(), ChallengeRecordType.ChallengeWin, RankStatusType.keep.getType()));
			}
			if (isPlayer) {
				if (roleRank > targetRank) {
					targRecord.addChallengeRecord(this.createChallengeRecord(targetRank, battleInfo.getRoleName(), ChallengeRecordType.BeChallengeLost, RankStatusType.down.getType()));
				} else {
					targRecord.addChallengeRecord(this.createChallengeRecord(targetRank, battleInfo.getRoleName(), ChallengeRecordType.BeChallengeLost, RankStatusType.keep.getType()));
				}
				// 如果目标不在线，保存目标的对战记录
				if (!isTargOnline) {
					GameContext.getQualifyStorage().saveRoleQualifyRecord(targRecord);
				}
			}
			// 获得奖励配置
			QualifyGiftConfig config = this.getWinQualifyGift(targetQualify.getRank(), role.getLevel());
			if (null == config) {
				return;
			}
			if (sendMessage) {
				this.pushGiveGiftMessage(role, config, type.getType());
			}
			this.giveQualifyGift(role, config);
			// 交换排名
			this.changeRoleRank(role, battleInfo.getTargetRoleId());
		} catch (Exception e) {
			logger.error("QualifyAppImpl.challengeOver error!", e);
		}
	}

	/**
	 * 创建挑战记录
	 * @param rank
	 * @param roleName
	 * @param challengeType
	 * @return
	 */
	private ChallengeRecord createChallengeRecord(short rank, String roleName, ChallengeRecordType recordType, byte type) {
		ChallengeRecord record = new ChallengeRecord();
		record.setChallengeTime(this.getIntTime(new Date().getTime()));
		record.setRoleName(roleName);
		record.setStatus(recordType.getType());
		record.setType(type);
		record.setCurrRank(rank);
		return record;
	}

	/**
	 * 发放排位赛挑战奖励
	 * @param gameMoney
	 * @param goldMoney
	 * @param honor
	 * @param potential
	 */
	private void giveQualifyGift(RoleInstance role, QualifyGiftConfig config) {
		if (config.getGameMoney() > 0) {
			GameContext.getUserAttributeApp().changeRoleMoney(role, AttributeType.gameMoney, OperatorType.Add, config.getGameMoney(), OutputConsumeType.qualify_challenge_output);
		}
		if (config.getGoldMoney() > 0) {
			GameContext.getUserAttributeApp().changeRoleMoney(role, AttributeType.goldMoney, OperatorType.Add, config.getGoldMoney(), OutputConsumeType.qualify_challenge_output);
		}
		if (config.getHonours() > 0) {
			role.getBehavior().changeAttribute(AttributeType.honor, OperatorType.Add, config.getHonours());
		}
		if (config.getPotential() > 0) {
			role.getBehavior().changeAttribute(AttributeType.potential, OperatorType.Add, config.getPotential());
		}
		role.getBehavior().notifyAttribute();
	}
	
	/**
	 * PUSH领奖面板
	 * @param role
	 * @param gameMoney
	 * @param goldMoney
	 * @param honor
	 * @param potential
	 * @param result
	 */
	private void pushGiveGiftMessage(RoleInstance role, QualifyGiftConfig config, byte result) {
		List<AttriTypeValueItem> awardAttrList = Lists.newArrayList();
		if (config.getGameMoney() > 0) {
			awardAttrList.add(this.createAttriTypeValueItem(AttributeType.gameMoney.getType(), config.getGameMoney()));
		}
		if (config.getGoldMoney() > 0) {
			awardAttrList.add(this.createAttriTypeValueItem(AttributeType.goldMoney.getType(), config.getGoldMoney()));
		}
		if (config.getHonours() > 0) {
			awardAttrList.add(this.createAttriTypeValueItem(AttributeType.honor.getType(), config.getHonours()));
		}
		if (config.getPotential() > 0) {
			awardAttrList.add(this.createAttriTypeValueItem(AttributeType.potential.getType(), config.getPotential()));
		}
		C1759_QualifyGiftPanelRespMessage message = new C1759_QualifyGiftPanelRespMessage();
		message.setSuccess(result);
		message.setAwardAttrList(awardAttrList);
		GameContext.getMessageCenter().sendSysMsg(role, message);
	}
	
	/**
	 * 领奖信息
	 * @param attriType
	 * @param attriValue
	 * @return
	 */
	private AttriTypeValueItem createAttriTypeValueItem(byte attriType, int attriValue) {
		AttriTypeValueItem item = new AttriTypeValueItem();
		item.setAttriType(attriType);
		item.setAttriValue(attriValue);
		return item;
	}

	/**
	 * 交换排行榜上的排名
	 */
	private void changeRoleRank(RoleInstance role, String targId) {
		short roleRank = this.getRoleRank(role.getRoleId());
		short targRank = this.getRoleRank(targId);
		if (roleRank <= targRank) {
			return;
		}
		// 修改排行信息
		QualifyRank roleQualify = this.getQualifyRank(roleRank);
		// 如果玩家在榜外，添加榜单信息
		if (null == roleQualify) {
			roleQualify = new QualifyRank();
			roleQualify.setRobot(PLAYER);
			roleQualify.setRoleId(role.getRoleId());
			roleQualify.setRoleLevel(role.getLevel());
			roleQualify.setRoleName(role.getRoleName());
			roleQualify.setBattleScore(role.getBattleScore());
		}
		
		// 如果玩家不在榜外，更改目标排行，否则目标掉榜
		if (roleRank != OUT_RANK) {
			QualifyRank targQualify = this.getQualifyRank(targRank);
			targQualify.setRank(roleRank);
			this.qualifyMap.put(roleRank, targQualify);
			this.rankMap.put(targId, roleRank);
		}
		// 如果目标排名在前十名，世界广播
		this.broadcast(role, targRank);
		roleQualify.setRank(targRank);
		this.qualifyMap.put(targRank, roleQualify);
		this.rankMap.put(role.getRoleId(), targRank);
	}
	
	/**
	 * 世界广播
	 * @param lvChanged
	 * @param role
	 * @param result
	 */
	private void broadcast(RoleInstance role, int targRank) {
		try {
			if (targRank > CHALLENGE_STRONG) {
				return ;
			}
			String broadcastInfo = this.qualifyBaseConfig.getBroadcastTips(role, targRank);
			if (Util.isEmpty(broadcastInfo)) {
				return ;
			}
			GameContext.getChatApp().sendSysMessage(ChatSysName.Goods_Strengthen, ChannelType.Publicize_Personal, broadcastInfo, null, null);
		} catch (Exception ex) {
			logger.error("strengthen broadcast error", ex);
		}
	}

	/**
	 * 挑战条件判断
	 * @param role
	 * @return
	 */
	private Result qualifyChallengeCondition(RoleInstance role) {
		Result result = new Result();
		if (this.getRemainChallengeTimes(role) <= 0) {
			result.setInfo(GameContext.getI18n().getText(TextId.Qualify_Challenge_No_Times));
			return result;
		}
		result.success();
		return result;
	}

	/**
	 * 发送二次确认信息
	 * @param role
	 * @param targetRoleId
	 */
	private void sendConfirmMessage(RoleInstance role, String targetRoleId, String confirmInfo) {
		role.getBehavior().sendMessage(this.getConfirmMessage(role, targetRoleId, confirmInfo));
	}

	/**
	 * 获得二次确认信息
	 * @param role
	 * @param targetRoleId
	 * @return
	 */
	private Message getConfirmMessage(RoleInstance role, String targetRoleId, String confirmInfo) {
		C0007_ConfirmationNotifyMessage message = new C0007_ConfirmationNotifyMessage();
		message.setAffirmCmdId(COMMANDID);
		message.setAffirmParam(targetRoleId + ":" + HAS_CONFIRM);
		message.setCancelCmdId((short) 0);
		message.setCancelParam("");
		message.setTime((byte) 0);
		message.setTimeoutCmdId((short) 0);
		message.setTimeoutParam("");
		message.setInfo(confirmInfo);
		return message;
	}

	/**
	 * 获得排行榜页数
	 * @return
	 */
	@Override
	public byte getMaxRankPage() {
		return (byte) ((RANK_NUMBER % PAGE_NUMBER) == 0 ? (RANK_NUMBER / PAGE_NUMBER) : (RANK_NUMBER / PAGE_NUMBER) + 1);
	}

	/**
	 * 根据页数获得排行榜信息
	 * @param page
	 * @return
	 */
	@Override
	public List<QualifyRank> getRoleQualifyList(short page) {
		List<QualifyRank> roleQualifyList = Lists.newArrayList();
		short downRank = (short) (page * PAGE_NUMBER);
		for (int i = 1; i <= PAGE_NUMBER; i++) {
			roleQualifyList.add(this.getQualifyRank(downRank - (PAGE_NUMBER - i)));
		}
		return roleQualifyList;
	}

	/**
	 * 获得对战记录
	 * @param role
	 * @return
	 */
	@Override
	public List<ChallengeRecord> getChallengeRecordList(RoleInstance role) {
		RoleQualifyRecord roleQualifyRecord = this.getRoleQualifyRecord(role.getRoleId());
		if (null == roleQualifyRecord) {
			return null;
		}
		return roleQualifyRecord.getChallengeRecordList();
	}

	/**
	 * 获得排位赛的规则说明
	 * @return
	 */
	@Override
	public String getQualifyRankDesc() {
		return this.qualifyBaseConfig.getRankDesc();
	}

	/**
	 * 发放排行奖励
	 * @param role
	 */
	@Override
	public void giveQualifyRankGift(QualifyRank roleQualify) {
		// 如果是不是玩家，即机器人，不发放奖励
		if (roleQualify.getRobot() != PLAYER) {
			return;
		}
		int level = roleQualify.getRoleLevel();
		// 如果玩家在线,等级从玩家身上取
		RoleInstance role = GameContext.getOnlineCenter().getRoleInstanceByRoleId(roleQualify.getRoleId());
		if (null != role) {
			level = role.getLevel();
		}
		QualifyGiftConfig config = this.getRankQualifyGiftConfig(roleQualify.getRank(), level);
		if (null == config) {
			return;
		}
		// 邮件发送奖励
		try {
			GameContext.getMailApp().sendMail(this.getGiveGiftMail(roleQualify, config));
		} catch (Exception e) {
			logger.error("Send RankGiftMail Error!", e);
		}
	}

	/**
	 * 获取发送礼物的邮件
	 * @param roleQualify
	 * @param config
	 * @return
	 * @throws Exception 
	 */
	private Mail getGiveGiftMail(QualifyRank roleQualify, QualifyGiftConfig config) throws Exception {
		Mail mail = new Mail(IdFactory.getInstance().nextId(IdType.MAIL));
		mail.setRoleId(roleQualify.getRoleId());
		mail.setTitle(GameContext.getI18n().getText(TextId.Qualify_Mail_Title));
		mail.setSendRole(GameContext.getI18n().getText(TextId.Qualify_Mail_Sender));
		mail.setSilverMoney(config.getGameMoney());
		mail.setGold(config.getGoldMoney());
		mail.setPotential(config.getPotential());
		mail.setHonor(config.getHonours());
		List<GoodsOperateBean> goodsList = config.getGoodsList();
		if (!Util.isEmpty(goodsList)) {
			for (GoodsOperateBean bean : goodsList) {
				if (null == bean) {
					continue;
				}
				GoodsBase goodsBase = GameContext.getGoodsApp().getGoodsBase(bean.getGoodsId());
				mail.addRoleGoods(goodsBase.createSingleRoleGoods(roleQualify.getRoleId(), bean.getGoodsNum()));
			}
		}
		String mailContent = GameContext.getI18n().messageFormat(TextId.Qualify_Mail_Content, DateUtil.date2Str(new Date(), "yyyy年MM月dd日"), roleQualify.getRank(), config.getGoldMoney(), config.getGameMoney(), config.getHonours());
		mail.setContent(mailContent);
		return mail;
	}

	/**
	 * 封装排行榜出战英雄
	 * @param roleHeroList
	 * @return
	 */
	@Override
	public List<HeroBattleItem> getQualifyHeroList(QualifyRank qualifyRank) {
		if (qualifyRank.getRobot() != PLAYER) {
			QualifyRobot robot = this.getQualifyRobot(qualifyRank.getRoleId());
			if (null == robot) {
				return null;
			}
			return this.getHeroBattleList(robot.getHeroIdList(), robot.getLevel());
		}
		List<RoleHero> roleHeroList = this.getRoleHeroList(qualifyRank);
		if (Util.isEmpty(roleHeroList)) {
			return null;
		}
		List<HeroBattleItem> list = Lists.newArrayList();
		for (RoleHero roleHero : roleHeroList) {
			if (null == roleHero) {
				continue;
			}
			list.add(this.getHeroBattleItem(roleHero));
		}
		return list;
	}

	/**
	 * 获得出战英雄列表
	 * @param qualifyRank
	 * @return
	 */
	private List<RoleHero> getRoleHeroList(QualifyRank qualifyRank) {
		return GameContext.getHeroApp().getRoleSwitchableHeroList(qualifyRank.getRoleId());
	}

	/**
	 * 封装挑战对手的出战英雄
	 * @param roleHeroList
	 * @return
	 */
	@Override
	public List<HeroBattleItem> getQualifyBattleHero(QualifyRank qualifyRank) {
		if (qualifyRank.getRobot() != PLAYER) {
			QualifyRobot robot = this.getQualifyRobot(qualifyRank.getRoleId());
			if (null == robot) {
				return null;
			}
			return this.getQualifyBattleHero(robot.getHeroIdList().get(0), robot.getLevel());
		}
		List<RoleHero> roleHeroList = this.getRoleHeroList(qualifyRank);
		if (Util.isEmpty(roleHeroList)) {
			return null;
		}
		RoleHero roleHero = roleHeroList.get(0);
		List<HeroBattleItem> list = Lists.newArrayList();
		list.add(this.getHeroBattleItem(roleHero));
		return list;
	}

	/**
	 * 获得机器人信息
	 * @param roleId
	 * @return
	 */
	@Override
	public QualifyRobot getQualifyRobot(String roleId) {
		return this.robotMap.get(roleId);
	}

	/**
	 * 获得机器人出战英雄
	 * @param heroId
	 * @param level
	 * @return
	 */
	private HeroBattleItem getHeroBattleItem(int heroId, int level) {
		GoodsHero goodsHero = GameContext.getGoodsApp().getGoodsTemplate(GoodsHero.class, heroId);
		if (null == goodsHero) {
			return null;
		}
		HeroBattleItem item = new HeroBattleItem();
		item.setImageId(goodsHero.getImageId());
		item.setGearId(goodsHero.getGearId());
		item.setSeriesId(goodsHero.getSeriesId());
		item.setLevel((byte) level);
		item.setQuality(goodsHero.getQualityType());
		item.setStar(goodsHero.getStar());
		return item;
	}
	
	private HeroBattleItem getHeroBattleItem(RoleHero roleHero) {
		GoodsHero goodsHero = GameContext.getGoodsApp().getGoodsTemplate(GoodsHero.class, roleHero.getHeroId());
		if (null == goodsHero) {
			return null;
		}
		HeroBattleItem item = new HeroBattleItem();
		item.setImageId(goodsHero.getImageId());
		item.setGearId(goodsHero.getGearId());
		item.setSeriesId(goodsHero.getSeriesId());
		item.setLevel((byte) roleHero.getLevel());
		item.setQuality(roleHero.getQuality());
		item.setStar(roleHero.getStar());
		return item;
	}

	/**
	 * 获取机器人出战英雄列表
	 * @param heroId1
	 * @param heroId2
	 * @param heroId3
	 * @param level
	 * @return
	 */
	private List<HeroBattleItem> getHeroBattleList(List<Integer> idList, int level) {
		List<HeroBattleItem> list = Lists.newArrayList();
		for (int id : idList) {
			if (0 == id) {
				continue;
			}
			HeroBattleItem item = this.getHeroBattleItem(id, level);
			list.add(item);
		}
		return list;
	}

	/**
	 * 获得机器人出战英雄
	 * @param heroId
	 * @param level
	 * @return
	 */
	private List<HeroBattleItem> getQualifyBattleHero(int heroId, int level) {
		List<HeroBattleItem> list = Lists.newArrayList();
		list.add(this.getHeroBattleItem(heroId, level));
		return list;
	}

	/**
	 * 获得挑战对手的ID
	 * @param roleId
	 * @return
	 */
	@Override
	public String getChallengeOpponentId(String roleId) {
		String targetRoleId = this.challengeOpponentMap.get(roleId);
		if (Util.isEmpty(targetRoleId)) {
			return null;
		}
		return targetRoleId;
	}

	/**
	 * 获得机器人的英雄列表
	 * @param roleId
	 * @return
	 */
	@Override
	public List<RoleHero> getRobotHeroList(String roleId) {
		QualifyRobot robot = this.getQualifyRobot(roleId);
		if (null == robot) {
			return null;
		}
		List<RoleHero> heroList = Lists.newArrayList();
		for (int heroId : robot.getHeroIdList()) {
			GoodsHero goodsHero = GameContext.getGoodsApp().getGoodsTemplate(GoodsHero.class, heroId);
			if (null == goodsHero) {
				continue;
			}
			RoleHero roleHero = new RoleHero();
			roleHero.setRoleId(robot.getRoleId());
			roleHero.setHeroId(heroId);
			roleHero.setStar(goodsHero.getStar());
			roleHero.setQuality(goodsHero.getQualityType());
			roleHero.setLevel(robot.getLevel());
			heroList.add(roleHero);
		}
		return heroList;
	}

	/**
	 * 获得机器人出战英雄属性
	 * @param roleId
	 * @param heroId
	 * @return
	 */
	@Override
	public AttriBuffer getRobotHeroBuffer(String roleId, int heroId) {
		QualifyRobot robot = this.getQualifyRobot(roleId);
		if (null == robot) {
			return null;
		}
		return this.getHeroAttriBuffer(heroId, robot.getLevel());
	}

	/**
	 * 获得机器人的人物属性
	 * @param roleId
	 * @return
	 */
	@Override
	public AsyncPvpRoleAttr getRobotAsyncPvpRoleAttr(String roleId) {
		AsyncPvpRoleAttr attr = new AsyncPvpRoleAttr();
		QualifyRobot robot = this.getQualifyRobot(roleId);
		if (null == robot) {
			return attr;
		}
		attr.setRoleId(robot.getRoleId());
		attr.setRoleName(robot.getRoleName());
		attr.setLevel(robot.getLevel());
		attr.setSpeed(this.roleCalct.getBaseValue(AttributeType.speed));
		attr.setCritAtkProb(this.roleCalct.getBaseValue(AttributeType.critAtkProb));
		attr.setHealRate(this.roleCalct.getBaseValue(AttributeType.healRate));
		attr.setMpConsumeRate(this.roleCalct.getBaseValue(AttributeType.mpConsumeRate));
		attr.setCdRate(this.roleCalct.getBaseValue(AttributeType.cdRate));
		for (AttriItem item : this.getRobotAttriBuffer(robot).getMap().values()) {
			if (null == item) {
				continue;
			}
			attr.setAttriValue(item.getAttriTypeValue(), (int) item.getValue());
		}
		return attr;
	}

	/**
	 * 获得排位赛基本配置
	 * @return
	 */
	@Override
	public QualifyBaseConfig getQualifyBaseConfig() {
		return this.qualifyBaseConfig;
	}
	
	/**
	 * 创建机器人英雄相关
	 * @param type
	 * @return
	 */
	private int getRobotHeroNumber(QualityType type) {
		Integer number = this.numberMap.get(type);
		if (number == null) {
			return 0;
		}
		return number;
	}
	
	/**
	 * 创建机器人英雄相关
	 * @param type
	 * @return
	 */
	private void incrRobotHeroNumber(QualityType type, int maxSize) {
		Integer number = this.numberMap.get(type);
		if (null == number) {
			number = 0;
		}
		if (number + 1 >= maxSize) {
			this.numberMap.put(type, 0);
			return;
		}
		this.numberMap.put(type, number + 1);
	}
	
	private List<Integer> getAllHeroList(QualityType type) {
		switch (type) {
		case green:
			return this.greenHeroList;
		case blue:
			return this.blueHeroList;
		case purple:
			return this.purpleHeroList;
		case orange:
			return this.orangeHeroList;
		default:
			return null;
		}
	}

	@Override
	public Map<Short, QualifyRank> getQualifyRankMap() {
		return this.qualifyMap;
	}

}
