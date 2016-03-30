package com.game.draco.app.equip.config;

import lombok.Data;
import sacred.alliance.magic.util.KeySupport;

public @Data class StarMaterialFormula extends FormulaSupport implements KeySupport<String>{

	private int goodsId ;
	
	@Override
	public String getKey() {
		return String.valueOf(goodsId);
	}

}
