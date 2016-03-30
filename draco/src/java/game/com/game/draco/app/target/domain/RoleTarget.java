package com.game.draco.app.target.domain;

import lombok.Data;

import com.game.draco.GameContext;
import com.game.draco.app.target.config.TargetConfig;

public @Data class RoleTarget {
	public final static byte STATUS_ACHIEVE_NO = 0;
	public final static byte STATUS_ACHIEVE_YES = 1;
	public final static byte STATUS_AWARD_YES = 2;
	public final static String ROLE_ID = "roleId" ;
	
	private int roleId;
	private short line1Id; //目标1线已完成的目标id
	private byte line1Status; //目标1线已完成的目标状态
	private short line2Id; //目标2线已完成的目标id
	private byte line2Status; //目标2线已完成的目标状态
	private short line3Id; //目标3线已完成的目标id
	private byte line3Status; //目标3线已完成的目标状态
	private short line4Id; //等同于1线,记录当前未完成目标id
	
	//标识数据库里面是否有这条记录
	private boolean existRecord;
	
	public short getLineId(byte lineType) {
		if(lineType == TargetConfig.line1) {
			return line1Id;
		}
		if(lineType == TargetConfig.line2) {
			return line2Id;
		}
		if(lineType == TargetConfig.line3) {
			return line3Id;
		}
		if(lineType == TargetConfig.line4) {
			return line4Id;
		}
		return 0 ;
	}
	
	public byte getLineStatus(byte lineType) {
		if (lineType == TargetConfig.line1) {
			return line1Status;
		}
		if (lineType == TargetConfig.line2) {
			return line2Status;
		}
		if (lineType == TargetConfig.line3) {
			return line3Status;
		}
		return 0;
	}
	
	public void updateLine(byte lineType, short targetId, byte status) {
		if (lineType == TargetConfig.line1) {
			line1Id = targetId;
			line1Status = status;
			return;
		}
		if (lineType == TargetConfig.line2) {
			line2Id = targetId;
			line2Status = status;
			return;
		}
		if (lineType == TargetConfig.line3) {
			line3Id = targetId;
			line3Status = status;
			return;
		}
		if (lineType == TargetConfig.line4 
				&& status == STATUS_ACHIEVE_YES) {
			// 如果目标达成则更新4线目标id
			TargetConfig target = GameContext.getTargetApp().getTargetConfig(
					this.line4Id);
			TargetConfig nextTarget = target.getNextTarget();
			short nextId = (null == nextTarget) ? TargetConfig.DEFAULT_TARGET
					: nextTarget.getTargetId();
			this.updateLine4(nextId);
		}
	}
	
	public void updateLine4(short targetId) {
		this.line4Id = targetId;
	}
	
	/**
	 * 提示是否有奖励
	 * @return 0:否,1:是
	 */
	public byte getHintAwardStatus() {
		if(line1Status == STATUS_ACHIEVE_YES || STATUS_ACHIEVE_YES == line2Status
				|| STATUS_ACHIEVE_YES == line3Status) {
			return 1;
		}
		return 0;
	}
}
