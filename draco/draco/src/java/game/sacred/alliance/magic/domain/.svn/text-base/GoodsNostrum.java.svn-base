package sacred.alliance.magic.domain;

import java.util.List;

import lombok.Data;
import sacred.alliance.magic.app.attri.AttriItem;

import com.game.draco.message.item.GoodsBaseItem;
import com.game.draco.message.item.GoodsBaseNostrumItem;

public @Data class GoodsNostrum extends GoodsBase{
	
	private byte attrType;//属性类型
	private int attrValue;//属性值
	
	@Override
	public List<AttriItem> getAttriItemList() {
		return null;
	}
	
	@Override
	public GoodsBaseItem getGoodsBaseInfo(RoleGoods roleGoods) {
		GoodsBaseNostrumItem item = new GoodsBaseNostrumItem();
		this.setGoodsBaseItem(roleGoods, item);
		item.setAttrType(this.activateType);
		item.setAttrValue(this.attrValue);
		return item;
	}
	
	@Override
	public void init(Object initData) {
		
	}
}
