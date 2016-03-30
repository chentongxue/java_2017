package sacred.alliance.magic.app.goods;

import java.util.List;

import sacred.alliance.magic.domain.GoodsBase;

public interface GoodsBaseInitCallback {

	void callback(List<? extends GoodsBase> goodsBaseList) ;
}
