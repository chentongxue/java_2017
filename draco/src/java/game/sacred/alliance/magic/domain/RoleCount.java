package sacred.alliance.magic.domain;

import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;

import lombok.Data;

import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.count.type.CountType;
import sacred.alliance.magic.app.count.type.DelType;
import sacred.alliance.magic.app.count.type.OperateType;
import sacred.alliance.magic.app.count.vo.CountRecord;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.util.DateUtil;
import sacred.alliance.magic.util.KeySupport;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

import com.baidu.bjf.remoting.protobuf.Codec;
import com.baidu.bjf.remoting.protobuf.FieldType;
import com.baidu.bjf.remoting.protobuf.ProtobufProxy;
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.baidu.bjf.remoting.protobuf.annotation.ProtobufKV;
import com.game.draco.GameContext;
import com.google.common.collect.Maps;

public @Data class RoleCount implements KeySupport<String>{
	private final static Logger logger = LoggerFactory.getLogger(RoleCount.class);
	private final static String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss" ;
	public final static String ROLE_ID = "roleId" ;
	
	private String roleId;
	private int flowerNum;// 收到鲜花总数
	private int todayFlowerNum;// 今日收到鲜花数量
	/**
	 * 淘宝:泰坦当天数目
	 */
	private int todayTaitan ;
	/**
	 * 淘宝:巨龙当天数目
	 */
	private int todayJulong ;
	private Date dayTime = new Date();
	private byte[] data;
	
	//标识数据库里面是否有这条记录
	private boolean existRecord;
	private RoleInstance role ;
	
	// Map<对应功能KEY，Map<是否每日清除，值>
	@Protobuf(fieldType = FieldType.KV, order = 1)
	@ProtobufKV(clazz=CountRecord.class)
	private Map<Integer, CountRecord> timesMap = Maps.newConcurrentMap();


	public void updateTaobao(short id,int num){
		if(0 == id){
			this.todayTaitan += num ;
			return ;
		}
		if(1 == id){
			this.todayJulong += num ;
			return ;
		}
	}

	private Map<Integer, CountRecord> parseData(byte[] targetData)
			throws IOException {
		Codec<RoleCount> codec = ProtobufProxy.create(RoleCount.class);
		RoleCount times = codec.decode(targetData);
		return times.getTimesMap();
	}

	public void parseDataBase() {
		try {
			if(data == null){
				return;
			}
			timesMap = parseData(this.data);
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("RoleCount postFromDatabase.initDB() error: ", e);
		}
	}

	private byte[] buildData() {
		try {
			Codec<RoleCount> codec = ProtobufProxy.create(RoleCount.class);
			return codec.encode(this);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public void buildDatabase() {
		this.data = buildData();
	}

	/**
	 * 修改数据
	 * @param type
	 * @return
	 */
	public boolean changeTimes(CountType type) {
		if (type.getO() instanceof Byte || type.getO() instanceof Integer || type.getO() instanceof Long) {
			return changeTimes(type,0);
		}
		return changeTimes(type,"");
	}
	
	/**
	 * 修改数据带参数
	 * @param type
	 * @param v
	 * @return
	 */
	public boolean changeTimes(CountType type, Object v) {
		boolean flag = false;
		CountType t = CountType.get(type.getType());
		CountRecord record = null;
		if (t.getO() instanceof Byte || t.getO() instanceof Integer || t.getO() instanceof Long) {
			if (!NumberUtils.isNumber(v.toString())) {
				return false;
			}
			Number value = 0;
			
			if(!Util.isEmpty(timesMap)){
				if(timesMap.containsKey(t.getType())){
					record = timesMap.get(t.getType());
					if(v instanceof Byte){
						value = Byte.parseByte(record.getV());
					}else if(v instanceof Integer){
						value = Integer.parseInt(record.getV());
					}else if(v instanceof Long){
						value = Long.parseLong(record.getV());
					}
				}
			}

			if(record == null){
				record = new CountRecord();
				record.setId(t.getType());
			}
			
			if (t.getOperateType() == OperateType.Add) {
				if(v instanceof Byte){
					value = value.byteValue()+1;
				}else if(v instanceof Integer){
					value = value.intValue()+1;
				}else if(v instanceof Long){
					value = value.longValue()+1;
				}
			}  else if (t.getOperateType() == OperateType.Increment) {
				if(v instanceof Byte){
					value = value.byteValue() + Byte.parseByte(String.valueOf(v.toString()));
				}else if(v instanceof Integer){
					value = value.intValue() + Integer.parseInt(String.valueOf(v.toString()));
				}else if(v instanceof Long){
					value = value.longValue() + Long.parseLong(String.valueOf(v.toString()));
				}
			} 	else if (t.getOperateType() == OperateType.Reduce) {
				if(v instanceof Byte){
					value = value.byteValue()-1;
				}else if(v instanceof Integer){
					value = value.intValue()-1;
				}else if(v instanceof Long){
					value = value.longValue()-1;
				}
			} else if (t.getOperateType() == OperateType.Modify) {
				if(v instanceof Byte){
					value = Byte.parseByte(String.valueOf(v));
				}else if(v instanceof Integer){
					value = Integer.parseInt(String.valueOf(v));
				}else if(v instanceof Long){
					value = Long.parseLong(String.valueOf(v));
				}
			}
			record.setV(String.valueOf(value));
			flag = true;
			timesMap.put(t.getType(), record);
		} else if (t.getO() instanceof String) {
			String value = "";
			if(!Util.isEmpty(timesMap)){
				if(timesMap.containsKey(t.getType())){
					record = timesMap.get(t.getType());
					value = record.getV();
				}
			}
			
			if(record == null){
				record = new CountRecord();
				record.setId(t.getType());
			}
			
			if (t.getOperateType() == OperateType.Add) {
				value += v;
			} else if (t.getOperateType() == OperateType.Modify) {
				value = (String)v;
			}
			record.setV(value);
			flag = true;
			timesMap.put(t.getType(), record);
			flag = true;
		}
		return flag;
	}

	/**
	 * int类型获得次数 
	 * @param type
	 * @return
	 */
	public int getRoleTimesToInt(CountType type) {
		if (!timesMap.containsKey(type.getType())) {
			return 0;
		}
		CountRecord record = timesMap.get(type.getType());
		if (NumberUtils.isNumber(record.getV())) {
			return Integer.parseInt(record.getV());
		}
		return -1;
	}
	
	/**
	 * byte类型获得次数
	 * @param type
	 * @return
	 */
	public byte getRoleTimesToByte(CountType type) {
		if (!timesMap.containsKey(type.getType())) {
			return 0;
		}
		CountRecord record = timesMap.get(type.getType());
		if (NumberUtils.isNumber(record.getV())) {
			return Byte.parseByte(record.getV());
		}
		return -1;
	}

	/**
	 * long类型获得次数
	 * @param type
	 * @return
	 */
	public long getRoleTimesToLong(CountType type) {
		if (!timesMap.containsKey(type.getType())) {
			return (long) 0;
		}
		CountRecord record = timesMap.get(type.getType());
		if (NumberUtils.isNumber(record.getV())) {
			return Long.parseLong(record.getV());
		}
		return -1;
	}
	
	/**
	 * date类型获得次数
	 * @param type
	 * @return
	 */
	public Date getRoleTimesToDate(CountType type,Date date) {
		if (!timesMap.containsKey(type.getType())) {
			return date;
		}
		CountRecord record = timesMap.get(type.getType());
		return new Date(Long.parseLong(record.getV()));
	}

	/**
	 * String类型获得次数
	 * @param type
	 * @return
	 */
	public String getRoleTimesToString(CountType type) {
		if (!timesMap.containsKey(type.getType())) {
			return null;
		}
		CountRecord record = timesMap.get(type.getType());
		return record.getV();
	}

	/**
	 * 需要特殊处理的数据
	 */
	private void cleanSpecialData(){
		int todayHookExp = getRoleTimesToInt(CountType.TodayHookExp);
//		GameContext.getCountApp().onHookExpDataReset(this.role,todayHookExp,dayTime);
		resetData(CountType.TodayHookExp);
		resetData(CountType.TodayHookCleanTimes);
		long todayAppJoin = getRoleTimesToLong(CountType.JoinApp) ;
		GameContext.getCountApp().onJoinAppDataReset(this.role,todayAppJoin,dayTime);
		resetData(CountType.JoinApp);
	}

	/**
	 * 重置数据
	 */
	public void resetDay() {
		Date now = new Date();
		if (DateUtil.sameDay(dayTime, now)) {
			return;
		}
		/*
		 * 如果最后一次登录是昨天，或者更早，置换昨天的在线时间为ToDayOnlineTimeSeconds
		 */
		Date lastLoginTime = role.getLastLoginTime();
		if(DateUtil.sameDay(lastLoginTime, now)){
			int seconds = getRoleTimesToInt(CountType.ToDayOnlineTimeSeconds);
			changeTimes(CountType.YesterDayOnlineTimeSeconds, seconds);
		}
		
		//清除每日鲜花
		this.todayFlowerNum = 0 ;
		for (Entry<Integer, CountRecord> map : timesMap.entrySet()) {
			CountType t = CountType.get(map.getKey());
			if(t == null){
				continue;
			}
			if(t.isSpecial() || t.getDelType() == DelType.No){
				continue;
			}
			if (t.getDelType() == DelType.Day) {
				resetData(t);
				continue;
			}
			if (t.getDelType() == DelType.Month) {
				resetData(t);
				continue;
			}
			if (t.getDelType() == DelType.Year) {
				resetData(t);
			}
		}
		cleanSpecialData();
		int onlieSeconds = getRoleTimesToInt(CountType.YesterDayOnlineTimeSeconds);
		GameContext.getRecoveryApp().saveHungUpRecovery(role, onlieSeconds);
		this.dayTime = now;
	}
	
	/**
	 * 重置某一项数据
	 * @param t
	 */
	private void resetData(CountType t){
		Date now = new Date();
		CountRecord record = timesMap.get(t.getType());
		if(record == null){
			return;
		}
		if (t.getO() instanceof Byte || t.getO() instanceof Integer || t.getO() instanceof Long) {
			record.setV(String.valueOf(0));
		}else if (t.getO() instanceof String) {
			record.setV("");
		} else if (t.getO() instanceof Date) {
			record.setV(String.valueOf(now.getTime()));
		} 
		timesMap.put(t.getType(),record);
	}

	/**
	 * 计数信息，离线时记录日志
	 */
	public String getSelfInfo() {
		StringBuffer sb = new StringBuffer();
		if (Util.isEmpty(timesMap)) {
			return sb.toString();
		}
		sb.append(this.roleId).append(Cat.pound);
		sb.append(DateUtil.date2Str(dayTime, DATE_FORMAT));
		for (Entry<Integer, CountRecord> map : timesMap.entrySet()) {
			CountType t = CountType.get(map.getKey());
			sb.append(t.getOperateType().name() + "=" + map.getValue().getV())
					.append(Cat.pound);
		}
		return sb.toString();
	}
////	//=======================================================
////	//当天
////	//淘宝 (当天)
//	private int todayTaobaoTotal; //淘宝总次数
//	private int todayTaobaoLand; //淘宝地宫
//	private int todayTaobaoGod; //淘宝妖宫
//	private int todayTaobaoSky; //淘宝天宫
//	
//	//藏宝图,总，普通，神秘，远古挖宝次数  (当天)
//	private int treasureMapTotal;
//	private int treasureMapGreen;
//	private int treasureMapBlue;
//	private int treasureMapPurple;
//	private int treasureMapGolden;
//	private int treasureMapOrange;
//	
//	private int dayFreeTransport ; //当天免费传输数目
//	private int dayFreeReborn ; //当天免费原地复活数目
//	
//	// 社交

//	private int praiseTimes;// 点赞的次数
//	private int todayPraiseTimes;// 今天点赞的次数
//	private int receivePraiseTimes;// 被赞的次数
//	private int todayReceivePraiseTimes;// 今天被赞的次数
//	private int haveReceivePraiseGift;// 是否已经领取被赞奖励
//	private int transmissionTimes;// 传功次数
//	private int receiveTransmissionTimes;// 被传功次数
//	private int friendBatchNum;// 批量加友次数
//	
//	// 副本
//	private int heroCopyEnter;// 英雄副本今日进入次数
//	private int heroCopyBuy;// 英雄副本今日购买次数
//	private int teamCopyEnter;// 组队副本今日进入次数
//	private int teamCopyBuy;// 组队副本今日购买次数
//	
//	private int killCount;//杀人数，buff清除时清空
//	private int todayKillCount;//杀人排行榜计数，每天清空
//	/**
//	 * 领取等级奖励的等级列表
//	 */
//	private String levelGift = "" ;
//	/**
//	 * 当前月前面信息
//	 */
//	private int monthSign = 0;
//	/**
//	 * 当签名领取奖励信息
//	 */
//	private int currSignRecv =0 ;
//	/**
//	 * 当前签名次数
//	 */
//	private int currSignTimes = 0 ;
//	/**
//	 * 当前已经补签的次数
//	 */
//	private int repairSignTimes = 0;
//	/**
//	 * 罗盘活动第几轮，用于判断积分奖励是否清零
//	 */
//	private int whichRound;
//	/**
//	 * 罗盘活动积分奖励
//	 */
//	private int compassRewardPoints;
//	/**
//	 * 当日连续几次未出现暴击
//	 */
//	private byte alchemyNoBreakOutCount;
//	/**
//	 * 当日炼金次数{"2":3,"1":2,"4":5}
//	 */
//	private String alchemyCountJsonStr = "";
//	/**
//	 */
//	private int luckyBoxUsedTimes;
//	/**
//	 * 当日幸运宝箱位置对应个数 8 个{"2_1":3,"1":0,"4":0,"4":5,"4":5,"4":5,"4":0}j
//	 */
//	private String luckyBoxCountJsonStr;
//	/**
//	 * 是0则不记
//	 * 已开幸运宝箱位置 0-8 个{"2_1":3,"4":5,"4":5,"4":5}j
//	 */
//	private String luckyBoxPlaceJsonStr;
//	private Date luckyBoxLastOpenTime = new Date();
//	private int luckyBoxRefreshTimes = 0;//幸运转盘（原海盗宝箱），玩家每次玩后剩余的可玩轮数
//	
//	private int accumulateLoginAwardReceiveDays;
//	private int accumulateLoginDays = 1;//注意，创建角色时的最后登录时间为当前时间
//	//当前挂机获得经验
//	private int todayHookExp = 0 ;
//	/**
//	 * 当前天清除挂机疲劳度次数
//	 */
//	private int todayHookCleanTimes = 0 ;
//	
//	// 当前挑战次数
//	private byte challengeTimes = 0;
//	// 当前购买次数
//	private byte buyChallengeTimes = 0;
//	// 立即冷却挑战次数
//	private byte codChallengeTimes = 0;
//	// 上次挑战时间
//	private Date challengeTime = new Date();
//	/**
//	 * 是否参加了某个功能模块
//	 * 按照位运算
//	 */
//	private long joinApp = 0 ;
//	
//	// 生存战场领奖次数
//	private byte survivalRewardNum = 0;
//	
//	public void cleanHookExp(){
//		GameContext.getCountApp().onHookExpDataReset(this);
//		this.todayHookExp = 0 ;
//		this.todayHookCleanTimes = 0 ;
//	}
//	
//	public void incrTodayHookExp(int exp){
//		this.todayHookExp += exp ;
//	}
//	
//	public void clearAlchemyCount(){
//		this.alchemyNoBreakOutCount = 0;
//		this.alchemyCountJsonStr = "";
//	}
//	
//
//	
//	public void incrDayFreeTransport(){
//		this.dayFreeTransport ++ ;
//	}
//	
//	public void incrDayFreeReborn(){
//		this.dayFreeReborn ++ ;
//	}
//	
	public void updateFlowerNum(int count){
//		changeTimes(CountType.TodayFlowerNum,count);
//		changeTimes(CountType.FlowerNum,count);
		this.todayFlowerNum += count;
		this.flowerNum += count;
	}

	@Override
	public String getKey() {
		return roleId;
	}
//	
//	public void incrFriendBatchNum(){
//		this.friendBatchNum ++ ;
//	}
//	
//	public void incrTransmissionTimes() {
//		this.transmissionTimes ++;
//	}
//	
//	public void incrReceiveTransmissionTimes() {
//		this.receiveTransmissionTimes ++;
//	}
//	
//	public void incrChallengeTimes() {
//		this.challengeTimes ++;
//	}
//	
//	public void incrBuyChallengeTimes() {
//		this.buyChallengeTimes ++;
//	}
//	
//	public void incrCodChallengeTimes() {
//		this.codChallengeTimes ++;
//	}
//	
//	public void incrTodayPraiseTimes() {
//		this.todayPraiseTimes ++;
//	}
//	
//	public void incrPraiseTimes() {
//		this.praiseTimes ++;
//	}
//	
//	public void incrTodayReceivePraiseTimes() {
//		this.todayReceivePraiseTimes ++;
//	}
//	
//	public void incrReceivePraiseTimes() {
//		this.receivePraiseTimes ++;
//	}
//	
//	public void incrHeroCopyEnter() {
//		this.heroCopyEnter ++;
//	}
//	
//	public void incrHeroCopyBuy() {
//		this.heroCopyBuy ++;
//	}
//	
//	public void incrTeamCopyEnter() {
//		this.teamCopyEnter ++;
//	}
//	
//	public void incrTeamCopyBuy() {
//		this.teamCopyBuy ++;
//	}
//	
//	public void incrSurvivalRewardNum(){
//		this.survivalRewardNum++;
//	}
//	
//	/**
//	 * 
//	 * 清除计数数据，积分奖励rewardPoints并不清空
//	 */
//	private void cleanToday(){
//		this.dayFreeReborn = 0 ;
//		this.dayFreeTransport = 0 ;
//		this.todayFlowerNum = 0;
//		this.todayPraiseTimes = 0;
//		this.todayReceivePraiseTimes = 0;
//		this.haveReceivePraiseGift = 0;
//		this.transmissionTimes = 0;
//		this.receiveTransmissionTimes = 0;
//		this.friendBatchNum = 0;
//		this.todayTaobaoTotal= 0; //淘宝总次数
//		this.todayTaobaoLand= 0; //淘宝地宫
//		this.todayTaobaoSky= 0; //淘宝天宫
//		this.todayTaobaoGod= 0; //淘宝妖宫
//		//藏宝图,总，普通，神秘，远古挖宝次数  (当天)
//		this.treasureMapTotal= 0;
//		this.treasureMapGreen= 0;
//		this.treasureMapBlue= 0;
//		this.treasureMapPurple= 0;
//		this.treasureMapGolden= 0;
//		this.treasureMapOrange= 0;
//		this.todayKillCount = 0;
//		this.challengeTimes = 0;
//		this.buyChallengeTimes = 0;
//		this.codChallengeTimes = 0;
//		// 副本
//		this.heroCopyEnter = 0;
//		this.heroCopyBuy = 0;
//		this.teamCopyEnter = 0;
//		this.teamCopyBuy = 0;
//		this.survivalRewardNum = 0;
//		//炼金       （当今）
//		this.clearAlchemyCount();
//		this.cleanHookExp();
//		//！！！ 先调用
//		GameContext.getCountApp().onJoinAppDataReset(this);
//		this.joinApp = 0 ;
//	}
//	
//	private void cleanMonth(){
//		this.monthSign = 0 ;
//		repairSignTimes = 0;
//	}
//	
//	// 次日清空，但积分奖励rewardPoints并不清空
//	public void resetDay(){
//		Date now = new Date() ;
//		if(!DateUtil.sameDay(dayTime, now)){
//			this.cleanToday();
//			if(!DateUtil.isSameMonth(dayTime, now)){
//				this.cleanMonth();
//			}
//			this.dayTime = now ;
//		}
//	}
//	
//	/**
//	 * 计数信息，离线时记录日志
//	 */
//	public String getSelfInfo(){
//		StringBuffer sb = new StringBuffer();
//		sb.append(this.roleId).append(Cat.pound)
//			.append(this.todayTaobaoTotal).append(Cat.pound)
//			.append(this.todayTaobaoSky).append(Cat.pound)
//			.append(this.todayTaobaoLand).append(Cat.pound)
//			.append(this.todayTaobaoGod).append(Cat.pound)
//			.append(this.treasureMapTotal).append(Cat.pound)
//			.append(this.treasureMapGreen).append(Cat.pound)
//			.append(this.treasureMapBlue).append(Cat.pound)
//			.append(this.treasureMapPurple).append(Cat.pound)
//			.append(this.treasureMapGolden).append(Cat.pound)
//			.append(this.treasureMapOrange).append(Cat.pound)
//			.append(this.flowerNum).append(Cat.pound)
//			.append(this.friendBatchNum).append(Cat.pound)
//			.append(this.dayFreeTransport).append(Cat.pound)
//			.append(this.dayFreeReborn).append(Cat.pound)
//			.append(DateUtil.date2Str(dayTime, DATE_FORMAT))
//			.append(this.compassRewardPoints).append(Cat.pound)
//			.append(this.challengeTime).append(Cat.pound)
//			.append(this.buyChallengeTimes).append(Cat.pound)
//			.append(this.challengeTime).append(Cat.pound)
//			.append(this.heroCopyEnter).append(Cat.pound)
//			.append(this.heroCopyBuy).append(Cat.pound)
//			.append(this.teamCopyEnter).append(Cat.pound)
//			.append(this.teamCopyBuy).append(Cat.pound)
//			.append(this.survivalRewardNum).append(Cat.pound)
//			
//			;
//		return sb.toString();
//	}
	
	/*public int getTaobao(short id){
		if(id <0){
			this.resetDay();
			return this.todayTaobaoTotal ;
		}
		if(id == 0){
			this.resetDay();
			return this.todayTaobaoLand ;
		}
		if(id == 1){
			this.resetDay();
			return this.todayTaobaoGod ;
		}
		if(id == 2){
			this.resetDay();
			return this.todayTaobaoSky ;
		}
		return 0 ;
	}
	*/
	
	/*public void updateTaobao(short id, int num){
		if(id == 0){
			this.resetDay();
			this.todayTaobaoLand += num;
			this.todayTaobaoTotal += num;
			return ;
		}
		if(id == 1){
			this.resetDay();
			this.todayTaobaoGod += num;
			this.todayTaobaoTotal += num;
			return ;
		}
		if(id == 2){
			this.resetDay();
			this.todayTaobaoSky += num;
			this.todayTaobaoTotal += num;
			return ;
		}
		
	}*/
	
	/*public int getTreasureMap(byte qualityType){
		if(qualityType < 0){
			this.resetDay();
			return this.treasureMapTotal ;
		}
		if(qualityType == QualityType.green.getType()){
			this.resetDay();
			return this.treasureMapGreen  ;
		}
		if(qualityType == QualityType.blue.getType()){
			this.resetDay();
			return this.treasureMapBlue ;
		}
		if(qualityType == QualityType.purple.getType()){
			this.resetDay();
			return this.treasureMapPurple ;
		}
		if(qualityType == QualityType.red.getType()){
			this.resetDay();
			return this.treasureMapGolden ;
		}
		if(qualityType == QualityType.orange.getType()){
			this.resetDay();
			return this.treasureMapOrange  ;
		}
		return 0 ;
	}*/
	
	/*public void updateTreasureMap(byte qualityType){
		if(qualityType == QualityType.green.getType()){
			this.resetDay();
			this.treasureMapGreen += 1;
			this.treasureMapTotal += 1;
			return ;
		}else if(qualityType == QualityType.blue.getType()){
			this.resetDay();
			this.treasureMapBlue += 1;
			this.treasureMapTotal += 1;
			return ;
		}else if(qualityType == QualityType.purple.getType()){
			this.resetDay();
			this.treasureMapPurple += 1;
			this.treasureMapTotal += 1;
			return ;
		}else if(qualityType == QualityType.red.getType()){
			this.resetDay();
			this.treasureMapGolden += 1;
			this.treasureMapTotal += 1;
			return ;
		}else if(qualityType == QualityType.orange.getType()){
			this.resetDay();
			this.treasureMapOrange += 1;
			this.treasureMapTotal += 1;
			return ;
		}
	}*/
	
}
