package com.game.draco.app.operate.discount.type.attris;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import sacred.alliance.magic.app.attri.AttriItem;
import sacred.alliance.magic.app.goods.Util;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.app.operate.discount.config.Discount;
import com.game.draco.app.operate.discount.domain.RoleDiscount;
import com.game.draco.app.operate.discount.type.DiscountTypeLogic;

public class DiscountTypeEquipRecasting extends DiscountTypeLogic {

	private int getValue(RoleInstance role, RoleDiscount discountDbInfo, int value) {
		//取全身装备
		List<RoleGoods> equips = null ; //role.getEquipBackpack().getEquipGoods();
		//TODO
		if(Util.isEmpty(equips)){
			return 0;
		}
		int recastingTotal = 0;
		for(RoleGoods goods : equips){
			if(null == goods){
				continue;
			}
			ArrayList<AttriItem> attriItemes = goods.getAttrVarList();
			if(Util.isEmpty(attriItemes)){
				continue;
			}
			for(AttriItem attriItem : attriItemes){
				if(null == attriItem){
					continue;
				}
				int star = (int)((attriItem.getValue() / 10) + 1);
				recastingTotal += star;
			}
		}
		return recastingTotal;
	}

	@Override
	public boolean isCurCountMeet(int curCount) {
		return (curCount == 0);
	}

	@Override
	public boolean isSameCycle(RoleDiscount discountDbInfo, Date now) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void updateCount(RoleInstance role, RoleDiscount roleDiscount, Discount discount, int value, boolean online) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getCurrValue(RoleDiscount roleDiscount) {
		// TODO Auto-generated method stub
		return 0;
	}

}
