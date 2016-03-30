package com.game.draco.app.operate.discount.type.attris;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sacred.alliance.magic.app.goods.Util;
import sacred.alliance.magic.app.goods.behavior.derive.GoodsDeriveSupport;
import sacred.alliance.magic.domain.GoodsRune;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.app.operate.discount.config.Discount;
import com.game.draco.app.operate.discount.config.DiscountCond;
import com.game.draco.app.operate.discount.domain.RoleDiscount;
import com.game.draco.app.operate.discount.type.DiscountTypeLogic;
import com.game.draco.app.rune.domain.MosaicRune;

public class DiscountTypeEquipMosaic extends DiscountTypeLogic {

	@Override
	public boolean isCurCountMeet(int curCount) {
		return (curCount == 0);
	}

	public void updateCondCount(RoleInstance role, RoleDiscount discountDbInfo, Discount discount, int value, boolean online) {
		List<DiscountCond> condList = discount.getCondList();
		if(Util.isEmpty(condList)){
			return;
		}
		//取人身上装备信息
		//TODO
		List<RoleGoods> equips = null ;//role.getEquipBackpack().getEquipGoods();
		if(Util.isEmpty(equips)){
			return;
		}
		//<宝石级数，宝石数量>
		Map<Byte, Byte> levelNumMap = new HashMap<Byte, Byte>();
		for(RoleGoods equip : equips){
			if(null == equip){
				continue;
			}
			MosaicRune[] holes = equip.getMosaicRune();
			if(null == holes || holes.length <= 0){
				continue;
			}
			for(MosaicRune hole : holes){
				if(null == hole){
					continue;
				}
				GoodsRune gemTemplate = GoodsDeriveSupport.getGemTemplate(hole.getGoodsId());
				if(null == gemTemplate){
					continue;
				}
				this.updateTypeNum(levelNumMap, (byte)gemTemplate.getLevel(), (byte)1);
			}
			
		}
		this.updateCondCount(condList, discountDbInfo, levelNumMap, value);
	}
	
	/**
	 * 服务于xx类型xx值的条件
	 * @param condList
	 * @param dbInfo
	 * @param typeNumMap
	 * @param value
	 */
	protected boolean updateCondCount(List<DiscountCond> condList, RoleDiscount dbInfo, Map<Byte, Byte> typeNumMap, int value){
		if(Util.isEmpty(typeNumMap)){
			return false;
		}
		boolean setTime = false ;
		for(int i=0; i<condList.size(); i++){
			DiscountCond cond = condList.get(i);
			if(null == cond){
				continue;
			}
			int condCount = dbInfo.getMeetCount(i);
			byte type = (byte)(cond.getParam1());
			Byte num = typeNumMap.get(type);
			if(null == num || num <= 0){
				continue;
			}
			if(isCurCountMeet(condCount) && cond.isMeet(condCount, num)){
				setTime = true ;
				dbInfo.updateCondCount(i);
			}
		}
		if(setTime){
			Date now = new Date();
			dbInfo.setOperateDate(now);
		}
		return setTime ;
	}
	
	/**
	 * 服务于xx类型xx值的条件
	 * @param map
	 * @param type
	 * @param count
	 */
	protected void updateTypeNum(Map<Byte, Byte> map, byte type, byte count){
		Byte curCount = map.get(type);
		if(null == curCount){
			map.put(type, count);
			return;
		}
		map.put(type, (byte)(curCount + count));
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
		return 0;
	}

}
