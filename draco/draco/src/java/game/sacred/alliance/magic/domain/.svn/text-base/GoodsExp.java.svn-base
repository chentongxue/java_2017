package sacred.alliance.magic.domain;

import java.util.List;

import com.game.draco.message.item.GoodsBaseExpItem;
import com.game.draco.message.item.GoodsBaseItem;

import lombok.Data;
import sacred.alliance.magic.app.attri.AttriItem;

public @Data class GoodsExp extends GoodsApply{

	private short triggerBuffId;
	private int triggerBuffLv;
	
	@Override
	public List<AttriItem> getAttriItemList() {
		return null;
	}
	
	@Override
	public GoodsBaseItem getGoodsBaseInfo(RoleGoods roleGoods) {
		GoodsBaseExpItem item = new GoodsBaseExpItem();
		this.setGoodsBaseItem(roleGoods, item);
		item.setDesc(desc);
		item.setLvLimit((byte)lvLimit);
		return item;
	}
	
	
	
	@Override
	public void init(Object initData) {
		
	}
}
