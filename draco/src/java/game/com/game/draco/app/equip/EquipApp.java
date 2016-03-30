package com.game.draco.app.equip;

import java.util.List;

import sacred.alliance.magic.app.attri.AttriBuffer;
import sacred.alliance.magic.app.goods.behavior.param.EquipUpgradeStarParam;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.domain.GoodsEquipment;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.app.equip.config.FormulaSupport;
import com.game.draco.app.equip.config.StarMaterialFormula;
import com.game.draco.app.equip.config.StarMaterialWays;
import com.game.draco.app.equip.config.StarUpgradeFormula;
import com.game.draco.app.equip.config.StarWays;
import com.game.draco.app.hero.domain.RoleHero;
import com.game.draco.message.item.EquipBaseAttriItem;
import com.game.draco.message.item.GoodsLiteNamedItem;
import com.game.draco.message.item.HeroEquipFormulaItem;

public interface EquipApp {

	public AttriBuffer getBaseAttriBuffer(int goodsId,int quality,int star) ;
	
	public AttriBuffer getStrengthenAttriBuffer(int goodsId,int quality,int star,int strengthenLevel) ;
	
	public List<AttributeType> getAttributeTypeList() ;
	
	public List<EquipBaseAttriItem> getBaseAttriItem(RoleGoods roleGoods,GoodsEquipment equip) ;
	
	public List<EquipBaseAttriItem> getStrengthenAttriDifferent(RoleGoods roleGoods,GoodsEquipment equip,int incrStrengLevel) ;
	
	public List<HeroEquipFormulaItem> getHeroEquipFormula(RoleInstance role,RoleHero roleHero) ;
	
	public void onHeroEquipFormulaChanged(RoleInstance role,int heroId,int equipPos) ;
	
	public StarMaterialFormula getStarMaterialFormula(int goodsId) ;
	
	public StarMaterialWays getStarMaterialWays(int goodsId);
	
	public StarWays getStarWays(int waysId) ;
	
	public Result formulaMix(RoleInstance role,int targetGoodsId,int mixNum);
	
	//public Result equipUpgradeStar() ;
	
	public List<GoodsLiteNamedItem> getMaterialsList(FormulaSupport formula);
	
	public Message getHeroEquipStarDetailRespMessage(RoleInstance role,int heroId,String goodsInstanceId,byte pos) ;
	
	public StarUpgradeFormula getNextStarUpgradeFormula(int goodsId,int quality,int star) ;
	
	public StarUpgradeFormula getStarUpgradeFormula(int goodsId,int quality,int star) ;
	
	public Result equipUpgradeStar(EquipUpgradeStarParam starParam) ;
	
	public int getHeroEquipslotType(int heroId,int goodsId) ;
	
	public byte getEquipMaxHole(RoleGoods roleGoods);
	
	public byte getOpenHoleLevel(byte hole);
	
	public Message getNextStarEquipDetail(RoleInstance role,byte bagType, String goodsInstanceId,
			int targetId);

	public List<Integer> getEquipOpenCondList() ;

	public int getEquipOpenQuality(int equipPos) ;
}
