package sacred.alliance.magic.app.goods;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.goods.decompose.DecomposeConfig;
import sacred.alliance.magic.app.goods.derive.EquipRecatingAttrWeightConfig;
import sacred.alliance.magic.app.goods.derive.EquipRecatingConfig;
import sacred.alliance.magic.app.goods.derive.EquipRecatingLockConfig;
import sacred.alliance.magic.app.goods.derive.EquipStrengthenConfig;
import sacred.alliance.magic.app.goods.derive.EquipStrengthenVip;
import sacred.alliance.magic.base.GoodsType;
import sacred.alliance.magic.base.XlsSheetNameType;
import sacred.alliance.magic.component.id.IdFactory;
import sacred.alliance.magic.component.id.IdType;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.data.GoodsLoader;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.domain.MosaicConfig;
import sacred.alliance.magic.util.KeySupport;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.util.XlsPojoUtil;

import com.game.draco.GameContext;
import com.google.common.collect.Maps;

/**
 * 物品系统所有配置文件加载及处理模版
 * 
 * @author Wang.K
 * 
 */
public class GoodsAppImpl implements GoodsApp, Service {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private IdFactory<String> idFactory;
	private GoodsLoader goodsLoader;

	// 装备强化配置
	private Map<String, EquipStrengthenConfig> strengthenMap = Maps
			.newHashMap();
	private Map<String, EquipStrengthenVip> strengthenVipMap = Maps
			.newHashMap();
	// 宝石镶嵌相关配置
	private MosaicConfig mosaicConfig;
	// 装备洗练配置
	private Map<String, EquipRecatingConfig> equipRecatingMap = Maps
			.newHashMap();
	private TreeMap<Byte, Short> equipRecatingLockRatioMap = Maps.newTreeMap();
	private short[] equipRecatingLockRatio;
	private Map<String, EquipRecatingAttrWeightConfig> equipRecatingAttrWeightMap = Maps
			.newHashMap();
	private List<Byte> equipRecatingAttrList = new ArrayList<Byte>();

	// 物品分解表
	private Map<Integer, DecomposeConfig> decomposeConfigMap = Maps
			.newHashMap();

	@Override
	public DecomposeConfig getDecomposeConfig(int inputGoodsId) {
		if (Util.isEmpty(decomposeConfigMap)) {
			return null;
		}
		return decomposeConfigMap.get(inputGoodsId);
	}

	private String getDecomposeDesc(DecomposeConfig cf) {
		if (cf == null) {
			return "";
		}
		String name = "";
		if (cf.isAttribute()) {
			name = cf.getAttribute().getName();
		}
		if (cf.isGoods()) {
			name = GameContext.getGoodsApp().getGoodsBase(cf.getOutputId())
					.getName();
		}
		if (cf.getMinNum() == cf.getMaxNum()) {
			return GameContext.getI18n().messageFormat(TextId.DECOMPOSE_DESC_2,
					name, cf.getMinNum());
		}
		return GameContext.getI18n().messageFormat(TextId.DECOMPOSE_DESC, name,
				cf.getMinNum(), cf.getMaxNum());
	}

	/** 通过ID生成器获取物品实例ID */
	public String newGoodsInstanceId() {
		String id;
		try {
			id = idFactory.nextId(IdType.GOODS);
		} catch (Exception e) {
			logger.error("The idfactory generate goodsInstanceId  exception", e);
			return null;
		}
		return id;
	}

	public void setGoodsLoader(GoodsLoader goodsLoader) {
		this.goodsLoader = goodsLoader;
	}

	public GoodsBase getGoodsBase(int goodsId) {
		if (goodsId <= 0) {
			return null;
		}
		GoodsBase goodsBase = goodsLoader.getDataMap().get(
				String.valueOf(goodsId));
		if (null == goodsBase) {
			logger.error("goodsId = " + goodsId + " is no exist");
		}
		return goodsBase;
	}

	public boolean isExistGoods(int goodsId) {
		return (null != this.getGoodsBase(goodsId));
	}

	public int getResId(int goodsId) {
		GoodsBase goodsBase = getGoodsBase(goodsId);
		if (goodsBase == null) {
			return 0;
		}
		return goodsBase.getResId();
	}

	/** 获取物品模版对象 */
	public <T extends GoodsBase> T getGoodsTemplate(Class<T> clazz, int goodsId) {
		try {
			GoodsBase gb = this.getGoodsBase(goodsId);
			if (null == gb) {
				return null;
			}
			return (T) gb;
		} catch (Exception e) {
			return null;
		}
	}

	/** 通过物品ID获取物品类型 */
	public GoodsType getGoodsType(int goodsId) {
		GoodsBase goodsBase = this.getGoodsBase(goodsId);
		if (goodsBase == null) {
			return GoodsType.GoodsDefault;
		}
		return GoodsType.get(goodsBase.getGoodsType());
	}

