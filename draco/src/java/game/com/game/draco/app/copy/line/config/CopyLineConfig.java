package com.game.draco.app.copy.line.config;

import com.game.draco.GameContext;

import lombok.Data;
import sacred.alliance.magic.app.map.data.MapConfig;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.MapLogicType;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

@Data
public class CopyLineConfig {
	
	private byte chapterId;//章节ID
	private byte copyIndex;//副本序列
	private short copyId;//副本ID
	private String copyName;//副本名称
	private int	minLevel;//开启等级
	private int power;//消耗体力值
	private int timeLimit;//时间限制（秒）
	private String mapId;
	private short mapX;
	private short mapY;
	private String needKillNpcId;
	private boolean needKillAll;
	private String passTips;
	
	private boolean firstCopy = false;
	private boolean lastCopy = false;
	
	public void checkInit(String fileInfo){
		String info = fileInfo + "chapterId = " + this.chapterId + ", copyId = " + this.copyId + ", ";
		if(this.chapterId <= 0){
			this.checkFail(info + "chapterId is error.");
		}
		if(this.copyId <= 0){
			this.checkFail(info + "copyIndex is error.");
		}
		if(Util.isEmpty(this.copyName)){
			this.checkFail(info + "copyName is empty.");
		}
		if(this.minLevel < 0){
			this.checkFail(info + "minLevel is error.");
		}
		if(this.power < 0){
			this.checkFail(info + "power is error.");
		}
		if(this.timeLimit < 0){
			this.checkFail(info + "timeLimit is error.");
		}
		sacred.alliance.magic.app.map.Map map = GameContext.getMapApp().getMap(this.mapId);
		if(map == null){
			this.checkFail(info + "the map is not exist");
		}
		MapConfig mapConfig = map.getMapConfig();
		if(MapLogicType.copyLine != mapConfig.getMapLogicType()){
			this.checkFail(info + "the MapLogicType is not copyLine.");
		}
		if(mapConfig.getCopyId() > 0 ){
			this.checkFail(info + "this map is in copyId = "+ mapConfig.getCopyId() + " (MapLogicType is copyLine).");
		}
		if(this.mapX <= 0 || this.mapY <= 0){
			this.checkFail(info + "mapX and mapY config error.");
		}
		//必须赋值。设置章节副本的ID
		mapConfig.setCopyId(this.copyId);
	}
	
	private void checkFail(String info){
		Log4jManager.CHECK.error(info);
		Log4jManager.checkFail();
	}
	
	public boolean isSuitCondition(RoleInstance role){
		return role.getLevel() >= this.minLevel ;
	}
	
	/** 是否有通关条件 **/
	public boolean hasPassCondition(){
		return this.needKillAll || !Util.isEmpty(this.needKillNpcId);
	}
	
}
