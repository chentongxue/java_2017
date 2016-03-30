package com.game.draco.app.luckybox;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.count.type.CountType;
import sacred.alliance.magic.app.goods.GoodsOperateBean;
import sacred.alliance.magic.app.goods.behavior.result.AddGoodsBeanResult;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.BindingType;
import sacred.alliance.magic.base.OperatorType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.base.XlsSheetNameType;
import sacred.alliance.magic.constant.RespTypeStatus;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.domain.RoleCount;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.util.ConsumeByLevel;
import sacred.alliance.magic.util.DateUtil;
import sacred.alliance.magic.util.KeySupport;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.PiecewiseWrapper;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.util.XlsPojoUtil;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.hint.vo.HintType;
import com.game.draco.app.luckybox.config.LuckyBoxAppConfig;
import com.game.draco.app.luckybox.config.LuckyBoxDiamandsConsumeConfig;
import com.game.draco.app.luckybox.config.LuckyBoxRefreshConfig;
import com.game.draco.app.luckybox.config.LuckyBoxRewardConfig;
import com.game.draco.app.mail.type.MailSendRoleType;
import com.game.draco.app.vip.type.VipPrivilegeType;
import com.game.draco.message.item.AttriTypeValueItem;
import com.game.draco.message.item.GoodsLiteItem;
import com.game.draco.message.item.GoodsLiteNamedItem;
import com.game.draco.message.item.LuckeyBoxItem;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.response.C1915_LuckyBoxDisplayRespMessage;
import com.game.draco.message.response.C1916_LuckyBoxPlayRespMessage;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * 每个玩家每一轮转盘免费转一次，是VIP的话会有相应的收费次数。 每个玩家默认开始三轮，在整个游戏周期中每隔3个小时增加一次
 */
public class LuckyBoxAppImpl implements LuckyBoxApp {
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	public final static int NORMAL_AWARD_POOL_SIZE = 7;
	private final static int AWARD_POOL_SIZE = 8; // 物品总数目
	private final static int AWARD_TYPE_GOODS = 1;
	private final static int AWARD_TYPE_ATTRIBUTE = 2;
	// 除了幸运券，角色身上最多累计的总数轮数
	private final static int MAX_COUNT_NUM = 10;
	// cache, <K,V> : 角色Id,每日的当前轮8个奖品
	private Map<String, LinkedHashMap<String, LuckyBoxPoolItem>> roleAwardPoolcacheMap = Maps.newConcurrentMap();
	private LuckyBoxAppConfig luckyBoxAppConfig = new LuckyBoxAppConfig();

	// 付费消耗 <K,V> : 付费玩的次数,消耗
	private Map<String, LuckyBoxDiamandsConsumeConfig> luckyBoxConsumeMap = Maps
			.newHashMap();

	//【new】幸运转盘
	private Map<String, LuckyBoxRewardConfig> luckyBoxRewardConfigMap = Maps.newLinkedHashMap();
	private Map<String, Integer> mustRewardOddsMap = Maps.newHashMap();
	private Map<String, Integer> rewardOddsMap = Maps.newHashMap();
	// 刷新时间
	private List<LuckyBoxRefreshConfig> refreshConfigList = Lists
			.newArrayList();

	// 玩家的奖励列表 roleId,List<Awards>
	private Map<String, List<LuckyBoxPoolItem>> awardMap = Maps.newConcurrentMap(); 
	
	//第一次转消耗游戏币
	private PiecewiseWrapper<ConsumeByLevel> levelConsume = null ;
	@Override
	public void setArgs(Object arg0) {

	}

	@Override
	public void start() {
		this.loadLuckyBoxAppConfig();
		this.loadLuckyBoxRefreshConfigList();
		this.loadLuckyBoxRewardConfig();
		this.loadDiamandsConsumeConfig();
		this.buildMustRewardOddMap();
		this.buildRewardOddMap();
		//加载接任务的消耗
		this.levelConsume = XlsPojoUtil.createPiecewiseWrapper(
				XlsSheetNameType.LuckyBoxConsumeConfig,ConsumeByLevel.class);
	}

	private int getLevelConsume(int level){
		ConsumeByLevel consume = this.levelConsume.getOrMax(level);
		return (null == consume)?0:consume.getConsumeValue() ;
	}
	
	@Override
	public void stop() {

	}

	private void loadLuckyBoxRewardConfig() {
		luckyBoxRewardConfigMap = loadConfigMap(
				XlsSheetNameType.LuckyBoxRewardPoolConfig,
				LuckyBoxRewardConfig.class, true);

	}

	private void loadDiamandsConsumeConfig() {
		luckyBoxConsumeMap = loadConfigMap(
				XlsSheetNameType.LuckyBoxDiamandsConsumeConfig,
				LuckyBoxDiamandsConsumeConfig.class, false);

	}

