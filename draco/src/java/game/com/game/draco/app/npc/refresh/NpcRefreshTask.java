package com.game.draco.app.npc.refresh;

import java.util.concurrent.atomic.AtomicBoolean;


import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import sacred.alliance.magic.vo.MapInstance;
import sacred.alliance.magic.vo.Point;

/**
 * 刷怪配置信息
 * @author wangkun
 *
 */
public @Data class NpcRefreshTask {
	//刷新类型
	private RefreshType refreshType = RefreshType.original;
	//刷新时间
	private long refreshTime = -1;
	private NpcRefreshConfig npcRefreshConfig;
	@JSONField(serialize = false)
	private MapInstance mapInstance;
	//当前npc的刷新规则
	private NpcRefreshRule npcRefreshRule;
	//是否出生喊话
	private boolean hadBornSpeak = false ;
	private AtomicBoolean lock = new AtomicBoolean(false);
	
	//每条规则只刷新一次,死亡也不再刷新
	private int onlyOnceRuleId = -1; //规则ID
	private int updateDay = -1; //刷新时间

	/**
	 * npc的出生点
	 */
	private Point bornPoint ;

	public void setMapInstance(MapInstance mapInstance){
		this.mapInstance = mapInstance ;
	}

	public Point getBornPoint(){
		if(null  == this.bornPoint){
			this.resetBornPoint();
		}
		return this.bornPoint ;
	}


	public void resetBornPoint(){
		if(null == bornPoint){
			this.bornPoint = npcRefreshConfig.newBornPoint();
			return ;
		}
		if(!this.npcRefreshConfig.isRandomBornPoint()){
			//不随机
			return ;
		}
		this.bornPoint = this.npcRefreshConfig.newBornPoint();
	}

	public void update(){
		if(this.npcRefreshConfig == null
				|| this.refreshType == RefreshType.none){
			return ;
		}
		if(lock.compareAndSet(false, true)){
			try{
				this.npcRefreshConfig.update(this);
			}finally{
				lock.set(false);
			}
		}
	}
	
	public void refreshBeforeSpeak(){
		if(this.npcRefreshConfig == null){
			return ;
		}
		if(lock.compareAndSet(false, true)){
			try{
				this.npcRefreshConfig.refreshBeforeSpeak(this);
			}finally{
				lock.set(false);
			}
		}
	}
	
}
