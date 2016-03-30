package com.game.draco.app.pet.domain;

import java.util.List;
import java.util.Map;

import lombok.Data;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.ForceRelation;
import sacred.alliance.magic.base.RoleType;
import sacred.alliance.magic.component.id.IdFactory;
import sacred.alliance.magic.component.id.IdType;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.domain.GoodsRune;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.rune.domain.MosaicRune;
import com.game.draco.base.QualityStarSupport;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public @Data class RolePet extends AbstractRole implements QualityStarSupport{

	public RolePet() {
		this.roleType = RoleType.PET;
		try {
			this.setRoleId(IdFactory.getInstance().nextId(IdType.PET));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public final static String MASTER_ID = "masterId";
	public final static String ROLE_ID = "roleId";
	public final static String PET_ID = "petId";

	private String masterId;// 角色Id
	private int petId;// 宠物Id
	private byte star = 0;// 宠物星级
	private int starProgress = 0;// 宠物当前提升星级
	private String mosaic;// 宠物镶嵌符文
	private int score = 0;// 宠物战斗力
	private byte quality = 0;// 宠物品质

	private Map<Byte, MosaicRune> mosaicRuneMap = Maps.newHashMap();// 镶嵌的符文
	private byte onBattle;
	private RoleInstance role;
	private int petAtk ;
	
	
	@Override
	public boolean set(byte enumValue, int value) {
		AttributeType attriType = AttributeType.get(enumValue);
		switch (attriType) {
			case petAtk:
				this.petAtk = value ; return true ;
			default:
				return super.set(enumValue, value) ;
		}
	}
	
	public int get(AttributeType attriType) {
		if (null == attriType) {
			return 0;
		}
		switch (attriType) {
		case petAtk:
			return this.petAtk;
		default:
			return super.get(attriType);
		}
	}
	
	
	public Map<Byte, MosaicRune> getMosaicRuneMap() {
		if (Util.isEmpty(this.mosaicRuneMap)) {
			Map<Byte, MosaicRune> mosaicRuneMap = GameContext.getRuneApp().getMosaicRuneMap(this.mosaic);
			if (Util.isEmpty(mosaicRuneMap)) {
				return null;
			}
			this.mosaicRuneMap = mosaicRuneMap;
		}
		return this.mosaicRuneMap;
	}

	public List<MosaicRune> getMosaicRuneList() {
		List<MosaicRune> mosaicRuneList = Lists.newArrayList();
		Map<Byte, MosaicRune> mosaicRuneMap = this.getMosaicRuneMap();
		if (Util.isEmpty(mosaicRuneMap)) {
			return mosaicRuneList;
		}
		mosaicRuneList.addAll(mosaicRuneMap.values());
		return mosaicRuneList;
	}
	
	// 获得孔位镶嵌的符文
	public MosaicRune getMosaicRune(byte hole) {
		Map<Byte, MosaicRune> mosaicRuneMap = this.getMosaicRuneMap();
		if (Util.isEmpty(mosaicRuneMap)) {
			return null;
		}
		return mosaicRuneMap.get(hole);
	}
	
	/**
	 * 统计镶嵌在宠物身上等级超过level的个数
	 * @param level
	 * @return
	 */
	public int countGemLevel(int level) {
		Map<Byte, MosaicRune> mosaicRuneMap = this.getMosaicRuneMap();
		if (Util.isEmpty(mosaicRuneMap)) {
			return 0;
		}
		int total = 0;
		for (MosaicRune hole : mosaicRuneMap.values()) {
			if (null == hole || hole.getGoodsId() <= 0) {
				continue;
			}
			GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(hole.getGoodsId());
			if (null == gb) {
				continue;
			}
			if (gb.getLevel() < level) {
				continue;
			}
			total++;
		}
		return total;
	}

	// 获得孔位镶嵌符文的模版ID
	public int getMosaicRuneId(byte hole) {
		Map<Byte, MosaicRune> mosaicRuneMap = this.getMosaicRuneMap();
		if (Util.isEmpty(mosaicRuneMap)) {
			return 0;
		}
		MosaicRune mosaicRune = mosaicRuneMap.get(hole);
		if (null == mosaicRune) {
			return 0;
		}
		return mosaicRune.getGoodsId();
	}

	// 镶嵌符文
	public void mosaicRune(MosaicRune mosaicRune) {
		this.mosaicRuneMap.put(mosaicRune.getHole(), mosaicRune);
		this.mosaic = GameContext.getRuneApp().getMosaicString(this.mosaicRuneMap);
	}

	// 卸下符文
	public MosaicRune dismountRune(byte hole) {
		MosaicRune mosaicRune = this.mosaicRuneMap.remove(hole);
		this.mosaic = GameContext.getRuneApp().getMosaicString(this.mosaicRuneMap);
		return mosaicRune;
	}
	
	// 清空镶嵌信息
	public void clearRune() {
		this.mosaicRuneMap.clear();
		this.mosaic = "";
	}

	// 是否镶嵌了该类型符文
	public boolean isMosaicSameTypeRune(GoodsRune goodsRune) {
		if (1 != goodsRune.getSecondType()) {
			return false;
		}
		Map<Byte, MosaicRune> mosaicRuneMap = this.getMosaicRuneMap();
		if (Util.isEmpty(mosaicRuneMap)) {
			return false;
		}
		// 获取单属性符文属性类型
		short runeAttriType = goodsRune.getRuneAttributeType();
		for (MosaicRune mosaicRune : mosaicRuneMap.values()) {
			short mosaicType = goodsRune.getRuneAttributeType(mosaicRune.getAttriList());
			if (runeAttriType == mosaicType) {
				return true;
			}
		}
		return false;
	}

	// 宠物是否镶嵌宝石
	public boolean isMosaicRune() {
		if (Util.isEmpty(this.mosaicRuneMap)) {
			return false;
		}
		return true;
	}
	
	@Override
	public byte getAttriGearId() {
		return 0;
	}

	@Override
	public byte getAttriSeriesId() {
		return 0;
	}


	@Override
	public AbstractRole getMasterRole() {
		return this.role;
	}

	@Override
	public String getRoleName() {
		return null;
	}

	
	@Override
	public ForceRelation getForceRelation(AbstractRole target) {
		return this.getMasterRole().getForceRelation(target);
	}

	// 获得宠物的实例ID
	public int getPetInstanceId() {
		return this.getIntRoleId();
	}

}