	// 读取并排序
	private void loadLuckyBoxRefreshConfigList() {
		refreshConfigList = loadConfigList(
				XlsSheetNameType.LuckyBoxRefreshConfig,
				LuckyBoxRefreshConfig.class);
		for (LuckyBoxRefreshConfig cf : refreshConfigList) {
			cf.init();
		}
		Collections.sort(refreshConfigList);
	}

	// 验证 幸运券、碎片两者的物品Id
	private void loadLuckyBoxAppConfig() {
		String fileName = XlsSheetNameType.LuckyBoxAppConfig.getXlsName();
		String sheetName = XlsSheetNameType.LuckyBoxAppConfig.getSheetName();
		String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
		luckyBoxAppConfig = XlsPojoUtil.getEntity(sourceFile, sheetName,
				LuckyBoxAppConfig.class);
		if (luckyBoxAppConfig.getDefaultRoundsCount() <= 0) {
			Log4jManager.CHECK.error("loadLuckyBoxAppConfig failed "
					+ luckyBoxAppConfig.getClass().getSimpleName() + " ,file="
					+ sourceFile + " sheet=" + sheetName);
			Log4jManager.checkFail();
		}
	}

	public void buildMustRewardOddMap() {
		int poolId = luckyBoxAppConfig.getMustAwardPoolId();
		mustRewardOddsMap = buildOddsMap(mustRewardOddsMap, poolId);
	}

	public void buildRewardOddMap() {
		int poolId = luckyBoxAppConfig.getNormalAwardPoolId();
		rewardOddsMap = buildOddsMap(rewardOddsMap, poolId);
	}

	// 取一个
	private String getMustReward() {
		String key = Util.getLuckyDrawUnique(1, mustRewardOddsMap).get(0);
		return key;
	}

	private List<String> getNormalRewards() {
		List<String> keys = Util.getLuckyDrawUnique(NORMAL_AWARD_POOL_SIZE,
				rewardOddsMap);
		return keys;
	}
	//将第一个奖励的Place设置为1
	private List<String> getFirstCountRewards() {
		List<String> keys = Lists.newArrayList();
		int poolId = luckyBoxAppConfig.getFirstCountPoolId();
		for (LuckyBoxRewardConfig cf : luckyBoxRewardConfigMap.values()) {
			if (cf.getPoolId() == poolId)
				keys.add(cf.getKey());
		}
		return keys;
	}

	private Map<String, Integer> buildOddsMap(Map<String, Integer> map,
			int poolId) {
		for (LuckyBoxRewardConfig cf : luckyBoxRewardConfigMap.values()) {
			if (cf.getPoolId() == poolId)
				map.put(cf.getKey(), cf.getOdds());
		}
		return map;
	}

	private <T> List<T> loadConfigList(XlsSheetNameType xls, Class<T> t) {
		List<T> list = null;
		String fileName = xls.getXlsName();
		String sheetName = xls.getSheetName();
		String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
		try {
			list = XlsPojoUtil.sheetToList(sourceFile, sheetName, t);
		} catch (Exception e) {
			Log4jManager.CHECK.error("load " + t.getSimpleName() + " error:fileName="
							+ fileName + ",sheetName=" + sheetName);
			Log4jManager.checkFail();
		}
		if (list == null) {
			Log4jManager.CHECK.error("load " + t.getSimpleName()
					+ " error: result is null fileName=" + fileName
					+ ",sheetName=" + sheetName);
			Log4jManager.checkFail();
		}
		return list;
	}

