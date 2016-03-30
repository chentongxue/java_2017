package com.game.draco.app.union.battle.config;

import java.util.List;

import com.game.draco.GameContext;
import com.game.draco.app.npc.domain.NpcTemplate;
import com.game.draco.message.item.GoodsLiteNamedItem;
import com.google.common.collect.Lists;

import lombok.Data;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.util.KeySupport;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;

public @Data class UnionBattleConfig implements KeySupport<Integer>{
	//xls
	private int battleId;
	private String name;
	private byte mapIndex;  //key
	private String map1;
	private String map2;
//	private String map3;
	private String exchangeId;
	private String bossId;

	private String npcId;
	private int x;
	private int y;
	private String attackerEnterNotice;
	private String defenderEnterNotice;
	private String defaultEnterNotice;
	//奖励
	private String awardIds;
	//可改名标识
	private byte renamable;//1：可改名，0：不可改名
	//
	private List<GoodsLiteNamedItem> goodsLiteNamedItemList = Lists.newArrayList();
	
	private byte battling;//1：交战中，0：未开战
	@Override
	public Integer getKey() {
		return battleId;
	}
	public void init(){
		if(Util.isEmpty(awardIds)){
			checkFail("UnionBattleConfig.init(): awardIds is not configured"+ awardIds);
		}
		String[] ids = Util.splitString(awardIds);
		try {
			for(String id: ids){
				int goodsId = Integer.parseInt(id);
				GoodsBase goodsBase = GameContext.getGoodsApp().getGoodsBase(goodsId);
				if(null == goodsBase){
					checkFail("UnionBattleConfig.init(): goodsId = " + goodsId + "not exsit");
					continue;
				}
				GoodsLiteNamedItem goodsLiteNamedItem = goodsBase.getGoodsLiteNamedItem();
				goodsLiteNamedItemList.add(goodsLiteNamedItem);
			}
		} catch (Exception e) {
			checkFail("UnionBattleConfig.init():"+ e.toString());
		}
	}
	public void check(){
		NpcTemplate boss = GameContext.getNpcApp().getNpcTemplate(bossId);
		if (boss == null) {
			checkFail("UnionBattleConfig.check() err: NPC not exist, bossId = "
					+ bossId);
		}
	}
	private void checkFail(String errInfo) {
		Log4jManager.CHECK.error(errInfo);
		Log4jManager.checkFail();
	}
}
