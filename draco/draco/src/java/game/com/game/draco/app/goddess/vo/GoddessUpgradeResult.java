package com.game.draco.app.goddess.vo;

import lombok.Data;
import sacred.alliance.magic.base.Result;

public @Data class GoddessUpgradeResult extends Result {
	private byte grade; //成功后的阶数
	private short bless; //失败的祝福值
	private int maxBless; //当前阶祝福值上限
	private short curGradeAttriAddRate; //成功后当前阶属性加成比率
	private short nextGradeAttriAddRate; //成功后下一阶属性加成比率
}
