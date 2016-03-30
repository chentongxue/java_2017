package com.game.draco.app.quest.base;

import com.game.draco.GameContext;

import sacred.alliance.magic.constant.TextId;

public enum QuestOpCode {
	success(1,""),
	questNotExists(2,TextId.QUEST_NOT_EXISTS),
	canotAccept(3,TextId.QUEST_CAN_NOT_ACCEPT),
	quetLogIsFull(4,TextId.QUEST_QUEST_LOG_IS_FULL),
	cannotSubmit(5,TextId.QUEST_CAN_NOT_SUBMIT),
	notEnoughMoney(6,TextId.QUEST_NOT_ENOUGH_MONEY),
	notEnoughGoods(7,TextId.QUEST_NOT_ENOUGH_GOODS),
	bagfullOrNoGoods(9,TextId.QUEST_BAG_FULL_OR_NO_GOODS),
	tooFar(10,TextId.QUEST_TOO_FAR),
	;
	QuestOpCode(int code,String info){
		this.code = code ;
		this.info = info ;
	}
	
	public int code ;
	public String info ;
	
	public int getCode() {
		return code;
	}
	
	public String getInfo() {
		return GameContext.getI18n().getText(info);
	}
	
	
}
