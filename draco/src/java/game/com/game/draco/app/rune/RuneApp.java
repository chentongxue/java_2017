package com.game.draco.app.rune;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import sacred.alliance.magic.app.attri.AttriItem;
import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.domain.GoodsBase;

import com.game.draco.app.AppSupport;
import com.game.draco.app.rune.config.RuneAttributeConfig;
import com.game.draco.app.rune.config.RuneComposeRuleConfig;
import com.game.draco.app.rune.config.RuneCostConfig;
import com.game.draco.app.rune.domain.MosaicRune;

public interface RuneApp extends Service, AppSupport {

	public Map<Byte, MosaicRune> getMosaicRuneMap(String mosaic);

	public Collection<AttriItem> getMosaicRuneAttriList(MosaicRune[] runes);

	public String getMosaicString(Map<Byte, MosaicRune> runeMap);

	public byte[] getMoasicRules(byte type);

	public int runeSmeltCost(int level);

	public int getRuleTemplateId(int level, int attriNum);

	public void initGoodsRune(List<? extends GoodsBase> goodsBaseList);


	public Map <Integer, RuneComposeRuleConfig> getRuneComposeRuleConfigMap();

	public ArrayList <AttriItem> getAttriItemListforRune(int attriNum,int level);

	public RuneComposeRuleConfig getTargetComposeGoods(int templateId);

	public byte getAttributePosition(byte attriType);
	
	public String getComposeDesc();
	
	public String getSmeltDesc();
	
	public Collection<RuneCostConfig> getAllRuneCostConfig() ;
	
	public RuneCostConfig getRuneCostConfig(int level) ;
	
	public RuneAttributeConfig getRuneAttriButeConfig(int level, int attriNum);

}
