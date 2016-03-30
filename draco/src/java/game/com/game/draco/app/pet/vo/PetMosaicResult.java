package com.game.draco.app.pet.vo;

import lombok.Data;
import sacred.alliance.magic.base.Result;

import com.game.draco.message.item.PetMosaicRuneItem;

public @Data class PetMosaicResult extends Result {
	
	private int petId;
	private byte holeNum;
	private int mosaicMoney;
	private byte isHavaRune;
	private PetMosaicRuneItem mosaicRuneItem = new PetMosaicRuneItem();
	private int battleScore;

}
