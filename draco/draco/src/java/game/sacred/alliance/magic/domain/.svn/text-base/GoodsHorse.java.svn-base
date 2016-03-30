package sacred.alliance.magic.domain;

import java.util.List;

import lombok.Data;
import sacred.alliance.magic.app.attri.AttriItem;

import com.game.draco.message.item.GoodsBaseHorseItem;
import com.game.draco.message.item.GoodsBaseItem;

public @Data class GoodsHorse extends GoodsBase {

	//坐骑ID
	private int horseId;
	
	@Override
	public List<AttriItem> getAttriItemList() {
		return null;
	}

	@Override
	public GoodsBaseItem getGoodsBaseInfo(RoleGoods roleGoods) {
		GoodsBaseHorseItem item = new GoodsBaseHorseItem();
		this.setGoodsBaseItem(roleGoods, item);
		item.setResId((short) this.getResId());
		return item;
	}

	@Override
	public void init(Object initData) {
	}
	
}
