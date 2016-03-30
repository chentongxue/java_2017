package com.game.draco.app.rank.logic;

import com.game.draco.app.rank.type.RankActorType;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.rank.domain.RankInfo;
import com.game.draco.app.rank.type.RankType;
import com.game.draco.app.union.domain.Union;
import com.game.draco.message.item.RankDetailItem;

public abstract class RankLogic<T> {
	public static final String LOG_SWITCH_FLAG = "///";
	public static final String LOG_OFFRANK_FLAG = "@@@";
	public static final String LOG_BASIC_DATA_FLAG = "###";
	public static final int LOG_BASIC_DATA_FLAG_LEN = 3;
	public static final String CAT = "#";
	public static final String UNDERLINE_CAT = "_";
	public static final String ROW_CAT = "\n";
	// 排行显示一页条数
	public static final int PRE_PAGE_COUNT = 5;
	// 综合排行下标是-1，其他依次向后推，
	public static final int RANK_ALL = -1;

	public abstract RankType getRankType();

	/**
	 * 分析日志
	 * 
	 * @param row
	 * @return
	 */
	public abstract RankDetailItem parseLog(String row);

	/**
	 * 是否符合打印日志需求
	 * 
	 * @param role
	 * @param rankInfo
	 * @return
	 */
	protected abstract boolean canPrintLog(T t, RankInfo rankInfo);

	/**
	 * 排行榜逻辑计数
	 */
	public abstract void count(T t, RankInfo rankInfo, int data1, int data2);

	/**
	 * 具体打印日志逻辑
	 * 
	 * @param role
	 * @param rankInfo
	 * @param schedulerFlag
	 *            为true时是定时打印
	 */
	protected abstract void doPrintLog(T t, RankInfo rankInfo,
			boolean schedulerFlag, String timeStr);

	// 2014 06 16
	public void printLog4init(T t, RankInfo rankInfo) {
		printLog(t, rankInfo, false, null);
	}

	/**
	 * 打印角色的排行日志
	 * 
	 * @param role
	 */
	public void printLog(T t, RankInfo rankInfo, boolean schedulerFlag,
			String timeStr) {
		if (!this.canPrintLog(t, rankInfo)) {
			return;
		}
		if (this.doFrozenRole(t, rankInfo)) {
			return;
		}
		this.doPrintLog(t, rankInfo, schedulerFlag, timeStr);
	}

	/**
	 * 除了封停的角色帐号
	 * 
	 * @param t
	 * @param rankInfo
	 * @return
	 */
	private boolean doFrozenRole(T t, RankInfo rankInfo) {
		// 处理t是公会实例
		if (rankInfo.getRankType().getActorType() != RankActorType.ROLE) {
			return false;
		}
		RoleInstance role = (RoleInstance) t;
		if (role.getFrozenEndTime() == null) {
			return false;
		}
        this.offRankLog(rankInfo,role.getRoleId());
		return true;
	}



	/**
	 * 默认用db数据初始化log 活动排行榜初始化读取RankDbInfo
	 */
	public abstract void initLogData(RankInfo rankInfo);

	/**
	 * 切换日志文件 标志：///
	 */
	public void switchLog(RankInfo rankInfo) {
		rankInfo.getLogger().info(LOG_SWITCH_FLAG);
	}

	/**
	 * 下榜日志
	 */
	public void offRankLog(RankInfo rankInfo, String id) {
		rankInfo.getLogger().info(LOG_OFFRANK_FLAG + id);
	}

	protected int getRecordLimit(RankInfo rankInfo) {
		return rankInfo.getDisCount();
	}

	public String getUnionName(String unionId) {
		if (Util.isEmpty(unionId)) {
			return "";
		}
		Union union = GameContext.getUnionApp().getUnion(unionId);
		if (null == union) {
			return "";
		}
		return union.getUnionName();
	}

	protected String get(String[] cols, int index) {
		if (null == cols || index >= cols.length || index < 0) {
			return "";
		}
		return cols[index];
	}

	public void doWriteLogFile(RankInfo rankInfo, boolean schedulerFlag,
			String timeStr, String sb) {
		if (schedulerFlag) {
			String fileName = rankInfo.getSchedulerLoggerFileName() + "."
					+ timeStr;
			//先打下切换日志
			this.switchLog(rankInfo);
			GameContext.getRankApp().writeLogFile(fileName, sb);
		} else {
			rankInfo.getLogger().info(sb);

		}
	}
}
