package com.game.draco.app.quest.phase;

import com.game.draco.app.quest.QuestHelper;
import com.game.draco.app.quest.QuestPhaseAdator;
import com.game.draco.app.quest.QuestTerm;
import com.game.draco.app.quest.base.QuestTermType;
import com.game.draco.app.quest.phase.term.TypeTerm;

import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.vo.RoleInstance;

public class AttributeQuestPhase extends QuestPhaseAdator {
	
	private AttributeType attrType;//属性类型
	
	private int value;//数值

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}
	
	@Override
	public boolean isSpecialCurrCount() {
		return QuestTermType.Attribute.isSpecial();
	}
	
	@Override
	public boolean isPhaseComplete(RoleInstance role) {
		if(role.get(this.attrType.getType()) < value){
			return false;
		}
		return true ;
	}
	
	@Override
	public int getAttribute(RoleInstance role,int type) {
		int index = startIndex ;
		for(QuestTerm term : this.questTermList){
			index ++ ;
			if(!String.valueOf(type).equals(term.getParameter())){
				continue ;
			}
			int nowNum = this.getCurrentNum(role, index);
			if(nowNum < term.getCount()){
				//未达到数量，发进度提示
				QuestHelper.pushQuestTipMessage(role, this.master, nowNum, term, index);
				return 0;
			}
			return 1;
		}
		return  0 ;
	}
	
	public AttributeQuestPhase(int type, int value){
		this.attrType = AttributeType.get((byte)type);
		if(null == this.attrType){
			return;
		}
		this.value = value;
		this.questTermList.add(new TypeTerm(QuestTermType.Attribute, value, this.attrType.getType()));
	}
	
	@Override
	public int getCurrentNum(RoleInstance role,int index){
		return role.get(this.attrType.getType());
	}

	public AttributeType getType() {
		return attrType;
	}

	public void setType(AttributeType type) {
		this.attrType = type;
	}
}
