package sacred.alliance.magic.app.active.discount.type.attris;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.game.draco.GameContext;

import sacred.alliance.magic.app.active.discount.type.DiscountTypeUpdate;
import sacred.alliance.magic.app.active.vo.Discount;
import sacred.alliance.magic.app.active.vo.DiscountCond;
import sacred.alliance.magic.app.goods.Util;
import sacred.alliance.magic.domain.DiscountDbInfo;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.vo.RoleInstance;

public class DiscountTypeEquipQuality extends DiscountTypeUpdate {

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
			return false ;
		}
		//取人身上装备信息
		List<RoleGoods> equips = role.getEquipBackpack().getEquipGoods();
		if(Util.isEmpty(equips)){
			return false ;
		}
		//<装备品质，装备数量>
		Map<Byte, Byte> qualityNumMap = new HashMap<Byte, Byte>();
		for(RoleGoods goods : equips){
			if(null == goods){
				continue;
			}
			GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(goods.getGoodsId());
			if(null == gb){
				continue;
			}
			this.updateTypeNum(qualityNumMap, gb.getQualityType(), (byte)1);
		}
		return this.updateCondCount(condList, discountDbInfo, qualityNumMap, value);
	}

}
