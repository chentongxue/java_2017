package com.game.draco.app.quest.poker;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.quickbuy.QuickCostHelper;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.OperatorType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.base.SaveDbStateType;
import sacred.alliance.magic.base.XlsSheetNameType;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.dao.BaseDAO;
import sacred.alliance.magic.util.*;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.dailyplay.DailyPlayType;
import com.game.draco.app.hint.vo.HintType;
import com.game.draco.app.quest.Quest;
import com.game.draco.app.quest.QuestHelper;
import com.game.draco.app.quest.base.QuestAcceptType;
import com.game.draco.app.quest.base.QuestOpCode;
import com.game.draco.app.quest.base.QuestStatus;
import com.game.draco.app.quest.domain.RoleQuestLogInfo;
import com.game.draco.app.quest.poker.config.PokerRoundBuyConsumeConfig;
import com.game.draco.app.quest.poker.config.PokerSecondWeightConfig;
import com.game.draco.app.quest.poker.config.PokerThirdWeightConfig;
import com.game.draco.app.quest.poker.config.QuestPokerAwardConfig;
import com.game.draco.app.quest.poker.config.QuestPokerAwardRatioConfig;
import com.game.draco.app.quest.poker.config.QuestPokerBaseConfig;
import com.game.draco.app.quest.poker.config.RmQuestAwardConfig;
import com.game.draco.app.quest.poker.config.RmQuestAwardWeight;
import com.game.draco.app.quest.poker.config.RmQuestConfig;
import com.game.draco.app.quest.poker.config.RmQuestWeight;
import com.game.draco.app.quest.poker.domian.RoleQuestPoker;
import com.game.draco.app.quest.poker.vo.QuestPokerRoleData;
import com.game.draco.app.vip.type.VipPrivilegeType;
import com.game.draco.message.item.QuestPokerInfoItem;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.request.C0722_QuestPokerBuyCountTimeReqMessage;
import com.game.draco.message.response.C0712_QuestPokerPanelRespMessage;
import com.google.common.collect.Maps;

public class QuestPokerAppImpl implements QuestPokerApp {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final short QUEST_POKER_BUY_COUNT_CMDID = new C0722_QuestPokerBuyCountTimeReqMessage()
			.getCommandId();

	private BaseDAO baseDAO;

	/** 任务集合权重：KEY=角色等级,VALUE=权重配置 */
	private Map<Integer, RmQuestWeight> questWeightMap = Maps.newHashMap();
	/** 任务集合：KEY=集合ID,VALUE=任务集合信息 */
	private Map<Integer, RmQuestConfig> questConfigMap = Maps.newHashMap();

	/** 任务奖励权重：KEY=角色等级,VALUE=奖励权重配置 */
	private Map<Integer, RmQuestAwardWeight> awardWeightMap = Maps.newHashMap();
	/** 任务奖励：KEY=奖励ID,VALUE=奖励信息 */
	private Map<Integer, RmQuestAwardConfig> awardConfigMap = Maps.newHashMap();

	private QuestPokerBaseConfig baseConfig;
	private Map<Integer, QuestPokerAwardConfig> pokerAwardMap = Maps
			.newHashMap();
	private Map<Integer, QuestPokerAwardRatioConfig> pokerAwardRatioMap = Maps
			.newHashMap();
	private Map<Integer, Integer> secondWeightMap = Maps.newHashMap();
	private Map<Integer, PokerThirdWeightConfig> thirdWeighMap = Maps
			.newHashMap();

	/** 购买轮次配置：KEY=购买第几轮,VALUE=消耗数目（价格） */
	private Map<Integer, PokerRoundBuyConsumeConfig> buyConsumeConfigMap;

	private PiecewiseWrapper<ConsumeByLevel> acceptQuestConsume = null ;

	private Map<String, QuestPokerRoleData> roleDataMap = Maps.newHashMap();

	@Override
	public void setArgs(Object arg0) {

	}

	@Override
	public void start() {
		this.loadRandomQuestConfig();
		this.loadCountBuyConfig();
		//加载接任务的消耗
		this.acceptQuestConsume = XlsPojoUtil.createPiecewiseWrapper(
				XlsSheetNameType.poker_accept_quest_consume,ConsumeByLevel.class);
	}

	@Override
	public void stop() {

	}

	public BaseDAO getBaseDAO() {
		return baseDAO;
	}

	public void setBaseDAO(BaseDAO baseDAO) {
		this.baseDAO = baseDAO;
	}

