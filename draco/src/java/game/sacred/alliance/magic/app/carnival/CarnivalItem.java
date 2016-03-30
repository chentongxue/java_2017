package sacred.alliance.magic.app.carnival;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.goods.GoodsOperateBean;
import sacred.alliance.magic.base.DateTimeBean;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.constant.FormatConstant;
import sacred.alliance.magic.domain.CarnivalRankInfo;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.util.DateConverter;
import sacred.alliance.magic.util.DateUtil;
import sacred.alliance.magic.util.KeySupport;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.item.GoodsLiteNamedItem;

public @Data class CarnivalItem implements KeySupport<Integer>{
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final int DEFAULT_DETAIL_REWARD_RANK = 1;//界面展示第一名的物品
	private final int DEFAULT_DETAIL_ALL_REWARD_RANK = -1;//界面展示符合条件的奖励物品，配置为-1
	private int activeId;
	private int itemId; //活动id
	private String name; //活动名
	private String rewardDesc;
	private String allRewardDesc;
	private String statStartTime; //数据统计开始时间
	private String statEndTime; //数据统计结束时间
	private int statStartTimeRel; //数据统计开始时间（相对）
	private int statEndTimeRel; //数据统计结束时间（相对）
	
	private Date statStartDate;
	private Date statEndDate;
	
	private String timeDesc;
	
	private int ruleId;//规则ID
	private CarnivalRule carnivalRule;
	
	private List<CarnivalRankInfo> rankList = new ArrayList<CarnivalRankInfo>();
	private boolean hasEnd = false;
	private String activeTimeStr;
	//展示用，只显示第一名，启动是加载好
	private Map<Byte, List<GoodsLiteNamedItem>> rewardGoodsMap = new HashMap<Byte, List<GoodsLiteNamedItem>>();
	private Map<Byte, List<GoodsLiteNamedItem>> allRewardGoodsMap = new HashMap<Byte, List<GoodsLiteNamedItem>>();
	
	/**
	 * 是否成功初始化
	 */
	public boolean init(CarnivalRule rule){
		
		//数据统计开始结束时间
		DateTimeBean statBean = DateConverter.getDateTimeBean(this.statStartTimeRel, this.statEndTimeRel, 
				this.statStartTime, this.statEndTime, FormatConstant.DEFAULT_YMD);
		if(null == statBean){
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("activeCarnival itemId= " + itemId + ", statStartTime, statEndTime config error!");
			return false;
		}
		statStartDate = statBean.getStartDate();
		statEndDate = statBean.getEndDate();
		if(null == this.statStartDate || null == this.statEndDate){
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("activeCarnival itemId= " + itemId + ", Please config the statStartTime or the endTime!");
			return false;
		}
		if(this.statStartDate.after(this.statEndDate)){
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("activeCarnival itemId= " + itemId + ", statStartTime should be before statEndTime!");
			return false;
		}
		//更新显示字符串
		this.statStartTime = DateUtil.date2Str(statStartDate, FormatConstant.DEFAULT_YMD);
		this.statEndTime = DateUtil.date2Str(statEndDate, FormatConstant.DEFAULT_YMD);
		
		if(DateUtil.sameDay(statStartDate, statEndDate)){
			activeTimeStr = statStartTime;
		}else{
			activeTimeStr = statStartTime + Cat.strigula + statEndTime;
		}
		
		/*for(CareerType career : CareerType.values()) {
			if(career.getType() < 0) {
				continue ;
			}
			byte type = career.getType();
			CarnivalReward carnivalReward = GameContext.getCarnivalApp().getCarnivalReward(itemId, DEFAULT_DETAIL_REWARD_RANK, type);
			if(null != carnivalReward) {
				List<GoodsOperateBean> goodsList = carnivalReward.getGoodsList();
				List<GoodsLiteNamedItem> rewardList = getGoodsListItem(goodsList);
				rewardGoodsMap.put(type, rewardList);
			}
			
			CarnivalReward carnivalAllReward = GameContext.getCarnivalApp().getCarnivalReward(itemId, DEFAULT_DETAIL_ALL_REWARD_RANK, type);
			if(null != carnivalAllReward) {
				List<GoodsOperateBean> allRewardGoodsList = carnivalAllReward.getGoodsList();
				List<GoodsLiteNamedItem> allRewardList = getGoodsListItem(allRewardGoodsList);
				allRewardGoodsMap.put(type, allRewardList);
			}
		}*/
		
		this.carnivalRule = rule;
		
		List<CarnivalRankInfo> dbCarnivalRole = GameContext.getBaseDAO().selectList(CarnivalRankInfo.class, "activeId", itemId);
		if(!Util.isEmpty(dbCarnivalRole)) {
			rankList.addAll(dbCarnivalRole);
		}
		
		//如果过时并且没有产生数据，调用产生数据的接口
		if(isOutDate() && Util.isEmpty(rankList)){
			getCarnivalRank();
		}
		
		return true;
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
	 * 判断date是否过期
	 */
	public boolean isOutDate(){
		if(statEndDate == null){
			return false;
		}
		else{
			Date date = new Date();
			return date.compareTo(statEndDate)==1;
		}
	}
	
	public boolean isTimeToReward(){
		if(statEndDate == null){
			return false;
		}
		Calendar endCal = Calendar.getInstance();
		endCal.setTime(statEndDate);
		
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.HOUR, -1);
		
		if(DateUtil.sameDay(endCal.getTime(), cal.getTime())) {
			return true;
		}
		return false;
	}
	
	public boolean canReward(RoleInstance role){
		return false;
	}

	@Override
	public Integer getKey() {
		return this.itemId;
	}
	
	public List<CarnivalRankInfo> getCarnivalRank(){
		rankList = carnivalRule.getCarnivalRank(itemId);
		
		GameContext.getBaseDAO().delete(CarnivalRankInfo.class, "activeId", itemId);
		
		for(CarnivalRankInfo carnivalRankInfo : rankList){
			if(null == carnivalRankInfo) {
				continue;
			}
			carnivalRankInfo.setActiveId(itemId);
			GameContext.getBaseDAO().insert(carnivalRankInfo);
		}
		return rankList;
	}
	
	/**
	 * 发奖
	 */
	public void reward(){
		carnivalRule.reward(itemId, rankList);
	}
	
	private List<GoodsLiteNamedItem> getGoodsListItem(List<GoodsOperateBean> goodsList) {
		List<GoodsLiteNamedItem> list = new ArrayList<GoodsLiteNamedItem>();
		if(Util.isEmpty(goodsList)) {
			return list;
		}
		GoodsLiteNamedItem item = null;
		for(GoodsOperateBean bean : goodsList) {
			GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(bean.getGoodsId());
			if(null == gb) {
				continue;
			}
			item = gb.getGoodsLiteNamedItem();
			item.setBindType(bean.getBindType().getType());
			item.setNum((short)bean.getGoodsNum());
			list.add(item);
		}
		return list;
	}
}