	public void setIdFactory(IdFactory<String> idFactory) {
		this.idFactory = idFactory;
	}

	@Override
	public void setArgs(Object args) {

	}

	/**
	 * 1.初始化魂魄物品下一个物品的ID 2.加载强化所需物品信息 3.加载物品品质对应属性提升信息 4.打孔配置信息 5.宝石合成配置信息
	 * 6.加载装备品质系数 7.加载装备穿戴位置评分系数
	 */
	@Override
	public void start() {
		this.initDecomposeConfigMap();
		// 初始化个goodsbase
		for (GoodsBase gb : goodsLoader.getDataMap().values()) {
			gb.init(gb.getInitData());
		}
		initStrengthenConfig();
		initStrengthenVipConfig();
		initMosaicConfig();
		this.initEquipRecatingConfig();
	}

	private void initDecomposeConfigMap() {
		decomposeConfigMap = loadConfigMap(XlsSheetNameType.decompose_config,
				DecomposeConfig.class, true);
		for (Map.Entry<Integer, DecomposeConfig> entry : decomposeConfigMap
				.entrySet()) {
			DecomposeConfig cf = entry.getValue();
			if (cf == null) {
				continue;
			}
			cf.init();
			// 根据分解配置初始化物品信息
			GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(
					cf.getInputGoodsId());
			if (null == gb) {
				continue;
			}
			// 检查是否是可分解的物品，是则置可回收标志和可回收价格为0。
			String decomposeDesc = this.getDecomposeDesc(cf);
			if (!Util.isEmpty(decomposeDesc)) {
				gb.setRecycling(0);
				gb.setRecyclePrice(0);
				gb.setDecomposeDesc(decomposeDesc);
			}

		}
	}

