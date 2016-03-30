package sacred.alliance.magic.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.game.draco.GameContext;
import com.game.draco.message.item.AttriTypeStrValueItem;
import com.game.draco.message.item.GoodsBaseGemItem;
import com.game.draco.message.item.GoodsBaseItem;
import com.game.draco.message.item.GoodsLiteItem;
import com.google.common.collect.Lists;

import lombok.Data;
import sacred.alliance.magic.app.attri.AttriItem;
import sacred.alliance.magic.app.goods.Util;
import sacred.alliance.magic.base.AttributeType;

/**
 * 宝石
 * @author Administrator
 */
public @Data class GoodsRune extends GoodsBase {
	
	private static final byte FIFTEEN = 15;

	//宝石属性
	private byte attrType;	
	private int  attrValue;	
	private int gs;
	private Map<Byte, Integer> attriMap = new HashMap<Byte, Integer>();
	
	private void initAttriMap() {
		this.addAttriMap(attrType, attrValue);
	}
	
	@Override
	public GoodsBaseItem getGoodsBaseInfo(RoleGoods roleGoods){
		GoodsBaseGemItem it = new GoodsBaseGemItem();
		this.setGoodsBaseItem(roleGoods, it);
		it.setSecondType(this.secondType);// 单属性，多属性
		it.setLvLimit((byte) this.lvLimit);
		if (1 == this.secondType) {
			it.setAttriType(this.getRuneAttributeType());
			it.setItemes(this.getDisplayAttriItem());
			return it;
		}
		// 容错判断
		if (null == roleGoods) {
			it.setAttriType((short) 0);
			return it;
		}
		it.setAttriType(this.getRuneAttributeType(roleGoods));
		it.setItemes(this.getAttriStrValueList(roleGoods.getAttrVarList()));
		return it;
	}
	
	private List<AttriTypeStrValueItem> getAttriStrValueList(List<AttriItem> attriList) {
		if (Util.isEmpty(attriList)) {
			return null;
		}
		List<AttriTypeStrValueItem> list = Lists.newArrayList();
		for (AttriItem attriItem : attriList) {
			AttriTypeStrValueItem item = new AttriTypeStrValueItem();
			item.setType(attriItem.getAttriTypeValue());
			item.setValue(String.valueOf((int) attriItem.getValue()));
			list.add(item);
		}
		return list;
	}
	
	/**
	 * 初始化宝石属性
	 * @param attriType
	 * @param value
	 */
	private void addAttriMap(byte attriType, int value){
		if(0 >= attriType || 0 >= value){
			return;
		}
		Integer val = attriMap.get(attriType);
		if(null != val){
			value += val;
		}
		attriMap.put(attriType, value);
	}

	/**
	 * 获得宝石属性Item
	 * @return
	 */
	public List<AttriTypeStrValueItem> getDisplayAttriItem() {
		List<AttriTypeStrValueItem> list = new ArrayList<AttriTypeStrValueItem>();
		for (Map.Entry<Byte, Integer> entry : attriMap.entrySet()) {
			AttriTypeStrValueItem item = new AttriTypeStrValueItem();
			item.setType(entry.getKey());
			item.setValue(AttributeType.formatValue(entry.getKey(), entry.getValue()));
			list.add(item);
		}
		return list;
	}

	@Override
	public List<AttriItem> getAttriItemList() {
		List<AttriItem> attrList = new ArrayList<AttriItem>();
		for (Map.Entry<Byte, Integer> entry : attriMap.entrySet()) {
			attrList.add(new AttriItem(entry.getKey(),entry.getValue(),0f));
		}
		return attrList;
	}
	
	@Override
	public void init(Object initData) {
		this.initAttriMap();
	} 

	@Override
	protected void setGoodsLiteItem(GoodsLiteItem item,RoleGoods roleGoods){
		super.setGoodsLiteItem(item, roleGoods);
		if (1 == this.secondType) {
			item.setStar(this.getRuneAttributeType());
			return;
		}
		if (null == roleGoods) {
			item.setStar((byte) 0);
			return;
		}
		item.setStar(this.getRuneAttributeType(roleGoods));
	}
	
	// 得到符文属性（位运算），单属性符文
	public short getRuneAttributeType() {
		return this.getRuneAttributeType(this.getAttriItemList());
	}
	
	// 得到符文属性（位运算），多属性符文
	public short getRuneAttributeType(RoleGoods roleGoods) {
		if (null == roleGoods) {
			return 0;
		}
		return this.getRuneAttributeType(roleGoods.getAttrVarList());
	}
	
	// 得到符文属性（位运算）
	public short getRuneAttributeType(List<AttriItem> attriList) {
		if (Util.isEmpty(attriList)) {
			return 0;
		}
		short type = 0;
		byte rePosition = -1;
		for (AttriItem attriItem : attriList) {
			if (null == attriItem) {
				continue;
			}
			byte position = GameContext.getRuneApp().getAttributePosition(attriItem.getAttriTypeValue());
			if (-1 == position) {
				continue;
			}
			if (this.isPositionHaveFlag(type, position)) {
				rePosition = position;
			}
			type = this.flagAttributeType(type, position);
		}
		if (-1 != rePosition) {
			type = this.flagRepeatType(type, rePosition);
		}
		return type;
	}

	private short flagAttributeType(short type, byte position) {
		return (short) (type | (1 << position));
	}
	
	private boolean isPositionHaveFlag(short type, byte position) {
		return ((type>>position)&1) == 1;
	}
	
	private short flagRepeatType(short type, byte position) {
		short flag = (short) (2 << position);
		if (this.isHighPosition(type, flag)) {
			type = this.flagAttributeType(type, FIFTEEN);
		}
		return type;
	}
	
	private boolean isHighPosition(short type, short flag) {
		return (short) (type & flag) == 0;
	}

	@Override
	public RoleGoods createSingleRoleGoods(String roleId, int overlapCount) {
		RoleGoods roleGoods = super.createSingleRoleGoods(roleId, overlapCount);
		if (1 == this.secondType) {
			return roleGoods;
		}
		roleGoods.setAttrVarList(GameContext.getRuneApp().getAttriItemListforRune(this.secondType, this.level));
		return roleGoods;
	}
	
}
