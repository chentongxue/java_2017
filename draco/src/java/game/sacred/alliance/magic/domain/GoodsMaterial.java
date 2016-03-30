package sacred.alliance.magic.domain;

import java.util.List;

import com.game.draco.message.item.GoodsBaseItem;
import com.game.draco.message.item.GoodsBaseMaterialsItem;

import lombok.Data;
import sacred.alliance.magic.app.attri.AttriItem;

/**
 * 材料
 *
 */
public @Data class GoodsMaterial extends GoodsBase{


	@Override
	public GoodsBaseItem getGoodsBaseInfo(RoleGoods roleGoods) {
		GoodsBaseMaterialsItem item = new GoodsBaseMaterialsItem();
		this.setGoodsBaseItem(roleGoods, item);
		item.setDesc(this.getDesc());
		item.setLvLimit((byte) this.getLvLimit());
		item.setSecondType((byte) this.getSecondType());
		
		return item;
	}
	
	@Override
	public List<AttriItem> getAttriItemList() {
		return null;
	}

	@Override
	public void init(Object initData) {
		
	}



	


}
