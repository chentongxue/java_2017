package com.game.draco.app.operate.donate.config;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import lombok.Data;
import sacred.alliance.magic.base.DateTimeBean;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.util.DateConverter;
import sacred.alliance.magic.util.DateUtil;
import sacred.alliance.magic.util.KeySupport;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.operate.donate.DonateApp;
import com.game.draco.app.operate.donate.DonateResult;
import com.game.draco.app.rank.domain.RankInfo;
import com.game.draco.message.item.GoodsLiteNamedItem;

public @Data
class DonateInfo implements KeySupport<Integer> {
	private static final String CAT = ",";
	private int id; // 排行榜活动id
	private String name; // 活动名
	private String title; // 活动标题
	private String ruleDesc; // 活动规则
	private String statStartTime; // 数据统计开始时间
	private String statEndTime; // 数据统计结束时间
	private String rewardStartTime; // 领奖开始时间
	private String rewardEndTime; // 领奖结束时间
	private int statStartTimeRel; // 数据统计开始时间（相对）
	private int statEndTimeRel; // 数据统计结束时间（相对）
	private int rewardStartTimeRel; // 领奖开始时间（相对）
	private int rewardEndTimeRel; // 领奖结束时间（相对）
	private int rankId; // 排行榜活动对应的排行榜id列表
	private int ruleId; // 规则id
	private String donateGoodsIds; // 捐献物品ids，英文逗号分隔
	private int intoRankMinScore; // 达到此积分进入排行榜

	private Date statStartDate;
	private Date statEndDate;
	private Date rewardStartDate;
	private Date rewardEndDate;
	private RankInfo rankInfo = null;
	private List<Integer> donateGoodsList;
	private DonateRule donateRule;

	@Override
	public Integer getKey() {
		return this.id;
	}

	/**
	 * 是否成功初始化
	 */
	public Result init(DonateResult donateResult) {
		Result result = new Result();
		result.failure();
		// 数据统计开始结束时间
		DateTimeBean statBean = DateConverter.getDateTimeBean(
				this.statStartTimeRel, this.statEndTimeRel, this.statStartTime,
				this.statEndTime, DateUtil.format_yyyy_MM_dd);
		if (null == statBean) {
			result.setInfo("WorldDonateInfo id= " + id
					+ ", statStartTime, statEndTime config error!");
			return result;
		}
		statStartDate = statBean.getStartDate();
		statEndDate = statBean.getEndDate();
		if (null == this.statStartDate || null == this.statEndDate) {
			result.setInfo("WorldDonateInfo id= " + id
					+ ", Please config the statStartTime or the endTime!");
			return result;
		}
		if (this.statStartDate.after(this.statEndDate)) {
			result.setInfo("WorldDonateInfo id= " + id
					+ ", statStartTime should be before statEndTime!");
			return result;
		}
		// 更新显示字符串
		this.statStartTime = DateUtil.date2Str(statStartDate, DateUtil.format_yyyy_MM_dd);
		this.statEndTime = DateUtil.date2Str(statEndDate, DateUtil.format_yyyy_MM_dd);

		// 领奖开始结束时间
		DateTimeBean rewardBean = DateConverter.getDateTimeBean(
				this.rewardStartTimeRel, this.rewardEndTimeRel,
				this.rewardStartTime, this.rewardEndTime, DateUtil.format_yyyy_MM_dd);
		if (null == rewardBean) {
			result.setInfo("WorldDonateInfo id= " + id
					+ ", rewardStartTime, rewardEndTime config error!");
			return result;
		}
		rewardStartDate = rewardBean.getStartDate();
		rewardEndDate = rewardBean.getEndDate();
		if (null == this.rewardStartDate || null == this.rewardEndDate) {
			result.setInfo("WorldDonateInfo id= "
					+ id
					+ ", Please config the rewardStartTime or the rewardEndTime.");
			return result;
		}
		if (this.rewardStartDate.after(this.rewardEndDate)) {
			result.setInfo("WorldDonateInfo id= " + id
					+ ", rewardStartTime should be before rewardEndTime!");
			return result;
		}
		// 更新显示字符串
		this.rewardStartTime = DateUtil.date2Str(rewardStartDate,
				DateUtil.format_yyyy_MM_dd);
		this.rewardEndTime = DateUtil.date2Str(rewardEndDate, DateUtil.format_yyyy_MM_dd);

		// 判断是否存在对应的排行榜
		RankInfo rankItem = GameContext.getRankApp().getRankInfo(rankId);
		if (null == rankItem) {
			result.setInfo("WorldDonateInfo id= " + id + ", config rankId= "
					+ rankId + ", don't exist");
			return result;
		}
		if (rankItem.getActiveRankInfo() != null) {
			result.setInfo("WorldDonateInfo id= " + id + ", rankId= " + rankId
					+ "exist in more then one ActiveRank");
			return result;
		}
		this.rankInfo = rankItem;
		// 活动排行榜放入list
		GameContext.getRankApp().addActiveRank(rankItem);

		// 捐献的物品id
		if (Util.isEmpty(donateGoodsIds)) {
			result.setInfo("WorldDonateInfo id= " + id
					+ ", have no config donateGoodsIds");
			return result;
		}
		Map<Integer, DonateScore> scoreMap = donateResult.getScoreMap();
		String[] ids = Util.splitString(donateGoodsIds, CAT);
		for (int i = 0; i < ids.length; i++) {
			int goodsId = Integer.valueOf(ids[i]);
			GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(goodsId);
			if (null == gb) {
				result.setInfo("WorldDonateInfo id= " + id
						+ ", config goodsId= " + goodsId + ", don't exist");
				return result;
			}

			if (!scoreMap.containsKey(goodsId)) {
				result.setInfo("WorldDonateInfo id= " + id
						+ ", config donateGoodsIds= " + goodsId
						+ ", not exsit in WorldDonateScore");
				return result;
			}

			if (null == donateGoodsList) {
				donateGoodsList = new ArrayList<Integer>();
			}
			donateGoodsList.add(goodsId);
		}
		result.success();
		return result;
	}

