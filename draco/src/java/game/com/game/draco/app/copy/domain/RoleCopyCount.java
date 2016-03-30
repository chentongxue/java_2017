package com.game.draco.app.copy.domain;

import java.util.Date;

import com.game.draco.GameContext;
import com.game.draco.app.copy.config.CopyConfig;
import com.game.draco.app.copy.vo.CopyCountType;

import lombok.Data;
import sacred.alliance.magic.util.DateUtil;
import sacred.alliance.magic.vo.RoleInstance;

public @Data class RoleCopyCount {

	public static final String ROLE_ID = "roleId";
	public static final String COPY_ID = "copyId";
	public static final byte COPY_PASS = 1;
	
	private String roleId;
	private short copyId;// 副本
	private int enterNum;// 进入次数
	private int buyNum;// 购买次数
	private byte copyPass;// 是否通关过
	private Date updateTime;// 更新时间
	
	// -------------------------------
	private boolean insertDB;
	private boolean updateDB;
	
	/**
	 * 获取今日副本进入次数
	 * @return
	 */
	public int getEnterNum(RoleInstance role) {
		this.reset(role);
		return this.enterNum;
	}
	
	/**
	 * 获取今日副本进入次数（不重置次数）
	 * @return
	 */
	public int getEnterNum() {
		return this.enterNum;
	}
	
	/**
	 * 增加进入次数
	 */
	public void incrEnterNum(Date now) {
		this.enterNum ++;
		this.updateTime = now;
		this.setUpdateDB(true);
	}
	
	/**
	 * 获取今日副本购买次数
	 * @return
	 */
	public int getBuyNum(RoleInstance role) {
		this.reset(role);
		return this.buyNum;
	}
	
	/**
	 * 获取今日副本购买次数（不重置次数）
	 * @return
	 */
	public int getBuyNum() {
		return this.buyNum;
	}
	
	/**
	 * 增加购买次数
	 */
	public void incrBuyNum(Date now) {
		this.buyNum ++;
		this.updateTime = now;
		this.setUpdateDB(true);
	}
	
	/**
	 * 标记副本通关
	 */
	public void copyPass() {
		this.copyPass = COPY_PASS;
	}
	
	/**
	 * 副本是否通关
	 * @return
	 */
	public boolean havePassCopy() {
		return this.copyPass == COPY_PASS;
	}

	/**
	 * 重置副本次数
	 * @param role
	 */
	public void reset(RoleInstance role) {
		Date now = new Date();
		// 如果是每周类型，则判断是否是同一周；否则判断是否是同一天。
		if (CopyCountType.Weekly == this.getCopyCountType()) {
			if (DateUtil.isSameWeek(this.updateTime, now)) {
				return;
			}
		} else if (DateUtil.sameDay(this.updateTime, now)) {
			return;
		}
		// 副本重置前记录一键追回信息
		GameContext.getCopyLogicApp().onCopyCountDataReset(role, this);
		this.enterNum = 0;
		this.buyNum = 0;
		this.updateTime = now;
	}

	/**
	 * 获得副本的计数类型
	 * @return
	 */
	private CopyCountType getCopyCountType() {
		CopyConfig config = GameContext.getCopyLogicApp().getCopyConfig(this.copyId);
		if (null != config) {
			return config.getCopyCountType();
		}
		return null;
	}
	
	/**
	 * 入库操作
	 */
	public void updateDB() {
		if (this.isInsertDB()) {
			GameContext.getBaseDAO().insert(this);
			return;
		}
		if (this.isUpdateDB()) {
			GameContext.getBaseDAO().update(this);
		}
	}

}
