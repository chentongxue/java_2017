package com.game.draco.app.operate.discount.config;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.Data;
import sacred.alliance.magic.base.DateTimeBean;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.constant.FormatConstant;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.util.DateConverter;
import sacred.alliance.magic.util.DateUtil;
import sacred.alliance.magic.util.KeySupport;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.operate.discount.domain.RoleDiscount;
import com.game.draco.app.operate.discount.type.DiscountRewardStat;
import com.game.draco.app.operate.discount.type.DiscountType;
import com.game.draco.app.operate.discount.type.DiscountTypeLogic;
import com.game.draco.app.operate.vo.OperateActiveBaseConfig;

public @Data class Discount extends OperateActiveBaseConfig implements KeySupport<Integer> {

	public static final String ALL = "-1";// 所有服务器（渠道）
	public static final int MAX_COND_NUM = 10;// 最大条件数
	public static final int REWARDED_NO_SHOW = 1;// 领完奖不显示

	private String startTime;// 活动开始时间（绝对）格式：2012-8-20
	private String endTime;// 活动结束时间（绝对）
	private String statStartTime;// 数据统计开始时间（绝对）
	private String statEndTime;// 数据统计结束时间（绝对）
	private String rewardStartTime;// 领奖开始时间（绝对）
	private String rewardEndTime;// 领奖结束时间（绝对）
	private int startTimeRel;// 活动开始时间（相对）格式：xx
	private int endTimeRel;// 活动结束时间（相对）
	private int statStartTimeRel;// 数据统计开始时间（相对）
	private int statEndTimeRel;// 数据统计结束时间（相对）
	private int rewardStartTimeRel;// 领奖开始时间（相对）
	private int rewardEndTimeRel;// 领奖结束时间（相对）

	private int rewardedShow;// 玩家领完奖该活动是否显示0：是，1：否
	private String channelId = "";// 渠道号ID
	private String serverId = "";// 服务器Id
	private byte condType;// 活动类型

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

	private List<DiscountCond> condList = new ArrayList<DiscountCond>(10);// 条件
	private List<DiscountReward> rewardList = new ArrayList<DiscountReward>(10);// 奖励
	private DiscountTypeLogic discountTypeLogic = null;// 逻辑类
	private DiscountType discountType = null;// 活动类型
	private Date startDate;// 活动开始时间
	private Date endDate;// 活动结束时间
	private Date statStartDate;// 统计开始时间
	private Date statEndDate;// 统计结束时间
	private Date rewardStartDate;// 领奖开始时间
	private Date rewardEndDate;// 领奖结束时间
	private Set<String> channelIdSet = new HashSet<String>();// 渠道
	private Set<String> serverIdSet = new HashSet<String>();// 服务器

	/**
	 * 获取指定层条件Id
	 * @param index
	 * @return
	 */
	public int getCond(int index) {
		switch (index) {
		case 1:
			return this.cond1;
		case 2:
			return this.cond2;
		case 3:
			return this.cond3;
		case 4:
			return this.cond4;
		case 5:
			return this.cond5;
		case 6:
			return this.cond6;
		case 7:
			return this.cond7;
		case 8:
			return this.cond8;
		case 9:
			return this.cond9;
		case 10:
			return this.cond10;
		default:
			return 0;
		}
	}

	/**
	 * 获取指定层奖励Id
	 * @param index
	 * @return
	 */
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

	/**
	 * 启服初始化
	 * @return
	 */
	public Result init() {
		Result result = new Result();
		String info = "discount id=" + this.getActiveId() + ".";
		// 活动开始结束时间
		DateTimeBean bean = DateConverter.getDateTimeBean(this.startTimeRel, this.endTimeRel, this.startTime, this.endTime, FormatConstant.DEFAULT_YMD_HMS);
		if (null == bean) {
			return result.setInfo(info + "startTime, endTime config error!");
		}
		// 获取活动开始和结束时间
		this.startDate = bean.getStartDate();
		this.endDate = bean.getEndDate();
		if (null == this.startDate || null == this.endDate) {
			return result.setInfo(info + "Please config the startTime or the endTime.");
		}
		// 开始时间必须早于结束时间
		if (this.startDate.after(this.endDate)) {
			 return result.setInfo(info + "startTime should be before endTime!");
		}
		// 更新显示字符串
		this.startTime = DateUtil.date2Str(startDate, FormatConstant.DEFAULT_YMD_HMS);
		this.endTime = DateUtil.date2Str(endDate, FormatConstant.DEFAULT_YMD_HMS);

		// 数据统计开始结束时间
		DateTimeBean statBean = DateConverter.getDateTimeBean(this.statStartTimeRel, this.statEndTimeRel, this.statStartTime, this.statEndTime,
				FormatConstant.DEFAULT_YMD_HMS);
		if (null == statBean) {
			return result.setInfo(info + "statStartTime, statEndTime config error!");
		}
		this.statStartDate = statBean.getStartDate();
		this.statEndDate = statBean.getEndDate();
		if (null == this.statStartDate || null == this.statEndDate) {
			return result.setInfo(info + "Please config the statStartTime or the endTime.");
		}
		if (this.statStartDate.after(this.statEndDate)) {
			 return result.setInfo(info + "statStartTime should be before statEndTime!");
		}
		// 统计时间应该在活动时间内
		if (this.statStartDate.before(this.startDate) || this.statEndDate.after(this.endDate)) {
			return result.setInfo(info + "statStartTime, statEndTime should be in active time!");
		}
		// 更新显示字符串
		this.statStartTime = DateUtil.date2Str(statStartDate, FormatConstant.DEFAULT_YMD_HMS);
		this.statEndTime = DateUtil.date2Str(statEndDate, FormatConstant.DEFAULT_YMD_HMS);

		// 领奖开始结束时间
		DateTimeBean rewardBean = DateConverter.getDateTimeBean(this.rewardStartTimeRel, this.rewardEndTimeRel, this.rewardStartTime, this.rewardEndTime,
				FormatConstant.DEFAULT_YMD_HMS);
		if (null == rewardBean) {
			return result.setInfo(info + "rewardStartTime, rewardEndTime config error!");
		}
		this.rewardStartDate = rewardBean.getStartDate();
		this.rewardEndDate = rewardBean.getEndDate();
		if (null == this.rewardStartDate || null == this.rewardEndDate) {
			return result.setInfo(info + "Please config the rewardStartTime or the rewardEndTime.");
		}
		if (this.rewardStartDate.after(this.rewardEndDate)) {
			return result.setInfo(info + "rewardStartTime should be before rewardEndTime!");
		}
		// 领奖时间应该在活动时间内
		if (this.rewardStartDate.before(this.startDate) || this.rewardEndDate.after(this.endDate)) {
			return result.setInfo(info + "rewardStartTime, rewardEndTime should be in active time!");
		}
		// 更新显示字符串
		this.rewardStartTime = DateUtil.date2Str(rewardStartDate, FormatConstant.DEFAULT_YMD_HMS);
		this.rewardEndTime = DateUtil.date2Str(rewardEndDate, FormatConstant.DEFAULT_YMD_HMS);
		
		// 创建活动逻辑类
		this.discountType = DiscountType.get(condType);
		if (null == discountType) {
			result.setInfo(info + "Discount condType config error!");
			return result;
		}
		this.discountTypeLogic = discountType.createDiscountType();
		
		// 可见渠道
		if (!Util.isEmpty(this.channelId)) {
			String[] channelIds = this.channelId.trim().split(Cat.comma);
			for (String s : channelIds) {
				if (Util.isEmpty(s)) {
					continue;
				}
				this.channelIdSet.add(s.trim());
			}
		}
		// 可见服务器
		if (!Util.isEmpty(this.serverId)) {
			String[] serverIds = this.serverId.trim().split(Cat.comma);
			for (String s : serverIds) {
				if (Util.isEmpty(s)) {
					continue;
				}
				this.serverIdSet.add(s.trim());
			}
		}
		
		// 验证通过
		return result.success();
	}

	/**
	 * 是否在统计时间内
	 * @return
	 */
	public boolean inCountDate() {
		return DateUtil.dateInRegion(new Date(), statStartDate, statEndDate);
	}

	/**
	 * 获取活动时间介绍
	 * @return
	 */
	public String getTimeDesc() {
		StringBuffer sb = new StringBuffer();
		// 统计时间
		sb.append(GameContext.getI18n().getText(TextId.DISCOUNT_STAT_TIME_PRE));
		sb.append(this.statStartTime);
		sb.append(Cat.strigula).append(Cat.strigula);
		sb.append(this.statEndTime);
		sb.append(Cat.newline);
		// 领奖时间
		sb.append(GameContext.getI18n().getText(TextId.DISCOUNT_REWARD_TIME_PRE));
		sb.append(this.rewardStartTime);
		sb.append(Cat.strigula).append(Cat.strigula);
		sb.append(this.rewardEndTime);
		return sb.toString();
	}

	/**
	 * 是否在活动时间内
	 * @return
	 */
	public boolean isInDate() {
		if (startDate == null && endDate == null) {
			return true;
		}
		return DateUtil.dateInRegion(new Date(), startDate, endDate);
	}

	/**
	 * 获取某层领取状态
	 * @param roleDiscount
	 * @param condIndex
	 * @return
	 */
	public DiscountRewardStat getRewardStatus(RoleDiscount roleDiscount, int condIndex) {
		if (null == roleDiscount) {
			return DiscountRewardStat.REWARD_CANNOT;
		}
		DiscountCond cond = condList.get(condIndex);
		if (null == cond) {
			return DiscountRewardStat.REWARD_CANNOT;
		}
		Date now = new Date();
		// 不在领奖时间内
		if (!DateUtil.dateInRegion(now, rewardStartDate, rewardEndDate)) {
			return DiscountRewardStat.REWARD_CANNOT;
		}
		int condCount = roleDiscount.getMeetCount(condIndex);
		int rewardCount = roleDiscount.getRewardCount(condIndex);
		int timesLimit = cond.getTimesLimit();
		// 判断次数限制
		if (timesLimit != DiscountCond.TIMES_NO_LIMIT && rewardCount >= timesLimit) {
			return DiscountRewardStat.REWARD_DONE;
		}
		// 如果discountType=PAY_SUITE || BUY_SUITE,特殊处理
		if (discountType == DiscountType.PAY_SUITE || discountType == DiscountType.BUY_SUITE) {
			return getSuitTypeRewardState(roleDiscount);
		}
		// 如果还有领取次数则返回可领取
		if (condCount > rewardCount) {
			return DiscountRewardStat.REWARD_CAN;
		} else if (condCount != 0 && rewardCount >= condCount) {
			return DiscountRewardStat.REWARD_DONE;
		}
		return DiscountRewardStat.REWARD_CANNOT;
	}

	@Override
	public Integer getKey() {
		return this.getActiveId();
	}

	/**
	 * 是否可以领奖
	 * @param role
	 * @return
	 */
	public boolean canReward(RoleInstance role) {
		RoleDiscount discountDbInfo = GameContext.getDiscountApp().getRoleDiscount(role.getRoleId(), this.getActiveId());
		for (int i = 0; i < condList.size(); i++) {
			DiscountCond cond = condList.get(i);
			if (null == cond) {
				continue;
			}
			if (getRewardStatus(discountDbInfo, i) == DiscountRewardStat.REWARD_CAN) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 对discountType=PAY_SUITE || BUY_SUITE 特殊处理 条件1,2,3,4 奖励 A,B,C,D
	 * 条件的满足计数放到条件1上,判断能否领取时A+B+C+D <= 条件1计数
	 */
	public void calcCondByTotal(RoleDiscount dbInfo) {
		DiscountCond cond = condList.get(0);
		if (null == cond) {
			return;
		}
		int minValue = cond.getParam1();
		if (minValue <= 0) {
			return;
		}
		int meetCount = dbInfo.getTotalValue() / minValue;
		dbInfo.updateCondCount(0, (short) meetCount);
	}

	/**
	 * 得到套餐类型的领奖状态
	 * @param dbInfo
	 * @return
	 */
	public DiscountRewardStat getSuitTypeRewardState(RoleDiscount dbInfo) {
		// 如果discountType=PAY_SUITE || BUY_SUITE,特殊处理
		// 如果条件1计数 <= A+B+C+D的领取之和则不能领取
		if (dbInfo.getMeetCount(0) <= dbInfo.getRewardCountSum()) {
			return DiscountRewardStat.REWARD_CANNOT;
		}
		return DiscountRewardStat.REWARD_CAN;
	}

	/**
	 * 根据消费类型判断是否统计
	 * @param outputConsumeType
	 * @return
	 */
	public boolean canCount(OutputConsumeType outputConsumeType) {
		if (outputConsumeType == null) {
			return true;
		}
		// 排除拍卖行消费
		if (outputConsumeType == OutputConsumeType.auction_shop_buy_minus_money) {
			return false;
		}
		// 特殊消费类型活动
		if (discountType == DiscountType.TAOBAO_BUY && outputConsumeType != OutputConsumeType.compass_consume) {
			return false;
		} else if (discountType == DiscountType.SECRETSHOP_BUY
				&& (outputConsumeType != OutputConsumeType.shop_secret_refresh || outputConsumeType != OutputConsumeType.shop_secret_goods_output)) {
			// 神秘商店的刷新和购买
			return false;
		} else if (discountType == DiscountType.SHOP_BUY
				&& (outputConsumeType != OutputConsumeType.shop_time_buy_consume || outputConsumeType != OutputConsumeType.shop_buy_consume)) {
			// 商城消费和限时商城消费
			return false;
		}
		return true;
	}

	/**
	 * 获取活动逻辑类
	 * @return
	 */
	public DiscountTypeLogic getDiscountTypeLogic() {
		return this.discountTypeLogic;
	}
	
	/**
	 * 是否展示在精彩活动中
	 * @param role
	 * @return
	 */
	public boolean canShow(RoleInstance role) {
		if (!this.isChannelCanShow(role.getChannelId()) || !this.isServerCanShow()) {
			return false;
		}
		// 如果当前不在配置的展示时间内
		Date now = new Date();
		if (!DateUtil.dateInRegion(now, this.startDate, this.endDate)) {
			return false;
		}
		RoleDiscount roleDiscount = GameContext.getDiscountApp().getRoleDiscount(role.getRoleId(), this.getActiveId());
		if (null == roleDiscount) {
			return true;
		}
		return this.isRewardedCanShow(role, roleDiscount);
	}
	
	/**
	 * 渠道是否可见
	 * @param channelId
	 * @return
	 */
	private boolean isChannelCanShow(int channelId) {
		return this.channelIdSet.contains(ALL) || this.channelIdSet.contains(String.valueOf(channelId));
	}

	/**
	 * 服务器是否可见
	 * @return
	 */
	private boolean isServerCanShow() {
		return this.serverIdSet.contains(ALL) || this.serverIdSet.contains(String.valueOf(GameContext.getServerId()));
	}
	
	/**
	 * 领完奖励是否可见
	 * @param role
	 * @param roleDiscountInfo
	 * @return
	 */
	private boolean isRewardedCanShow(RoleInstance role, RoleDiscount roleDiscount) {
		if (this.rewardedShow != REWARDED_NO_SHOW) {
			return true;
		}
		for (int i = 0; i < condList.size(); i++) {
			DiscountCond cond = condList.get(i);
			DiscountReward reward = rewardList.get(i);
			if (null == cond || null == reward) {
				continue;
			}
			// 无领奖次数限制一直显示
			int timesLimit = cond.getTimesLimit();
			if (timesLimit == DiscountCond.TIMES_NO_LIMIT) {
				return true;
			}
			// 有未完成或未领取或领取次数未达到限制
			int rewardCount = roleDiscount.getRewardCount(i);
			if (rewardCount <= 0 || rewardCount < timesLimit) {
				return true;
			}
			// 有可领取次数
			if (roleDiscount.getMeetCount(i) > rewardCount) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 是否在统计时间内
	 * @param role
	 * @return
	 */
	public boolean isOpen(RoleInstance role) {
		if (!this.isChannelCanShow(role.getChannelId()) || !this.isServerCanShow()) {
			return false;
		}
		Date now = new Date();
		if (!DateUtil.dateInRegion(now, this.statStartDate, this.statEndDate)) {
			return false;
		}
		RoleDiscount roleDiscount = GameContext.getDiscountApp().getRoleDiscount(role.getRoleId(), this.getActiveId());
		if (null == roleDiscount) {
			return true;
		}
		// 判断是否全部达到最大领奖次数
		for (int i = 0; i < condList.size(); i++) {
			DiscountCond cond = condList.get(i);
			DiscountReward reward = rewardList.get(i);
			if (null == cond || null == reward) {
				continue;
			}
			// 有未完成或未领取或领取次数未达到限制
			int rewardCount = roleDiscount.getRewardCount(i);
			if (rewardCount <= 0 || rewardCount < cond.getTimesLimit()) {
				return true;
			}
		}
		return false;
	}
	
}
