package com.game.draco.app.npc.inspire.vo;

import com.game.draco.GameContext;
import com.game.draco.app.npc.inspire.NpcInspireType;

import lombok.Data;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;

public @Data class NpcInspireFunc {
	
	private String npcId;//npc模板ID
	private String functionName;//功能选项名称
	private byte type;//鼓舞方式
	private short buffId;//增加buffId
	
	/**
	 * 初始化验证配置信息
	 * @param fileInfo
	 */
	public void checkAndInit(String fileInfo){
		String info = fileInfo + "npcId=" + this.npcId + ",";
		if(Util.isEmpty(this.npcId)){
			this.checkFail(info + "npcId is error.");
		}
		if(null == GameContext.getNpcApp().getNpcTemplate(this.npcId)){
			this.checkFail(info + "this npc is not exist.");
		}
		if(null == NpcInspireType.get(this.type)){
			this.checkFail(info + "type=" + this.type + ",type is not exist.");
		}
		if(this.buffId <= 0){
			this.checkFail(info + "buffId=" + this.buffId + ",buffId is error.");
		}
		if(null == GameContext.getBuffApp().getBuff(this.buffId)){
			this.checkFail(info + "buffId=" + this.buffId + ",buffId is not exist.");
		}
	}
	
	private void checkFail(String info){
		Log4jManager.CHECK.error(info);
		Log4jManager.checkFail();
	}
	
}
