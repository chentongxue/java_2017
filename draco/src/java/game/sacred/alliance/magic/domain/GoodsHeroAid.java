package sacred.alliance.magic.domain;

import java.util.List;

import lombok.Data;
import sacred.alliance.magic.app.attri.AttriItem;

import com.game.draco.message.item.GoodsBaseHeroAidItem;
import com.game.draco.message.item.GoodsBaseItem;

public @Data class GoodsHeroAid extends GoodsBase{
	
	private int swallowExp ;

	
	@Override
	public List<AttriItem> getAttriItemList() {
		return null ;
	}
	
	@Override
	public GoodsBaseItem getGoodsBaseInfo(RoleGoods roleGoods) {
		GoodsBaseHeroAidItem item = new GoodsBaseHeroAidItem();
		this.setGoodsBaseItem(roleGoods, item);
		item.setSwallowExp(swallowExp);
		item.setSecondType(secondType);
		return item;
	}
	
	
	
	@Override
	public void init(Object initData) {
		
	}
	
}
