package sacred.alliance.magic.app.active.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.game.draco.GameContext;

import lombok.Data;
import sacred.alliance.magic.app.active.discount.type.DiscountRewardStat;
import sacred.alliance.magic.app.active.discount.type.DiscountType;
import sacred.alliance.magic.app.active.discount.type.DiscountTypeLogic;
import sacred.alliance.magic.base.DateTimeBean;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.constant.FormatConstant;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.domain.DiscountDbInfo;
import sacred.alliance.magic.util.DateConverter;
import sacred.alliance.magic.util.DateUtil;
import sacred.alliance.magic.util.KeySupport;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

public @Data class Discount implements KeySupport<Integer>{
	private static final String ALL = "-1" ;
	public static final int MAX_COND_NUM = 10 ;
	public static final String DISPLAY_FORMAT = "yyyy-MM-dd HH:mm:ss";
	public static final String CHANGE_LINE = "\n";
	public static final String CAT = "--";
	public static final int REWARDED_NO_SHOW = 1; //领完奖不显示
	private int id;
	private String name;
	private String tips;
	private String startTime; //活动开始时间（绝对）格式：2012-8-20
	private String endTime; //活动结束时间（绝对）
	private String statStartTime; //数据统计开始时间（绝对）
	private String statEndTime; //数据统计结束时间（绝对）
	private String rewardStartTime; //领奖开始时间（绝对）
	private String rewardEndTime; //领奖结束时间（绝对）
	private int startTimeRel; //活动开始时间（相对）格式：xx
	private int endTimeRel; //活动结束时间（相对）
	private int statStartTimeRel; //数据统计开始时间（相对）
	private int statEndTimeRel; //数据统计结束时间（相对）
	private int rewardStartTimeRel; //领奖开始时间（相对）
	private int rewardEndTimeRel; //领奖结束时间（相对）
	private int rewardedShow; //玩家领完奖该活动是否显示0：是，1：否
	private int channelId = -1 ; //渠道号ID
	private String serverId = "" ;
	private String desc;
	private byte condType;
	private int cond1;
	private int reward1;
	private int cond2;
	private int reward2;
	private int cond3;
	private int reward3;
	private int cond4;
	private int reward4;
	private int cond5;
	private int reward5;
	private int cond6;
	private int reward6;
	private int cond7;
	private int reward7;
	private int cond8;
	private int reward8;
	private int cond9;
	private int reward9;
	private int cond10;
	private int reward10;
	
	private List<DiscountCond> condList = new ArrayList<DiscountCond>(10);
	private List<DiscountReward> rewardList = new ArrayList<DiscountReward>(10);
	private DiscountTypeLogic discountTypeLogic = null;
	private DiscountType discountType = null ;
	private Date startDate;
	private Date endDate;
	private Date statStartDate;
	private Date statEndDate;
	private Date rewardStartDate;
	private Date rewardEndDate;
	private Set<String> serverIdSet = new HashSet<String>();
	
	public int getCond(int index){
		switch(index){
		case 1 : return this.cond1 ;
		case 2 : return this.cond2 ;
		case 3 : return this.cond3 ;
		case 4 : return this.cond4 ;
		case 5 : return this.cond5 ;
		case 6 : return this.cond6 ;
		case 7 : return this.cond7 ;
		case 8 : return this.cond8 ;
		case 9 : return this.cond9 ;
		case 10 : return this.cond10 ;
		default : return 0 ;
		}
	}
	
	public int getReward(int index) {
		switch (index) {
		case 1:
			return this.reward1;
		case 2:
			return this.reward2;
		case 3:
			return this.reward3;
		case 4:
			return this.reward4;
		case 5:
			return this.reward5;
		case 6:
			return this.reward6;
		case 7:
			return this.reward7;
		case 8:
			return this.reward8;
		case 9:
			return this.reward9;
		case 10:
			return this.reward10;
		default:
			return 0;
		}
	}
	
	public Result init(){
		Result result = new Result();
		String info = "discount id=" + this.id + ".";
		//活动开始结束时间
		DateTimeBean bean = DateConverter.getDateTimeBean(this.startTimeRel, this.endTimeRel, this.startTime, this.endTime, FormatConstant.DEFAULT_YMD_HMS);
		if(null == bean){
			return result.setInfo(info + "startTime, endTime config error!");
		}
		this.startDate = bean.getStartDate();
		this.endDate = bean.getEndDate();
		if(null == this.startDate || null == this.endDate){
			return result.setInfo(info + "Please config the startTime or the endTime.");
		}
		if(this.startDate.after(this.endDate)){
			//return result.setInfo(info + "startTime should be before endTime!");
			Log4jManager.CHECK.warn(info + "startTime should be before endTime!");
		}
		//更新显示字符串
		this.startTime = DateUtil.date2Str(startDate, DISPLAY_FORMAT);
		this.endTime = DateUtil.date2Str(endDate, DISPLAY_FORMAT);
		
		//数据统计开始结束时间
		DateTimeBean statBean = DateConverter.getDateTimeBean(this.statStartTimeRel, this.statEndTimeRel, this.statStartTime, this.statEndTime, FormatConstant.DEFAULT_YMD_HMS);
		if(null == statBean){
			return result.setInfo(info + "statStartTime, statEndTime config error!");
		}
		statStartDate = statBean.getStartDate();
		statEndDate = statBean.getEndDate();
		if(null == this.statStartDate || null == this.statEndDate){
			return result.setInfo(info + "Please config the statStartTime or the endTime.");
		}
		if(this.statStartDate.after(this.statEndDate)){
			//return result.setInfo(info + "statStartTime should be before statEndTime!");
			Log4jManager.CHECK.warn(info + "statStartTime should be before statEndTime!");
		}
		
		//更新显示字符串
		this.statStartTime = DateUtil.date2Str(statStartDate, DISPLAY_FORMAT);
		this.statEndTime = DateUtil.date2Str(statEndDate, DISPLAY_FORMAT);
		
		//领奖开始结束时间
		DateTimeBean rewardBean = DateConverter.getDateTimeBean(this.rewardStartTimeRel, this.rewardEndTimeRel, this.rewardStartTime, this.rewardEndTime, FormatConstant.DEFAULT_YMD_HMS);
		if(null == rewardBean){
			return result.setInfo(info + "rewardStartTime, rewardEndTime config error!");
		}
		rewardStartDate = rewardBean.getStartDate();
		rewardEndDate = rewardBean.getEndDate();
		if(null == this.rewardStartDate || null == this.rewardEndDate){
			return result.setInfo(info + "Please config the rewardStartTime or the rewardEndTime.");
		}
		if(this.rewardStartDate.after(this.rewardEndDate)){
			//return result.setInfo(info + "rewardStartTime should be before rewardEndTime!");
			Log4jManager.CHECK.warn(info + "rewardStartTime should be before rewardEndTime!");
		}
		
		//更新显示字符串
		this.rewardStartTime = DateUtil.date2Str(rewardStartDate, DISPLAY_FORMAT);
		this.rewardEndTime = DateUtil.date2Str(rewardEndDate, DISPLAY_FORMAT);
		
		result = statRewardTimeInActive(info);
		if(!result.isSuccess()){
			return result;
		}
		
		discountType = DiscountType.get(condType);
		if(null != discountType){
			discountTypeLogic = discountType.createDiscountType();
		}
		if(null == discountType || null == discountTypeLogic){
			Log4jManager.CHECK.error("Discount condType config error,id=" + this.id + " condType=" + condType);
			Log4jManager.checkFail();
		}
		if(!Util.isEmpty(this.serverId)){
			String[] serverIds = this.serverId.trim().split(Cat.comma);
			for(String s : serverIds){
				if(Util.isEmpty(s)){
					continue ;
				}
				this.serverIdSet.add(s.trim());
			}
		}
		
		result.setResult(Result.SUCCESS);
		return result;
	}
	
	/**
	 * 统计时间，领奖时间是否是活动时间子集 
	 */
	private Result statRewardTimeInActive(String info){
		Result result = new Result();
		//统计时间应该在活动时间内
		if(this.statStartDate.before(this.startDate) || this.statEndDate.after(this.endDate)){
			return result.setInfo(info + "statStartTime, statEndTime should be in active time!");
		}
		//领奖时间应该在活动时间内
		if(this.rewardStartDate.before(this.startDate) || this.rewardEndDate.after(this.endDate)){
			return result.setInfo(info + "rewardStartTime, rewardEndTime should be in active time!");
		}
		
		result.setResult(Result.SUCCESS);
		return result;
	}
	
	public boolean inCountDate(){
		return DateUtil.dateInRegion(new Date(), statStartDate, statEndDate);
	}
	
	public String getTimeDesc(){
		StringBuffer sb = new StringBuffer();
		/*sb.append(CHANGE_LINE);
		sb.append("活动时间：");
		sb.append(this.startTime);
		sb.append(CAT);
		sb.append(this.endTime);
		
		sb.append(CHANGE_LINE);*/
		
		sb.append(GameContext.getI18n().getText(TextId.DISCOUNT_STAT_TIME_PRE));
		sb.append(this.statStartTime);
		sb.append(CAT);
		sb.append(this.statEndTime);
		
		sb.append(CHANGE_LINE);
		sb.append(GameContext.getI18n().getText(TextId.DISCOUNT_REWARD_TIME_PRE));
		sb.append(this.rewardStartTime);
		sb.append(CAT);
		sb.append(this.rewardEndTime);
		
		return sb.toString();
	}
	
	/**
	 * 是否过期
	 * @return
	 */
	public boolean isInDate(){
		if(startDate == null && endDate == null){
			return true;
		}
		return DateUtil.dateInRegion(new Date(), startDate, endDate);
	}
	
	public DiscountRewardStat getRewardStatus(RoleInstance role, int condIndex){
		DiscountDbInfo discountDbInfo = role.getDiscountDbInfo().get(this.id);
		return this.getRewardStatus(discountDbInfo, condIndex);
	}
	
	public DiscountRewardStat getRewardStatus(DiscountDbInfo discountDbInfo, int condIndex){
		if(null == discountDbInfo){
			return DiscountRewardStat.REWARD_CANNOT;
		}
		DiscountCond cond = condList.get(condIndex);
		if(null == cond){
			return DiscountRewardStat.REWARD_CANNOT;
		}
		Date now = new Date();
		//不在同一个时间周期内，计数清零
		if(!discountTypeLogic.isSameCycle(discountDbInfo, now)){
			discountDbInfo.resetAllCount();
			discountDbInfo.setOperateDate(now);
			return DiscountRewardStat.REWARD_CANNOT;
		}
		//不在领奖时间内
		if(!DateUtil.dateInRegion(now, rewardStartDate, rewardEndDate)){
			return DiscountRewardStat.REWARD_CANNOT;
		}
		int condCount = discountDbInfo.getCondCount(condIndex);
		int rewardCount = discountDbInfo.getRewardCount(condIndex);
		int timesLimit = cond.getTimesLimit();
		//判断次数限制
		if(timesLimit != DiscountCond.TIMES_NO_LIMIT && rewardCount >= timesLimit){
			return DiscountRewardStat.REWARD_DONE;
		}
		//如果discountType=PAY_SUITE || BUY_SUITE,特殊处理
		if(discountType == DiscountType.PAY_SUITE || discountType == DiscountType.BUY_SUITE){
			return getSuitTypeRewardState(discountDbInfo);
		}
		//如果还有领取次数则返回可领取
		if(condCount > rewardCount){
			return DiscountRewardStat.REWARD_CAN;
		}else if(condCount!=0 && rewardCount >= condCount){
			return DiscountRewardStat.REWARD_DONE;
		}
		return DiscountRewardStat.REWARD_CANNOT;
	}

	@Override
	public Integer getKey() {
		return this.id;
	}
	
	public boolean canReward(RoleInstance role){
		DiscountDbInfo discountDbInfo = role.getDiscountDbInfo().get(this.id);
		for(int i=0; i<condList.size(); i++){
			DiscountCond cond = condList.get(i);
			if(null == cond){
				continue;
			}
			if(getRewardStatus(discountDbInfo, i) == DiscountRewardStat.REWARD_CAN){
				return true;
			}
		}
		return false;
	}
	
	
	
	
	public boolean isChannelCanShow(int channelId){
		return -1 == this.channelId || channelId == this.channelId ;
	}
	
	public boolean isServerCanShow(){
		return this.serverIdSet.contains(ALL) 
			|| this.serverIdSet.contains(String.valueOf(GameContext.getServerId()));
	}
	
	public boolean isRewardedCanShow(RoleInstance role){
		if(this.rewardedShow != REWARDED_NO_SHOW){
			return true;
		}
		DiscountDbInfo discountDbInfo = role.getDiscountDbInfo().get(this.id);
		if(null == discountDbInfo){
			return true;
		}
		for(int i=0; i < condList.size(); i++){
			DiscountCond cond = condList.get(i);
			DiscountReward reward = rewardList.get(i);
			if(null == cond || null == reward){
				continue;
			}
			//无次数限制一直显示
			int timesLimit = cond.getTimesLimit();
			if(timesLimit == DiscountCond.TIMES_NO_LIMIT){
				return true;
			}
			int rewardCount = discountDbInfo.getRewardCount(i);
			if(rewardCount <= 0 || rewardCount < timesLimit){
				return true;
			}
			if(discountDbInfo.getCondCount(i) > rewardCount){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 对discountType=PAY_SUITE || BUY_SUITE 特殊处理
	 * 条件1,2,3,4 奖励 A,B,C,D
	 * 条件的满足计数放到条件1上,判断能否领取时A+B+C+D <= 条件1计数
	 */
	public void calcCondByTotal(DiscountDbInfo dbInfo){
		DiscountCond cond = condList.get(0);
		if(null == cond){
			return ;
		}
		int minValue = cond.getMinValue();
		if(minValue <= 0){
			return ;
		}
		int meetCount = dbInfo.getTotalValue() / minValue;
		dbInfo.updateCondCount(0, (short)meetCount);
	}
	
	/**
	 * 得到套餐类型的领奖状态
	 * @param dbInfo
	 * @return
	 */
	public DiscountRewardStat getSuitTypeRewardState(DiscountDbInfo dbInfo){
		//如果discountType=PAY_SUITE || BUY_SUITE,特殊处理
		//如果条件1计数 <= A+B+C+D的领取之和则不能领取
		if(dbInfo.getCondCount(0) <= dbInfo.getRewardCountSum()){
			return DiscountRewardStat.REWARD_CANNOT;
		}
		return DiscountRewardStat.REWARD_CAN;
	}
	
	/**
	 * 根据消费类型判断是否统计
	 * @param outputConsumeType
	 * @return
	 */
	public boolean canCount(OutputConsumeType outputConsumeType){
		if(outputConsumeType == null){
			return true;
		}
		//排除拍卖行消费
		if(outputConsumeType == OutputConsumeType.auction_shop_buy_minus_money){
			return false;
		}
		
		if(discountType == DiscountType.TAOBAO_BUY
				&& outputConsumeType != OutputConsumeType.compass_consume){
			return false;
		}
		else if(discountType == DiscountType.SECRETSHOP_BUY
				&& (!(outputConsumeType == OutputConsumeType.shop_secret_refresh
						|| outputConsumeType == OutputConsumeType.shop_secret_goods_output))){
			return false;
		}
		else if(discountType == DiscountType.SHOP_BUY
				&& (!(outputConsumeType == OutputConsumeType.shop_time_buy_consume
				    || outputConsumeType == OutputConsumeType.shop_buy_consume))){
			return false;
		}
		return true;
	}
}
