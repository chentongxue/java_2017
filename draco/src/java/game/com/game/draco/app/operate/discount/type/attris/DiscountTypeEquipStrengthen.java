package com.game.draco.app.operate.discount.type.attris;

import java.util.Date;
import java.util.List;

import sacred.alliance.magic.app.goods.Util;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.app.operate.discount.config.Discount;
import com.game.draco.app.operate.discount.config.DiscountCond;
import com.game.draco.app.operate.discount.domain.RoleDiscount;
import com.game.draco.app.operate.discount.type.DiscountTypeLogic;

public class DiscountTypeEquipStrengthen extends DiscountTypeLogic {

	@Override
	public boolean isCurCountMeet(int curCount) {
		return (curCount == 0);
	}
	
	public void updateCondCount(RoleInstance role, RoleDiscount discountDbInfo, Discount discount, int value, boolean online) {
		List<DiscountCond> condList = discount.getCondList();
		if(Util.isEmpty(condList)){
			return;
		}
		this.updateCondCount(role, condList, discountDbInfo);
		/*//取人身上装备信息
		List<RoleGoods> equips = role.getEquipBackpack().getEquipGoods();
		if(Util.isEmpty(equips)){
			return false;
		}
		//<装备品质，装备数量>
		Map<Byte, Byte> qualityNumMap = new HashMap<Byte, Byte>();
		for(RoleGoods goods : equips){
			if(null == goods){
				continue;
			}
			this.updateTypeNum(qualityNumMap, goods.getStarNum(), (byte)1);
		}
		return this.updateCondCount(condList, discountDbInfo, qualityNumMap, value);*/
	}
	
	protected boolean updateCondCount(RoleInstance role,List<DiscountCond> condList, 
			RoleDiscount dbInfo){
		boolean setTime = false ;
		for(int i=0; i<condList.size(); i++){
			DiscountCond cond = condList.get(i);
			if(null == cond){
				continue;
			}
			int condCount = dbInfo.getMeetCount(i);
			//要求强化等级
			int strengLevel = cond.getParam1();
			if(isCurCountMeet(condCount)){
				//int num = role.getEquipBackpack().totalEffectStrengthenLevel(strengLevel);
				//TODO:
				int num = 0 ;
				if(cond.isMeet(condCount, num)){
					setTime = true ;
					dbInfo.updateCondCount(i);
				}
			}
		}
		if(setTime){
			Date now = new Date();
			dbInfo.setOperateDate(now);
		}
		return setTime ;
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

	/**
	 * 强化等级>=要求的强化等级就算数
	 *//*
	@Override
	protected void updateTypeNum(Map<Byte, Byte> map, byte strengLevel, byte count){
		Byte curCount = map.get(strengLevel);
		if(null == curCount){
			map.put(strengLevel, count);
			return;
		}
		map.put(strengLevel, (byte)(curCount + count));
		for(byte i=1;i<strengLevel;i++){
			updateTypeNum(map,i,count);
		}
	}*/
}
