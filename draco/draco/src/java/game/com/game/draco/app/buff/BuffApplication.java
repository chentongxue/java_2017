package com.game.draco.app.buff;

import java.util.Map;

import sacred.alliance.magic.core.Service;

import com.google.common.collect.Maps;

public abstract class BuffApplication implements Service{
	protected static Map<Short, Buff> buffMap = Maps.newConcurrentMap();
	
	public abstract Buff getBuff(short buff);
	
	public static void registerBuff(Buff buff){
		if(null == buff){
			return ;
		}
		//便于buff重载将判断重复取得
		//判断buff是否重复
		/*if(buffMap.containsKey(buff.getBuffId())){
			return;
		}*/
		buffMap.put(buff.getBuffId(), buff);
	}
	
	public abstract boolean reLoad() ;
	
	//public abstract GoodsBuffDetail getBuffDetail(int buffId);
	
	//public abstract GoodsBuffDetail getGoodsBuffDetail(int buff,int buffLevel);
}
