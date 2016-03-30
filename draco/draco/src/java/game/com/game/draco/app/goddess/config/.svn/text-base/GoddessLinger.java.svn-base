package com.game.draco.app.goddess.config;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import sacred.alliance.magic.app.attri.AttriItem;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.util.KeySupport;

import com.game.draco.message.item.AttriTypeValueItem;

public @Data class GoddessLinger extends GoddessAttribute implements KeySupport<String>{
	private int goddessId; //女神id
	private int lingerNum; //缠绵次数
	private int silverMoney; //消耗游戏币
	private int lq; //消耗灵气
	
	
	@Override
	public String getKey() {
		return this.goddessId + Cat.underline + this.lingerNum;
	}
	
	public List<AttriTypeValueItem> getAttriTypeValueList() {
		List<AttriItem> attriItemList = this.getAttriItemList();
		List<AttriTypeValueItem> list = new ArrayList<AttriTypeValueItem>();
		for(AttriItem attriItem : attriItemList) {
			AttriTypeValueItem item = new AttriTypeValueItem();
			item.setAttriType(attriItem.getAttriTypeValue());
			item.setAttriValue((int)attriItem.getValue());
			list.add(item);
		}
		return list;
	}
	
}
