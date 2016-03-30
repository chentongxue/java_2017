package com.game.draco.app.npc.refresh;

import java.util.ArrayList;
import java.util.List;

import com.game.draco.GameContext;

import lombok.Data;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.util.Log4jManager;

public @Data class BossLoot {
	
	private String lootId ;
	private int goodsId1 ;
	private int goodsId2 ;
	private int goodsId3 ;
	private int goodsId4 ;
	private int goodsId5 ;
	private int goodsId6 ;
	private int goodsId7 ;
	private int goodsId8 ;
	private int goodsId9 ;
	private int goodsId10 ;
	private int goodsId11 ;
	private int goodsId12 ;
	private int goodsId13 ;
	private int goodsId14 ;
	private int goodsId15 ;
	private int goodsId16 ;
	private int goodsId17 ;
	private int goodsId18 ;
	private int goodsId19 ;
	private int goodsId20 ;
	
	private List<Integer> goodsList = new ArrayList<Integer>();
	
	public void init(){
		this.init(this.goodsId1);
		this.init(this.goodsId2);
		this.init(this.goodsId3);
		this.init(this.goodsId4);
		this.init(this.goodsId5);
		this.init(this.goodsId6);
		this.init(this.goodsId7);
		this.init(this.goodsId8);
		this.init(this.goodsId9);
		this.init(this.goodsId10);
		this.init(this.goodsId11);
		this.init(this.goodsId12);
		this.init(this.goodsId13);
		this.init(this.goodsId14);
		this.init(this.goodsId15);
		this.init(this.goodsId16);
		this.init(this.goodsId17);
		this.init(this.goodsId18);
		this.init(this.goodsId19);
		this.init(this.goodsId20);
	}
	
	private void init(int value){
		if(value <=0){
			return ;
		}
		GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(value);
		if(null == gb){
			Log4jManager.CHECK.error("BossLoot init error,goodsId=" + value + " not exist,lootId=" + lootId);
			Log4jManager.checkFail();
			return ;
		}
		this.goodsList.add(value);
	}
	
}
