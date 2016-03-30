package sacred.alliance.magic.app.active.discount.type.attris;

import java.util.Date;
import java.util.List;
import java.util.Map;

import sacred.alliance.magic.app.active.discount.type.DiscountTypeUpdate;
import sacred.alliance.magic.app.active.vo.Discount;
import sacred.alliance.magic.app.active.vo.DiscountCond;
import sacred.alliance.magic.app.goods.Util;
import sacred.alliance.magic.domain.DiscountDbInfo;
import sacred.alliance.magic.vo.RoleInstance;

public class DiscountTypeEquipStrengthen extends DiscountTypeUpdate {

	@Override
	public int getValue(RoleInstance role, DiscountDbInfo discountDbInfo, int value) {
		return 0;
	}

	@Override
	public boolean isCurCountMeet(int curCount) {
		return (curCount == 0);
	}
	
	@Override
	public boolean updateCondCount(RoleInstance role, DiscountDbInfo discountDbInfo, Discount discount, int value) {
		List<DiscountCond> condList = discount.getCondList();
		if(Util.isEmpty(condList)){
			return false;
		}
		return this.updateCondCount(role, condList, discountDbInfo);
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
			DiscountDbInfo dbInfo){
		boolean setTime = false ;
		for(int i=0; i<condList.size(); i++){
			DiscountCond cond = condList.get(i);
			if(null == cond){
				continue;
			}
			int condCount = dbInfo.getCondCount(i);
			//要求强化等级
			int strengLevel = cond.getMinValue();
			if(isCurCountMeet(condCount)){
				int num = role.getEquipBackpack().totalEffectStrengthenLevel(strengLevel);
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
