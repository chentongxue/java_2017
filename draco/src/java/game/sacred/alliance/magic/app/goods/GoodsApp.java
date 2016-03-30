package sacred.alliance.magic.app.goods;

import java.util.List;

import sacred.alliance.magic.app.goods.decompose.DecomposeConfig;
import sacred.alliance.magic.app.goods.derive.EquipRecatingAttrWeightConfig;
import sacred.alliance.magic.app.goods.derive.EquipRecatingConfig;
import sacred.alliance.magic.app.goods.derive.EquipStrengthenConfig;
import sacred.alliance.magic.app.goods.derive.EquipStrengthenVip;
import sacred.alliance.magic.app.goods.exception.GoodsNotFoundException;
import sacred.alliance.magic.base.GoodsType;
import sacred.alliance.magic.domain.GoodsBase;
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
	public EquipStrengthenConfig getEquipStrengthenConfig(int level);
	
	public EquipStrengthenVip getEquipStrengthenVip(int level) ;
	
	
	/** 获取物品模版对象 */
	public <T extends GoodsBase> T getGoodsTemplate(Class<T> clazz, int goodsId);
	
	/** 调试日志 */
	public void debug(String info);
	
	/** 通过物品ID获取物品类型 */
	public GoodsType getGoodsType(int goodsId);
	
	public MosaicConfig getMosaicConfig() ;
	
	public EquipRecatingConfig getEquipRecatingConfig(int quality,int star);
	
	public short[] getRquipRecatingLockRatio();
	
	public short getEquipRecatingLockRatio(byte lockNum);
	
	public List<Byte> getEquipRecatingAttrList();
	
	public EquipRecatingAttrWeightConfig getEquipRecatingAttrWeightConfig(byte attrType, int quality,int star);

	public DecomposeConfig getDecomposeConfig(int inputGoodsId);
	
}
