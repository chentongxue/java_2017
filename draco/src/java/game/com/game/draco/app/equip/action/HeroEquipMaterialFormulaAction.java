package com.game.draco.app.equip.action;

import java.util.List;

import com.game.draco.message.item.GoodsLiteNamedItem;
import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.domain.GoodsBase;

import com.game.draco.GameContext;
import com.game.draco.app.equip.config.StarMaterialFormula;
import com.game.draco.app.equip.config.StarMaterialWays;
import com.game.draco.app.equip.config.StarWays;
import com.game.draco.message.item.EquipFormulaWaysItem;
import com.game.draco.message.request.C1277_HeroEquipMaterialFormulaReqMessage;
import com.game.draco.message.response.C1277_HeroEquipMaterialFormulaRespMessage;
import com.game.draco.message.response.C1280_HeroEquipFormulaWaysRespMessage;
import com.google.common.collect.Lists;

public class HeroEquipMaterialFormulaAction extends BaseAction<C1277_HeroEquipMaterialFormulaReqMessage>{

	@Override
	public Message execute(ActionContext context,
			C1277_HeroEquipMaterialFormulaReqMessage reqMsg) {
		int goodsId = reqMsg.getGoodsId() ;
		int heroId = reqMsg.getHeroId() ;
		StarMaterialFormula formula = GameContext.getEquipApp().getStarMaterialFormula(goodsId);
		if(null != formula){
			C1277_HeroEquipMaterialFormulaRespMessage formulaRespMsg = new C1277_HeroEquipMaterialFormulaRespMessage();
			GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(goodsId);
            GoodsLiteNamedItem targetItem = gb.getGoodsLiteNamedItem();
            targetItem.setNum(reqMsg.getNeedNum());
			formulaRespMsg.setTargetGoods(targetItem);
			formulaRespMsg.setGameMoney(formula.getGameMoney());
			formulaRespMsg.setMaterialsList(GameContext.getEquipApp().getMaterialsList(formula));
			formulaRespMsg.setHeroId(heroId);
			return formulaRespMsg ;
		}
		StarMaterialWays mw = GameContext.getEquipApp().getStarMaterialWays(goodsId);
		if(null == mw){
			return null ;
		}
		List<EquipFormulaWaysItem> ways = Lists.newArrayList();
		for(short id : mw.getWaysIdList()){
			StarWays sw = GameContext.getEquipApp().getStarWays(id);
			if(null == sw){
				continue ;
			}
			EquipFormulaWaysItem item = new EquipFormulaWaysItem();
			item.setName(sw.getWaysName());
			item.setForwardId(sw.getForwardId());
			ways.add(item);
		}
		//没有配方发送产出
		C1280_HeroEquipFormulaWaysRespMessage waysRespMsg = new C1280_HeroEquipFormulaWaysRespMessage();
		waysRespMsg.setWays(ways);
		GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(goodsId);
		if(null != gb){
			waysRespMsg.setName(gb.getName()) ;
			waysRespMsg.setQuality(gb.getQualityType());
		}
		return waysRespMsg;
	}

}
