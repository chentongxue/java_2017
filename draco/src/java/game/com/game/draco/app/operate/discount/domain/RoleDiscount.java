package com.game.draco.app.operate.discount.domain;

import java.util.Date;

import lombok.Data;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.util.DateUtil;

import com.game.draco.GameContext;

public @Data class RoleDiscount {
	
	public final static String ROLE_ID = "roleId";
	public final static String ACTIVE_ID = "activeId";
	private boolean insertDB;
	private boolean updateDB;
	
	private String roleId;
	private int activeId;
	private Date operateDate; // 操作时间（不能在创建的时候初始化，否则连续充值或消费类型无法触发）
	
	private int totalValue; // 如果活动类型，充值则记录的是充值的总额，消费则记录的是消费的总额
	private int curDayTotal; // 当前天充值总额
	
	private short meetCond1Count; // 条件1满足次数
	private short meetCond2Count; // 条件2满足次数
	private short meetCond3Count; // 条件3满足次数
	private short meetCond4Count; // 条件4满足次数
	private short meetCond5Count; // 条件5满足次数
	private short meetCond6Count; // 条件6满足次数
	private short meetCond7Count; // 条件7满足次数
	private short meetCond8Count; // 条件8满足次数
	private short meetCond9Count; // 条件9满足次数
	private short meetCond10Count; // 条件10满足次数
	private short reward1Count; // 条件1领奖次数
	private short reward2Count; // 条件2领奖次数
	private short reward3Count; // 条件3领奖次数
	private short reward4Count; // 条件4领奖次数
	private short reward5Count; // 条件5领奖次数
	private short reward6Count; // 条件6领奖次数
	private short reward7Count; // 条件7领奖次数
	private short reward8Count; // 条件8领奖次数
	private short reward9Count; // 条件9领奖次数
	private short reward10Count; // 条件10领奖次数
	
	private String extraInfo; // 预留字段（保存最新更新是里活动统计开始第几天）

	public String getSelfInfo() {
		StringBuffer sb = new StringBuffer();
		sb.append(activeId);
		sb.append(Cat.pound);
		sb.append(roleId);
		sb.append(Cat.pound);
		sb.append(totalValue);
		sb.append(Cat.pound);
		sb.append(DateUtil.getTimeByDate(operateDate));
		sb.append(Cat.pound);
		sb.append(meetCond1Count);
		sb.append(Cat.pound);
		sb.append(meetCond2Count);
		sb.append(Cat.pound);
		sb.append(meetCond3Count);
		sb.append(Cat.pound);
		sb.append(meetCond4Count);
		sb.append(Cat.pound);
		sb.append(meetCond5Count);
		sb.append(Cat.pound);
		sb.append(meetCond6Count);
		sb.append(Cat.pound);
		sb.append(meetCond7Count);
		sb.append(Cat.pound);
		sb.append(meetCond8Count);
		sb.append(Cat.pound);
		sb.append(meetCond9Count);
		sb.append(Cat.pound);
		sb.append(meetCond10Count);
		sb.append(Cat.pound);
		sb.append(reward1Count);
		sb.append(Cat.pound);
		sb.append(reward2Count);
		sb.append(Cat.pound);
		sb.append(reward3Count);
		sb.append(Cat.pound);
		sb.append(reward4Count);
		sb.append(Cat.pound);
		sb.append(reward5Count);
		sb.append(Cat.pound);
		sb.append(reward6Count);
		sb.append(Cat.pound);
		sb.append(reward7Count);
		sb.append(Cat.pound);
		sb.append(reward8Count);
		sb.append(Cat.pound);
		sb.append(reward9Count);
		sb.append(Cat.pound);
		sb.append(reward10Count);
		sb.append(Cat.pound);

		return sb.toString();
	}

	public int getMeetCount(int index) {
		switch (index) {
		case 0:
			return meetCond1Count;
		case 1:
			return meetCond2Count;
		case 2:
			return meetCond3Count;
		case 3:
			return meetCond4Count;
		case 4:
			return meetCond5Count;
		case 5:
			return meetCond6Count;
		case 6:
			return meetCond7Count;
		case 7:
			return meetCond8Count;
		case 8:
			return meetCond9Count;
		case 9:
			return meetCond10Count;
		}
		return -1;
	}

	public int getRewardCount(int index) {
		switch (index) {
		case 0:
			return reward1Count;
		case 1:
			return reward2Count;
		case 2:
			return reward3Count;
		case 3:
			return reward4Count;
		case 4:
			return reward5Count;
		case 5:
			return reward6Count;
		case 6:
			return reward7Count;
		case 7:
			return reward8Count;
		case 8:
			return reward9Count;
		case 9:
			return reward10Count;
		}
		return -1;
	}

	public void updateCondCount(int index) {
		switch (index) {
		case 0:
			meetCond1Count++;
			break;
		case 1:
			meetCond2Count++;
			break;
		case 2:
			meetCond3Count++;
			break;
		case 3:
			meetCond4Count++;
			break;
		case 4:
			meetCond5Count++;
			break;
		case 5:
			meetCond6Count++;
			break;
		case 6:
			meetCond7Count++;
			break;
		case 7:
			meetCond8Count++;
			break;
		case 8:
			meetCond9Count++;
			break;
		case 9:
			meetCond10Count++;
			break;
		}
	}

	public void updateCondCount(int index, short value) {
		switch (index) {
		case 0:
			meetCond1Count = value;
			break;
		case 1:
			meetCond2Count = value;
			break;
		case 2:
			meetCond3Count = value;
			break;
		case 3:
			meetCond4Count = value;
			break;
		case 4:
			meetCond5Count = value;
			break;
		case 5:
			meetCond6Count = value;
			break;
		case 6:
			meetCond7Count = value;
			break;
		case 7:
			meetCond8Count = value;
			break;
		case 8:
			meetCond9Count = value;
			break;
		case 9:
			meetCond10Count = value;
			break;
		}
	}

	public void updateRewardCount(int index) {
		switch (index) {
		case 0:
			reward1Count++;
			break;
		case 1:
			reward2Count++;
			break;
		case 2:
			reward3Count++;
			break;
		case 3:
			reward4Count++;
			break;
		case 4:
			reward5Count++;
			break;
		case 5:
			reward6Count++;
			break;
		case 6:
			reward7Count++;
			break;
		case 7:
			reward8Count++;
			break;
		case 8:
			reward9Count++;
			break;
		case 9:
			reward10Count++;
			break;
		}
	}
	
	public void resetAllCount(Date now) {
		meetCond1Count = 0;
		meetCond2Count = 0;
		meetCond3Count = 0;
		meetCond4Count = 0;
		meetCond5Count = 0;
		meetCond6Count = 0;
		meetCond7Count = 0;
		meetCond8Count = 0;
		meetCond9Count = 0;
		meetCond10Count = 0;
		reward1Count = 0;
		reward2Count = 0;
		reward3Count = 0;
		reward4Count = 0;
		reward5Count = 0;
		reward6Count = 0;
		reward7Count = 0;
		reward8Count = 0;
		reward9Count = 0;
		reward10Count = 0;
		
		this.totalValue = 0;
		this.curDayTotal = 0;
		this.extraInfo = null;
		this.operateDate = now;
	}
	
	/**
	 * 设置今天的累计值
	 * @param value
	 * @param now
	 */
	public void setCurDayTotal(int value, Date now) {
		if (DateUtil.sameDay(now, this.operateDate)) {
			this.curDayTotal += value;
			return;
		}
		this.curDayTotal = value;
	}

	/**
	 * 返回领取计数和
	 * @return
	 */
	public int getRewardCountSum() {
		return this.reward1Count + this.reward2Count + this.reward3Count + this.reward4Count + this.reward5Count + this.reward6Count + this.reward7Count + this.reward8Count + this.reward9Count
				+ this.reward10Count;
	}

	/**
	 * 更新数据库
	 */
	public void updateDB() {
		if (this.isInsertDB()) {
			this.setInsertDB(false);
			this.setUpdateDB(false);
			GameContext.getBaseDAO().insert(this);
			return;
		} else if (this.isUpdateDB()) {
			this.setUpdateDB(false);
			GameContext.getBaseDAO().update(this);
		}
	}

}
