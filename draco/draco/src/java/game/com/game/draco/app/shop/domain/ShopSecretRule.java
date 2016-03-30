package com.game.draco.app.shop.domain;

import java.util.HashMap;
import java.util.Map;

import lombok.Data;

public @Data class ShopSecretRule {
	private int sumWeight;
	/**
	 * key:id , value:权重
	 */
	private Map<Integer, Integer> ruleMap = new HashMap<Integer, Integer>();
}
