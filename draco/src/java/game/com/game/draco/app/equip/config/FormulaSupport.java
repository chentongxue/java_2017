package com.game.draco.app.equip.config;

import java.util.List;

import lombok.Data;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.util.Initable;
import sacred.alliance.magic.util.Log4jManager;

import com.game.draco.GameContext;
import com.game.draco.message.item.MaterialItem;
import com.google.common.collect.Lists;

public @Data class FormulaSupport implements Initable{

	protected int material1	;
	protected short num1	;
	protected int material2 ;
	protected short num2	;
	protected int material3 ;
	protected short num3	;
	protected int gameMoney ;
	
	private List<MaterialItem> materialList = Lists.newArrayList();
	
	
	@Override
	public void init() {
		List<MaterialItem> save = Lists.newArrayList();
		this.init(save,material1, num1);
		this.init(save,material2, num2);
		this.init(save,material3, num3);
		materialList.clear();
		materialList.addAll(save);
		
		save.clear();
		save = null ;
	}
	
	private void init(List<MaterialItem> save,int material,short num){
		if(material <= 0 || num <=0 ){
			return ;
		}
		GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(material);
		if(null == gb){
			Log4jManager.CHECK.error("goods not exist,goodsId=" + material + " class=" + this.getClass().getSimpleName());
			Log4jManager.checkFail();
			return ;
		}
		MaterialItem item = new MaterialItem();
		item.setGoodsId(material);
		item.setGoodsNum(num);
		save.add(item);
	}
}