	private void loadRandomQuestConfig() {
		String fileName = XlsSheetNameType.poker_quest_list.getXlsName();
		String sheetName = XlsSheetNameType.poker_quest_list.getSheetName();
		String info = "load excel error: fileName=" + fileName + ",sheetName="
				+ sheetName + ".";
		try {
			// 1.加载任务集合配置
			String sourceFile = GameContext.getPathConfig().getXlsPath()
					+ fileName;
			Map<String, RmQuestConfig> questMap = XlsPojoUtil.sheetToMap(
					sourceFile, sheetName, RmQuestConfig.class);
			for (RmQuestConfig questConfig : questMap.values()) {
				if (null == questConfig) {
					continue;
				}
				// 初始化任务
				questConfig.init(info);
				this.questConfigMap.put(questConfig.getSetId(), questConfig);
			}
			// 2.加载任务集合权重
			sheetName = XlsSheetNameType.poker_quest_weight.getSheetName();
			info = "fileName=" + fileName + ",sheetName=" + sheetName + ".";
			int minLevel = 0;
			int maxLevel = 0;
			Map<String, RmQuestWeight> questWeightMap = XlsPojoUtil.sheetToMap(
					sourceFile, sheetName, RmQuestWeight.class);
			for (RmQuestWeight questWeight : questWeightMap.values()) {
				if (null == questWeight) {
					continue;
				}
				// 任务权重初始化
				questWeight.init(info);
				for (int setId : questWeight.getWeightMap().keySet()) {
					if (!this.questConfigMap.containsKey(setId)) {
						this.checkFail(info + "setId=" + setId
								+ ",it's not exist!");
					}
				}
				this.questWeightMap
						.put(questWeight.getRoleLevel(), questWeight);
				int level = questWeight.getRoleLevel();
				if (0 == minLevel || level < minLevel) {
					minLevel = level;
				}
				if (level > maxLevel) {
					maxLevel = level;
				}
			}
			// 3.加载任务奖励信息
			sheetName = XlsSheetNameType.poker_award_list.getSheetName();
			info = "load excel error: fileName=" + fileName + ",sheetName="
					+ sheetName + ".";
			Map<String, RmQuestAwardConfig> awardMap = XlsPojoUtil.sheetToMap(
					sourceFile, sheetName, RmQuestAwardConfig.class);
			for (RmQuestAwardConfig awardConfig : awardMap.values()) {
				if (null == awardConfig) {
					continue;
				}
				// 奖励配置初始化
				Result result = awardConfig.checkAndInit();
				if (!result.isSuccess()) {
					this.checkFail(info + result.getInfo());
				}
				this.awardConfigMap.put(awardConfig.getAwardId(), awardConfig);
			}
			// 4.加载任务奖励权重
			sheetName = XlsSheetNameType.poker_award_weight.getSheetName();
			info = "load excel error: fileName=" + fileName + ",sheetName="
					+ sheetName + ".";
			Map<String, RmQuestAwardWeight> awardWeightMap = XlsPojoUtil
					.sheetToMap(sourceFile, sheetName, RmQuestAwardWeight.class);
			for (RmQuestAwardWeight awardWeight : awardWeightMap.values()) {
				if (null == awardWeight) {
					continue;
				}
				// 奖励权重初始化
				awardWeight.init(info);
				for (int awardId : awardWeight.getWeightMap().keySet()) {
					if (!this.awardConfigMap.containsKey(awardId)) {
						this.checkFail(info + "awardId=" + awardId
								+ ",it's not exist!");
					}
				}
				this.awardWeightMap
						.put(awardWeight.getRoleLevel(), awardWeight);
			}
			// 5.加载相关条件
			sheetName = XlsSheetNameType.poker_base_config.getSheetName();
			info = "load excel error: fileName=" + fileName + ",sheetName="
					+ sheetName + ".";
			List<QuestPokerBaseConfig> baseList = XlsPojoUtil.sheetToList(
					sourceFile, sheetName, QuestPokerBaseConfig.class);
			this.baseConfig = baseList.get(0);
			this.baseConfig.init(info);// 初始化配置
			if (null == this.baseConfig) {
				this.checkFail(info + "base_config is null.");
			}
			// 6.加载第二张牌的权重配置
			sheetName = XlsSheetNameType.poker_second_weight.getSheetName();
			info = "load excel error: fileName=" + fileName + ",sheetName="
					+ sheetName + ".";
			List<PokerSecondWeightConfig> secondList = XlsPojoUtil.sheetToList(
					sourceFile, sheetName, PokerSecondWeightConfig.class);
			for (PokerSecondWeightConfig config : secondList) {
				if (null == config) {
					continue;
				}
				config.init(info);
				this.secondWeightMap.put(config.getTwoType(),
						config.getWeight());
			}
			// 7.加载第三张牌的权重配置
			sheetName = XlsSheetNameType.poker_third_weight.getSheetName();
			info = "load excel error: fileName=" + fileName + ",sheetName="
					+ sheetName + ".";
			List<PokerThirdWeightConfig> thirdList = XlsPojoUtil.sheetToList(
					sourceFile, sheetName, PokerThirdWeightConfig.class);
			for (PokerThirdWeightConfig config : thirdList) {
				if (null == config) {
					continue;
				}
				config.init(info);
				this.thirdWeighMap.put(config.getTwoType(), config);
			}
			// 8.加载poker默认奖励配置
			sheetName = XlsSheetNameType.poker_award.getSheetName();
			info = "load excel error: fileName=" + fileName + ",sheetName="
					+ sheetName + ".";
			List<QuestPokerAwardConfig> paList = XlsPojoUtil.sheetToList(
					sourceFile, sheetName, QuestPokerAwardConfig.class);
			for (QuestPokerAwardConfig config : paList) {
				if (null == config) {
					continue;
				}
				config.init(info);
				this.pokerAwardMap.put(config.getRoleLevel(), config);
			}
			// 9.加载poker奖励倍率配置
			sheetName = XlsSheetNameType.poker_award_ratio.getSheetName();
			info = "load excel error: fileName=" + fileName + ",sheetName="
					+ sheetName + ".";
			List<QuestPokerAwardRatioConfig> parList = XlsPojoUtil.sheetToList(
					sourceFile, sheetName, QuestPokerAwardRatioConfig.class);
			for (QuestPokerAwardRatioConfig config : parList) {
				if (null == config) {
					continue;
				}
				config.init(info);
				this.pokerAwardRatioMap.put(config.getType(), config);
			}
			// 10.验证权重中的等级是否连续，是否配置错误
			this.checkRmQuestRoleLevelConfig(minLevel, maxLevel, info);
		} catch (Exception e) {
			this.checkFail(info + e.toString());
		}
	}

	/**
	 * 加载购买轮数的消耗配置
	 */
	private void loadCountBuyConfig() {
		buyConsumeConfigMap = loadConfigMap(
				XlsSheetNameType.poker_round_buy_consume,
				PokerRoundBuyConsumeConfig.class, true);
		// for(Map.Entry<String, PokerRoundBuyConsumeConfig> entry:
		// buyConsumeConfigMap.entrySet()){
		// PokerRoundBuyConsumeConfig cf = entry.getValue();
		// buyConsumeConfigMultiMap.put(cf.getBuyTime(), cf);
		// }
	}

