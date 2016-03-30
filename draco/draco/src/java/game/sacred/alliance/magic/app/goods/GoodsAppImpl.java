package sacred.alliance.magic.app.goods;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.goods.derive.EquipRecatingAttrWeightConfig;
import sacred.alliance.magic.app.goods.derive.EquipRecatingConfig;
import sacred.alliance.magic.app.goods.derive.EquipRecatingLockConfig;
import sacred.alliance.magic.app.goods.derive.EquipUpgradeConfig;
import sacred.alliance.magic.app.goods.derive.StorySuitConfig;
import sacred.alliance.magic.app.goods.derive.StorySuitEquipConfig;
import sacred.alliance.magic.app.goods.derive.StorySuitImageConfig;
import sacred.alliance.magic.base.GoodsType;
import sacred.alliance.magic.base.XlsSheetNameType;
import sacred.alliance.magic.component.id.IdFactory;
import sacred.alliance.magic.component.id.IdType;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.data.GoodsLoader;
import sacred.alliance.magic.domain.EquStrengthenEffect;
import sacred.alliance.magic.domain.EquStrengthenstar;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.domain.GoodsGem;
import sacred.alliance.magic.domain.MixFormula;
import sacred.alliance.magic.domain.MosaicConfig;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.XlsPojoUtil;

import com.game.draco.GameContext;
import com.google.common.collect.Maps;

/**
 * 物品系统所有配置文件加载及处理模版
 * @author Wang.K
 *
 */
public class GoodsAppImpl  implements GoodsApp,Service {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private IdFactory<String> idFactory;
	private GoodsLoader goodsLoader;
	
	//装备强化配置
	private Map<String, EquStrengthenstar> strengthenMap = Maps.newHashMap(); 
	private Map<String, EquStrengthenEffect> strengthenEffectMap = Maps.newHashMap();
	//宝石合成配置
	private Map<Integer, MixFormula> allMixFormula = Maps.newLinkedHashMap();
	//宝石镶嵌相关配置
	private MosaicConfig mosaicConfig ;
	//装备升级配置
	private Map<String,EquipUpgradeConfig> equipUpgradeMap = Maps.newHashMap();
	//装备洗练配置
	private Map<Integer,EquipRecatingConfig> equipRecatingMap = Maps.newHashMap();
	private TreeMap<Byte,Short> equipRecatingLockRatioMap = Maps.newTreeMap();
	private short[] equipRecatingLockRatio;
	private Map<String,EquipRecatingAttrWeightConfig> equipRecatingAttrWeightMap = Maps.newHashMap();
	private List<Byte> equipRecatingAttrList = new ArrayList<Byte>();
	//传说套装配置
	private TreeMap<Short,StorySuitConfig> storySuitMap = Maps.newTreeMap();
	private Map<String,StorySuitEquipConfig> storySuitEquipMap = Maps.newHashMap();
	private Map<Short,TreeMap<Byte,Short>> storySuitImageMap = Maps.newHashMap();
	private Map<Short,TreeMap<Integer,Map<Byte,Integer>>> storySuitTargetGoodsMap = Maps.newHashMap();
	
	/** 通过ID生成器获取物品实例ID */
	public String newGoodsInstanceId(){
		String id;
		try {
			id = idFactory.nextId(IdType.GOODS);
		} catch (Exception e) {
			logger.error("The idfactory generate goodsInstanceId  exception",e);
			return null;
		}
		return id;
	}
	
	public void setGoodsLoader(GoodsLoader goodsLoader) {
		this.goodsLoader = goodsLoader;
	}
	
	public GoodsBase getGoodsBase(int goodsId){
		if(goodsId <=0){
			return null ;
		}
		GoodsBase goodsBase = goodsLoader.getDataMap().get(String.valueOf(goodsId));
		if (null == goodsBase) {
			logger.error("goodsId = " + goodsId +" is no exist");
		}
		return goodsBase;
	}
	
	public boolean isExistGoods(int goodsId){
		return (null != this.getGoodsBase(goodsId));
	}
	
