package com.game.draco.app.npc.inspire.vo;

import com.game.draco.app.npc.inspire.NpcInspireType;

import lombok.Data;
import sacred.alliance.magic.constant.ParasConstant;
import sacred.alliance.magic.util.Log4jManager;

public @Data class NpcInspireRatio {

	private short buffId;//buffId
	private int buffLevel;//buff等级
	private byte type;//鼓舞方式
	private int costValue;//消耗金钱/真气值
	private int ratio;//成功率
	private String successInfo;//成功提示信息
	private String failInfo;//失败提示信息
	
	private NpcInspireType npcInspireType;
	
	/**
	 * 初始化验证配置信息
	 * @param fileInfo
	 */
	public void checkAndInit(String fileInfo){
		String info = fileInfo + "buffId=" + this.buffId + ",type=" + this.type + ",buffLevel=" + this.buffLevel + ".";
		this.npcInspireType = NpcInspireType.get(this.type);
		if(null == this.npcInspireType){
			this.checkFail(info + "type is not exist.");
		}
//		if(this.costValue <= 0){
//			this.checkFail(info + "costValue is error.");
//		}
		if(this.ratio <=0 || this.ratio > ParasConstant.PERCENT_BASE_VALUE){
			this.checkFail(info + "ratio config error.");
		}
	}
	
	private void checkFail(String info){
		Log4jManager.CHECK.error(info);
		Log4jManager.checkFail();
	}
	
}