	private <K, V extends KeySupport<K>> Map<K, V> loadConfigMap(
			XlsSheetNameType xls, Class<V> clazz, boolean linked) {
		String fileName = xls.getXlsName();
		String sheetName = xls.getSheetName();
		String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
		Map<K, V> map = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName,
				clazz, linked);
		if (Util.isEmpty(map)) {
			checkFail("not config the " + clazz.getSimpleName() + " ,file="
					+ sourceFile + " sheet=" + sheetName);
		}
		return map;
	}

	/**
	 * 验证权重中的等级是否配置错误 是否连续
	 * 
	 * @param minLevel
	 *            任务等级下限
	 * @param maxLevel
	 *            任务等级上限
	 * @param info
	 */
	private void checkRmQuestRoleLevelConfig(int minLevel, int maxLevel,
			String info) {
		// 验证权重中的等级和基本配置中是否一致
		int minRoleLevel = this.baseConfig.getMinLevel();
		if (minRoleLevel < minLevel || minRoleLevel > maxLevel) {
			this.checkFail(info + "base_config minLevel =" + minRoleLevel
					+ ",but quest_weight level is [" + minLevel + ","
					+ maxLevel + "].");
		}
		int maxRoleLevel = this.baseConfig.getMaxLevel();
		if (maxRoleLevel < minLevel || maxRoleLevel > maxLevel) {
			this.checkFail(info + "base_config maxLevel =" + minRoleLevel
					+ ",but quest_weight level is [" + minLevel + ","
					+ maxLevel + "].");
		}
		// 验证权重中的等级是否连续
		String questError = info
				+ "roleLevel of quest_weight is not straight [" + minLevel
				+ "," + maxLevel + "], not contains";
		String awardError = info
				+ "roleLevel of award_weight is not straight [" + minLevel
				+ "," + maxLevel + "], not contains";
		boolean awardIsError = false;
		boolean questIsError = false;
		// 验证权重是否连续
		for (int level = minLevel; level <= maxLevel; level++) {
			if (!this.questWeightMap.containsKey(level)) {
				questError += level + ",";
				questIsError = true;
			}
			if (!this.awardWeightMap.containsKey(level)) {
				awardError += level + ",";
				awardIsError = true;
			}
		}
		if (questIsError) {
			this.checkFail(questError);
		}
		if (awardIsError) {
			this.checkFail(awardError);
		}
	}

	private void checkFail(String info) {
		Log4jManager.CHECK.error(info);
		Log4jManager.checkFail();
	}

	private String getText(String textId) {
		return GameContext.getI18n().getText(textId);
	}

	@Override
	public Message getQuestPokerPanelMessage(RoleInstance role, boolean isManual) {
		try {
			Result result = this.checkCondition(role);
			// 不符合条件
			if (!result.isSuccess()) {
				return new C0003_TipNotifyMessage(result.getInfo());
			}
			return this.buildQuestPokerPanelRespMessage(role, isManual);
		} catch (Exception e) {
			this.logger.error(this.getClass().getName()
					+ ".getRmQuestPanelMessage error: ", e);
			return new C0003_TipNotifyMessage(
					this.getText(TextId.Quest_Poker_System_Error));
		}
	}

	private int getAcceptQuestConsume(int level){
		ConsumeByLevel consume = this.acceptQuestConsume.getOrMax(level);
		return (null == consume)?0:consume.getConsumeValue() ;
	}

	// 当没有剩余轮数的时候，引导购买轮数
	private C0712_QuestPokerPanelRespMessage buildQuestPokerPanelRespMessage(
			RoleInstance role, boolean isManual) {
		C0712_QuestPokerPanelRespMessage message = new C0712_QuestPokerPanelRespMessage();
		message.setRefreshType(isManual ? (byte) 1 : (byte) 0);
		try {
			QuestPokerRoleData roleData = this.getQuestPokerRoleData(role
					.getRoleId());
			int currCount = this.getCurrCount(role);
			int maxCount = this.getTotalTime(role);
			message.setCurrCount((short) (maxCount - currCount));
			message.setMaxCount((short) maxCount);
			message.setRefreshGold(this.baseConfig.getRefreshGold());
			RoleQuestPoker rolePoker = roleData.getRoleQuestPoker();
			// 容错：如果在未有购买功能前的老用户当日跟新，可能来不及清除历史的打开d 任务的信息
			if (rolePoker.isFinishedAll() && currCount < maxCount) {
				rolePoker.resetThreePoker();
			}

			RoleQuestLogInfo logInfo = roleData.getQuestLogInfo();
			List<QuestPokerInfoItem> finishList = roleData
					.getFinishedPokerInfoList();
			message.setFinishList(finishList);

			int questId = 0;
			int awardId = 0;
			if (null != logInfo) {
				questId = logInfo.getQuestId();
				awardId = logInfo.getAwardId();
			} else if (null != rolePoker) {
				questId = rolePoker.getTempQuestId();
				awardId = rolePoker.getTempAwardId();
			}
			// 购买
			int nextConsume = getNextBuyCountConsumption(role);
			byte showConsumeButton = getShowConsumeButtonFlag(role);
			String countDesc = getCountDesc(role, roleData);
			message.setNextConsumeDiamonds(nextConsume);
			message.setCountDesc(countDesc);
			message.setShowConsumeButton(showConsumeButton);
			//接任务消耗的游戏币
			message.setAcceptQuestGameMoney(this.getAcceptQuestConsume(role.getLevel()));

			Quest quest = GameContext.getQuestApp().getQuest(questId);
			// 容错，如果任务不存在，让玩家可以重新打开一个任务
			if (null == quest) {
				roleData.clearTempInfo();
				return message;
			}
			message.setQuestId(questId);
			message.setQuestName(quest.getQuestName());
			message.setQuestDesc(quest.getQuestDesc());
			QuestStatus status = QuestStatus.canAccept;
			// 已有任务的任务状态
			if (null != logInfo) {
				status = QuestHelper.getQuestStatus(role, quest, logInfo);
			}
			message.setStatus((byte) status.getType());
			message.setTermList(QuestHelper.getQuestTermItemList(role, quest));
			message.setAcceptType(quest.getQuestAcceptType().getType());
			RmQuestAwardConfig award = this.getRmQuestAwardConfig(awardId);
			if (null != award) {
				message.setAttrAwardList(award.getQuestAward()
						.getAttrAwardList());
				message.setGoodsAwardList(award.getQuestAward()
						.getGoodsAwardList(role));
			}
			QuestPokerInfoItem pokerInfo = roleData.getUnderwayPokerInfo();
			if (null != pokerInfo) {
				message.setPokerInfo(pokerInfo);
				PokerThreeType threeType = this.getPokerThreeType(finishList,
						pokerInfo);
				QuestPokerAwardRatioConfig awardRatio = this
						.getQuestPokerAwardRatioConfig(threeType);
				if (null != awardRatio) {
					message.setResId(awardRatio.getResId());
					message.setRatio(awardRatio.getRatio());
				}
			}
			QuestPokerAwardConfig config = this.getQuestPokerAwardConfig(role
					.getLevel());
			if (null != config) {
				message.setBaseExp(config.getExp());
			}
			return message;
		} catch (Exception e) {
			this.logger.error(this.getClass().getName()
					+ ".buildQuestPokerPanelRespMessage error: ", e);
			return message;
		}
	}

	/**
     *
	 * @return 1:展示购买次数按钮 0:不展示展示
     * 下面情况展示购买次数按钮
     * a.免费次数未用完毕 && 有购买次数
     * b.免费次数未用完毕 && 没有购买次数 && 下级vip购买次数>当前vip购买次数
	 * @date 2014-10-30 下午03:59:03
	 */
	private byte getShowConsumeButtonFlag(RoleInstance role) {
		int maxTime = getTotalTime(role);
		int curTime = getCurrCount(role);

		int buyTime = getBuyTime(role);
		int vipTime = getVipBuyTime(role);
        int normalBuyTime = this.baseConfig.getNormalBuyTimes() ;
		byte vipLevel = GameContext.getVipApp().getVipLevel(role);
		int nextVipTime = getVipBuyTime(vipLevel + 1);// 获得下一个VIP等级的可购买数目

		if (curTime > 0 && curTime == maxTime) {// n/n
			// 可以购买（今天未购买完，或未购买）
			if (buyTime < (vipTime + normalBuyTime)) {
				return 1;
			}
			// 今天已经购买完,再买需要更高等级
			if ((vipTime + normalBuyTime)== buyTime && nextVipTime > 0) {
				return 1;
			}
		}
		return 0;
	}

	private boolean needUpgradeVipLevel(RoleInstance role) {
		int maxTime = getTotalTime(role);
		int curTime = getCurrCount(role);
		int buyTime = getBuyTime(role);
		int vipTime = getVipBuyTime(role);
        int normalBuyTime = this.baseConfig.getNormalBuyTimes() ;
		byte vipLevel = GameContext.getVipApp().getVipLevel(role);
		int nextVipTime = getVipBuyTime(vipLevel + 1);// 获得下一个VIP等级的可购买数目
		if (curTime > 0 && curTime == maxTime && (vipTime + normalBuyTime) == buyTime
				&& nextVipTime > 0) {
			return true;
		}
		return false;
	}

	/**
	 * 当前的Vip还有可以购买的次数
	 * 
	 * @param role
	 * @return
	 */
	private boolean canBuyRoundTime(RoleInstance role) {
        int normalBuyTime = this.baseConfig.getNormalBuyTimes() ;
		int vipTime = getVipBuyTime(role);
		int buyTime = getBuyTime(role);
		return buyTime < (vipTime + normalBuyTime) ? true : false;
	}

	@Override
	public int getTotalTime(RoleInstance role) {
		return getBuyTime(role) + getMaxConfigCount();
	}

	private int getBuyTime(RoleInstance role) {
		if (role == null) {
			return 0;
		}
		QuestPokerRoleData roleData = this.getQuestPokerRoleData(role
				.getRoleId());
		if (roleData == null || roleData.getRoleQuestPoker() == null) {
			return 0 ;
		}
		return roleData.getRoleQuestPoker().getBuyNum();
	}

	private String getCountDesc(RoleInstance role, QuestPokerRoleData roleData) {
		int currCount = getCurrCount(role);
		int maxCount = getTotalTime(role);
		int buyTime = roleData.getRoleQuestPoker().getBuyNum();
		int vipTime = getVipBuyTime(role);

		if (currCount == maxCount) {
            int normalBuyTime = this.baseConfig.getNormalBuyTimes() ;
			if (buyTime >= (vipTime + normalBuyTime)) {
                // 购买次数已经达到VIP配置的最多次数
				return getText(TextId.QUEST_POKER_DESC_CLEAR);// 每日任务已经全部完成，请明天再来
			}
			return getText(TextId.QUEST_POKER_DESC_CLEAR_BUY_GUID);// 今日免费日常任务已经全部完成，付费可以额外获得任务机会
		}
		return getText(TextId.QUEST_POKER_DESC_NORMAL);

	}

	/**
	 * 获得配置的不同VIP每天可以购买的轮数
	 * 
	 * @param role
	 * @return
	 * @date 2014-10-30 上午11:22:54
	 */
	private int getVipBuyTime(RoleInstance role) {
		String roleId = role.getRoleId();
		return GameContext.getVipApp().getVipPrivilegeTimes(roleId,
				VipPrivilegeType.QUEST_POKER_BUY_TIMES.getType(), "");
	}

	private int getVipBuyTime(int vipLevel) {
		return GameContext.getVipApp().getVipPrivilegeTimes(vipLevel,
				VipPrivilegeType.QUEST_POKER_BUY_TIMES.getType(), "");
	}

	private PokerThreeType getPokerThreeType(
			List<QuestPokerInfoItem> finishList, QuestPokerInfoItem thirdInfo) {
		if (null == finishList || null == thirdInfo) {
			return null;
		}
		int finishSize = finishList.size();
		if (finishSize < 2) {
			return null;
		}
		int x = this.buildPokerValue(finishList.get(0));
		int y = this.buildPokerValue(finishList.get(1));
		int z = this.buildPokerValue(thirdInfo);
		return PokerHelper.getPokerThreeType(x, y, z);
	}

	private int buildPokerValue(QuestPokerInfoItem info) {
		return info.getPokerNumber() * PokerHelper.Four + info.getPokerStylor();
	}

	@Override
	public Result openPoker(RoleInstance role, byte index) {
		Result result = new Result();
		try {
			if (index <= 0) {
				return result.setInfo(this
						.getText(TextId.Quest_Poker_Param_Error));
			}
			Result res = this.checkCondition(role);
			if (!res.isSuccess()) {
				return res;
			}
			QuestPokerRoleData roleData = this.getQuestPokerRoleData(role
					.getRoleId());
			// 判断是否达到最大次数
			RoleQuestPoker rolePoker = roleData.getRoleQuestPoker();
			if (null != rolePoker) {
				if (rolePoker.getCurrCount(role) >= this.getTotalTime(role)) {
					return result.setInfo(this
							.getText(TextId.Quest_Poker_Max_Count));
				}
			}
			// 任务正在进行
			RoleQuestLogInfo logInfo = roleData.getQuestLogInfo();
			if (null != logInfo) {
				return result
						.setInfo(this.getText(TextId.Quest_Poker_Underway));
			}
			// 是否已经刷出过任务
			if (rolePoker.hasTempQuestPoker()) {
				return result
						.setInfo(this.getText(TextId.Quest_Poker_Has_Open));
			}
			//判断游戏币是否足够
			int consumeGameMoney = this.getAcceptQuestConsume(role.getLevel());
			if(consumeGameMoney > 0){
				Result consumeResult = GameContext.getUserAttributeApp().getEnoughResult(role,AttributeType.gameMoney,consumeGameMoney);
				if(!consumeResult.isSuccess() || consumeResult.isIgnore()){
					return consumeResult ;
				}
				//扣除游戏币
				GameContext.getUserAttributeApp().changeRoleMoney(role, AttributeType.gameMoney,
						OperatorType.Decrease, consumeGameMoney, OutputConsumeType.quest_poker_accept_quest_consume);
				role.getBehavior().notifyAttribute();
			}

			// 随机一个任务
			int questId = this.getRandomQuestId(role);
			// 随机一个奖励
			RmQuestAwardConfig award = this.getRandomAward(role);
			int awardId = 0;
			if (null != award) {
				awardId = award.getAwardId();
			}
			// 随机扑克
			int pokerValue = this.openRandomPoker(rolePoker);
			rolePoker.setTempQuestPoker(questId, awardId, index, pokerValue);
			// 主推面板消息
			this.sendMessage(role,
					this.buildQuestPokerPanelRespMessage(role, false));
			return result.success();
		} catch (Exception e) {
			this.logger.error(this.getClass().getName() + ".openPoker error: ",
					e);
			return result.setInfo(Status.Sys_Error.getTips());
		}
	}

	private void sendMessage(RoleInstance role, Message message) {
		GameContext.getMessageCenter().sendSysMsg(role, message);
	}

	private int openRandomPoker(RoleQuestPoker poker) {
		// 第一章牌，纯随机
		if (null == poker) {
			return PokerHelper.randomFirstPoker();
		}
		int finishNum = poker.getFinishedNumber();
		// 完成了1张，根据权重随机第二张牌
		if (1 == finishNum) {
			return PokerHelper.randomSecondPoker(poker.getPokerValue1());
		}
		// 完成了2张，根据权重随机第三张牌
		if (2 == finishNum) {
			return PokerHelper.randomThirdPoker(poker.getPokerValue1(),
					poker.getPokerValue2());
		}
		return PokerHelper.randomFirstPoker();
	}

	private int refreshRandomPoker(RoleQuestPoker poker) {
		// 第一章牌，纯随机
		if (null == poker) {
			return PokerHelper.randomFirstPoker();
		}
		int finishNum = poker.getFinishedNumber();
		// 完成了1张，刷新第二张牌
		if (1 == finishNum) {
			return PokerHelper.refreshSecondPoker(poker.getPokerValue1());
		}
		// 完成了2张，刷新第三张牌
		if (2 == finishNum) {
			return PokerHelper.refreshThirdPoker(poker.getPokerValue1(),
					poker.getPokerValue2());
		}
		return PokerHelper.randomFirstPoker();
	}

	@Override
	public int getMaxConfigCount() {
		return this.baseConfig.getMaxTimes();
	}

	@Override
	public int getCurrCount(RoleInstance role) {
		QuestPokerRoleData roleData = this.getQuestPokerRoleData(role
				.getRoleId());
		if (null == roleData) {
			return 0;
		}
		RoleQuestPoker info = roleData.getRoleQuestPoker();
		if (null == info) {
			return 0;
		}
		return info.getCurrCount(role);
	}

	@Override
	public Result refreshQuest(RoleInstance role){
		Result result = new Result();
		QuestPokerRoleData roleData = this.getQuestPokerRoleData(role
				.getRoleId());
		RoleQuestLogInfo logInfo = roleData.getQuestLogInfo();
		if (null == logInfo) {
			//没有接到任务不需要刷新
			return result.setInfo(this.getText(TextId.Quest_Poker_Refresh_Quest_Not_Any_Quest));
		}
		//判断是否有poker牌
		RoleQuestPoker rolePoker = roleData.getRoleQuestPoker();
		if (!rolePoker.hasTempQuestPoker()) {
			//必须要先有poker牌
			return result.setInfo(this.getText(TextId.Quest_Poker_Param_Error));
		}
		int questId = logInfo.getQuestId();
		//判断任务是否可提交
		Quest quest = GameContext.getQuestApp().getQuest(questId);
		if(null != quest && quest.canSubmit(role)){
			//任务可提交不需要刷新
			return result.setInfo(this.getText(TextId.Quest_Poker_Refresh_Quest_Can_Submit));
		}
		//随机刷新任务
		int newQuestId = 0 ;
		for(int index=0;index <5;index ++){
			newQuestId = this.getRandomQuestId(role);
			if(newQuestId != 0 && newQuestId != questId){
				//最大试5次
				break ;
			}
		}
		if(newQuestId == questId){
			//任务相同只有不处理
			return result.success();
		}
		//接新任务
		QuestOpCode value = null ;
		try {
			value = GameContext.getUserQuestApp().acceptQuest(role, newQuestId);
		}catch (Exception ex){
			logger.error("refreshQuest acceptQuest error",ex);
			return result.setInfo(this.getText(TextId.Sys_Error));
		}
		if (QuestOpCode.success != value) {
			return result.setInfo(value.getInfo());
		}
		try {
			//放弃老任务
			GameContext.getUserQuestApp().giveUpQuest(role,questId);
		}catch (Exception ex){
			logger.error("refreshQuest giveUpQuest error",ex);
		}
		// 任务日志上要记录奖励ID
		RoleQuestLogInfo questLogInfo = role.getQuestLogInfo(newQuestId);
		questLogInfo.setAwardId(rolePoker.getTempAwardId());
		roleData.setQuestLogInfo(questLogInfo);
		//更新
		rolePoker.updateTempQuestId(newQuestId);
		// 主推面板消息
		this.sendMessage(role,
				this.buildQuestPokerPanelRespMessage(role, false));
		return result.success();
	}

	@Override
	public Result refresh(RoleInstance role) {
		Result result = new Result();
		try {
			// 判断vip等级
			int openVipLevel = GameContext.getVipApp().getOpenVipLevel(
					VipPrivilegeType.REFRESH_QUEST_POKER.getType(), "");
			int roleVipLevel = GameContext.getVipApp().getVipLevel(role);
			if (roleVipLevel < openVipLevel) {
				result.setInfo(GameContext.getI18n().messageFormat(
						TextId.VIP_FUNC_OPEN_NEED_VIPLEVEL,
						String.valueOf(openVipLevel)));
				return result;
			}
			QuestPokerRoleData roleData = this.getQuestPokerRoleData(role
					.getRoleId());
			// 身上有此类型的随机任务，只能先提交或放弃，才能刷新任务
			RoleQuestLogInfo logInfo = roleData.getQuestLogInfo();
			if (null != logInfo) {
				return result
						.setInfo(this.getText(TextId.Quest_Poker_Underway));
			}
			RoleQuestPoker rolePoker = roleData.getRoleQuestPoker();
			if (!rolePoker.hasTempQuestPoker()) {
				return result.setInfo(this
						.getText(TextId.Quest_Poker_Param_Error));
			}
			// 判断金钱是否足够
			int goldMoney = this.baseConfig.getRefreshGold();
			// 【游戏币/潜能/钻石不足弹板】 判断
			Result ar = GameContext.getUserAttributeApp().getEnoughResult(role,
					AttributeType.goldMoney, goldMoney);
			if (ar.isIgnore()) {
				return ar;
			}
			if (!ar.isSuccess()) {
				return result.setInfo(this
						.getText(TextId.Quest_Poker_Gold_Not_Enouth));
			}
			// if(role.getGoldMoney() < goldMoney){
			// return
			// result.setInfo(this.getText(TextId.Quest_Poker_Gold_Not_Enouth));
			// }
			// 随机一个任务
			int questId = this.getRandomQuestId(role);
			// 随机一个奖励
			RmQuestAwardConfig award = this.getRandomAward(role);
			int awardId = 0;
			if (null != award) {
				awardId = award.getAwardId();
			}
			// 刷新随机扑克
			int pokerValue = this.refreshRandomPoker(rolePoker);
			// 修改任务、奖励、花色、数字
			rolePoker.updateTempQuestPoker(questId, awardId, pokerValue);
			// 扣钱
			GameContext.getUserAttributeApp().changeRoleMoney(role,
					AttributeType.goldMoney, OperatorType.Decrease, goldMoney,
					OutputConsumeType.quest_poker_refresh_consume);
			role.getBehavior().notifyAttribute();
			// 主推面板消息
			this.sendMessage(role,
					this.buildQuestPokerPanelRespMessage(role, true));
			return result.success();
		} catch (Exception e) {
			this.logger
					.error(this.getClass().getName() + ".refresh error: ", e);
			return result.setInfo(Status.Sys_Error.getTips());
		}
	}

	@Override
	public Result accept(RoleInstance role) {
		Result result = new Result();
		try {
			Result res = this.checkCondition(role);
			// 不符合条件
			if (!res.isSuccess()) {
				return res;
			}
			QuestPokerRoleData roleData = this.getQuestPokerRoleData(role
					.getRoleId());
			// 身上有任务，不能接
			if (null != roleData.getQuestLogInfo()) {
				return result
						.setInfo(this.getText(TextId.Quest_Poker_Underway));
			}
			// 判断是否达到最大次数
			RoleQuestPoker rolePoker = roleData.getRoleQuestPoker();
			if (!rolePoker.hasTempQuestPoker()) {
				return result.setInfo(Status.Sys_Param_Error.getTips());
			}
			if (null != rolePoker) {
				if (rolePoker.getCurrCount(role) >= this.getTotalTime(role)) {
					return result.setInfo(this
							.getText(TextId.Quest_Poker_Max_Count));
				}
			}
			int questId = rolePoker.getTempQuestId();
			Quest quest = GameContext.getQuestApp().getQuest(questId);
			if (null == quest) {
				return result.setInfo(this.getText(TextId.Quest_Not_Exist));
			}
			QuestOpCode value = GameContext.getUserQuestApp().acceptQuest(role,
					questId);
			if (QuestOpCode.success != value) {
				return result.setInfo(value.getInfo());
			}
			// 更新数据库
			roleData.updateRolePokerForAccept();
			// 任务日志上要记录奖励ID
			RoleQuestLogInfo questLogInfo = role.getQuestLogInfo(questId);
			questLogInfo.setAwardId(rolePoker.getTempAwardId());
			// 添加随机任务的任务日志（单独保存）
			roleData.setQuestLogInfo(questLogInfo);
			return result.success();
		} catch (Exception e) {
			this.logger.error(this.getClass().getName() + ".accept error: ", e);
			return result.setInfo(Status.Sys_Error.getTips());
		}
	}

	/**
	 * 验证随机任务的条件
	 * 
	 * @param role
	 * @return
	 */
	private Result checkCondition(RoleInstance role) {
		Result result = new Result();
		int roleLevel = role.getLevel();
		if (roleLevel < this.baseConfig.getMinLevel()
				|| roleLevel > this.baseConfig.getMaxLevel()) {
			return result.setInfo(this
					.getText(TextId.Quest_Poker_Level_Not_Suit));
		}
		return result.success();
	}

	@Override
	public Result submit(RoleInstance role) {
		Result result = new Result();
		try {
			QuestPokerRoleData roleData = this.getQuestPokerRoleData(role
					.getRoleId());
			// 是否有随机任务
			RoleQuestLogInfo questLogInfo = roleData.getQuestLogInfo();
			if (null == questLogInfo) {
				return result.setInfo(this
						.getText(TextId.Quest_Poker_No_Submit_Quest));
			}
			int questId = questLogInfo.getQuestId();
			QuestOpCode value = GameContext.getUserQuestApp().submitQuest(role,
					questId);
			if (QuestOpCode.success != value) {
				return result.setInfo(value.getInfo());
			}
			roleData.completeQuest(role);
			// 活跃度
			GameContext.getDailyPlayApp().incrCompleteTimes(role, 1,
					DailyPlayType.quest_poker, "");
			// 刷新红点提示
			if (!GameContext.getActiveApp().hasHint(role)) {
				GameContext.getHintApp().hintChange(role, HintType.active, false);
			}
			return result.success();
		} catch (Exception e) {
			this.logger.error(this.getClass().getName() + ".submit error: ", e);
			return result.setInfo(Status.Sys_Error.getTips());
		}
	}

	@Override
	public int onLogin(RoleInstance role, Object context) {
		try {
			String roleId = role.getRoleId();
			QuestPokerRoleData roleData = new QuestPokerRoleData();
			roleData.setRoleId(roleId);
			this.roleDataMap.put(roleId, roleData);
			// 初始化未完成任务
			this.initRoleQuestLogInfo(role, roleData);
			// 查询
			RoleQuestPoker rolePoker = this.baseDAO.selectEntity(
					RoleQuestPoker.class, RoleQuestPoker.ROLEID, roleId);
			if (null == rolePoker) {
				// 如果没有则创建一个
				rolePoker = new RoleQuestPoker();
				rolePoker.setRoleId(roleId);
				rolePoker.setSaveDbStateType(SaveDbStateType.Insert);
			} else {
				// 触发一次是否重置的判断
				rolePoker.getCurrCount(role);
			}
			roleData.setRoleQuestPoker(rolePoker);
		} catch (Exception e) {
			this.logger.error(this.getClass().getName() + ".login error: ", e);
			return 0;
		}

		return 1;
	}

	private void initRoleQuestLogInfo(RoleInstance role,
			QuestPokerRoleData roleData) {
		try {
			for (RoleQuestLogInfo info : role.getQuestLogMap().values()) {
				if (null == info) {
					continue;
				}
				Quest quest = GameContext.getQuestApp().getQuest(
						info.getQuestId());
				if (null == quest) {
					continue;
				}
				// 找到未完成的随机任务
				QuestAcceptType acceptType = quest.getQuestAcceptType();
				if (QuestAcceptType.Poker == acceptType) {
					roleData.setQuestLogInfo(info);
					break;
				}
			}
		} catch (Exception e) {
			this.logger.error(this.getClass().getName()
					+ ".initRoleQuestLogInfo error: ", e);
		}
	}

	@Override
	public int onLogout(RoleInstance role, Object context) {
		try {
			String roleId = role.getRoleId();
			QuestPokerRoleData roleData = this.roleDataMap.remove(roleId);
			if(null == roleData){
				return 0 ;
			}
			RoleQuestPoker rolePoker = roleData.getRoleQuestPoker();
			if (SaveDbStateType.Insert == rolePoker.getSaveDbStateType()) {
				if (rolePoker.isModified()) {
					// 修改过的才需要入库
					this.baseDAO.insert(rolePoker);
				}
			} else if (SaveDbStateType.Update == rolePoker.getSaveDbStateType()) {
				this.baseDAO.update(rolePoker);
			}
		} catch (Exception e) {
			this.logger.error(this.getClass().getName() + ".logout error: ", e);
			return 0;
		}

		return 1;
	}

	@Override
	public int onCleanup(String roleId, Object context) {
		this.roleDataMap.remove(roleId);
		return 0;
	}

	@Override
	public QuestPokerRoleData getQuestPokerRoleData(String roleId) {
		return this.roleDataMap.get(roleId);
	}

	@Override
	public RmQuestAwardConfig getRmQuestAwardConfig(int awardId) {
		return this.awardConfigMap.get(awardId);
	}

	@Override
	public RoleQuestLogInfo getCurrPokerQuestLog(RoleInstance role) {
		QuestPokerRoleData roleData = this.getQuestPokerRoleData(role
				.getRoleId());
		return roleData.getQuestLogInfo();
	}

	private RmQuestWeight getRmQuestWeight(int level) {
		return this.questWeightMap.get(level);
	}

	private RmQuestConfig getRmQuestConfig(int setId) {
		return this.questConfigMap.get(setId);
	}

	private RmQuestAwardWeight getRmQuestAwardWeight(int level) {
		return this.awardWeightMap.get(level);
	}

	private int getRandomQuestId(RoleInstance role) {
		int level = role.getLevel();
		RmQuestWeight weight = this.getRmQuestWeight(level);
		if (null == weight) {
			this.logger.error(this.getClass().getName()
					+ ".getRandomQuestId error: RmQuestWeight is null.");
			return 0;
		}
		int setId = weight.getRandomSetId();
		RmQuestConfig config = this.getRmQuestConfig(setId);
		if (null == config) {
			this.logger.error(this.getClass().getName()
					+ ".getRandomQuestId error: RmQuestConfig is null.");
			return 0;
		}
		return config.getRandomQuestId();
	}

	private RmQuestAwardConfig getRandomAward(RoleInstance role) {
		int level = role.getLevel();
		RmQuestAwardWeight weight = this.getRmQuestAwardWeight(level);
		if (null == weight) {
			this.logger.error(this.getClass().getName()
					+ ".getRandomQuestId error: RmQuestAwardWeight is null.");
			return null;
		}
		int awardId = weight.getRandomAwardId();
		return this.getRmQuestAwardConfig(awardId);
	}

	@Override
	public String getDescribe() {
		return this.baseConfig.getDescribe();
	}

	public PokerThirdWeightConfig getPokerThirdWeightConfig(PokerTwoType twoType) {
		if (null == twoType) {
			return null;
		}
		int key = twoType.getType();
		return this.thirdWeighMap.get(key);
	}

	@Override
	public Map<Integer, Integer> getPokerThirdWeightMap(PokerTwoType twoType) {
		PokerThirdWeightConfig config = this.getPokerThirdWeightConfig(twoType);
		return config.getWeightMap();
	}

	@Override
	public Map<Integer, Integer> getPokerSecondWeightMap() {
		return this.secondWeightMap;
	}

	@Override
	public void sendThreePokerReward(RoleInstance role, int pokerVal1,
			int pokerVal2, int pokerVal3) {
		try {
			QuestPokerAwardConfig config = this.getQuestPokerAwardConfig(role
					.getLevel());
			if (null == config) {
				return;
			}
			PokerThreeType threeType = PokerHelper.getPokerThreeType(pokerVal1,
					pokerVal2, pokerVal3);
			QuestPokerAwardRatioConfig ratio = this
					.getQuestPokerAwardRatioConfig(threeType);
			if (null == ratio) {
				return;
			}
			int exp = (int) (config.getExp() * ratio.getRealRatio());
			GameContext.getUserAttributeApp().changeAttribute(role,
					AttributeType.exp, OperatorType.Add, exp,
					OutputConsumeType.quest_poker_finish);
			role.getBehavior().notifyAttribute();
		} catch (Exception e) {
			this.logger
					.error(this.getClass().getName()
							+ ".sendThreePokerReward error: RmQuestAwardWeight is null.");
		}
	}

	private QuestPokerAwardConfig getQuestPokerAwardConfig(int level) {
		return this.pokerAwardMap.get(level);
	}

	private QuestPokerAwardRatioConfig getQuestPokerAwardRatioConfig(
			PokerThreeType threeType) {
		if (null == threeType) {
			return null;
		}
		int type = threeType.getType();
		return this.pokerAwardRatioMap.get(type);
	}

	@Override
	public Collection<QuestPokerAwardRatioConfig> getPokerAwardRatioList() {
		return this.pokerAwardRatioMap.values();
	}

	@Override
	public void giveUpPokerQuest(RoleInstance role) {
		try {
			QuestPokerRoleData roleData = this.getQuestPokerRoleData(role
					.getRoleId());
			if (null == roleData) {
				return;
			}
			RoleQuestLogInfo info = roleData.getQuestLogInfo();
			if (null == info) {
				return;
			}
			int questId = info.getQuestId();
			GameContext.getUserQuestApp().giveUpQuest(role, questId);
			roleData.setQuestLogInfo(null);
		} catch (Exception e) {
			this.logger.error(this.getClass().getName()
					+ ".giveUpPokerQuest error: ", e);
		}
	}

	@Override
	public void onQuestPokerDataReset(RoleInstance role, RoleQuestPoker poker) {
		// 判断是否昨天
		Date yesterday = DateUtil.addDayToDate(new Date(), -1);
		int count = poker.getCount();
		int buyNum = poker.getBuyNum();
		if (!DateUtil.sameDay(poker.getUpdateTime(), yesterday)) {
			// 昨天以前的记录,昨天没有参加任何活动
			count = 0;
			buyNum = 0;
		}
		int remainCount = this.getMaxConfigCount() - count + buyNum;
		if (remainCount <= 0) {
			return;
		}
		GameContext.getRecoveryApp().saveDailyQuestRecovery(role, remainCount);
	}

	@Override
	public Result buyCountTime(RoleInstance role, byte confirm) {
		Result result = new Result().failure();
		try {
			QuestPokerRoleData roleData = this.getQuestPokerRoleData(role
					.getRoleId());

			if (!canBuyRoundTime(role)) {
				if (needUpgradeVipLevel(role)) {// 需要提升等级
					return result
							.setInfo(getText(TextId.QUEST_POKER_DESC_VIP_LIMIT));
				}
				return result.setInfo(getText(TextId.QUEST_POKER_DESC_CLEAR));// //每日任务已经全部完成，请明天再来,已经到达最大等级
			}

			// 判断消耗
			int money = getNextBuyCountConsumption(role);
			if (money <= 0) {
				return result.setInfo(getText(TextId.Quest_Poker_Param_Error));// 消耗不对
			}
			AttributeType attr = AttributeType.goldMoney;
			// 二次确认
			if (confirm == 0) {
				confirm = 1;
				String BuyTips = GameContext.getI18n().messageFormat(
						TextId.QUEST_POKER_BUY_COUNT_CONFIRM, attr.getName(),
						money);
				Message notifyMsg = QuickCostHelper.getMessage(role,
						QUEST_POKER_BUY_COUNT_CMDID, "" + confirm, (short) 0,
						"", money, 0, BuyTips);
				role.getBehavior().sendMessage(notifyMsg);
				result.setIgnore(true);
				return result;
			}
			// 【游戏币/潜能/钻石不足弹板】 判断
			Result ar = GameContext.getUserAttributeApp().getEnoughResult(role,
					attr, money);
			if (ar.isIgnore()) {
				return ar;
			}
			if (!ar.isSuccess()) {
				return ar.setInfo(ar.getInfo());
			}
			GameContext.getUserAttributeApp().changeRoleMoney(role, attr,
					OperatorType.Decrease, money,
					OutputConsumeType.quest_poker_buy_count_consume);
			// 通知用户属性变化
			role.getBehavior().notifyAttribute();

			// 更新购买次数
			RoleQuestPoker rolePoker = roleData.getRoleQuestPoker();
			rolePoker.setBuyNum(rolePoker.getBuyNum() + 1);
			if (SaveDbStateType.Insert == rolePoker.getSaveDbStateType()) {
				if (rolePoker.isModified()) {
					// 修改过的才需要入库
					this.baseDAO.insert(rolePoker);
				}
			} else if (SaveDbStateType.Update == rolePoker.getSaveDbStateType()) {
				this.baseDAO.update(rolePoker);
			}

			result.setResult(Result.SUCCESS);
			return result;
		} catch (Exception e) {
			this.logger.error(this.getClass().getName()
					+ ".buyCountTime error: ", e);
			return result;
		}
	}

	private int getBuyCountConsumption(RoleInstance role, int buyTime) {
		if (!buyConsumeConfigMap.containsKey(buyTime)) {
			return 0;
		}
		return buyConsumeConfigMap.get(buyTime).getDiamond();
	}

	private int getNextBuyCountConsumption(RoleInstance role) {
		if (!canBuyRoundTime(role)) {
			return 0;
		}
		int buyTime = getBuyTime(role);
		return getBuyCountConsumption(role, buyTime + 1);
	}
	
}
