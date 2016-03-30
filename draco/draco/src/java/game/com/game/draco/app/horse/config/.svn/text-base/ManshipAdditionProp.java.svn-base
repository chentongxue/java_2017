package com.game.draco.app.horse.config;

import java.util.List;

import org.python.google.common.collect.Lists;

import sacred.alliance.magic.app.attri.AttriItem;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.util.KeySupport;
import lombok.Data;

/**
 * 坐骑骑术加成
 * @author zhouhaobing
 *
 */
public @Data class ManshipAdditionProp implements KeySupport<Integer>{

	//骑术等级
	private int manshipLevel;
	//攻击加成
	private int additionProp;
//	//防御加成
//	private int additionRit;
//	//生命加成
//	private int additionHp;
//	//法力加成
//	private int additionMp;

	
	@Override
	public Integer getKey(){
		return this.getManshipLevel();
	}
	
	public List<AttriItem> getAttriItemList() {
		List<AttriItem> list = Lists.newArrayList();
		this.append(list, AttributeType.atk, additionProp);
		this.append(list, AttributeType.rit, additionProp);
		this.append(list, AttributeType.maxHP, additionProp);
		this.append(list, AttributeType.maxMP, additionProp);
		//暂时不要了
//		this.append(list, AttributeType.critAtk, additionCrit);
//		this.append(list, AttributeType.critRit, additionTenacity);
//		this.append(list, AttributeType.dodge, additionDodge);
//		this.append(list, AttributeType.hit, additionHit);
		return list ;
	}
	
	private void append(List<AttriItem> list, AttributeType at, int value) {
		if(null == at || value <=0) {
			return ;
		}
		list.add(new AttriItem(at.getType(), value, true));
	}
	
	public int getAttriValue(AttributeType at){
		if(null == at){
			return 0 ;
		}
		switch(at){
			case atk : return additionProp;
			case rit : return additionProp;
			case maxHP : return additionProp;
			case maxMP : return additionProp;
//			case critAtk : return additionCrit;
//			case critRit : return additionTenacity;
//			case dodge : return additionDodge;
//			case hit : return additionHit;
			default : return 0 ;
	    }
	}
	
}
