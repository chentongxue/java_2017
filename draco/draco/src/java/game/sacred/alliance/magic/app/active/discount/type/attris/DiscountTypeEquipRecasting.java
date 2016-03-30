package sacred.alliance.magic.app.active.discount.type.attris;

import java.util.ArrayList;
import java.util.List;

import sacred.alliance.magic.app.active.discount.type.DiscountTypeUpdate;
import sacred.alliance.magic.app.attri.AttriItem;
import sacred.alliance.magic.app.goods.Util;
import sacred.alliance.magic.domain.DiscountDbInfo;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.vo.RoleInstance;

public class DiscountTypeEquipRecasting extends DiscountTypeUpdate {

	@Override
	public int getValue(RoleInstance role, DiscountDbInfo discountDbInfo, int value) {
		//取全身装备
		List<RoleGoods> equips = role.getEquipBackpack().getEquipGoods();
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

}
