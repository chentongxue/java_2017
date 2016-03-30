package com.game.draco.app.rank.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.Data;

import org.slf4j.Logger;

import com.game.draco.app.rank.type.RankCycleType;
import com.game.draco.app.rank.type.RankType;

import sacred.alliance.magic.app.active.rank.ActiveRankInfo;
import sacred.alliance.magic.app.goods.Util;
import sacred.alliance.magic.util.DateUtil;
import sacred.alliance.magic.util.KeySupport;
import sacred.alliance.magic.util.Wildcard;

public @Data class RankInfo implements KeySupport<Integer> {
	private final static String CAT = "-";
	private final static String COMMA = ",";
	
	private int id;
	private String name;
	private String sortRule;
	private String refreshRule;
	private String rewardRule;
	private byte type; //排行榜11大类型
	private byte subType; //排行榜小类型,eg:等级包含（阵营1, 阵营2, 阵营3）
	private short tagResId; //排行榜页签文字 to be deleted
	private short disCount; //排行显示条目数,100
	private byte rewardCycleType; //世界排行领奖周期类型
	private String rewardTime; //世界排行领奖间隔时间
	private short broadcastRank;
	private String mailDesc; //邮件说明
	
	private ActiveRankInfo activeRankInfo; 
	private int activeRankId ; 
	//log4j对象
	private Logger logger = null; //在RankAppImpl initLogger里设置
	private String schedulerLoggerFileName = null; //在RankAppImpl initLogger里设置
	private RankType rankType;
	private List<RankRewardTime> rewardTimeList;
	
	/**
	 * 是否活动排行榜
	 * @return
	 */
	public boolean isActiveRank(){
		return null != this.activeRankInfo ;
	}
	
	/**
	 * 是否在统计时间内
	 * @return
	 */
	public boolean isInStatDate(){
		if(null == this.activeRankInfo){
			return true ;
		}
		return this.activeRankInfo.isInStatDate() ;
	}
	
	/**
	 * 获得排行榜的周期类型
	 * 用户日志文件名
	 * @return
	 */
	public RankCycleType getRankCycle(){
		if(null !=  activeRankInfo){
			//活动相关的排行榜
			return RankCycleType.Forever ;
		}
		return rankType.getRankCycle();
	}
	
	/**
	 * 获得开始时间
	 *  用户日志文件名
	 *  格式: yyMMdd
	 * @return
	 */
	public String getStartTimeStr(){
		if(null == activeRankInfo){
			//非活动
			return "000000" ;
		}
		Date statStartDate = activeRankInfo.getStatStartDate();
		if(null == statStartDate){
			return "000000" ;
		}
		return DateUtil.date2Str(statStartDate, "yyMMdd");
	}
	
	/**
	 * 获得开始时间
	 *  用户日志文件名
	 *  格式: yyMMdd
	 * @return
	 */
	public String getEndTimeStr(){
		if(null == activeRankInfo){
			//非活动
			return "000000" ;
		}
		Date statEndDate = activeRankInfo.getStatEndDate();
		if(null == statEndDate){
			return "000000" ;
		}
		return DateUtil.date2Str(statEndDate, "yyMMdd");
	}
	
	
	@Override
	public Integer getKey() {
		return this.id;
	}
	
	/**
	 * 是否综合排行榜
	 * @return
	 */
	public boolean isComposite(){
		return this.subType == -1 ;
	}
	
	public boolean init(){
		if(Util.isEmpty(rewardTime)){
			return true;
		}
		String[] times = Util.splitStr(rewardTime, COMMA);// COMMA = ","
		if(Util.isEmpty(times)){
			return false;
		}
		for(int i = 0; i < times.length; i++){
			String[] time = Util.splitStr(times[i], CAT);
			if(Util.isEmpty(time)){
				continue;
			}
			RankRewardTime rewardTime = new RankRewardTime();
			rewardTime.setTimeCycleType(rewardCycleType);
			rewardTime.setTimeFirst(Integer.valueOf(time[0]));
			rewardTime.setTimeSecond(Integer.valueOf(time[1]));
			if(null == rewardTimeList){
				rewardTimeList = new ArrayList<RankRewardTime>();
			}
			rewardTimeList.add(rewardTime);
		}
		return true;
	}
	
	public String getMailInfo(short rank, String rewardTimeStr){
		return mailDesc.replace(Wildcard.Rank_RewardTime, rewardTimeStr)
									 .replace(Wildcard.Rank_Name, name)
									 .replace(Wildcard.Rank_Rank, String.valueOf(rank));
	}
	
}
