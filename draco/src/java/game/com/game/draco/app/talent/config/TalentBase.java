package com.game.draco.app.talent.config;

import lombok.Data;

public @Data class TalentBase{
	
	//天赋ID
	private int talentId;
	
	//属性类型
	private byte attrType;
	
	//属性描述
	private String attrDes;

}
