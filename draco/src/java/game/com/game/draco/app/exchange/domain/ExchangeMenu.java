package com.game.draco.app.exchange.domain;


import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import sacred.alliance.magic.util.KeySupport;
import sacred.alliance.magic.vo.RoleInstance;

public @Data class ExchangeMenu implements KeySupport<Integer>{
	//xls
	private int id;
	private String name;
	private int consumeType;
	private String npcId;
	private byte type;
	private List<ExchangeItem> childExchanges = new ArrayList<ExchangeItem>();

	@Override
	public Integer getKey() {
		return this.id ;
	}
	
	public boolean canDisplay(RoleInstance role){
		for(ExchangeItem exchangeItem : childExchanges){
			if(!exchangeItem.isOutDate() && exchangeItem.isMeetConditionsAndDis(role)){
				return true;
			}
		}
		return false;
	}
}
