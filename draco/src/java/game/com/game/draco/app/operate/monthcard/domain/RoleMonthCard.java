package com.game.draco.app.operate.monthcard.domain;

import java.util.Date;

import lombok.Data;
import sacred.alliance.magic.util.DateUtil;

import com.baidu.bjf.remoting.protobuf.FieldType;
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.game.draco.app.operate.domain.RoleOperateActive;
import com.game.draco.app.operate.monthcard.MonthCardAppImpl;
import com.game.draco.app.operate.vo.OperateAwardType;

public @Data class RoleMonthCard extends RoleOperateActive {

	@Protobuf(fieldType = FieldType.DATE, order = 10)
	private Date beginDate;
	@Protobuf(fieldType = FieldType.DATE, order = 11)
	private Date operateDate;

	/**
	 * 判断今天是否已领奖
	 * @return
	 */
	public boolean isReceiveAwards() {
		if (DateUtil.sameDay(this.beginDate, this.operateDate)) {
			return true;
		}
		return false;
	}

	/**
	 * 领奖，修改领奖时间
	 */
	public void receiveAwards() {
		this.setOperateDate(new Date());
		this.setUpdateDB(true);
	}

	/**
	 * 创建用户月卡记录
	 * @return
	 */
	public static RoleMonthCard createRoleMonthCard() {
		RoleMonthCard card = new RoleMonthCard();
		card.setBeginDate(DateUtil.getDateZero(new Date()));
		card.setInsertDB(true);
		return card;
	}

	/**
	 * 判断月卡是否有效
	 * @return
	 */
	public boolean isEffective() {
		if (this.getDateDistance() > MonthCardAppImpl.EFFECTIVE_TIME) {
			this.deleteDB();
			return false;
		}
		return true;
	}
	
	/**
	 * 获取月卡剩余天数
	 * @return
	 */
	public int getDateRemain() {
		return MonthCardAppImpl.EFFECTIVE_TIME - this.getDateDistance();
	}

	/**
	 * 月卡生效天数
	 * @return
	 */
	public int getDateDistance() {
		return DateUtil.dateDiffDay(this.beginDate, new Date()) + 1;
	}
	
	/**
	 * 格式化月卡开始时间
	 * @return
	 */
	public String getStrBeginDate() {
		return DateUtil.date2Str(this.beginDate, "yyyy-MM-dd");
	}
	
	/**
	 * 格式化月卡结束时间
	 * @return
	 */
	public String getStrEndDate() {
		Date endDate = DateUtil.addDayToDate(this.beginDate, MonthCardAppImpl.EFFECTIVE_TIME);
		return DateUtil.date2Str(endDate, "yyyy-MM-dd");
	}
	
	/**
	 * 月卡领奖状态
	 * @return
	 */
	public byte getReceiveAwardsType() {
		if (!this.isEffective()) {
			return OperateAwardType.default_receive.getType();
		}
		if (DateUtil.sameDay(this.operateDate, new Date())) {
			return OperateAwardType.have_receive.getType();
		}
		return OperateAwardType.can_receive.getType();
	}

}
