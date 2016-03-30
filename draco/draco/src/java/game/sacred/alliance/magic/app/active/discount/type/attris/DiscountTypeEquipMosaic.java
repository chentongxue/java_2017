package sacred.alliance.magic.app.active.discount.type.attris;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sacred.alliance.magic.app.active.discount.type.DiscountTypeUpdate;
import sacred.alliance.magic.app.active.vo.Discount;
import sacred.alliance.magic.app.active.vo.DiscountCond;
import sacred.alliance.magic.app.goods.MosaicHole;
import sacred.alliance.magic.app.goods.Util;
import sacred.alliance.magic.app.goods.behavior.derive.GoodsDeriveSupport;
import sacred.alliance.magic.domain.DiscountDbInfo;
import sacred.alliance.magic.domain.GoodsGem;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.vo.RoleInstance;

public class DiscountTypeEquipMosaic extends DiscountTypeUpdate {

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
		//取人身上装备信息
		List<RoleGoods> equips = role.getEquipBackpack().getEquipGoods();
		if(Util.isEmpty(equips)){
			return false;
		}
		//<宝石级数，宝石数量>
		Map<Byte, Byte> levelNumMap = new HashMap<Byte, Byte>();
		for(RoleGoods equip : equips){
			if(null == equip){
				continue;
			}
			MosaicHole[] holes = equip.getMosaicHoles();
			if(null == holes || holes.length <= 0){
				continue;
			}
			for(MosaicHole hole : holes){
				if(null == hole){
					continue;
				}
				GoodsGem gemTemplate = GoodsDeriveSupport.getGemTemplate(hole.getGoodsId());
				if(null == gemTemplate){
					continue;
				}
				this.updateTypeNum(levelNumMap, (byte)gemTemplate.getLevel(), (byte)1);
			}
			
		}
		return this.updateCondCount(condList, discountDbInfo, levelNumMap, value);
	}
}