	private <K, V extends KeySupport<K>> Map<K, V> loadConfigMap(
			XlsSheetNameType xls, Class<V> clazz, boolean linked) {
		String fileName = xls.getXlsName();
		String sheetName = xls.getSheetName();
		String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
		Map<K, V> map = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName,
				clazz, linked);
		if (Util.isEmpty(map)) {
			checkFail("not config the " + clazz.getSimpleName() + " ,file="
					+ sourceFile + " sheet=" + sheetName);
		}
		return map;
	}

	@Override
	public void stop() {

	}

	@Override
	public EquipStrengthenConfig getEquipStrengthenConfig(int level) {
		return this.strengthenMap.get(String.valueOf(level));
	}

	@Override
	public EquipStrengthenVip getEquipStrengthenVip(int level) {
		String key = String.valueOf(level);
		return sacred.alliance.magic.util.Util.fromMap(this.strengthenVipMap,
				key);
	}

	private void initMosaicConfig() {
		String fileName = "";
		String sheetName = "";
		String xlsPath = GameContext.getPathConfig().getXlsPath();
		try {
			fileName = XlsSheetNameType.equip_baoshi_config.getXlsName();
			sheetName = XlsSheetNameType.equip_baoshi_config.getSheetName();
			String sourceFile_level = xlsPath + fileName;
			List<MosaicConfig> configList = XlsPojoUtil.sheetToList(
					sourceFile_level, sheetName, MosaicConfig.class);
			if (null == configList || 0 == configList.size()) {
				Log4jManager.CHECK
						.error("load mosaicConfig error,not config : sourceFile = "
								+ fileName + " sheetName =" + sheetName);
				Log4jManager.checkFail();
			}
			this.mosaicConfig = configList.get(0);
		} catch (Exception e) {
			Log4jManager.CHECK.error("load mosaicConfig error : sourceFile = "
					+ fileName + " sheetName =" + sheetName, e);
			Log4jManager.checkFail();
		}
	}

	private void initStrengthenConfig() {
		String fileName = "";
		String sheetName = "";
		String xlsPath = GameContext.getPathConfig().getXlsPath();
		try {
			fileName = XlsSheetNameType.equip_qianghua_list.getXlsName();
			sheetName = XlsSheetNameType.equip_qianghua_list.getSheetName();
			String sourceFile = xlsPath + fileName;
			this.strengthenMap = XlsPojoUtil.sheetToGenericMap(sourceFile,
					sheetName, EquipStrengthenConfig.class);
			for (EquipStrengthenConfig item : this.strengthenMap.values()) {
				item.init();
			}
		} catch (Exception e) {
			Log4jManager.CHECK.error("loadExel error : sourceFile = "
					+ fileName + " sheetName =" + sheetName, e);
			Log4jManager.checkFail();
		}
	}

	private void initStrengthenVipConfig() {
		String fileName = "";
		String sheetName = "";
		String xlsPath = GameContext.getPathConfig().getXlsPath();
		try {
			fileName = XlsSheetNameType.equip_streng_vip.getXlsName();
			sheetName = XlsSheetNameType.equip_streng_vip.getSheetName();
			String sourceFile = xlsPath + fileName;
			this.strengthenVipMap = XlsPojoUtil.sheetToGenericMap(sourceFile,
					sheetName, EquipStrengthenVip.class);
			if (null == this.strengthenVipMap) {
				return;
			}
			for (EquipStrengthenVip item : this.strengthenVipMap.values()) {
				item.init();
			}
		} catch (Exception e) {
			Log4jManager.CHECK.error("loadExel error : sourceFile = "
					+ fileName + " sheetName =" + sheetName, e);
			Log4jManager.checkFail();
		}
	}

	private void initEquipRecatingConfig() {
		String fileName = "";
		String sheetName = "";
		String info = "";
		try {
			// 洗练消耗
			fileName = XlsSheetNameType.equip_xilian_list.getXlsName();
			sheetName = XlsSheetNameType.equip_xilian_list.getSheetName();
			info = "loadExel error : sourceFile = " + fileName + " sheetName ="
					+ sheetName + ".";
			String xlsPath = GameContext.getPathConfig().getXlsPath();
			List<EquipRecatingConfig> rcList = XlsPojoUtil.sheetToList(xlsPath
					+ fileName, sheetName, EquipRecatingConfig.class);
			for (EquipRecatingConfig config : rcList) {
				if (null == config) {
					continue;
				}
				config.init(info);
				this.equipRecatingMap.put(config.getKey(), config);
			}
			// 锁消耗配置
			fileName = XlsSheetNameType.equip_xilian_lock_ratio.getXlsName();
			sheetName = XlsSheetNameType.equip_xilian_lock_ratio.getSheetName();
			info = "loadExel error : sourceFile = " + fileName + " sheetName ="
					+ sheetName + ".";
			List<EquipRecatingLockConfig> list = XlsPojoUtil.sheetToList(
					xlsPath + fileName, sheetName,
					EquipRecatingLockConfig.class);
			for (EquipRecatingLockConfig config : list) {
				if (null == config) {
					continue;
				}
				byte lockNum = config.getLockNum();
				short ratio = config.getRatio();
				if (lockNum <= 0) {
					this.checkFail(info + "lockNum must be greater than 0.");
				}
				if (ratio < EquipRecatingLockConfig.LOCK_RATIO_BASE_VALUE) {
					this.checkFail(info
							+ "ratio must be greater than or equal to "
							+ EquipRecatingLockConfig.LOCK_RATIO_BASE_VALUE);
				}
				this.equipRecatingLockRatioMap.put(lockNum, ratio);
				this.equipRecatingLockRatio = new short[this.equipRecatingLockRatioMap
						.size()];
				int i = 0;
				for (short value : this.equipRecatingLockRatioMap.values()) {
					equipRecatingLockRatio[i] = value;
					i++;
				}
			}
			// 属性权重配置
			fileName = XlsSheetNameType.equip_xilian_attr_weight.getXlsName();
			sheetName = XlsSheetNameType.equip_xilian_attr_weight
					.getSheetName();
			info = "loadExel error : sourceFile = " + fileName + " sheetName ="
					+ sheetName + ".";
			List<EquipRecatingAttrWeightConfig> awList = XlsPojoUtil
					.sheetToList(xlsPath + fileName, sheetName,
							EquipRecatingAttrWeightConfig.class);
			for (EquipRecatingAttrWeightConfig config : awList) {
				if (null == config) {
					continue;
				}
				config.init(info);
				this.equipRecatingAttrWeightMap.put(config.getKey(), config);
				byte attrType = config.getAttrType();
				if (!this.equipRecatingAttrList.contains(attrType)) {
					this.equipRecatingAttrList.add(attrType);
				}
			}
		} catch (Exception ex) {
			Log4jManager.CHECK.error(info, ex);
			Log4jManager.checkFail();
		}
	}

	private void checkFail(String info) {
		Log4jManager.CHECK.error(info);
		Log4jManager.checkFail();
	}

	@Override
	public void debug(String info) {

	}

	@Override
	public MosaicConfig getMosaicConfig() {
		return this.mosaicConfig;
	}

	@Override
	public EquipRecatingConfig getEquipRecatingConfig(int quality, int star) {
		return this.equipRecatingMap.get(EquipRecatingConfig.genKey(quality,
				star));
	}

	@Override
	public short[] getRquipRecatingLockRatio() {
		return this.equipRecatingLockRatio;
	}

	@Override
	public short getEquipRecatingLockRatio(byte lockNum) {
		Short value = this.equipRecatingLockRatioMap.get(lockNum);
		if (null == value) {
			return 0;
		}
		return value;
	}

	@Override
	public List<Byte> getEquipRecatingAttrList() {
		return this.equipRecatingAttrList;
	}

	@Override
	public EquipRecatingAttrWeightConfig getEquipRecatingAttrWeightConfig(
			byte attrType, int quality, int star) {
		return this.equipRecatingAttrWeightMap
				.get(EquipRecatingAttrWeightConfig.genKey(attrType, quality,
						star));
	}

}
