package sacred.alliance.magic.domain;

import java.util.List;

import com.game.draco.message.item.GoodsBaseContainItem;
import com.game.draco.message.item.GoodsBaseItem;

import lombok.Data;
import sacred.alliance.magic.app.attri.AttriItem;
import sacred.alliance.magic.app.goods.Util;

/**
 * 容器
 * 
 *
 */
public @Data class GoodsContain extends GoodsApply {

	private int grid;

	@Override
	public List<AttriItem> getAttriItemList() {
		return null;
	}

	@Override
	public void init(Object initData) {
	}


	

	@Override
	public GoodsBaseItem getGoodsBaseInfo(RoleGoods roleGoods) {
		GoodsBaseContainItem item = new GoodsBaseContainItem();
		this.setGoodsBaseItem(roleGoods, item);
		item.setSecondType((byte)secondType);
		item.setDesc(Util.replace(desc));
		item.setGrid((byte)this.grid);
		return item;
	}

	
}
