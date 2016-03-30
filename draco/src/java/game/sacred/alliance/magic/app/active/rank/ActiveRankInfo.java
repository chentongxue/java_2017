package sacred.alliance.magic.app.active.rank;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.game.draco.GameContext;
import com.game.draco.app.rank.domain.RankInfo;

import lombok.Data;
import sacred.alliance.magic.app.active.vo.Active;
import sacred.alliance.magic.base.DateTimeBean;
import sacred.alliance.magic.constant.FormatConstant;
import sacred.alliance.magic.util.DateConverter;
import sacred.alliance.magic.util.DateUtil;
import sacred.alliance.magic.util.KeySupport;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.util.Wildcard;
import sacred.alliance.magic.vo.RoleInstance;

public @Data class ActiveRankInfo implements KeySupport<Short>{
	private static final String CAT = "," ;
	private short id; //排行榜活动id
	private String name; //活动名
	private String statStartTime; //数据统计开始时间
	private String statEndTime; //数据统计结束时间
	private String rewardStartTime; //领奖开始时间
	private String rewardEndTime; //领奖结束时间
	private int statStartTimeRel; //数据统计开始时间（相对）
	private int statEndTimeRel; //数据统计结束时间（相对）
	private int rewardStartTimeRel; //领奖开始时间（相对）
	private int rewardEndTimeRel; //领奖结束时间（相对）
	private String rankIds; //排行榜活动对应的排行榜id列表
	
	private Date statStartDate;
	private Date statEndDate;
	private Date rewardStartDate;
	private Date rewardEndDate;
	private List<RankInfo> rankInfoList = null;
	
	@Override
	public Short getKey() {
		return this.id;
	}
	
	/**
	 * 是否成功初始化
	 */
	public boolean init(){
		Active active = GameContext.getActiveApp().getActive(this.id);
		if(null == active){
			return false;
		}
		
		//数据统计开始结束时间
		DateTimeBean statBean = DateConverter.getDateTimeBean(this.statStartTimeRel, this.statEndTimeRel, 
				this.statStartTime, this.statEndTime, FormatConstant.DEFAULT_YMD);
		if(null == statBean){
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("activeRank id= " + id + ", statStartTime, statEndTime config error!");
			return false;
		}
		statStartDate = statBean.getStartDate();
		statEndDate = statBean.getEndDate();
		if(null == this.statStartDate || null == this.statEndDate){
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("activeRank id= " + id + ", Please config the statStartTime or the endTime!");
			return false;
		}
		if(this.statStartDate.after(this.statEndDate)){
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("activeRank id= " + id + ", statStartTime should be before statEndTime!");
			return false;
		}
		//更新显示字符串
		this.statStartTime = DateUtil.date2Str(statStartDate, FormatConstant.DEFAULT_YMD);
		this.statEndTime = DateUtil.date2Str(statEndDate, FormatConstant.DEFAULT_YMD);
		
		//领奖开始结束时间
		DateTimeBean rewardBean = DateConverter.getDateTimeBean(this.rewardStartTimeRel, this.rewardEndTimeRel, 
				this.rewardStartTime, this.rewardEndTime, FormatConstant.DEFAULT_YMD);
		if(null == rewardBean){
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("activeRank id= " + id + ", rewardStartTime, rewardEndTime config error!");
			return false;
		}
		rewardStartDate = rewardBean.getStartDate();
		rewardEndDate = rewardBean.getEndDate();
		if(null == this.rewardStartDate || null == this.rewardEndDate){
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("activeRank id= " + id + ", Please config the rewardStartTime or the rewardEndTime.");
			return false;
		}
		if(this.rewardStartDate.after(this.rewardEndDate)){
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("activeRank id= " + id + ", rewardStartTime should be before rewardEndTime!");
			return false;
		}
		//更新显示字符串
		this.rewardStartTime = DateUtil.date2Str(rewardStartDate, FormatConstant.DEFAULT_YMD);
		this.rewardEndTime = DateUtil.date2Str(rewardEndDate, FormatConstant.DEFAULT_YMD);
		//更新活动描述
		String desc = active.getDesc();
		if(!Util.isEmpty(desc)){
			desc = desc.replace(Wildcard.Rank_StartTime, active.getStartDateAbs())
								 .replace(Wildcard.Rank_EndTime, active.getEndDateAbs())
								 .replace(Wildcard.Rank_StatStartTime, this.statStartTime)
								 .replace(Wildcard.Rank_StatEndTime, this.statEndTime);
			active.setDesc(desc);
		}
		
		/*if(isOutDate()){
			return false;
		}*/
		
		boolean  result = true;
		if(Util.isEmpty(rankIds)){
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("activeRank id= " + id + ", have no config rankIds");
			result = false;
		}
		String[] ids = Util.splitString(rankIds,CAT);
		for(int i=0; i < ids.length; i++){
			int rankId = Integer.valueOf(ids[i]);
			RankInfo rankItem = GameContext.getRankApp().getRankInfo(rankId);
			if(null == rankItem){
				Log4jManager.checkFail();
				Log4jManager.CHECK.error("activeRank id= " + id + ", config rankId= " + rankId + ", don't exist");
				result = false;
				continue;
			}
			if(rankItem.getActiveRankInfo() != null){
				Log4jManager.checkFail();
				Log4jManager.CHECK.error("activeRank id= " + id + ", rankId= " + rankId + "exist in more then one ActiveRank");
				result = false;
			}
			rankItem.setActiveRankInfo(this);
			//活动排行榜放入list
			GameContext.getRankApp().addActiveRank(rankItem);
			if(null == rankInfoList){
				rankInfoList = new ArrayList<RankInfo>();
			}
			rankInfoList.add(rankItem);
			
		}
		return result;
	}
	
	/**
	 * 是否在统计日期内
	 * @return
	 */
	public boolean isInStatDate(){
		if(statStartDate == null && statEndDate == null)
			return true;
		return DateUtil.dateInRegion(new Date(), statStartDate, statEndDate);
	}
	
	/**
	 * 是否在兑奖日期内
	 * @return
	 */
	public boolean isInRewardDate(){
		if(rewardStartDate == null && rewardEndDate == null)
			return true;
		return DateUtil.dateInRegion(new Date(), rewardStartDate, rewardEndDate);
	}
	
	/**
	 * 判断date是否过期
	 */
	public boolean isOutDate(){
		if(rewardEndDate == null){
			return false;
		}
		else{
			Date date = new Date();
			return date.compareTo(rewardEndDate)==1;
		}
	}
	
	public boolean canReward(RoleInstance role){
//		if(Util.isEmpty(rankInfoList)){
//			return false;
//		}
//		for(RankInfo rankItem : rankInfoList){
//			if(null == rankItem){
//				continue;
//			}
//			ActiveRankInfo aRankItem = rankItem.getActiveRankInfo();
//			if(null == aRankItem){
//				continue;
//			}
//			if(GameContext.getActiveRankApp().getRewardStat(role, rankItem) == ActiveRankApp.REWARD_STAT_ENABLE){
//				return true;
//			}
//		}
		return false;
	}

}
