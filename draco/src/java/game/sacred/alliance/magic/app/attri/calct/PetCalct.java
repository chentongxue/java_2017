package sacred.alliance.magic.app.attri.calct;

import java.util.List;
import java.util.Map;

import sacred.alliance.magic.app.attri.AttriBuffer;
import sacred.alliance.magic.app.attri.AttriItem;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.util.Util;

import com.game.draco.GameContext;
import com.game.draco.app.pet.PetAppImpl;
import com.game.draco.app.pet.config.AttributePetLevelConfig;
import com.game.draco.app.pet.config.PetAttribute;
import com.game.draco.app.pet.domain.GoodsPet;
import com.game.draco.app.pet.domain.RolePet;
import com.game.draco.app.rune.domain.MosaicRune;
import com.google.common.collect.Maps;

public class PetCalct extends DefaultCalct<RolePet> {
	private RoleFormulaCalct roleFormulaCalct;

	@Override
	protected void autoUpgrade(RolePet role) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void changeExp(RolePet role, AttriItem item) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void changeLevel(RolePet role, AttriItem item) {
		// TODO Auto-generated method stub

	}

	@Override
	protected int getByLevelup(RolePet rolePet, AttributeType attriType) {
		return 0;
	}

	@Override
	protected Map<Byte, AttriItem> getByMultAdvanced(RolePet rolePet) {
		int petId = rolePet.getPetId();
		AttributePetLevelConfig levelRate = GameContext.getPetApp().getAttributePetLevelConfig(rolePet.getLevel());
		PetAttribute petRate = GameContext.getPetApp().getAttributePetRateConfig(petId);
		PetAttribute qualityRate = GameContext.getPetApp().getAttributePetQualityConfig(rolePet.getQuality(), rolePet.getStar());
		PetAttribute typeRate = GameContext.getPetApp().getAttributePetTypeConfig();
		PetAttribute bornRate = null;
		GoodsPet pet = GameContext.getGoodsApp().getGoodsTemplate(GoodsPet.class, petId);
		if (null != pet) {
			bornRate = GameContext.getPetApp().getAttributePetBornConfig(rolePet.getQuality(), pet.getStar());
		}
		List<AttributeType> attributeTypeList = PetAppImpl.attributeTypeList;
		Map<Byte, AttriItem> attriMap = Maps.newHashMap();
		for (AttributeType at : attributeTypeList) {
			byte attType = at.getType();
			float bornValue = this.getAttriValue(bornRate, attType) * this.getAttriValue(petRate, attType) * this.getAttriValue(typeRate, attType);
			float growValue = this.getAttriValue(qualityRate, attType) * this.getAttriValue(levelRate, attType) * this.getAttriValue(petRate, attType)
					* this.getAttriValue(typeRate, attType);
			int totalValue = Math.max(0, (int) bornValue) + Math.max(0, (int) growValue);
			AttriItem item = new AttriItem(attType, totalValue, false);
			attriMap.put(attType, item);
		}
		// 获得符文加成
		Map<Byte, MosaicRune> mosaicRuneMap = rolePet.getMosaicRuneMap();
		if (!Util.isEmpty(mosaicRuneMap)) {
			for (MosaicRune mosaicRune : mosaicRuneMap.values()) {
				List<AttriItem> attriList = mosaicRune.getAttriList();
				for (AttriItem item : attriList) {
					if (null == item) {
						continue;
					}
					AttriItem petItem = attriMap.get(item.getAttriTypeValue());
					petItem.setValue(petItem.getValue() + item.getValue());
					attriMap.put(item.getAttriTypeValue(), petItem);
				}
			}
		}
		return attriMap;
	}

	private float getAttriValue(PetAttribute rate, byte type) {
		if (null == rate) {
			return 1;
		}
		return rate.getValue(type);
	}

	@Override
	protected int getFormulaCalct(RolePet role, AttributeType attriType) {
		return roleFormulaCalct.getBaseValue(attriType);
	}

	@Override
	protected void otherEffect(RolePet role, AttriBuffer buffer) {
		// TODO Auto-generated method stub

	}

	@Override
	public void bornAtrri(RolePet role) {
		// TODO Auto-generated method stub

	}
	
	public void setRoleFormulaCalct(RoleFormulaCalct roleFormulaCalct) {
		this.roleFormulaCalct = roleFormulaCalct;
	}

}
