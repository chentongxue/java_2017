package com.game.draco.app.choicecard.base;

import java.util.Map;

import com.google.common.collect.Maps;

import lombok.Data;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.util.KeySupport;

/**
 * 金币抽卡树数据
 * @author zhouhaobing
 *
 */
public @Data class BaseTree implements KeySupport<String>{

	//ID
	private int treeId ;
	//幸运值
	private int luck;
	//坐骑星级
	private byte star;
	//节点1
	private int son1;
	//节点1概率
	private int son1Prob;
	//节点2
	private int son2;
	//节点2概率
	private int son2Prob;
	//是否清幸运值
	private byte flag1;
	//是否清幸运值
	private byte flag2;
	
	@Override
	public String getKey(){
		return this.getTreeId() + Cat.underline + this.getLuck();
	}
	
	//变量
	public Map<Integer, Integer> weightMap = Maps.newHashMap();
	
	public Map<Integer,Byte> flagMap = Maps.newHashMap();
	
	public void init() {
		init(this.son1, this.son1Prob);
		init(this.son2, this.son2Prob);
		initFlag(this.son1, this.flag1);
		initFlag(this.son2, this.flag2);
	}
	
	public void init(int eventId, int weight) {
		if(eventId <= 0 || weight <= 0) {
			return ;
		}
		this.weightMap.put(eventId, weight);
	}
	
	public void initFlag(int eventId, byte flag) {
		if(eventId <= 0) {
			return ;
		}
		this.flagMap.put(eventId, flag);
	}
}