	public int getResId(int goodsId){
		GoodsBase goodsBase=getGoodsBase(goodsId);
		if(goodsBase==null){
			return 0;
		}
		return goodsBase.getResId();
	}
	
	/** 获取物品模版对象 */
	public <T extends GoodsBase> T getGoodsTemplate(Class<T> clazz, int goodsId){
		try{
			GoodsBase gb = this.getGoodsBase(goodsId);
			if(null == gb){
				return null ;
			}
			return (T)gb;
		}catch(Exception e){
			return null;
		}
	}
	
	/** 通过物品ID获取物品类型 */
	public GoodsType getGoodsType(int goodsId){
		GoodsBase goodsBase = this.getGoodsBase(goodsId);
		if(goodsBase == null){
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
	 * 1.初始化魂魄物品下一个物品的ID
	 * 2.加载强化所需物品信息
	 * 3.加载物品品质对应属性提升信息
	 * 4.打孔配置信息
	 * 5.宝石合成配置信息
	 * 6.加载装备品质系数
	 * 7.加载装备穿戴位置评分系数
	 */
	@Override
	public void start() {
		//初始化个goodsbase
		for(GoodsBase gb :  goodsLoader.getDataMap().values()){
			gb.init(gb.getInitData());
		}
		initStrengthenConfig();
		initStrengthenEffectConfig();
		initGemMixConfig();
		initMosaicConfig();
		this.initEquipUpgradeConfig();
		this.initEquipRecatingConfig();
		this.initStorySuitConfig();
	}
	
	@Override
	public void stop() {
		
	}
	
	@Override
	public EquStrengthenstar getStrengthenstar(int level) {
		return this.strengthenMap.get(String.valueOf(level));
	}
	
	@Override
	public EquStrengthenEffect getStrengthenEffect(int strengthenLevel, int qualityType){
		if(strengthenLevel <=0 ){
			return null ;
		}
		return this.strengthenEffectMap.get(strengthenLevel + Cat.colon + qualityType);
	}
	
	private void initMosaicConfig(){
		String fileName = "";
		String sheetName = "";
		String xlsPath = GameContext.getPathConfig().getXlsPath();
		try{
			fileName = XlsSheetNameType.equip_baoshi_config.getXlsName();
			sheetName = XlsSheetNameType.equip_baoshi_config.getSheetName();
			String sourceFile_level = xlsPath + fileName;
			List<MosaicConfig> configList = XlsPojoUtil.sheetToList(sourceFile_level,sheetName, MosaicConfig.class);
			if(null == configList || 0 == configList.size()){
				Log4jManager.CHECK.error("load mosaicConfig error,not config : sourceFile = " + fileName + " sheetName =" + sheetName );
				Log4jManager.checkFail();
			}
			this.mosaicConfig = configList.get(0);
		}catch(Exception e){
			Log4jManager.CHECK.error("load mosaicConfig error : sourceFile = " + fileName + " sheetName =" + sheetName, e);
			Log4jManager.checkFail();
		}
	}
	
	private void initStrengthenConfig(){
		String fileName = "";
		String sheetName = "";
		String xlsPath = GameContext.getPathConfig().getXlsPath();
		try{
			fileName = XlsSheetNameType.equip_qianghua_list.getXlsName();
			sheetName = XlsSheetNameType.equip_qianghua_list.getSheetName();
			String sourceFile = xlsPath + fileName;
			this.strengthenMap = XlsPojoUtil.sheetToGenericMap(sourceFile,sheetName, EquStrengthenstar.class);
			for(EquStrengthenstar item : this.strengthenMap.values()){
				item.init();
			}
		}catch(Exception e){
			Log4jManager.CHECK.error("loadExel error : sourceFile = " + fileName + " sheetName =" + sheetName, e);
			Log4jManager.checkFail();
		}
	}
	
	private void initStrengthenEffectConfig(){
		String fileName = "";
		String sheetName = "";
		String xlsPath = GameContext.getPathConfig().getXlsPath();
		try{
			fileName = XlsSheetNameType.equip_qianghua_effect.getXlsName();
			sheetName = XlsSheetNameType.equip_qianghua_effect.getSheetName();
			String sourceFile_level = xlsPath + fileName;
			strengthenEffectMap = XlsPojoUtil.sheetToGenericMap(sourceFile_level,sheetName, EquStrengthenEffect.class);
		}catch(Exception e){
			Log4jManager.CHECK.error("loadExel error : sourceFile = " + fileName + " sheetName =" + sheetName, e);
			Log4jManager.checkFail();
		}
	}

	private void initGemMixConfig(){
		String fileName = "";
		String sheetName = "";
		try{
		fileName = XlsSheetNameType.mix_formula.getXlsName();
		sheetName = XlsSheetNameType.mix_formula.getSheetName();
		String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
		allMixFormula = XlsPojoUtil.sheetToGenericLinkedMap(sourceFile, sheetName, MixFormula.class);
		for(MixFormula gf : allMixFormula.values()){
			this.initMix(gf);
		}
		}catch(Exception ex){
			Log4jManager.CHECK.error("loadExel gem_formula error : sourceFile = " + fileName + " sheetName =" + sheetName, ex);
			Log4jManager.checkFail();
		}
	}
	
	private void initMix(MixFormula gf){
		GoodsBase src = this.getGoodsBase(gf.getSrcId());
		GoodsBase target = this.getGoodsBase(gf.getTargetId());
		if(null == src || null == target || gf.getFee()<0){
			Log4jManager.CHECK.error("gem_formula config error,the goods template isn't exists  or fee<0" +
					" config: srcId=" + gf.getSrcId() + " targetId=" + gf.getTargetId());
			Log4jManager.checkFail();
			return ;
		}
		gf.setTargetGoods(target);
	}
	
	
	private void initEquipUpgradeConfig(){
		String fileName = "";
		String sheetName = "";
		try{
			fileName = XlsSheetNameType.equip_shengji_list.getXlsName();
			sheetName = XlsSheetNameType.equip_shengji_list.getSheetName();
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			this.equipUpgradeMap = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName, EquipUpgradeConfig.class);
			for(EquipUpgradeConfig config : this.equipUpgradeMap.values()){
				config.init();
			}
		}catch(Exception ex){
			Log4jManager.CHECK.error("loadExel error : sourceFile = " + fileName + " sheetName =" + sheetName, ex);
			Log4jManager.checkFail();
		}
	}
	
	private void initEquipRecatingConfig(){
		String fileName = "";
		String sheetName = "";
		String info = "";
		try{
			//洗练消耗
			fileName = XlsSheetNameType.equip_xilian_list.getXlsName();
			sheetName = XlsSheetNameType.equip_xilian_list.getSheetName();
			info = "loadExel error : sourceFile = " + fileName + " sheetName =" + sheetName + ".";
			String xlsPath = GameContext.getPathConfig().getXlsPath();
			List<EquipRecatingConfig> rcList = XlsPojoUtil.sheetToList(xlsPath + fileName, sheetName, EquipRecatingConfig.class);
			for(EquipRecatingConfig config : rcList){
				if(null == config){
					continue;
				}
				config.init(info);
				this.equipRecatingMap.put(config.getQualityType(), config);
			}
			//锁消耗配置
			fileName = XlsSheetNameType.equip_xilian_lock_ratio.getXlsName();
			sheetName = XlsSheetNameType.equip_xilian_lock_ratio.getSheetName();
			info = "loadExel error : sourceFile = " + fileName + " sheetName =" + sheetName + ".";
			List<EquipRecatingLockConfig> list = XlsPojoUtil.sheetToList(xlsPath + fileName, sheetName, EquipRecatingLockConfig.class);
			for(EquipRecatingLockConfig config : list){
				if(null == config){
					continue;
				}
				byte lockNum = config.getLockNum();
				short ratio = config.getRatio();
				if(lockNum <= 0){
					this.checkFail(info + "lockNum must be greater than 0.");
				}
				if(ratio < EquipRecatingLockConfig.LOCK_RATIO_BASE_VALUE){
					this.checkFail(info + "ratio must be greater than or equal to " + EquipRecatingLockConfig.LOCK_RATIO_BASE_VALUE);
				}
				this.equipRecatingLockRatioMap.put(lockNum, ratio);
				this.equipRecatingLockRatio = new short[this.equipRecatingLockRatioMap.size()];
				int i = 0;
				for(short value : this.equipRecatingLockRatioMap.values()){
					equipRecatingLockRatio[i] = value;
					i++;
				}
			}
			//属性权重配置
			fileName = XlsSheetNameType.equip_xilian_attr_weight.getXlsName();
			sheetName = XlsSheetNameType.equip_xilian_attr_weight.getSheetName();
			info = "loadExel error : sourceFile = " + fileName + " sheetName =" + sheetName + ".";
			List<EquipRecatingAttrWeightConfig> awList = XlsPojoUtil.sheetToList(xlsPath + fileName, sheetName, EquipRecatingAttrWeightConfig.class);
			for(EquipRecatingAttrWeightConfig config : awList){
				if(null == config){
					continue;
				}
				config.init(info);
				this.equipRecatingAttrWeightMap.put(config.getKey(), config);
				byte attrType = config.getAttrType();
				if(!this.equipRecatingAttrList.contains(attrType)){
					this.equipRecatingAttrList.add(attrType);
				}
			}
		}catch(Exception ex){
			Log4jManager.CHECK.error(info, ex);
			Log4jManager.checkFail();
		}
	}
	
	private void initStorySuitConfig(){
		String fileName = "";
		String sheetName = "";
		String info = "";
		try{
			//传说套装信息
			fileName = XlsSheetNameType.story_suit.getXlsName();
			sheetName = XlsSheetNameType.story_suit.getSheetName();
			info = "loadExel error : sourceFile = " + fileName + " sheetName =" + sheetName + ".";
			String xlsPath = GameContext.getPathConfig().getXlsPath();
			List<StorySuitConfig> suitList = XlsPojoUtil.sheetToList(xlsPath + fileName, sheetName, StorySuitConfig.class);
			for(StorySuitConfig config : suitList){
				if(null == config){
					continue;
				}
				config.init(info);
				this.storySuitMap.put(config.getSuitGroupId(), config);
			}
			//套装里装备的配置
			fileName = XlsSheetNameType.story_suit_equip_list.getXlsName();
			sheetName = XlsSheetNameType.story_suit_equip_list.getSheetName();
			info = "loadExel error : sourceFile = " + fileName + " sheetName =" + sheetName + ".";
			List<StorySuitEquipConfig> equiplist = XlsPojoUtil.sheetToList(xlsPath + fileName, sheetName, StorySuitEquipConfig.class);
			for(StorySuitEquipConfig config : equiplist){
				if(null == config){
					continue;
				}
				config.init(info);
				this.storySuitEquipMap.put(config.getKey(), config);
				//构建套装组、等级、部位、装备物品的关系
				short groupId = config.getSuitGroupId();
				TreeMap<Integer,Map<Byte,Integer>> treeMap = this.storySuitTargetGoodsMap.get(groupId);
				if(null == treeMap){
					treeMap = new TreeMap<Integer,Map<Byte,Integer>>();
					this.storySuitTargetGoodsMap.put(groupId, treeMap);
				}
				int level = config.getLevel();
				if(!treeMap.containsKey(level)){
					treeMap.put(level, new HashMap<Byte,Integer>());
				}
				treeMap.get(level).put(config.getEquipslotType(), config.getGoodsId());
			}
			//套装默认图标配置
			fileName = XlsSheetNameType.story_suit_default_image.getXlsName();
			sheetName = XlsSheetNameType.story_suit_default_image.getSheetName();
			info = "loadExel error : sourceFile = " + fileName + " sheetName =" + sheetName + ".";
			List<StorySuitImageConfig> imageList = XlsPojoUtil.sheetToList(xlsPath + fileName, sheetName, StorySuitImageConfig.class);
			for(StorySuitImageConfig config : imageList){
				if(null == config){
					continue;
				}
				short suitGroupId = config.getSuitGroupId();
				if(!this.storySuitImageMap.containsKey(suitGroupId)){
					this.storySuitImageMap.put(suitGroupId, new TreeMap<Byte,Short>());
				}
				this.storySuitImageMap.get(suitGroupId).put(config.getEquipslotType(), config.getImageId());
			}
		}catch(Exception ex){
			Log4jManager.CHECK.error(info, ex);
			Log4jManager.checkFail();
		}
	}
	
	private void checkFail(String info){
		Log4jManager.CHECK.error(info);
		Log4jManager.checkFail();
	}
	
	private GoodsGem getGemTemplate(int id){
		if(id <=0){
			return null ;
		}
		GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(id);
		if(null == gb || !(gb instanceof GoodsGem)){
			return null ;
		}
		return (GoodsGem)gb ;
	}

	@Override
	public void debug(String info) {
		/*Log log = Log4jManager.GOODS_DEBUG;
		if(log.isDebugEnabled()){
			log.debug(info);
		}*/
	}
	
	public Map<Integer, MixFormula> getAllMixFormula(){
		return this.allMixFormula;
	}


	@Override
	public MosaicConfig getMosaicConfig() {
		return this.mosaicConfig ;
	}
	
	@Override
	public EquipUpgradeConfig getEquipUpgradeConfig(int level,int qualityType ,int equipslotType){
		String key = level + Cat.underline + qualityType + Cat.underline + equipslotType ;
		return this.equipUpgradeMap.get(key);
	}
	
	@Override
	public EquipRecatingConfig getEquipRecatingConfig(int qualityType){
		return this.equipRecatingMap.get(qualityType);
	}

	@Override
	public short[] getRquipRecatingLockRatio() {
		return this.equipRecatingLockRatio;
	}

	@Override
	public short getEquipRecatingLockRatio(byte lockNum) {
		Short value = this.equipRecatingLockRatioMap.get(lockNum);
		if(null == value){
			return 0;
		}
		return value;
	}

	@Override
	public List<Byte> getEquipRecatingAttrList() {
		return this.equipRecatingAttrList;
	}

	@Override
	public EquipRecatingAttrWeightConfig getEquipRecatingAttrWeightConfig(byte attrType, int qualityType) {
		String key = attrType + Cat.underline + qualityType;
		return this.equipRecatingAttrWeightMap.get(key);
	}

	@Override
	public StorySuitConfig getStorySuitConfig(short suitGroupId) {
		return this.storySuitMap.get(suitGroupId);
	}

	@Override
	public StorySuitEquipConfig getStorySuitEquipConfig(short suitGroupId, int level, byte equipslotType) {
		String key = suitGroupId + Cat.underline + level + Cat.underline + equipslotType;
		return this.storySuitEquipMap.get(key);
	}

	@Override
	public TreeMap<Byte, Short> getStorySuitImageMap(short suitGroupId) {
		return this.storySuitImageMap.get(suitGroupId);
	}

	@Override
	public Collection<StorySuitConfig> getStorySuitConfigList() {
		return this.storySuitMap.values();
	}

	@Override
	public StorySuitEquipConfig getStorySuitEquipAutoLevelConfig(int roleLevel, short suitGroupId, byte equipslotType) {
		TreeMap<Integer,Map<Byte,Integer>> treeMap = this.storySuitTargetGoodsMap.get(suitGroupId);
		if(Util.isEmpty(treeMap)){
			return null;
		}
		int level = treeMap.firstKey();
		for(int lv : treeMap.keySet()){
			if(lv > roleLevel){
				break;
			}
			level = lv;
		}
		return this.getStorySuitEquipConfig(suitGroupId, level, equipslotType);
	}
}
