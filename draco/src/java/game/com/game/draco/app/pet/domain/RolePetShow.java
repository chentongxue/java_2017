package com.game.draco.app.pet.domain;

import java.util.List;
import java.util.Map;

import sacred.alliance.magic.util.Util;

import com.game.draco.app.rune.domain.MosaicRune;
import com.google.common.collect.Maps;

import lombok.Data;

public @Data class RolePetShow {
	
	private int petId; //模版id
	private int level; //等级
	private byte quality;// 品质
	private byte star; // 星级
	private int battleScore; //战斗力
	private List<MosaicRune> mosaicRuneList; // 宝石
	
	public RolePet createRolePet() {
		RolePet rolePet = new RolePet();
		rolePet.setPetId(this.petId);
		rolePet.setLevel(this.level);
		rolePet.setQuality(this.quality);
		rolePet.setStar(this.star);
		rolePet.setScore(this.battleScore);
		Map<Byte, MosaicRune> mosaicMap = this.getMosaicRuneMap();
		if (!Util.isEmpty(mosaicMap)) {
			rolePet.setMosaicRuneMap(mosaicMap);
		}
		return rolePet;
	}
	
	private Map<Byte, MosaicRune> getMosaicRuneMap() {
		if (Util.isEmpty(this.mosaicRuneList)) {
			return null;
		}
		Map<Byte, MosaicRune> mosaicRuneMap = Maps.newHashMap();
		for (MosaicRune mosaicRune : this.mosaicRuneList) {
			if (null == mosaicRune) {
				continue;
			}
			mosaicRuneMap.put(mosaicRune.getHole(), mosaicRune);
		}
		return mosaicRuneMap;
	}
	
}