	/**
	 * 是否在统计日期内
	 * 
	 * @return
	 */
	public boolean isInStatDate() {
		if (statStartDate == null && statEndDate == null) {
			return true;
		}
		return DateUtil.dateInRegion(new Date(),
				statStartDate, statEndDate);
	}

	/**
	 * 是否在兑奖日期内
	 * 
	 * @return
	 */
	public boolean isInRewardDate() {
		if (rewardStartDate == null && rewardEndDate == null) {
			return true;
		}
		return DateUtil
				.dateInRegion(new Date(), rewardStartDate, rewardEndDate);
	}

	/**
	 * 判断date是否过期
	 */
	public boolean isOutDate() {
		if (rewardEndDate == null) {
			return false;
		}
		Date date = new Date();
		return date.compareTo(rewardEndDate) == 1;
	}

	/**
	 * 是否在有效期内
	 * 
	 * @return
	 */
	public boolean isInDate() {
		if (statStartDate == null && rewardEndDate == null) {
			return true;
		}
		return DateUtil.dateInRegion(new Date(), statStartDate, rewardEndDate);
	}

	public boolean canReward(RoleInstance role) {
		if (null == this.rankInfo) {
			return false;
		}
		if (GameContext.getDonateApp().getRankRewardStat(role, this)
				.getRewardState() == DonateApp.REWARD_STATE_ENABLE) {
			return true;
		}
		return false;
	}

	public List<GoodsLiteNamedItem> getDonateGoodsLiteNamedList() {
		if (Util.isEmpty(donateGoodsList)) {
			return null;
		}
		List<GoodsLiteNamedItem> itemList = new ArrayList<GoodsLiteNamedItem>();
		for (int goodsId : donateGoodsList) {
			GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(goodsId);
			if (null == gb) {
				continue;
			}
			GoodsLiteNamedItem item = gb.getGoodsLiteNamedItem();
			itemList.add(item);
		}
		return itemList;
	}
}
