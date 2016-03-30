package sacred.alliance.magic.app.goods;

import lombok.Data;
import sacred.alliance.magic.domain.GoodsContain;

public @Data class ContainerConfig {

	private int defaultGoodsId ;
	private int minGoodsId ;
	
	private GoodsContain defaultGoods ;
	private GoodsContain minGoods ;
}
