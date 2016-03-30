package com.game.draco.app.horse.config;

import java.util.List;

import lombok.Data;

import org.python.google.common.collect.Lists;

import sacred.alliance.magic.app.attri.AttriItem;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.util.KeySupport;

/**
 * 坐骑等级加成
 * @author zhouhaobing
 *
 */
public @Data class HorseAdditionProp implements KeySupport<String>{

	//坐骑等级
	private short horseLevel;
	//坐骑Id
	private int horseId;
	//攻击加成
	private int additionAttack;
	//防御加成
	private int additionRit;
	//生命加成
	private int additionHp;
	//法力加成
	private int additionMp;
	//暴击加成
	private int additionCritAtk;
	//韧性加成
	private int additionCritRit;
	//闪避加成
	private int additionDodge;
	//命中加成
	private int additionHit;
		
	@Override
	public String getKey(){
		return getHorseId() + Cat.underline + getHorseLevel();
	}
	
	public List<AttriItem> getAttriItemList() {
		List<AttriItem> list = Lists.newArrayList();
		this.append(list, AttributeType.atk, additionAttack);
		this.append(list, AttributeType.rit, additionRit);
		this.append(list, AttributeType.maxHP, additionHp);
		this.append(list, AttributeType.maxMP, additionMp);
		this.append(list, AttributeType.critAtk, additionCritAtk);
		this.append(list, AttributeType.critRit, additionCritRit);
		this.append(list, AttributeType.dodge, additionDodge);
		this.append(list, AttributeType.hit, additionHit);
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
			case atk : return additionAttack;
			case rit : return additionRit;
			case maxHP : return additionHp;
			case maxMP : return additionMp;
			case critAtk : return additionCritAtk;
			case critRit : return additionCritRit;
			case dodge : return additionDodge;
			case hit : return additionHit;
			default : return 0 ;
	    }
	}
}