	private <K, V extends KeySupport<K>> Map<K, V> loadConfigMap(
			XlsSheetNameType xls, Class<V> clazz, boolean linked) {
		String fileName = xls.getXlsName();
		String sheetName = xls.getSheetName();
		String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
		Map<K, V> map = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName,
				clazz, linked);
		if (Util.isEmpty(map)) {
			Log4jManager.CHECK.error("not config the " + clazz.getSimpleName()
					+ " ,file=" + sourceFile + " sheet=" + sheetName);
			Log4jManager.checkFail();
		}
		return map;
	}

	// 得到下一次刷新时间的倒计时(分钟)
	private int getRefreshTimeSecounds(RoleInstance role) {
		Date now = new Date();
		for (LuckyBoxRefreshConfig c : refreshConfigList) {
			if (beforeFreshTime(now, c)) {
				return getCountDownSeconds(now, c);
			}
		}
		// never happen
		return 0;
	}

	/**
	 * @param d 验证时间
	 * @param c  参考时间
	 * @return true：d比c早，false：c与d相等或比d早
	 */
	public boolean beforeFreshTime(Date d, LuckyBoxRefreshConfig c) {
		int hour = DateUtil.getHour(d);
		int minutes = DateUtil.getMinutes(d);
		if (hour != c.getHour()) {
			return hour < c.getHour();
		}
		return minutes < c.getMinutes();
	}

	// 返回时间差
	public int getCountDownSeconds(Date d, LuckyBoxRefreshConfig c) {
		int hour = DateUtil.getHour(d);
		int minutes = DateUtil.getMinutes(d);
		int ss = DateUtil.getSeconds(d);
		return ((c.getHour() - hour) * 60 + c.getMinutes() - minutes) * 60 -ss;
	}

	/**
	 * 第一轮产生固定的8个奖励 每隔3个小时增加一次刷新次数 打开幸运宝箱面板,refreshFlag = 1时获得下一轮幸运宝箱
	 * ①取得rolecount,获得存贮的已用轮次，奖号-数量Map，奖号-开启位置Map
	 * ②根据roleId取当日轮次缓存，取不到则从roleCount信息恢复。得到当前轮奖池（1-8） |---④存贮,同步roleCount
	 * ③生成Message
	 */
	@Override
	public Message openLuckyBoxPanel(RoleInstance role, byte refreshFlag) {
		Map<String, LuckyBoxPoolItem> luckyBoxItemPoolMap = Maps
				.newLinkedHashMap();
		
		String roleId = role.getRoleId();
		int vipTimes = getVipTimes(roleId);// luckyBoxVipTimesMap.get(vipLevel+"").getVipTimes();
		int roundsLimit = 0;
		// 每三个小时增加一次
		RoleCount rc = role.getRoleCount();
		roundsLimit = getRefreshTimes(role, rc);//只有在这里增加轮数
		if(isFirstOpenLuckyBox(rc)){
			refreshFlag = 1;
		}
		// 免费券的物品ID
		int luckyTicketGoodsId = luckyBoxAppConfig.getLuckyTicketgoodsId();
		// 已经打开箱子的个数
		int openedBoxTimes = getOpenedBoxTimes(role);
		if (refreshFlag == 1) {// 1为见好就收
			if (roundsLimit <= 0) {
				boolean hasTickets = GameContext.getUserGoodsApp()
						.isExistGoodsForBag(role, luckyTicketGoodsId);
				if (!hasTickets) {// 无幸运券
					return getRoundsNotEnoughMessage(roundsLimit);
				}
				GameContext.getUserGoodsApp().deleteForBag(role, luckyTicketGoodsId, 1, OutputConsumeType.luckybox_consume);
			}else{
				roundsLimit --; //只有在这里减轮数
			}
			saveCacheMap2RoleCountTime(role, roundsLimit , new Date());
			openedBoxTimes = 0;
		}
		// 还可以打开的箱子个数
		int openableTimes = 0;
		openableTimes = vipTimes + luckyBoxAppConfig.getDefaultOpenTimes() - openedBoxTimes;
		// 根据roleId取当前轮奖池(取不到则从role恢复)
		luckyBoxItemPoolMap = this.generateRoleAwardPoolCacheMap(role, refreshFlag);
		// 需要排序
		List<LuckeyBoxItem> luckeyBoxItem4MessageList = buildLuckeyBoxItem4MessageList(luckyBoxItemPoolMap);
		String consumeInfo = "";
		int diamonds = getConfigConsume(openedBoxTimes + 1);
		if (diamonds != 0) {
			consumeInfo = MessageFormat.format(luckyBoxAppConfig
					.getConsumeDiamondsInfo(), diamonds);
		}
		int silverMoney = getLevelConsume(role.getLevel());
		C1915_LuckyBoxDisplayRespMessage msg = new C1915_LuckyBoxDisplayRespMessage();
		// 消耗描述
		msg.setConsumeInfo(consumeInfo);
		// 消耗的钻石数量
		msg.setDiamonds(diamonds);//
		msg.setLuckeyBoxItemList(luckeyBoxItem4MessageList);
		msg.setOpenableTimes((byte)openableTimes);
		msg.setRemainTimes(roundsLimit);
		msg.setGameMoney(silverMoney);
		msg.setOpenedBoxTimes((byte)openedBoxTimes);
		// 免费券
		GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(
				luckyTicketGoodsId);
		if (gb == null) {
			return msg;
		}
		GoodsLiteNamedItem ticket = gb.getGoodsLiteNamedItem();
		List<RoleGoods> tickets = GameContext.getUserGoodsApp()
				.getRoleGoodsForBag(role, luckyTicketGoodsId);
		int ticketNum = Util.isEmpty(tickets) ? 0 : tickets.size();
		ticket.setNum((short) ticketNum);
		msg.setTicket(ticket);

		// 倒计时
		int countDownSeconds = getRefreshTimeSecounds(role);
		msg.setCountDownSecounds(countDownSeconds);
		// 碎片ID
		msg.setFragmentGoodsId(luckyBoxAppConfig.getFragmentGoodsId());
		// 兑换ID
		msg.setExchangeId(luckyBoxAppConfig.getExchangeId());
		return msg;
	}
	private boolean isFirstOpenLuckyBox(RoleCount rc) {
		return rc.getRoleTimesToInt(CountType.LuckyBoxUsedTimes) == 0;//getLuckyBoxUsedTimes() == 0;
	}

	//更新刷新出来的轮次
	@Override
	public int getRefreshTimes(RoleInstance role, RoleCount rc) {
		int recordTimes = rc.getRoleTimesToInt(CountType.LuckyBoxRefreshTimes);
		Date lastTime = rc.getRoleTimesToDate(CountType.LuckyBoxLastOpenTime,null);
		Date now = new Date();
		int addTimes;
		addTimes = addRefreshTimes(now, lastTime);
		int roundsLeft = Util.isEmpty(rc.getRoleTimesToString(CountType.LuckyBoxCountJsonStr)) ? luckyBoxAppConfig.getDefaultRoundsCount():0;
		//最高不超过十次
		roundsLeft = Math.min((recordTimes + addTimes + roundsLeft), MAX_COUNT_NUM);
		if(roundsLeft <= 0){
			return 0;
		}
		// 添加到RoleCount
		return roundsLeft;
	}
	/**
	 * 每隔三个小时增加一次
	 * 如果是同一天，按照表格计算
	 * 如果不是同一天，按照分钟计算
	 */
	private int addRefreshTimes(Date now, Date lastTime) {
		if(lastTime == null){
			return MAX_COUNT_NUM;
		}
		int addTimes = 0;
		if(DateUtil.sameDay(now, lastTime)){
			 for (LuckyBoxRefreshConfig c : refreshConfigList) {
			 if(!beforeFreshTime(now, c) && beforeFreshTime(lastTime, c)){
				 addTimes ++;
			 	}
			 }
			 return addTimes;
		}
		return (int)DateUtil.dateDiffMinute(now, lastTime)/180;
	}


	private Message getRoundsNotEnoughMessage(int roundsLimit) {
		String info = GameContext.getI18n().getText(
				TextId.LUCKYBOX_ROUND_LEFT_NONE);
		return new C0003_TipNotifyMessage(info);
	}

	/**
	 * 生成角色当日奖池，并同步缓存以及roleCount
	 * @param role
	 * @return
	 */
	private Map<String, LuckyBoxPoolItem> generateRoleAwardPoolCacheMap(
			RoleInstance role, byte freshFlag) {
		//产生新一轮
		if (freshFlag == 1) {
			return generateNewRoleAwardPoolCacheMap(role);
		}
		//从缓存取
		LinkedHashMap<String, LuckyBoxPoolItem> luckyBoxItemPoolMap = roleAwardPoolcacheMap
				.get(role.getRoleId());
		if (luckyBoxItemPoolMap != null
				&& luckyBoxItemPoolMap.size() == AWARD_POOL_SIZE) {
			return roleAwardPoolcacheMap.get(role.getRoleId());
		// 从roleCount里恢复
		} 
		RoleCount rc = role.getRoleCount();
		String luckyBoxCountStr = rc.getRoleTimesToString(CountType.LuckyBoxCountJsonStr);//getLuckyBoxCountJsonStr();
		String luckyBoxPlaceStr = rc.getRoleTimesToString(CountType.LuckyBoxPlaceJsonStr);//getLuckyBoxPlaceJsonStr();
		Map<String, Integer> countMap = getRoleCountMap(luckyBoxCountStr);
		Map<String, Integer> placeMap = getRoleCountMap(luckyBoxPlaceStr);// 是否开出
		luckyBoxItemPoolMap = Maps.newLinkedHashMap();// 顺序
		if (!Util.isEmpty(countMap)) {
			int i = 1;
			for (Map.Entry<String, Integer> entry : countMap.entrySet()) {
				String awardKey = entry.getKey();
				int num = entry.getValue();
				Integer coorInteger = placeMap.get(awardKey);// ->判断空
				int coordinate = coorInteger == null ? 0 : coorInteger;
				LuckyBoxPoolItem boxItem = buildLuckyBoxItem((byte) i, num,
						awardKey, (byte) coordinate);
				luckyBoxItemPoolMap.put(awardKey, boxItem);
				i++;
			}
		} else {//never happen
			return generateNewRoleAwardPoolCacheMap(role);
		}
		return luckyBoxItemPoolMap;
	}

	/**
	 * 生成新的角色当日奖池，并同步缓存以及roleCount
	 * @param role
	 * @return
	 */
	private Map<String, LuckyBoxPoolItem> generateNewRoleAwardPoolCacheMap(
			RoleInstance role) {

		LinkedHashMap<String, LuckyBoxPoolItem> luckyBoxItemPoolMap = Maps.newLinkedHashMap();// 顺序
		Map<String, Integer> countMap = Maps.newLinkedHashMap();

		String awardKey;
		LuckyBoxPoolItem boxItem;
		// 如果是第一次玩转盘,从固定的奖励池里取固定的8个奖励,将第一个奖励的Place设置为1
		RoleCount rc = role.getRoleCount();
		if (isFirstOpenLuckyBox(rc)) {
			List<String> list = getFirstCountRewards();
			for (int i = 0; i < list.size(); i++) { // 1-8
				awardKey = list.get(i);
				boxItem = buildLuckyBoxItem((byte) (i + 1), (byte) 0, awardKey, (byte) 0);
				luckyBoxItemPoolMap.put(awardKey, boxItem);
				countMap.put(awardKey, boxItem.getNum());// ->null
			}
			addCacheRoleCount(role, luckyBoxItemPoolMap, countMap);
			return luckyBoxItemPoolMap;
		}
		// 必抽奖励
		String mustRewardKey = getMustReward();// new
		boxItem = buildLuckyBoxItem((byte) 1, (byte) 0, mustRewardKey, (byte) 0);// 可能返回空值
		luckyBoxItemPoolMap.put(mustRewardKey, boxItem);
		countMap.put(mustRewardKey, boxItem.getNum());
		// 注入普通奖励
		List<String> list = getNormalRewards();
		for (int i = 0; i < list.size(); i++) { // 2-8
			awardKey = list.get(i);
			boxItem = buildLuckyBoxItem((byte) (i + 2), (byte) 0, awardKey,
					(byte) 0);
			luckyBoxItemPoolMap.put(awardKey, boxItem);
			countMap.put(awardKey, boxItem.getNum());// ->null
		}
		addCacheRoleCount(role, luckyBoxItemPoolMap, countMap);
		return luckyBoxItemPoolMap;
	}

	private void addCacheRoleCount(RoleInstance role,
			LinkedHashMap<String, LuckyBoxPoolItem> luckyBoxItemPoolMap,
			Map<String, Integer> countMap) {
		// 加到缓存
		this.buildRoleAwardPoolcacheMap(role.getRoleId(), luckyBoxItemPoolMap);
		// 添加到RoleCount
		String luckyBoxCountJsonStr = Util.strIntMapToString(countMap);
		GameContext.getCountApp().setLuckyBoxCount(role, luckyBoxCountJsonStr, "");
	}
	/**
	 * 添加缓存
	 */
	private void buildRoleAwardPoolcacheMap(String roleId,
			LinkedHashMap<String, LuckyBoxPoolItem> luckyBoxItemPoolMap) {
		roleAwardPoolcacheMap.put(roleId, luckyBoxItemPoolMap);
	}

	/**
	 * 要发的奖池列表(注意既有物品也有属性)
	 * @param luckyBoxItemPoolMap
	 * @return
	 * @date 2014-4-11 下午07:39:06
	 */
	private List<LuckeyBoxItem> buildLuckeyBoxItem4MessageList(
			Map<String, LuckyBoxPoolItem> luckyBoxItemPoolMap) {
		List<LuckeyBoxItem> list4Message = Lists.newArrayList();
		for (LuckyBoxPoolItem it : luckyBoxItemPoolMap.values()) {
			LuckeyBoxItem item = build4messageItem(it);
			list4Message.add(item);
		}
		return list4Message;
	}

	/**
	 * @param it
	 * @return
	 * @date 2014-4-15 下午05:09:56
	 */
	private LuckeyBoxItem build4messageItem(LuckyBoxPoolItem it) {
		LuckeyBoxItem lbt = new LuckeyBoxItem();
		byte awardType = it.getAwardType();
		byte place = it.getPlace();
		lbt.setAwardType(it.getAwardType());// 设置类型
		lbt.setPlace(place);
		lbt.setOpenFlag(it.getOpenFlag());
		if (awardType == AWARD_TYPE_GOODS) {
			GoodsBase goodsBase = it.getAwardGoods();
			GoodsLiteItem item = goodsBase.getGoodsLiteItem();
			item.setBindType(it.getBind());
			item.setNum((short) it.getNum());
			lbt.setItem(item);
			return lbt;
		}
		AttriTypeValueItem attItem = new AttriTypeValueItem();
		byte attriType = (byte) it.getAwardId();
		attItem.setAttriType(attriType);
		attItem.setAttriValue(it.getNum());
		lbt.setAttItem(attItem);
		return lbt;
	}

	/**
	 * vipLevel和times，用于查找8个奖励的权重表,不设置权重
	 * @param place
	 * @param num
	 * @param awardKey
	 * @return
	 * @date 2014-4-15 上午11:55:52
	 */
	private LuckyBoxPoolItem buildLuckyBoxItem(byte place, int num,
			String awardKey, byte openFlag) {
		LuckyBoxRewardConfig boxRewardPoolConfig = getLuckyBoxRewardConfig(awardKey);
		if (boxRewardPoolConfig == null)
			return null;
		int awardId = boxRewardPoolConfig.getAwardId();
		byte awardType = boxRewardPoolConfig.getAwardType();
		byte bind = boxRewardPoolConfig.getBind();

		int min = boxRewardPoolConfig.getNumLower();
		int max = boxRewardPoolConfig.getNumUpper();
		if (num <= 0)
			num = Util.randomInt(min, max);
		LuckyBoxPoolItem boxItem = new LuckyBoxPoolItem();
		boxItem.setAwardId(awardId);
		boxItem.setAwardType(awardType);
		boxItem.setBind(bind);
		boxItem.setPlace(place);
		boxItem.setAwardKey(awardKey);
		boxItem.setNum(num);
		boxItem.setOpenFlag(openFlag);
		return boxItem;
	}

	private <T> LuckyBoxRewardConfig getLuckyBoxRewardConfig(T awardId) {
		String awardKey = String.valueOf(awardId);
		LuckyBoxRewardConfig boxRewardPoolConfig = luckyBoxRewardConfigMap.get(awardKey);
		return boxRewardPoolConfig;
	}

	/**
	 * 有序 roleCount
	 * @param rc
	 * @return
	 * @date 2014-4-11 下午06:24:43
	 */
	private Map<String, Integer> getRoleCountMap(String jsonStr) {
		LinkedHashMap<String, Integer> map = (LinkedHashMap<String, Integer>) Util
			.parseStringIntLinkedMap(jsonStr);
		if (map == null) {
			map = Maps.newLinkedHashMap();
		}
		return map;
	}

	/**
	 * 1916打开宝箱 验证位置是否合法，当日是否还有剩余次数，钻石是否足够
	 * 根据奖励池生成权重Map,打开宝箱,发奖励 0707 快速打开宝箱能发送两个奖品的问题
	 */
	@Override
	public Message playLuckyBox(RoleInstance role) {

		C1916_LuckyBoxPlayRespMessage msg = new C1916_LuckyBoxPlayRespMessage();
		// 生成的奖池1-8,每次生成一轮的时候生成,需要加在缓存
		Map<String, LuckyBoxPoolItem> luckyBoxItemPoolMap = null;
		RoleCount rc = role.getRoleCount();
		rc.resetDay();
		// 已经打开个数
		byte openedBoxTimes = getOpenedBoxTimes(role);
		
		// 第几次打开
		int openBoxTimes = openedBoxTimes + 1;
		if(openBoxTimes > AWARD_POOL_SIZE){
			msg.setType(RespTypeStatus.FAILURE);
			msg.setInfo(this.getText(TextId.Luckybox_Draw_End));
			return msg;
		}
		msg.setOpenedBoxTimes((byte)openBoxTimes);
		// 根据roleId取缓存，取不到则从roleCount恢复
		byte refreshFlag = 0;
		luckyBoxItemPoolMap = this.generateRoleAwardPoolCacheMap(role,refreshFlag);
		//每次抽取重置几率
		int vipTimes = getVipTimes(role.getRoleId());
		// 还可以打开多少
		int openableTimes = Math.min((luckyBoxAppConfig.getDefaultOpenTimes() + vipTimes),AWARD_POOL_SIZE) - openedBoxTimes;
		if (openableTimes <= 0) {
			msg.setType(RespTypeStatus.FAILURE);
			msg.setInfo(this
						.getText(TextId.Luckybox_OpenTimes_Not_Enough));
				return msg;
		}
		int openableTimesNext = openableTimes - 1;
		int feeTimes = 0;
		// 验证消耗是否满足
		int consume = 0;
		// 玩的第几次
		feeTimes = openBoxTimes;
		//第一次是金币消耗
		int silverMoney = getLevelConsume(role.getLevel());
		AttributeType attr = feeTimes > 1?AttributeType.goldMoney : AttributeType.gameMoney;
		//钻石消耗
		consume = feeTimes > 1?getConfigConsume(feeTimes):silverMoney;// luckyBoxDiamandsConsumeMap.get(feeTimes+"");
		if (consume > 0) {
			// 【游戏币/潜能/钻石不足弹板】 判断
			Result ar = GameContext.getUserAttributeApp().getEnoughResult(
					role, attr, consume);
			if (ar.isIgnore()) {
				return null;
			}
			if (!ar.isSuccess()) {
				msg.setType(RespTypeStatus.FAILURE);
				msg.setInfo(ar.getInfo());
				return msg;
			}
			GameContext.getUserAttributeApp().changeRoleMoney(role,
					attr, OperatorType.Decrease,
					consume, OutputConsumeType.luckybox_consume);
			// 通知用户属性变化 消耗提示
			role.getBehavior().notifyAttribute();
		}
		String consumeInfo = null;
		int nextConsume = 0;
		int nextFeeTimes = feeTimes + 1;
		consumeInfo = luckyBoxAppConfig.getConsumeDiamondsInfo();
		nextConsume = getConfigConsume(nextFeeTimes);
		//生成抽奖
		String drawAwardKey = this.getDrawResult(luckyBoxItemPoolMap,
				openBoxTimes,role);
		if (drawAwardKey == null) {
			this.logger.error("LuckyBoxApp.getLuckyBoxDraw error: ");
			msg.setType(RespTypeStatus.FAILURE);
			msg.setInfo(this.getText(TextId.Luckybox_Draw_Fail));
			return msg;
		}
		LuckyBoxPoolItem lbpItem = luckyBoxItemPoolMap.get(drawAwardKey);
		lbpItem.setOpenFlag((byte) 1);
		//抽奖结束后不发奖励，等待客户端请求发送
//		sendLuckyBoxAward(role, lbpItem);
		LuckeyBoxItem lukeyBoxItem = build4messageItem(lbpItem);
		putAwardMap(role.getRoleId(), lbpItem);
		msg.setDiamonds(nextConsume);
		msg.setConsumeInfo(MessageFormat.format(consumeInfo,
				nextConsume + ""));
		msg.setOpenableTimes((byte)openableTimesNext);
		msg.setLukeyBoxItem(lukeyBoxItem);
		msg.setType(RespTypeStatus.SUCCESS);
		// 添加到RoleCount
		saveCacheMap2RoleCount(role,luckyBoxItemPoolMap);
		// 刷新红点提示
		if (!GameContext.getActiveApp().hasHint(role)) {
			GameContext.getHintApp().hintChange(role, HintType.active, false);
		}
		return msg;
	}
	private void putAwardMap(String roleId, LuckyBoxPoolItem it) {
		if(Util.isEmpty(roleId)  || it == null){//never happen
			return;
		}
		List<LuckyBoxPoolItem> list = awardMap.get(roleId);
		if(list == null){
			list = Lists.newArrayList();
			awardMap.put(roleId, list);
		}
		list.add(it);
	}

	/**
	 * 发送奖励,直接添加到背包，背包满则发邮件
	 * @param role
	 * @param message
	 * @param lbpItem
	 * @date 2014-10-14 下午08:15:56
	 */
	private void sendLuckyBoxAward(RoleInstance role, LuckyBoxPoolItem lbpItem) {
		if(role == null || lbpItem == null){
			return;
		}
		boolean sendReslut = getSendLuckyBoxAwardResult(role, lbpItem);
		if (!sendReslut) {
			this.logger.error("LuckyBoxApp.getLuckyBoxDraw error: ");
			C0003_TipNotifyMessage msg = new C0003_TipNotifyMessage(this.getText(TextId.Luckybox_Draw_Fail));
			role.getBehavior().sendMessage(msg);
		}
		role.getBehavior().notifyAttribute();
	}

	// 发送奖励
	private boolean getSendLuckyBoxAwardResult(RoleInstance role,final LuckyBoxPoolItem lbpItem) {
		byte rewardType = lbpItem.getAwardType();
		if (rewardType == AWARD_TYPE_GOODS) {
			List<GoodsOperateBean> addList = new ArrayList<GoodsOperateBean>();
			GoodsOperateBean bean = new GoodsOperateBean();
			bean.setGoodsId(lbpItem.getAwardId());
			bean.setGoodsNum(lbpItem.getNum());
			bean.setBindType(BindingType.get(lbpItem.getBind()));
			addList.add(bean);
			// 直接发会弹物品
			AddGoodsBeanResult goodsResult = GameContext.getUserGoodsApp()
					.addSomeGoodsBeanForBag(role, addList,
							OutputConsumeType.luckybox_output);
			// 背包满了则发邮件
			List<GoodsOperateBean> putFailureList = goodsResult
					.getPutFailureList();
			try {
				if (!Util.isEmpty(putFailureList)) {
					String context = this.getText(TextId.Luckybox_Mail_Context);
					GameContext.getMailApp().sendMail(role.getRoleId(),
							MailSendRoleType.LuckyBox.getName(), context,
							MailSendRoleType.LuckyBox.getName(),
							OutputConsumeType.luckybox_mail_output.getType(),
							putFailureList);
					//发送广播
					C0003_TipNotifyMessage msg = new C0003_TipNotifyMessage(getText(TextId.Luckybox_REWARD_BY_EMAIL_TIPS));
					role.getBehavior().sendMessage(msg);
				}
				return true;
			} catch (Exception e) {
				logger.error("", e);
			}
		} else if (rewardType == AWARD_TYPE_ATTRIBUTE) {
			byte attrtType = (byte) lbpItem.getAwardId();
			AttributeType at = AttributeType.get(attrtType);
			if (null != at) {
				int rewardNumber = lbpItem.getNum();
				GameContext.getUserAttributeApp().changeAttribute(role, at,
						OperatorType.Add, rewardNumber,
						OutputConsumeType.luckybox_output);
				return true;
			}
		}
		return false;
	}

	/**
	 * 当前轮已经打开箱子的个数
	 */
	private byte getOpenedBoxTimes(RoleInstance role) {
		RoleCount rc = role.getRoleCount();
		String luckyBoxPlaceStr = rc.getRoleTimesToString(CountType.LuckyBoxPlaceJsonStr);//getLuckyBoxPlaceJsonStr();
		Map<String, Integer> placeMap = getRoleCountMap(luckyBoxPlaceStr);
		return (byte)placeMap.size();
	}

	/**
	 * 将缓存同步到RoleCount
	 * @param luckyBoxItemPoolMap
	 * @date 2014-4-12 下午06:04:02
	 */
	private void saveCacheMap2RoleCount(RoleInstance role, 
			Map<String, LuckyBoxPoolItem> luckyBoxItemPoolMap) {
		Map<String, Integer> countMap = Maps.newLinkedHashMap();
		Map<String, Integer> placeMap = Maps.newLinkedHashMap();
		// "2_2":3,"2_3":2
		for (Map.Entry<String, LuckyBoxPoolItem> entry : luckyBoxItemPoolMap
				.entrySet()) {
			String awardKey = entry.getKey();
			LuckyBoxPoolItem it = entry.getValue();
			countMap.put(awardKey, it.getNum() + 0);
			if (it.getOpenFlag() != 0) {
				placeMap.put(awardKey, (int) it.getOpenFlag());
			}
		}
		String luckyBoxCountStr = Util.strIntMapToString(countMap);
		String luckyBoxPlaceStr = Util.strIntMapToString(placeMap);
		GameContext.getCountApp().setLuckyBoxCount(role,
				luckyBoxCountStr, luckyBoxPlaceStr);
	}
	private void saveCacheMap2RoleCountTime(RoleInstance role, int leftTimes, Date luckyBoxLastOpenTime) {
		if(leftTimes <0){
			leftTimes = 0;
		}
		GameContext.getCountApp().setLuckyBoxTime(role, leftTimes, luckyBoxLastOpenTime);
	}

	/**
	 * 从奖池中抽奖
	 * @param luckyBoxItemPoolMap
	 * @return
	 * @date 2014-4-12 下午05:38:34
	 */
	private String getDrawResult(
			Map<String, LuckyBoxPoolItem> luckyBoxItemPoolMap, int openBoxTimes, RoleInstance role) {
		RoleCount rc = role.getRoleCount();
		if (openBoxTimes == 1 && isFirstOpenLuckyBox(rc)) {// 如果是第一次转转盘，则返回必出奖
			for (LuckyBoxPoolItem it : luckyBoxItemPoolMap.values()) {
				if (it.getPlace() == 1) {
					GameContext.getCountApp().setLuckyFirstUsed(role);
					return it.getAwardKey();
				}
			}
			return null;
		}
		Map<String, Integer> weightMap = Maps.newHashMap();
		for (Map.Entry<String, LuckyBoxPoolItem> entry : luckyBoxItemPoolMap
				.entrySet()) {
			String awardKey = entry.getKey();
			LuckyBoxPoolItem it = entry.getValue();
			if (it.getOpenFlag() == 0) {
				weightMap.put(awardKey, 1);
			}
		}
		if (Util.isEmpty(weightMap)) {
			return null;
		}
		List<String> list = Util.getLuckyDraw(1, weightMap);
		return list.get(0);
	}

	private String getText(String textId) {
		return GameContext.getI18n().getText(textId);
	}

	public int getRemainTimes(RoleInstance role) {
		if (luckyBoxAppConfig == null)
			return 0;
		RoleCount rc = role.getRoleCount();
		int times = rc == null ? 0 : rc.getRoleTimesToInt(CountType.LuckyBoxUsedTimes);//getLuckyBoxUsedTimes();
		times++;
		int remainTimes = luckyBoxAppConfig.getDefaultRoundsCount() - times;
		return remainTimes < 0 ? 0 : remainTimes;
	}

	private int getVipTimes(String roleId) {
		return GameContext.getVipApp().getVipPrivilegeTimes(roleId,
				VipPrivilegeType.LUCKEY_BOX_VIP_TIMES.getType(), "");
	}

	private <T> int getConfigConsume(T times) {
		LuckyBoxDiamandsConsumeConfig c = luckyBoxConsumeMap.get(times + "");
		int consume = c == null ? 0 : c.getDimands();
		return consume;
	}

	@Override
	public int onLogin(RoleInstance role, Object context) {
		return 0;
	}

	@Override
	public int onLogout(RoleInstance role, Object context) {
		try {
			roleAwardPoolcacheMap.remove(role.getRoleId());
			//发送奖励
			clearRewards(role);
		} catch (Exception e) {
			this.logger.error("LuckyBoxApp.offline error:" + e);
			return 0;
		}

		return 1;
	}

	@Override
	public int onCleanup(String roleId, Object context) {
		return 0;
	}

	@Override
	public int getPlayTodayTimes(RoleInstance role) {
		RoleCount rc = role.getRoleCount();
		return rc == null ? 0 : rc.getRoleTimesToInt(CountType.LuckyBoxUsedTimes);//getLuckyBoxUsedTimes();
	}

	@Override
	public Message clearRewards(RoleInstance role) {
		String roleId = role.getRoleId();
		Collection<LuckyBoxPoolItem> awards = awardMap.remove(roleId);
		if(Util.isEmpty(awards)){
			return null;
		}
		for (LuckyBoxPoolItem it : awards) {
			sendLuckyBoxAward(role,it);
		}
		return null;
	}
	
}
