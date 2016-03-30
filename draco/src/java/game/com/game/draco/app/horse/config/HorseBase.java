package com.game.draco.app.horse.config;

import java.util.List;

import lombok.Data;

import org.python.google.common.collect.Lists;

import sacred.alliance.magic.app.attri.AttriItem;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.util.KeySupport;

/**
 * 坐骑基础数据
 * @author zhouhaobing
 *
 */
public @Data class HorseBase implements KeySupport<Integer> {

	//坐骑ID
	private int id ;
	//坐骑名称
	private String name;
	//品质
	private byte quality;
	//坐骑星级
	private byte star;
	//基础攻击
	private int attack;
	//基础防御
	private int rit;
	//基础生命
	private int hp;
	//破防
	private int breakDefense;
	//暴击
	private int critAtk;
	//韧性
	private int critRit;
	//闪避
	private int dodge;
	//命中
	private int hit;
	//移动速度
	private int moveSpeed;
	//获取途径
	private String des;
	
	@Override
	public Integer getKey(){
		return this.getId();
	}
	
	public List<AttriItem> getAttriItemList() {
		List<AttriItem> list = Lists.newArrayList();
		this.append(list, AttributeType.atk, attack);
		this.append(list, AttributeType.rit, rit);
		this.append(list, AttributeType.maxHP, hp);
		this.append(list, AttributeType.breakDefense, breakDefense);
		this.append(list, AttributeType.critAtk, critAtk);
		this.append(list, AttributeType.critRit, critRit);
		this.append(list, AttributeType.dodge, dodge);
		this.append(list, AttributeType.hit, hit);
		return list ;
	}
	
	private void append(List<AttriItem> list, AttributeType at, int value) {
		if(null == at || value <=0) {
			return ;
		}
		list.add(new AttriItem(at.getType(), value, false));
	}
	
	public int getAttriValue(AttributeType at){
		if(null == at){
			return 0 ;
		}
		switch(at){
			case atk : return this.attack ;
			case rit : return this.rit ;
			case maxHP : return this.hp ;
			case breakDefense : return this.breakDefense ;
			case critAtk : return critAtk;
			case critRit : return critRit;
			case dodge : return dodge;
			case hit : return hit;
			default : return 0 ;
	    }
	}
		
}
