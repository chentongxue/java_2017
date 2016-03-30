package com.game.draco.app.goddess.config;

import java.util.List;

import lombok.Data;

import org.python.google.common.collect.Lists;

import sacred.alliance.magic.app.attri.AttriItem;
import sacred.alliance.magic.base.AttributeType;

public @Data class GoddessAttribute {
	protected int atk ;
	protected int rit ;
	protected int hp;
	protected int mp;
	
	public List<AttriItem> getAttriItemList() {
		List<AttriItem> list = Lists.newArrayList();
		this.append(list, AttributeType.atk, atk);
		this.append(list, AttributeType.rit, rit);
		this.append(list, AttributeType.maxHP, hp);
		this.append(list, AttributeType.maxMP, mp);
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
			case atk : return this.atk ;
			case rit : return this.rit ;
			case maxHP : return this.hp ;
			case maxMP : return this.mp ;
			default : return 0 ;
	    }
	}
}
