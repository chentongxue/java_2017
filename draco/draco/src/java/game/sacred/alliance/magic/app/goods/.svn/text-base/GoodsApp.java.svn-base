package sacred.alliance.magic.app.goods;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import sacred.alliance.magic.app.goods.derive.EquipRecatingAttrWeightConfig;
import sacred.alliance.magic.app.goods.derive.EquipRecatingConfig;
import sacred.alliance.magic.app.goods.derive.EquipUpgradeConfig;
import sacred.alliance.magic.app.goods.derive.StorySuitConfig;
import sacred.alliance.magic.app.goods.derive.StorySuitEquipConfig;
import sacred.alliance.magic.app.goods.exception.GoodsNotFoundException;
import sacred.alliance.magic.base.GoodsType;
import sacred.alliance.magic.domain.EquStrengthenEffect;
import sacred.alliance.magic.domain.EquStrengthenstar;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.domain.MixFormula;
import sacred.alliance.magic.domain.MosaicConfig;

public interface GoodsApp{	
	
	/**
	 * 获得新的物品ID
	 * @return
	 */
	public String newGoodsInstanceId();
	
	/**
	 * 根据物品模板id获取物品基本信息
	 * @param goodsId
	 * @return
	 * @throws GoodsNotFoundException
	 */
	public GoodsBase getGoodsBase(int goodsId);
	
	public boolean isExistGoods(int goodsId);

	/**
	 * 获得资源id
	 * @param goodsId
	 * @return
	 */
	public int getResId(int goodsId);
	/**
	 * 根据强化类型，获得强化条件信息
	 * @param level 当前强化等级
	 * @param strengthenType 强化类型
	 */
	public EquStrengthenstar getStrengthenstar(int level);
	
	/**
	 * 根据装备强化星级、品质，获得强化属性增幅率信息
	 * @param strengthenLevel
	 * @param qualityType
	 * @return
	 */
	public EquStrengthenEffect getStrengthenEffect(int strengthenLevel, int qualityType);
	
	/**
	 * 宝石合成配置信息
	 * @return
	 */
	public Map<Integer, MixFormula> getAllMixFormula();
	
	/** 获取物品模版对象 */
	public <T extends GoodsBase> T getGoodsTemplate(Class<T> clazz, int goodsId);
	
	/** 调试日志 */
	public void debug(String info);
	
	/** 通过物品ID获取物品类型 */
	public GoodsType getGoodsType(int goodsId);
	
	public MosaicConfig getMosaicConfig() ;
	
	public EquipUpgradeConfig getEquipUpgradeConfig(int level,int qualityType ,int equipslotType);
	
	public EquipRecatingConfig getEquipRecatingConfig(int qualityType);
	
	public short[] getRquipRecatingLockRatio();
	
	public short getEquipRecatingLockRatio(byte lockNum);
	
	public List<Byte> getEquipRecatingAttrList();
	
	public EquipRecatingAttrWeightConfig getEquipRecatingAttrWeightConfig(byte attrType, int qualityType);
	
	public Collection<StorySuitConfig> getStorySuitConfigList();
	
	public StorySuitConfig getStorySuitConfig(short suitGroupId);
	
	public StorySuitEquipConfig getStorySuitEquipConfig(short suitGroupId, int level, byte equipslotType);
	
	public TreeMap<Byte,Short> getStorySuitImageMap(short suitGroupId);
	
	public StorySuitEquipConfig getStorySuitEquipAutoLevelConfig(int roleLevel, short suitGroupId, byte equipslotType);
	
}
