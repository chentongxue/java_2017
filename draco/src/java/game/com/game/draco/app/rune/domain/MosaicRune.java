package com.game.draco.app.rune.domain;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import sacred.alliance.magic.app.attri.AttriBuffer;
import sacred.alliance.magic.app.attri.AttriItem;
import sacred.alliance.magic.base.BindingType;
import sacred.alliance.magic.domain.GoodsRune;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.util.Util;

import com.game.draco.GameContext;
import com.game.draco.message.item.AttriTypeStrValueItem;
import com.game.draco.message.item.GoodsBaseGemItem;
import com.game.draco.message.item.GoodsBaseItem;
import com.game.draco.message.item.GoodsLiteItem;
import com.google.common.collect.Lists;

/**
 * 封装镶嵌的符文信息
 */
public @Data class MosaicRune {

	private byte hole;// 镶嵌的孔位
	private int goodsId; // 所镶嵌的符文模板ID
	private List<AttriItem> attriList = Lists.newArrayList();

	public MosaicRune() {
	}

	public MosaicRune(byte hole, int goodsId, List<AttriItem> attriList) {
		this.hole = hole;
		this.goodsId = goodsId;
		this.attriList = attriList;
	}

	public RoleGoods getRoleGoods(String roleId) {
		GoodsRune goodsRune = GameContext.getGoodsApp().getGoodsTemplate(GoodsRune.class, this.goodsId);
		if (null == goodsRune) {
			return null;
		}
		RoleGoods roleRune = goodsRune.createSingleRoleGoods(roleId, 1);
		roleRune.setBind(BindingType.already_binding.getType());
		roleRune.setAttrVarList((ArrayList<AttriItem>) this.attriList);
		return roleRune;
	}

	public GoodsLiteItem getGoodsLiteItem() {
		GoodsRune goodsRune = GameContext.getGoodsApp().getGoodsTemplate(GoodsRune.class, this.goodsId);
		if (null == goodsRune) {
			return null;
		}
		GoodsLiteItem item = goodsRune.getGoodsLiteItem();
		item.setNum((byte) 1);// 数量
		item.setBindType(BindingType.already_binding.getType());// 绑定类型
		item.setStar(goodsRune.getRuneAttributeType(this.attriList));// 位运算，宝石属性
		return item;
	}

	public GoodsBaseItem getGoodsBaseItem(String roleId) {
		GoodsRune goodsRune = GameContext.getGoodsApp().getGoodsTemplate(GoodsRune.class, this.goodsId);
		if (null == goodsRune) {
			return null;
		}
		GoodsBaseItem item = goodsRune.getGoodsBaseInfo(this.getRoleGoods(roleId));
		if (null == item) {
			return null;
		}
		GoodsBaseGemItem runeItem = (GoodsBaseGemItem) item;
		runeItem.setItemes(this.getAttriStrValueList());
		runeItem.setSecondType(goodsRune.getSecondType());
		runeItem.setLvLimit((byte) goodsRune.getLvLimit());
		return item;
	}

	public List<AttriTypeStrValueItem> getAttriStrValueList() {
		List<AttriTypeStrValueItem> list = Lists.newArrayList();
		for (AttriItem attriItem : this.attriList) {
			AttriTypeStrValueItem item = new AttriTypeStrValueItem();
			item.setType(attriItem.getAttriTypeValue());
			int value = (int) attriItem.getValue();
			item.setValue(String.valueOf(value));
			list.add(item);
		}
		return list;
	}

	public AttriBuffer getRuneAttriButeBuffer() {
		AttriBuffer buffer = AttriBuffer.createAttriBuffer();
		if (!Util.isEmpty(this.attriList)) {
			buffer.append(this.attriList);
		}
		return buffer;
	}

}
