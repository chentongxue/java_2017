package com.game.draco.app.shop;

import java.util.Comparator;

public class ShopGoodsComparator implements Comparator<ShopGoodsComparable> {

	@Override
	public int compare(ShopGoodsComparable o1, ShopGoodsComparable o2) {
		return o1.getWeight() - o2.getWeight();
	}

}
