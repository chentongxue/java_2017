package com.game.draco.app.goddess.config;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import sacred.alliance.magic.app.attri.AttriItem;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.util.KeySupport;

import com.game.draco.message.item.AttriTypeValueItem;

public @Data class GoddessLevelup extends GoddessAttribute implements KeySupport<String>{
	private int goddessId; //女神id
	private int level; //等级
	private int maxExp; //升级所需经验
	
	@Override
	public String getKey() {
		return goddessId + Cat.underline + level;
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
