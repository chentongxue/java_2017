package sacred.alliance.magic.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.game.draco.message.item.AttriTypeStrValueItem;
import com.game.draco.message.item.GoodsBaseGemItem;
import com.game.draco.message.item.GoodsBaseItem;

import lombok.Data;
import sacred.alliance.magic.app.attri.AttriItem;
import sacred.alliance.magic.base.AttributeType;

/**
 * 宝石
 * 
 * @author Administrator
 * 
 */
public @Data class GoodsGem extends GoodsBase {

	//宝石属性
	private byte attrType1;	
	private int  attrValue1;	
	private byte attrType2;	
	private int attrValue2;
	private byte attrType3;	
	private int  attrValue3;	
	
	private int gs;
	

	private Map<Byte, Integer> attriMap = new HashMap<Byte, Integer>();
	
	private void initAttriMap() {
		this.addAttriMap(attrType1, attrValue1);
		this.addAttriMap(attrType2, attrValue2);
		this.addAttriMap(attrType3, attrValue3);
	}
	
	
	
	@Override
	public GoodsBaseItem getGoodsBaseInfo(RoleGoods roleGoods){
		GoodsBaseGemItem it = new GoodsBaseGemItem();
		this.setGoodsBaseItem(roleGoods, it);
		it.setSecondType((byte) secondType);
		it.setQualityType((byte) qualityType);
		//it.setColorType(color);
		it.setLvLimit((byte)lvLimit);
		it.setItemes(this.getDisplayAttriItem());
		//it.setDesc(Util.replace(this.getDesc()).equals("0") ? "" : Util.replace(this.getDesc()));
		it.setDesc(this.desc);
		return it;
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
	
}
