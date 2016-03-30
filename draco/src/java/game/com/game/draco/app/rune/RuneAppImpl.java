package com.game.draco.app.rune;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.attri.AttriItem;
import sacred.alliance.magic.app.goods.Util;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.GoodsType;
import sacred.alliance.magic.base.XlsSheetNameType;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.domain.GoodsRune;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.RandomUtil;
import sacred.alliance.magic.util.XlsPojoUtil;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.rune.config.ComposeDescConfig;
import com.game.draco.app.rune.config.MosaicRulesConfig;
import com.game.draco.app.rune.config.RuneAttributeConfig;
import com.game.draco.app.rune.config.RuneComposeRuleConfig;
import com.game.draco.app.rune.config.RuneCostConfig;
import com.game.draco.app.rune.config.RuneWeightConfig;
import com.game.draco.app.rune.domain.MosaicRune;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class RuneAppImpl implements RuneApp {

	private static final byte PETMOSAICRULES = 1;

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private Map<Byte, MosaicRulesConfig> petMosaicRulesConfig = Maps.newHashMap();
	private Map<Byte, MosaicRulesConfig> equipmentMosaicRulesConfig = Maps.newHashMap();
	private Map<String, RuneAttributeConfig> runeAttributeConfigMap = Maps.newHashMap();
	private Map<String, RuneCostConfig> runeCostConfigMap = Maps.newHashMap();
	private Map<String, RuneWeightConfig> runeWeightConfigMap = Maps.newHashMap();
	private Map<Integer, RuneComposeRuleConfig> runeComposeRuleConfigMap = Maps.newHashMap();
	/**
	 * 宝石等级,属性数目对应的模板id key: level_attriNum
	 */
	private Map<String, Integer> multAttriTemplateMap = Maps.newHashMap();
	private static Map<AttributeType, Byte> attriPositionMap = Maps.newHashMap();
	private ComposeDescConfig composeDescConfig = new ComposeDescConfig();

	static {
		attriPositionMap.put(AttributeType.atk, (byte) 0);
		attriPositionMap.put(AttributeType.rit, (byte) 1);
		attriPositionMap.put(AttributeType.maxHP, (byte) 2);
		attriPositionMap.put(AttributeType.breakDefense, (byte) 3);
		attriPositionMap.put(AttributeType.critAtk, (byte) 4);
		attriPositionMap.put(AttributeType.critRit, (byte) 5);
		attriPositionMap.put(AttributeType.dodge, (byte) 6);
		attriPositionMap.put(AttributeType.hit, (byte) 7);
	}

	@Override
	public Map<Byte, MosaicRune> getMosaicRuneMap(String mosaic) {
		// 孔位:符文ID:属性类型:属性值:属性类型:属性值,孔位:符文ID:属性类型:属性值:属性类型:属性值
		if (Util.isEmpty(mosaic)) {
			return null;
		}
		String[] runes = Util.splitStr(mosaic, Cat.comma);
		if (Util.isEmpty(runes)) {
			return null;
		}
		Map<Byte, MosaicRune> mosaicRuneMap = Maps.newHashMap();
		for (String rune : runes) {
			String[] infos = Util.splitStr(rune, Cat.colon);
			if (Util.isEmpty(infos)) {
				continue;
			}
			List<AttriItem> attriList = Lists.newArrayList();
			byte hole = Byte.parseByte(infos[0]);
			int goodsId = Integer.parseInt(infos[1]);
			GoodsRune goodsRune = GameContext.getGoodsApp().getGoodsTemplate(GoodsRune.class, goodsId);
			if (null == goodsRune) {
				continue;
			}
			if (1 == goodsRune.getSecondType()) {
				// 如果是单属性符文直接从模版中取属性
				mosaicRuneMap.put(hole, new MosaicRune(hole, goodsId, goodsRune.getAttriItemList()));
				continue;
			}
			for (int i = 2; i < infos.length - 1; i++) {
				byte attriTypeValue = Byte.parseByte(infos[i++]);
				float value = Float.parseFloat(infos[i]);
				attriList.add(new AttriItem(attriTypeValue, this.getCorrectAttriValue(goodsRune, attriTypeValue, value), false));
			}
			MosaicRune mosaicRune = new MosaicRune(hole, goodsId, attriList);
			mosaicRuneMap.put(hole, mosaicRune);
		}
		return mosaicRuneMap;
	}
	
	private float getCorrectAttriValue(GoodsRune goodsRune,byte attriType, float value) {
		int[] values = this.getAttriValueRange(goodsRune.getLevel(), goodsRune.getSecondType(), attriType);
		if (null == values) {
			return 0;
		}
		if (value > values[1]) {
			return values[1];
		}
		return value;
	}

	@Override
	public String getMosaicString(Map<Byte, MosaicRune> runeMap) {
		if (Util.isEmpty(runeMap)) {
			return "";
		}
		StringBuffer mosaicBuffer = new StringBuffer();
		String temCat = "";
		for (MosaicRune mosaicRune : runeMap.values()) {
			if (null == mosaicRune) {
				continue;
			}
			mosaicBuffer.append(temCat);
			mosaicBuffer.append(mosaicRune.getHole());
			mosaicBuffer.append(Cat.colon);
			mosaicBuffer.append(mosaicRune.getGoodsId());
			temCat = Cat.colon;
			// 符文属性
			for (AttriItem attriItem : mosaicRune.getAttriList()) {
				if (null == attriItem) {
					continue;
				}
				mosaicBuffer.append(temCat);
				mosaicBuffer.append(attriItem.getAttriTypeValue());
				mosaicBuffer.append(Cat.colon);
				mosaicBuffer.append((int) attriItem.getValue());
			}
			temCat = Cat.comma;
		}
		return mosaicBuffer.toString();
	}

	@Override
	public Map<Integer, RuneComposeRuleConfig> getRuneComposeRuleConfigMap() {
		return runeComposeRuleConfigMap;
	}

	@Override
	public Collection<AttriItem> getMosaicRuneAttriList(MosaicRune[] runes) {
		if (null == runes || runes.length <= 0) {
			return null;
		}
		Map<Byte, AttriItem> attriMap = Maps.newHashMap();
		for (MosaicRune mosaicRune : runes) {
			if (null == mosaicRune) {
				continue;
			}
			List<AttriItem> attriItemList = mosaicRune.getAttriList();
			if (Util.isEmpty(attriItemList)) {
				continue;
			}
			for (AttriItem item : attriItemList) {
				if (null == item) {
					continue;
				}
				AttriItem attriItem = attriMap.get(item.getAttriTypeValue());
				if (null == attriItem) {
					attriItem = new AttriItem(item.getAttriTypeValue(), item.getValue(), false);
					attriMap.put(item.getAttriTypeValue(), attriItem);
					continue;
				}
				attriItem.setValue(attriItem.getValue() + item.getValue());
				attriMap.put(item.getAttriTypeValue(), attriItem);
			}
		}
		return attriMap.values();
	}

	@Override
	public ArrayList<AttriItem> getAttriItemListforRune(int attriNum, int level) {
		List<Byte> choiceList = sacred.alliance.magic.util.Util.getLuckyDraw(attriNum, this.getRuneWeightMap(level));
		if (null == choiceList) {
			return null;
		}
		ArrayList<AttriItem> AttrVarList = new ArrayList<AttriItem>();
		for (Byte attri : choiceList) {
			if (null == attri) {
				continue;
			}
			int[] range = this.getAttriValueRange(level, attriNum, attri);
			if (null == range) {
				return null;
			}
			int value = RandomUtil.randomInt(range[0], range[1]);
			AttriItem attriItem = new AttriItem(attri, value, 0);
			AttrVarList.add(attriItem);
		}
		if (AttrVarList.isEmpty()) {
			return null;
		}
		return AttrVarList;
	}

	@Override
	public RuneComposeRuleConfig getTargetComposeGoods(int templateId) {
		return runeComposeRuleConfigMap.get(templateId);
	}

	@Override
	public int getRuleTemplateId(int level, int attriNum) {
		String key = level + "_" + attriNum;
		Integer id = this.multAttriTemplateMap.get(key);
		return (null != id) ? id : 0;
	}

	@Override
	public void initGoodsRune(List<? extends GoodsBase> goodsBaseList) {
		if (Util.isEmpty(goodsBaseList)) {
			return;
		}
		for (GoodsBase gb : goodsBaseList) {
			if (GoodsType.GoodsRune.getType() != gb.getGoodsType()) {
				continue;
			}
			int attrNum = gb.getSecondType();
			if (attrNum <= 1) {
				// 只需要多属性
				continue;
			}
			String key = gb.getLevel() + "_" + attrNum;
			multAttriTemplateMap.put(key, gb.getId());
		}
	}

	@Override
	public void setArgs(Object arg0) {
	}

	@Override
	public void start() {
		String xlsPath = GameContext.getPathConfig().getXlsPath();
		this.loadEquipmentMosaicRulesConfig(xlsPath);
		this.loadPetMosaicRulesConfig(xlsPath);
		this.loadRuneSmeltConfig(xlsPath);
		this.loadRuneCostConfig(xlsPath);
		this.loadRuneWeightConfig(xlsPath);
		this.loadRuneComposeRuleConfig(xlsPath);
		this.loadComposeDescConfig(xlsPath);
	}

	public void loadRuneSmeltConfig(String xlsPath) {
		String fileName = XlsSheetNameType.rune_smelt_attributeValue.getXlsName();
		String sheetName = XlsSheetNameType.rune_smelt_attributeValue.getSheetName();
		String info = "load excel error:fileName=" + fileName + ",sheetName=" + sheetName + ".";
		try {
			this.runeAttributeConfigMap = XlsPojoUtil.sheetToGenericMap(xlsPath + fileName, sheetName, RuneAttributeConfig.class);
			for (RuneAttributeConfig runeSmeltConfig : this.runeAttributeConfigMap.values()) {
				if (runeSmeltConfig == null)
					continue;
				runeSmeltConfig.init(info);
			}
		} catch (Exception ex) {
			logger.error(info, ex);
			Log4jManager.checkFail();
		}
	}

	public void loadRuneCostConfig(String xlsPath) {
		String fileName = XlsSheetNameType.rune_smelt_cost.getXlsName();
		String sheetName = XlsSheetNameType.rune_smelt_cost.getSheetName();
		String info = "load excel error:fileName=" + fileName + ",sheetName=" + sheetName + ".";
		try {
			this.runeCostConfigMap = XlsPojoUtil.sheetToGenericMap(xlsPath + fileName, sheetName, RuneCostConfig.class);
			for (RuneCostConfig runeCostConfig : this.runeCostConfigMap.values()) {
				if (runeCostConfig == null) {
					continue;
				}
				runeCostConfig.init(info);
			}
		} catch (Exception ex) {
			logger.error(info, ex);
			Log4jManager.checkFail();
		}
	}

	public void loadRuneWeightConfig(String xlsPath) {
		String fileName = XlsSheetNameType.rune_smelt_weight.getXlsName();
		String sheetName = XlsSheetNameType.rune_smelt_weight.getSheetName();
		String info = "load excel error:fileName=" + fileName + ",sheetName=" + sheetName + ".";
		try {
			this.runeWeightConfigMap = XlsPojoUtil.sheetToGenericMap(xlsPath + fileName, sheetName, RuneWeightConfig.class);
			for (RuneWeightConfig runeWeightConfig : this.runeWeightConfigMap.values()) {
				if (runeWeightConfig == null) {
					continue;
				}
				runeWeightConfig.init(info);
			}
		} catch (Exception ex) {
			logger.error(info, ex);
			Log4jManager.checkFail();
		}
	}

	private void loadPetMosaicRulesConfig(String xlsPath) {
		String fileName = XlsSheetNameType.pet_mosaic_rules.getXlsName();
		String sheetName = XlsSheetNameType.pet_mosaic_rules.getSheetName();
		String info = "load excel error:fileName=" + fileName + ",sheetName=" + sheetName + ".";
		try {
			this.petMosaicRulesConfig = XlsPojoUtil.sheetToGenericMap(xlsPath + fileName, sheetName, MosaicRulesConfig.class);
			for (MosaicRulesConfig config : this.petMosaicRulesConfig.values()) {
				if (Util.isEmpty(config)) {
					continue;
				}
				config.init(info);
			}
		} catch (Exception e) {
			logger.error(info, e);
			Log4jManager.checkFail();
		}
	}

	private void loadEquipmentMosaicRulesConfig(String xlsPath) {
		String fileName = XlsSheetNameType.equipment_mosaic_rules.getXlsName();
		String sheetName = XlsSheetNameType.equipment_mosaic_rules.getSheetName();
		String info = "load excel error:fileName=" + fileName + ",sheetName=" + sheetName + ".";
		try {
			this.equipmentMosaicRulesConfig = XlsPojoUtil.sheetToGenericMap(xlsPath + fileName, sheetName, MosaicRulesConfig.class);
			for (MosaicRulesConfig config : this.equipmentMosaicRulesConfig.values()) {
				if (Util.isEmpty(config)) {
					continue;
				}
				config.init(info);
			}
		} catch (Exception e) {
			logger.error(info, e);
			Log4jManager.checkFail();
		}
	}

	private void loadRuneComposeRuleConfig(String xlsPath) {
		String fileName = XlsSheetNameType.rune_compose_rule.getXlsName();
		String sheetName = XlsSheetNameType.rune_compose_rule.getSheetName();
		String info = "load excel error:fileName=" + fileName + ",sheetName=" + sheetName + ".";
		try {
			this.runeComposeRuleConfigMap = XlsPojoUtil.sheetToGenericMap(xlsPath + fileName, sheetName, RuneComposeRuleConfig.class);
			for (RuneComposeRuleConfig config : runeComposeRuleConfigMap.values()) {
				if (null == config) {
					continue;
				}
				config.init(info);
			}
		} catch (Exception e) {
			logger.error(info, e);
			Log4jManager.checkFail();
		}
	}
	
	private void loadComposeDescConfig(String xlsPath) {
		String fileName = XlsSheetNameType.rules_desc.getXlsName();
		String sheetName = XlsSheetNameType.rules_desc.getSheetName();
		String info = "load excel error:fileName=" + fileName + ",sheetName=" + sheetName + ".";
		try {
			this.composeDescConfig = XlsPojoUtil.getEntity(xlsPath + fileName, sheetName, ComposeDescConfig.class);
		} catch (Exception e) {
			logger.error(info, e);
			Log4jManager.checkFail();
		}
	}


	public int runeSmeltCost(int level) {
		return runeCostConfigMap.get(level).getSmeltMoney();
	}

	@Override
	public void stop() {
	}

	@Override
	public int onCleanup(String roleId, Object context) {
		return 0;
	}

	@Override
	public int onLogin(RoleInstance role, Object context) {
		return 0;
	}

	@Override
	public int onLogout(RoleInstance role, Object context) {
		return 0;
	}

	@Override
	public byte[] getMoasicRules(byte type) {
		if (PETMOSAICRULES == type) {
			byte[] rules = new byte[this.petMosaicRulesConfig.size()];
			int i = 0;
			for (MosaicRulesConfig config : this.petMosaicRulesConfig.values()) {
				rules[i++] = config.getType();
			}
			return rules;
		}
		// 装备镶嵌规则
		byte[] rules = new byte[this.equipmentMosaicRulesConfig.size()];
		int i = 0;
		for (MosaicRulesConfig config : this.equipmentMosaicRulesConfig.values()) {
			rules[i++] = config.getType();
		}
		return rules;
	}

	private Map<Byte, Integer> getRuneWeightMap(int runeLevel) {
		RuneWeightConfig runeWeightConfig = runeWeightConfigMap.get("" + runeLevel);
		if(null == runeWeightConfig){
			return null ;
		}
		return runeWeightConfig.getRuneWeightMap();
	}

	
	private int[] getAttriValueRange(int level, int attriNum, byte attriType) {
		RuneAttributeConfig runeAttributeConfig = runeAttributeConfigMap.get(level + "_" + attriNum);
		return runeAttributeConfig.getAttriValueMap().get(attriType);
	}
	
	@Override
	public RuneAttributeConfig getRuneAttriButeConfig(int level, int attriNum) {
		return this.runeAttributeConfigMap.get(level + "_" + attriNum);
	}
	
	@Override
	public byte getAttributePosition(byte attriType) {
		AttributeType key = AttributeType.get(attriType);
		if (null == key) {
			return -1;
		}
		Byte position = attriPositionMap.get(key);
		if (null == position) {
			return -1;
		}
		return position;
	}

	@Override
	public String getComposeDesc() {
		if (null == this.composeDescConfig) {
			return null;
		}
		return this.composeDescConfig.getCompose_desc();
	}
	
	@Override
	public String getSmeltDesc() {
		if (null == this.composeDescConfig) {
			return null;
		}
		return this.composeDescConfig.getSmelt_desc();
	}

	@Override
	public Collection<RuneCostConfig> getAllRuneCostConfig() {
		return this.runeCostConfigMap.values() ;
	}

	@Override
	public RuneCostConfig getRuneCostConfig(int level) {
		return this.runeCostConfigMap.get(String.valueOf(level));
	}
}
