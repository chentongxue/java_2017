package com.game.draco.app.copy.vo;

import lombok.Data;
import sacred.alliance.magic.base.Result;

public @Data class CopyBuyNumResult extends Result{
	
	private byte copyRemCount;// 当前副本剩余参与次数
	private byte copyMaxCount;// 当前副本最大参与次数
	private byte typeRemCount;// 当前类副本的剩余参与次数
	private byte typeMaxCount;// 当前类副本的最大参与次数
	
}
