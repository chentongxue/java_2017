package sacred.alliance.magic.domain;

import java.util.Date;

import lombok.Data;
import sacred.alliance.magic.base.QualityType;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.util.DateUtil;

public @Data class RoleCount {
	private final static String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss" ;
	private final int SUO_YAO_TA_MIN_TIME = 9999 ;
	
	private String roleId;
	
	//=======================================================
	//当天
	//淘宝 (当天)
	private int todayTaobaoTotal; //淘宝总次数
	private int todayTaobaoLand; //淘宝地宫
	private int todayTaobaoGod; //淘宝妖宫
	private int todayTaobaoSky; //淘宝天宫
	
	//藏宝图,总，普通，神秘，远古挖宝次数  (当天)
	private int treasureMapTotal;
	private int treasureMapGreen;
	private int treasureMapBlue;
	private int treasureMapPurple;
	private int treasureMapGolden;
	private int treasureMapOrange;
	
	private int dayFreeTransport ; //当天免费传输数目
	private int dayFreeReborn ; //当天免费原地复活数目
	private int todayFlowerNum;//今日收到鲜花数量
	
	//=======================================================
	
	//社交
	private int flowerNum;//收到鲜花总数
	private int friendBatchNum;//批量加友次数
	

	
	private Date dayTime = new Date() ;
	
	//标识数据库里面是否有这条记录
	private boolean existRecord;
	
	private int killCount;//杀人数，buff清除时清空
	
	private int todayKillCount;//杀人排行榜计数，每天清空
	/**
	 * 领取等级奖励的等级列表
	 */
	private String levelGift = "" ;
	/**
	 * 当前月前面信息
	 */
	private int monthSign = 0;
	/**
	 * 当签名领取奖励信息
	 */
	private int currSignRecv =0 ;
	/**
	 * 当前签名次数
	 */
	private int currSignTimes = 0 ;
	/**
	 * 罗盘活动第几轮，用于判断积分奖励是否清零
	 */
	private int whichRound;
	/**
	 * 罗盘活动积分奖励
	 */
	private int compassRewardPoints;
	/**
	 * 当日连续几次未出现暴击
	 */
	private byte alchemyNoBreakOutCount;
	/**
	 * 当日炼金次数{"2":3,"1":2,"4":5}
	 */
	private String alchemyCountJsonStr = "";

	
	public void clearAlchemyCount(){
		this.alchemyNoBreakOutCount = 0;
		this.alchemyCountJsonStr = "";
	}
	/**
	 * 当日幸运宝箱已打开次数 0~8
	 */
	private int luckyBoxUsedTimes;
	/**
	 * 当日幸运宝箱位置对应个数 8 个{"2_1":3,"1":0,"4":0,"4":5,"4":5,"4":5,"4":0}j
	 */
	private String luckyBoxCountJsonStr;
	/**
	 * 是0则不记
	 * 已开幸运宝箱位置 0-8 个{"2_1":3,"4":5,"4":5,"4":5}j
	 */
	private String luckyBoxPlaceJsonStr;
	public void clearluckyBoxCount(){
		this.luckyBoxUsedTimes = 0;
		this.luckyBoxCountJsonStr = "";
		this.luckyBoxPlaceJsonStr = "";
	}
	/**
	 * 增加积分奖励
	 * @param points 所加点数
	 */
	public void addCompassRewardPoints(int points){
		this.compassRewardPoints += points;
	}
	/**
	 * 重置积分奖励
	 */
	public void resetCompassRewardPoints(int points,int whichRound){
		this.compassRewardPoints = points;
		this.whichRound = whichRound;
	}
	
	
	public void incrDayFreeTransport(){
		this.resetDay();
		this.dayFreeTransport ++ ;
	}
	
	public void incrDayFreeReborn(){
		this.resetDay() ;
		this.dayFreeReborn ++ ;
	}
	
	public int getDayFreeTransport(){
		this.resetDay();
		return this.dayFreeTransport ;
	}
	
	public int getDayFreeReborn(){
		this.resetDay();
		return this.dayFreeReborn ;
	}
	
	public void updateFlowerNum(int count){
		this.resetDay();
		this.todayFlowerNum += count;
		this.flowerNum += count;
	}
	
	public int getTodayFlowerNum(){
		this.resetDay();
		return this.todayFlowerNum;
	}
	
	public int getFriendBatchNum(){
		this.resetDay();
		return this.friendBatchNum;
	}
	
	public void incrFriendBatchNum(){
		this.resetDay();
		this.friendBatchNum ++ ;
	}
	/**
	 * 
	 * 清除计数数据，积分奖励rewardPoints并不清空
	 */
	private void cleanToday(){
		this.dayFreeReborn = 0 ;
		this.dayFreeTransport = 0 ;
		this.todayFlowerNum = 0;
		this.friendBatchNum = 0;
		this.todayTaobaoTotal= 0; //淘宝总次数
		this.todayTaobaoLand= 0; //淘宝地宫
		this.todayTaobaoSky= 0; //淘宝天宫
		this.todayTaobaoGod= 0; //淘宝妖宫
		//藏宝图,总，普通，神秘，远古挖宝次数  (当天)
		this.treasureMapTotal= 0;
		this.treasureMapGreen= 0;
		this.treasureMapBlue= 0;
		this.treasureMapPurple= 0;
		this.treasureMapGolden= 0;
		this.treasureMapOrange= 0;
		this.todayKillCount = 0;
		//炼金       （当今）
		this.clearAlchemyCount();
		//幸运宝箱
		this.clearluckyBoxCount();
		
	}
	
	private void cleanMonth(){
		this.monthSign = 0 ;
	}
	
	// 次日清空，但积分奖励rewardPoints并不清空
	public void resetDay(){
		Date now = new Date() ;
		if(!DateUtil.sameDay(dayTime, now)){
			this.cleanToday();
			if(!DateUtil.isSameMonth(dayTime, now)){
				this.cleanMonth();
			}
			this.dayTime = now ;
		}
		
	}
	/**
	 * 计数信息，离线时记录日志
	 */
	public String getSelfInfo(){
		StringBuffer sb = new StringBuffer();
		sb.append(this.roleId).append(Cat.pound)
			.append(this.todayTaobaoTotal).append(Cat.pound)
			.append(this.todayTaobaoSky).append(Cat.pound)
			.append(this.todayTaobaoLand).append(Cat.pound)
			.append(this.todayTaobaoGod).append(Cat.pound)
			.append(this.treasureMapTotal).append(Cat.pound)
			.append(this.treasureMapGreen).append(Cat.pound)
			.append(this.treasureMapBlue).append(Cat.pound)
			.append(this.treasureMapPurple).append(Cat.pound)
			.append(this.treasureMapGolden).append(Cat.pound)
			.append(this.treasureMapOrange).append(Cat.pound)
			.append(this.flowerNum).append(Cat.pound)
			.append(this.friendBatchNum).append(Cat.pound)
			.append(this.dayFreeTransport).append(Cat.pound)
			.append(this.dayFreeReborn).append(Cat.pound)
			.append(DateUtil.date2Str(dayTime, DATE_FORMAT))
			.append(this.compassRewardPoints).append(Cat.pound)
			;
		return sb.toString();
		
	}
	
	public int getTaobao(short id){
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
	
	public int todayKillCount(){
		this.resetDay();
		return this.todayKillCount;
	}
	
	public void updateTaobao(short id, int num){
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
		
	}
	
	public int getTreasureMap(byte qualityType){
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
	}
	
	public void updateTreasureMap(byte qualityType){
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
	}
	
}
