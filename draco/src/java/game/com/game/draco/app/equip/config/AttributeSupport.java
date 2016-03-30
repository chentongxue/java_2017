package com.game.draco.app.equip.config;

import java.util.List;

import lombok.Data;
import sacred.alliance.magic.app.attri.AttriItem;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.util.Initable;

import com.google.common.collect.Lists;

public @Data class AttributeSupport implements Initable {
	protected static final float FULL = 10000 ;
	
	protected float atk	;
	protected float rit	;
	protected float maxHp ;
	protected float breakDefense ;
	protected float hit	;
	protected float dodge;
	protected float critAtk	;
	protected float critRit ;
	
	@Override
	public void init(){
		if(!this.isRate()){
			return ;
		}
		this.atk /= FULL ;
		this.rit /= FULL ;
		this.maxHp /= FULL ;
		this.breakDefense /= FULL ;
		this.hit /= FULL ;
		this.dodge /= FULL ;
		this.critAtk /= FULL ;
		this.critRit /= FULL ;
	}
	
	/**
	 * 是否比率
	 * 是: 结果=值/10000
	 */
	
	protected boolean isRate(){
		return true ;
	}

	public float getValue(byte attriType){
		if(AttributeType.maxHP.getType() == attriType){
			return this.maxHp ;
		}
		if(AttributeType.breakDefense.getType() == attriType){
			return this.breakDefense ;
		}
		if(AttributeType.atk.getType() == attriType){
			return this.atk ;
		}
		if(AttributeType.rit.getType() == attriType){
			return this.rit ;
		}
		if(AttributeType.critAtk.getType() == attriType){
			return this.critAtk ;
		}
		if(AttributeType.critRit.getType() == attriType){
			return this.critRit ;
		}
		if(AttributeType.hit.getType() == attriType){
			return this.hit ;
		}
		if(AttributeType.dodge.getType() == attriType){
			return this.dodge ;
		}
		return isRate()?1:0 ;
	}
	
	public List<AttriItem> getAttriItemList(){
		List<AttriItem> list = Lists.newArrayList() ;
		this.append(list, AttributeType.maxHP, maxHp);
		this.append(list, AttributeType.breakDefense, breakDefense);
		this.append(list, AttributeType.atk, atk);
		this.append(list, AttributeType.rit, rit);
		this.append(list, AttributeType.critAtk, critAtk);
		this.append(list, AttributeType.critRit, critRit);
		this.append(list, AttributeType.hit, hit);
		this.append(list, AttributeType.dodge, dodge);
		return list ;
	}
	
	private void append(List<AttriItem> list,AttributeType at,float value){
		if(null == at || value <=0.0){
			return ;
		}
		list.add(new AttriItem(at.getType(),value,false));
	}
}
