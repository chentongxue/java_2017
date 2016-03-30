package com.game.draco.app.equip;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.game.draco.app.equip.config.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Getter;
import lombok.Setter;
import sacred.alliance.magic.app.attri.AttriBuffer;
import sacred.alliance.magic.app.attri.AttriItem;
import sacred.alliance.magic.app.goods.GoodsHelper;
import sacred.alliance.magic.app.goods.HeroEquipBackpack;
import sacred.alliance.magic.app.goods.RoleGoodsHelper;
import sacred.alliance.magic.app.goods.behavior.derive.GoodsDeriveSupport;
import sacred.alliance.magic.app.goods.behavior.param.EquipUpgradeStarParam;
import sacred.alliance.magic.base.*;
import sacred.alliance.magic.constant.ParasConstant;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.domain.GoodsEquipment;
import sacred.alliance.magic.domain.GoodsHero;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.util.*;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.chat.ChannelType;
import com.game.draco.app.chat.ChatSysName;
import com.game.draco.app.hero.domain.RoleHero;
import com.game.draco.app.medal.MedalType;
import com.game.draco.message.item.EquipBaseAttriItem;
import com.game.draco.message.item.GoodsBaseItem;
import com.game.draco.message.item.GoodsLiteNamedItem;
import com.game.draco.message.item.HeroEquipFormulaItem;
import com.game.draco.message.item.MaterialItem;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.response.C0504_GoodsInfoViewRespMessage;
import com.game.draco.message.response.C1275_HeroEquipFormulaChangedNotifyMessage;
import com.game.draco.message.response.C1276_HeroEquipStarDetailRespMessage;
import com.game.draco.message.response.C1277_HeroEquipMaterialFormulaRespMessage;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class EquipAppImpl implements EquipApp,Service{
    private final byte EQUIP_STAR_FAIL_BY_RATE = 2 ;
	private final int FULL_RATE = 10000 ;
	public final Logger logger = LoggerFactory.getLogger(this.getClass());
	//################## begin 属性相关
	@Getter @Setter private Map<String,AttriBase> attriBaseMap = Maps.newHashMap() ;
	@Getter @Setter private Map<String,AttriQianghuaRate> attriQianghuaRateMap = Maps.newHashMap() ;
	@Getter @Setter private Map<String,AttriJinhuaRate> attriJinhuaRateMap = Maps.newHashMap() ;
	@Getter @Setter private Map<String,AttriEquipRate> attriEquipRateMap = Maps.newHashMap() ;
	@Getter @Setter AttriRate attriRate = null ;
	@Getter @Setter private List<AttributeType> attributeTypeList = Lists.newArrayList(
			AttributeType.atk, AttributeType.maxHP,
			AttributeType.rit, AttributeType.breakDefense,
			AttributeType.critAtk, AttributeType.critRit,
			AttributeType.dodge, AttributeType.hit);
	//################## end 属性相关结束
	
	//################## begin shengxing
	
	@Getter @Setter private Map<String,StarUpgradeFormula> starUpgradeFormulaMap = Maps.newHashMap() ;
	@Getter @Setter private Map<String,StarMaterialFormula> starMaterialFormulaMap = Maps.newHashMap() ;
	@Getter @Setter private Map<String,StarMaterialWays> starMaterialWays = Maps.newHashMap() ;
	@Getter @Setter private Map<String,StarWays> starWaysMap = Maps.newHashMap() ;
	@Getter @Setter private Map<Integer, StrengLevelHoleConfig> strengHoleMap = Maps.newHashMap();
	@Getter @Setter private Map<String,StarUpgradeRate> starUpgradeRateMap = Maps.newHashMap() ;
	/**
	 * 每品质最大的星
	 */
	private Map<String,Byte> qualityMaxStarMap = Maps.newHashMap() ;
	//################## end shengxing 

	@Getter private List<Integer> equipOpenCondList = Lists.newArrayList(
			QualityType.green.getType(),QualityType.green.getType(),
			QualityType.green.getType(),QualityType.blue.getType(),
			QualityType.purple.getType(),QualityType.purple.getType());


	@Override
	public int getEquipOpenQuality(int equipPos) {
		if(equipPos < 0){
			equipPos = 0 ;
		}else if(equipPos > this.equipOpenCondList.size()-1){
			equipPos = this.equipOpenCondList.size()-1 ;
		}
		return this.equipOpenCondList.get(equipPos) ;
	}

	@Override
	public StarUpgradeFormula getStarUpgradeFormula(int goodsId,int quality,int star){
		return starUpgradeFormulaMap.get(StarUpgradeFormula.genKey(goodsId, quality, star));
	}
	
	@Override
	public StarUpgradeFormula getNextStarUpgradeFormula(int goodsId,int quality,int star){
		StarUpgradeFormula formula = this.getStarUpgradeFormula(goodsId, quality, star);
		if(null == formula){
			return null ;
		}
		return formula.getNextConf() ;
	}
	
	@Override
	public StarMaterialFormula getStarMaterialFormula(int goodsId){
		return starMaterialFormulaMap.get(String.valueOf(goodsId));
	}
	
	@Override
	public StarMaterialWays getStarMaterialWays(int goodsId){
		return starMaterialWays.get(String.valueOf(goodsId));
	}
	
	@Override
	public StarWays getStarWays(int waysId){
		return starWaysMap.get(String.valueOf(waysId));
	}
	
	private byte getMaxStar(int goodsId,int quality){
		String key = goodsId + "_" + quality ;
		Byte value = this.qualityMaxStarMap.get(key);
		return (null == value)?0:value ;
	}
	
	private void initQualityMaxStar(int goodsId,byte quality,byte star){
		byte value = this.getMaxStar(goodsId,quality);
		if(value > star){
			return ;
		}
		String key = goodsId + "_" + quality ;
		this.qualityMaxStarMap.put(key, star);
	}
	
	private AttriBase getAttriBase(int goodsId,int quality,int star){
		return attriBaseMap.get(AttriBase.genKey(goodsId, quality, star));
	}
	
	private AttriQianghuaRate getAttriQianghuaRate(int strengthenLevel){
		return attriQianghuaRateMap.get(String.valueOf(strengthenLevel));
	}
	
	private AttriJinhuaRate getAttriJinhuaRate(int quality,int star){
		return attriJinhuaRateMap.get(AttriJinhuaRate.genKey(quality, star));
	}
	
	private AttriEquipRate getAttriEquipRate(int goodsId){
		return attriEquipRateMap.get(String.valueOf(goodsId));
	}
	
	private  <K, V extends KeySupport<K>> Map<K, V> loadMap(
			XlsSheetNameType xls, Class<V> clazz,boolean init,
			Map<K, V> storeMap) {
		Map<K, V> map = XlsPojoUtil.loadMap(xls, clazz, false) ;
		if (Util.isEmpty(map)) {
			Log4jManager.CHECK.error("not config the " + clazz.getSimpleName()
						+ " ,file=" + xls.getXlsName() + " sheet=" + xls.getSheetName());
			Log4jManager.checkFail();
			return map ;
		}
		if(init){
			boolean jude = false ;
			boolean support = false ;
			for(V item : map.values()){
				if(!jude){
					support = (item instanceof Initable);
					jude = true ;
				}
				if(!support){
					continue ;
				}
				((Initable)item).init();
			}
		}
		storeMap.clear();
		storeMap.putAll(map);
		map.clear();
		map = null ;
		return storeMap;
	}
	
	private void loadStrengHoleMap() {
		this.loadMap(XlsSheetNameType.equip_streng_level_hole, StrengLevelHoleConfig.class, true, this.strengHoleMap);
	}
	
	private void loadAttriBase() {
		this.loadMap(XlsSheetNameType.equip_attri_base_attri,
						AttriBase.class, true,this.attriBaseMap);
	}
	
	private void loadQianghuaRate(){
		this.loadMap(XlsSheetNameType.equip_attri_qianghua_rate,
						AttriQianghuaRate.class, true,this.attriQianghuaRateMap);
	}
	
	private void loadJinhuaRate(){
		this.loadMap(XlsSheetNameType.equip_attri_jinhua_rate,
				AttriJinhuaRate.class, true,this.attriJinhuaRateMap);
	}
	
	private void loadEquipRate(){
		this.loadMap(XlsSheetNameType.equip_attri_equip_rate,
				AttriEquipRate.class, true,this.attriEquipRateMap);
	}
	
	private void loadAttriRate(){
		String fileName = XlsSheetNameType.equip_attri_attri_rate.getXlsName();
		String sheetName = XlsSheetNameType.equip_attri_attri_rate.getSheetName();
		String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
		AttriRate attriRate = XlsPojoUtil.getEntity(sourceFile, sheetName, AttriRate.class) ;
		if (null == attriRate) {
			Log4jManager.CHECK.error("not config the " + AttriRate.class.getSimpleName()
					+ " ,file=" + sourceFile + " sheet=" + sheetName);
			Log4jManager.checkFail();
			return ;
		}
		attriRate.init();
		this.attriRate = attriRate ;
	}
	
	private void loadStarWays() {
		this.loadMap(XlsSheetNameType.equip_shengxing_ways_list,
				StarWays.class, true, this.starWaysMap);
	}

	private void loadStarMaterialWays() {
		this.loadMap(XlsSheetNameType.equip_shengxing_material_ways,
				StarMaterialWays.class, true, this.starMaterialWays);
	}

	private void loadStarMaterialFormula() {
		this.loadMap(XlsSheetNameType.equip_shengxing_material_formula,
				StarMaterialFormula.class, true, this.starMaterialFormulaMap);
	}

	private void loadStarUpgradeRate(){
		this.loadMap(XlsSheetNameType.equip_shengxing_rate,
				StarUpgradeRate.class, true, this.starUpgradeRateMap);
	}

	private void loadStarUpgradeFormula() {
		this.loadMap(XlsSheetNameType.equip_shengxing_formula,
				StarUpgradeFormula.class, true, this.starUpgradeFormulaMap);
		
		List<StarUpgradeFormula> list = Lists.newArrayList();
		list.addAll(this.starUpgradeFormulaMap.values());
		//排序
		Comparator<StarUpgradeFormula> comparator = new Comparator<StarUpgradeFormula>(){
			@Override
			public int compare(StarUpgradeFormula r1, StarUpgradeFormula r2) {
				if(r1.getGoodsId() < r2.getGoodsId()){
					return 1 ;
				}
				if(r1.getGoodsId() > r2.getGoodsId()){
					return -1 ;
				}
				if(r1.getQuality() < r2.getQuality()){
					return 1 ;
				}
				if(r1.getQuality() > r2.getQuality()){
					return -1 ;
				}
				if(r1.getStar() < r2.getStar()){
					return 1 ;
				}
				if(r1.getStar() > r2.getStar()){
					return -1 ;
				}
				return 0 ;
			}
		};
		Collections.sort(list, comparator);
		StarUpgradeFormula curr = null ;
		StarUpgradeFormula next = null ;
		for(int i= list.size()-1;i>0;i--){
			curr = list.get(i);
			next = list.get(i-1);
			if(next.getGoodsId() == curr.getGoodsId()){
				curr.setNextConf(list.get(i-1));
			}
			this.initQualityMaxStar(curr.getGoodsId(),curr.getQuality(), curr.getStar());
		}
		//第一行记录
		curr = list.get(0) ;
		this.initQualityMaxStar(curr.getGoodsId(),curr.getQuality(), curr.getStar());
		list.clear();
		list = null ;
	}
	
	

	@Override
	public void setArgs(Object arg0) {
		
	}

	@Override
	public void start() {
		loadAttriBase();
		loadQianghuaRate();
		loadJinhuaRate();
		loadEquipRate();
		loadAttriRate();
		//必须在loadStarMaterialWays前面
		loadStarWays();
		loadStarMaterialWays();
		loadStarMaterialFormula();
		loadStarUpgradeFormula();
		this.loadStarUpgradeRate();
		loadStrengHoleMap();
	}

	@Override
	public void stop() {
		
	}
	
	@Override
	public AttriBuffer getBaseAttriBuffer(int goodsId,int quality,int star){
		AttriBuffer buffer = AttriBuffer.createAttriBuffer();
		AttriBase base = this.getAttriBase(goodsId, quality, star);
		if(null == base){
			return buffer ;
		}
		return buffer.append(base.getAttriItemList()) ;
	}
	
	@Override
	public AttriBuffer getStrengthenAttriBuffer(int goodsId,int quality,int star,int strengthenLevel){
		if(strengthenLevel <= 0){
			return null ;
		}
		//强化属性=round(品质系数*强化等级系数*属性系数*装备系数,0)
		AttriBuffer buffer = AttriBuffer.createAttriBuffer();
		AttriJinhuaRate jinhuaRate = this.getAttriJinhuaRate(quality, star);
		AttriQianghuaRate qianghuaRate = this.getAttriQianghuaRate(strengthenLevel);
		AttriEquipRate equipRate = this.getAttriEquipRate(goodsId);
		for(AttributeType at : this.getAttributeTypeList()){
			byte attriType = at.getType();
			//成长
			float value = this.getAttributeValue(attriType, jinhuaRate,1.0f)
					* this.getAttributeValue(attriType, qianghuaRate,1.0f)
					* this.getAttributeValue(attriType, equipRate,1.0f)
					* this.getAttributeValue(attriType, this.attriRate,1.0f);
			int totalValue = Math.max(0,(int)value);
			buffer.append(at,totalValue,false);
		}
		return buffer ;
	}

	private float getAttributeValue(byte attriType,AttributeSupport support,float defValue){
		if(null == support){
			return defValue ;
		}
		return support.getValue(attriType);
	}
	
	@Override
	public List<EquipBaseAttriItem> getStrengthenAttriDifferent(RoleGoods roleGoods,GoodsEquipment equip,int incrStrengLevel){
		AttriBuffer allBuffer = RoleGoodsHelper.getAttriBuffer(roleGoods);
		AttriBuffer preStrengthenBuffer = this.getStrengthenAttriBuffer(equip.getId(), roleGoods.getQuality(),
				roleGoods.getStar(), roleGoods.getStrengthenLevel());
		if(null != preStrengthenBuffer){
			preStrengthenBuffer.reverse();
		}
		AttriBuffer postStrengthenBuffer = this.getStrengthenAttriBuffer(equip.getId(), roleGoods.getQuality(),
				roleGoods.getStar(), roleGoods.getStrengthenLevel() + incrStrengLevel);
		postStrengthenBuffer.append(preStrengthenBuffer);
		
		List<EquipBaseAttriItem> list = Lists.newArrayList();
		for(AttributeType at : this.getAttributeTypeList()){
			int base = this.getValue(allBuffer, at) ;
			int incr = this.getValue(postStrengthenBuffer, at) ;
			if(base <= 0 && incr <=0){
				continue ;
			}
			EquipBaseAttriItem item = new EquipBaseAttriItem();
			item.setType(at.getType());
			item.setBase(base);
			item.setIncrease(incr);
			list.add(item);
		}
		return list ;
	}
	
	@Override
	public List<EquipBaseAttriItem> getBaseAttriItem(RoleGoods roleGoods,GoodsEquipment equip){
		int quality = equip.getQualityType() ;
		int star = equip.getStar() ;
		int strengthenLevel = 0 ;
		if(null != roleGoods ){
			quality = roleGoods.getQuality() ;
			star = roleGoods.getStar() ;
			strengthenLevel = roleGoods.getStrengthenLevel() ;
		}
		AttriBuffer baseBuffer = this.getBaseAttriBuffer(equip.getId(), quality, star);
		AttriBuffer addBuffer = this.getStrengthenAttriBuffer(equip.getId(), quality, star, strengthenLevel);
		List<EquipBaseAttriItem> list = Lists.newArrayList();
		for(AttributeType at : this.getAttributeTypeList()){
			int base = this.getValue(baseBuffer, at) ;
			int incr = this.getValue(addBuffer, at) ;
			if(base <=0 && incr <=0){
				continue ;
			}
			EquipBaseAttriItem item = new EquipBaseAttriItem();
			item.setType(at.getType());
			item.setBase(base);
			item.setIncrease(incr);
			list.add(item);
		}
		return list ;
	}
	
	private int getValue(AttriBuffer buffer,AttributeType at){
		if(null == buffer){
			return 0 ;
		}
		AttriItem item = buffer.getAttriItem(at);
		if(null == item){
			return 0 ;
		}
		return (int)item.getValue() ;
	}
	
	
	private HeroEquipFormulaItem getHeroEquipFormulaItem(RoleGoods roleGoods,GoodsHero goodsHero,int i){
		int goodsId = 0 ;
		int iconId = 0 ;
		StarUpgradeFormula next = null ;
		if(null != roleGoods){
			goodsId = roleGoods.getGoodsId() ;
			next = this.getNextStarUpgradeFormula(goodsId, roleGoods.getQuality(), roleGoods.getStar());
		}else {
			goodsId = goodsHero.getEquipId(i);
			GoodsEquipment equip = GameContext.getGoodsApp().getGoodsTemplate(GoodsEquipment.class, goodsId);
			iconId = equip.getImageId() ;
			next = this.getStarUpgradeFormula(goodsId, equip.getQualityType(), equip.getStar());
		}
		HeroEquipFormulaItem item = new HeroEquipFormulaItem();
		item.setGoodsId(goodsId);
		item.setPos((byte)i);
		item.setIconId((short)iconId);
		//材料列表
		if(null != next){
			item.setMaterialList(next.getMaterialList());
			item.setHeroLevel((byte)next.getHeroLevel());
		}else{
			//为空则表示已满
			//设置goodsId=0
			item.setGoodsId(0);
		}
		return item ;
	}
	
	@Override
	public List<HeroEquipFormulaItem> getHeroEquipFormula(RoleInstance role,RoleHero roleHero) {
		List<HeroEquipFormulaItem> list = Lists.newArrayList() ;
		//获得英雄模版
		//获得英雄当前的装备
		GoodsHero goodsHero = GameContext.getGoodsApp().getGoodsTemplate(GoodsHero.class, roleHero.getHeroId()) ;
		HeroEquipBackpack pack = GameContext.getUserHeroApp().getEquipBackpack(role.getRoleId(), roleHero.getHeroId());
		RoleGoods[] grids = pack.getGrids() ;
		for(int i=0;i< ParasConstant.HERO_EQUIP_MAX_NUM;i++){
			list.add(getHeroEquipFormulaItem(grids[i],goodsHero,i));
		}
		return list ;
	}
	
	/**
	 * 调用处
	 * 1.脱穿装备
	 * 2.装备升星成功
	 */
	@Override
	public void onHeroEquipFormulaChanged(RoleInstance role,int heroId,int equipPos){
		C1275_HeroEquipFormulaChangedNotifyMessage notifyMsg = new C1275_HeroEquipFormulaChangedNotifyMessage();
		notifyMsg.setHeroId(heroId);
		GoodsHero goodsHero = GameContext.getGoodsApp().getGoodsTemplate(GoodsHero.class, heroId) ;
		HeroEquipBackpack pack = GameContext.getUserHeroApp().getEquipBackpack(role.getRoleId(), heroId);
		RoleGoods[] grids = pack.getGrids() ;
		notifyMsg.setFormula(getHeroEquipFormulaItem(grids[equipPos],goodsHero,equipPos));
		role.getBehavior().sendMessage(notifyMsg);
	}
	
	private String getText(String textId){
		return GameContext.getI18n().getText(textId);
	}
	
	
	@Override
	public Result formulaMix(RoleInstance role,int targetGoodsId,int mixNum){
		//获得配方
		Result result = new Result();
		result.failure();
		FormulaSupport formula = this.getStarMaterialFormula(targetGoodsId);
		if(null == formula){
			//有可能是装备
			GoodsEquipment equip = GameContext.getGoodsApp().getGoodsTemplate(GoodsEquipment.class, targetGoodsId);
			if(null != equip){
				formula = this.getStarUpgradeFormula(targetGoodsId, equip.getQualityType(), equip.getStar());
			}
		}
		if(mixNum <=0 || null == formula){
			result.setInfo(this.getText(TextId.ERROR_INPUT));
			return result ;
		}
		int gameMoney = formula.getGameMoney()*mixNum;
		List<MaterialItem> materialList = formula.getMaterialList();
		if(Util.isEmpty(materialList)){
			//必须要有材料
			result.setInfo(this.getText(TextId.ERROR_DATA));
			return result ;
		}
		//判断游戏币是否足够
		//【游戏币/潜能/钻石不足弹板】 判断
		Result ar = GameContext.getUserAttributeApp().getEnoughResult(role, AttributeType.gameMoney, gameMoney);
		if(ar.isIgnore()){
			return ar;
		}
		if(!ar.isSuccess() || gameMoney < 0){
			return result.setInfo(this.getText(TextId.NOT_ENOUGH_GAME_MOENY));
		}

		Map<Integer,Integer> addMap = Maps.newHashMap();
		addMap.put(targetGoodsId, mixNum) ;
		
		Map<Integer,Integer> delMap = Maps.newHashMap();
		for(MaterialItem m : materialList){
			Integer num = delMap.get(m.getGoodsId());
			if(null == num){
				delMap.put(m.getGoodsId(), (int)(m.getGoodsNum()*mixNum));
				continue ;
			}
			delMap.put(m.getGoodsId(), m.getGoodsNum() + num);
		}
		result = GameContext.getUserGoodsApp().addDelGoodsForBag(role, 
				addMap, OutputConsumeType.hero_equip_material_mix, 
				delMap, OutputConsumeType.hero_equip_material_mix_consume) ;
		
		if(!result.isSuccess()){
			return result ;
		}
		//扣除游戏币
		if(gameMoney > 0){
			GameContext.getUserAttributeApp().changeRoleMoney(role, AttributeType.gameMoney, OperatorType.Decrease,
					gameMoney, OutputConsumeType.hero_equip_material_mix_consume);
			role.getBehavior().notifyAttribute();
		}
		return result ;
	}
	
	private Message buildErrorMessage(String info){
		return new C0003_TipNotifyMessage(info);
	}
	
	@Override
	public Message getNextStarEquipDetail(RoleInstance role,byte bagType, String goodsInstanceId,
			int targetId){
		StorageType st = StorageType.get(bagType);
		if(StorageType.bag != st && StorageType.hero != st){
			return this.buildErrorMessage(this.getText(TextId.ERROR_INPUT));
		}
		RoleGoods roleGoods = GameContext.getUserGoodsApp().getRoleGoods(role, st,goodsInstanceId, targetId) ;
		if(null == roleGoods){
			return this.buildErrorMessage(this.getText(TextId.GOODS_NO_FOUND));
		}
		GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(roleGoods.getGoodsId());
		if(null == gb){
			return this.buildErrorMessage(this.getText(TextId.GOODS_NO_FOUND));
		}
		StarUpgradeFormula formula = this.getNextStarUpgradeFormula(
				roleGoods.getGoodsId(), roleGoods.getQuality(), roleGoods.getStar()) ;
		if(null == formula){
			return buildErrorMessage(this.getText(TextId.EQUIP_STAR_HAVE_MAX));
		}
		//!!!!
		RoleGoods nextRoleGoods = roleGoods.clone() ;
		//物品绑定
		nextRoleGoods.setBind(BindingType.already_binding.getType());
		nextRoleGoods.setQuality(formula.getQuality());
		nextRoleGoods.setStar(formula.getStar());
		GoodsBaseItem goodsParItem = gb.getGoodsBaseInfo(nextRoleGoods);
		if(null == goodsParItem ){
			return null ;
		}
		//修改等级
		goodsParItem.setLevel((byte)formula.getHeroLevel());
		C0504_GoodsInfoViewRespMessage baseMsg = new C0504_GoodsInfoViewRespMessage();
		baseMsg.setId(String.valueOf(nextRoleGoods.getGoodsId()));
		baseMsg.setBaseItem(goodsParItem);
		return baseMsg ;
		
	}
	
	@Override
	public Result equipUpgradeStar(EquipUpgradeStarParam starParam) {
		Result result = new Result() ;
		byte bagType = starParam.getBagType() ;
		StorageType st = StorageType.get(bagType);
		if(StorageType.bag != st && StorageType.hero != st){
			result.setInfo(this.getText(TextId.ERROR_INPUT));
			return result ;
		}
		RoleGoods roleGoods = GameContext.getUserGoodsApp().getRoleGoods(starParam.getRole(), st,
				starParam.getGoodsInstanceId(), starParam.getTargetId()) ;
		if(null == roleGoods){
			result.setInfo(this.getText(TextId.GOODS_NO_FOUND));
			return result ;
		}
		StarUpgradeFormula formula = this.getNextStarUpgradeFormula(
				roleGoods.getGoodsId(), roleGoods.getQuality(), roleGoods.getStar()) ;
		if(null == formula){
			result.setInfo(this.getText(TextId.EQUIP_STAR_HAVE_MAX));
			return result ;
		}
		//成功率配置
		StarUpgradeRate rate = this.getStarUpgradeRate(formula.getQuality(), formula.getStar()) ;
		if(null == rate){
			result.setInfo(this.getText(TextId.ERROR_DATA));
			return result ;
		}
		RoleInstance role = starParam.getRole() ;
		int gameMoney = formula.getGameMoney();
		List<MaterialItem> materialList = formula.getMaterialList();
		if(Util.isEmpty(materialList)){
			//必须要有材料
			result.setInfo(this.getText(TextId.ERROR_DATA));
			return result ;
		}
		//判断游戏币是否足够
		//【游戏币/潜能/钻石不足弹板】 判断
		Result ar = GameContext.getUserAttributeApp().getEnoughResult(role, AttributeType.gameMoney, gameMoney);
		if(ar.isIgnore()){
			return ar;
		}
		if(!ar.isSuccess()){
			result.setInfo(this.getText(TextId.NOT_ENOUGH_GAME_MOENY));
		}
		//判断英雄是否达到了等级
		RoleHero roleHero = null ;
		if(StorageType.hero == st){
			roleHero = GameContext.getUserHeroApp().getRoleHero(role.getRoleId(), starParam.getTargetId()) ;
			if(null == roleHero){
				result.setInfo(this.getText(TextId.ERROR_INPUT));
				return result ;
			}
			if(roleHero.getLevel() < formula.getHeroLevel()){
				result.setInfo(GameContext.getI18n().messageFormat(TextId.EQUIP_STAR_HERO_LEVEL_MUST_REACH, 
						formula.getHeroLevel())) ;
				return result ;
			}
		}
		Map<Integer,Integer> delMap = Maps.newHashMap();
		for(MaterialItem m : materialList){
			Integer num = delMap.get(m.getGoodsId());
			if(null == num){
				delMap.put(m.getGoodsId(), (int)m.getGoodsNum());
				continue ;
			}
			delMap.put(m.getGoodsId(), m.getGoodsNum() + num);
		}
		result = GameContext.getUserGoodsApp().deleteForBagByMap(role, delMap, OutputConsumeType.equip_upgrade_star_consume);
		if(!result.isSuccess()){
			return result ;
		}
        result.failure();
		//扣除游戏币
		if(gameMoney > 0){
			GameContext.getUserAttributeApp().changeRoleMoney(role, AttributeType.gameMoney, OperatorType.Decrease,
					gameMoney, OutputConsumeType.equip_upgrade_star_consume);
			role.getBehavior().notifyAttribute();
		}
		//计算成功率
		if(RandomUtil.absRandomInt(FULL_RATE) >= rate.getRate()){
			result.setInfo(GameContext.getI18n().getText(TextId.EQUIP_STAR_FAIL_BY_RATE)) ;
            //标识应为几率问题导致升星失败
            result.setResult(EQUIP_STAR_FAIL_BY_RATE) ;
			return result ;
		}
        result.success();
		boolean isOn = GoodsHelper.isOnBattleHero(role.getRoleId(), bagType, starParam.getTargetId());
		AttriBuffer buffer = null ;
		if(isOn){
			buffer = RoleGoodsHelper.getAttriBuffer(roleGoods);
			buffer.reverse();
		}
		//物品绑定
		roleGoods.setBind(BindingType.already_binding.getType());
		roleGoods.setQuality(formula.getQuality());
		roleGoods.setStar(formula.getStar());
		// 重新计算基本属性中的属性,并且通知同步基本属性
		GoodsDeriveSupport.initBaseAttrNotifyGoodsInfo(role, roleGoods, bagType);
		if(isOn){
			buffer.append(RoleGoodsHelper.getAttriBuffer(roleGoods));
			GameContext.getUserAttributeApp().changeAttribute(role,buffer);
			role.getBehavior().notifyAttribute();
		}
        //通知装备配方改变
        this.onHeroEquipFormulaChanged(role, roleHero.getHeroId(), roleGoods.getGridPlace());

		boolean isOnSwitchHero = GoodsHelper.isOnSwitchHero(role.getRoleId(),bagType, starParam.getTargetId());
		if(isOnSwitchHero){
			//胸章
			GameContext.getMedalApp().updateMedal(role, MedalType.XiLian, roleGoods);
			//更新其他英雄的战斗力
			//当前出战英雄已经计算
			GameContext.getHeroApp().syncBattleScore(role, starParam.getTargetId(),!isOn);
		}
		// 世界广播
		this.broadcast(role, formula);
		result.setInfo(GameContext.getI18n().getText(TextId.EQUIP_STAR_SUCCESS)) ;
		return result ;
	}
	
	/**
	 * 走马灯广播
	 * @param role
	 * @param formula
	 */
	private void broadcast(RoleInstance role, StarUpgradeFormula formula) {
		try {
			String broadcastInfo = formula.getBroadcastTips(role);
			if (Util.isEmpty(broadcastInfo)) {
				return ;
			}
			GameContext.getChatApp().sendSysMessage(ChatSysName.Goods_UpgradeStar, ChannelType.Publicize_Personal, broadcastInfo, null, null);
		} catch (Exception e) {
			logger.error("equipUpgradeStar broadcast error", e);
		}
	}
	
	@Override
	public List<GoodsLiteNamedItem> getMaterialsList(FormulaSupport formula){
		List<GoodsLiteNamedItem> materialsList = Lists.newArrayList();
		if(null == formula){
			return materialsList ;
		}
		for(MaterialItem m : formula.getMaterialList()){
			GoodsBase base = GameContext.getGoodsApp().getGoodsBase(m.getGoodsId());
			GoodsLiteNamedItem goodsItem = base.getGoodsLiteNamedItem();
			goodsItem.setNum(m.getGoodsNum());
			materialsList.add(goodsItem);
		}
		return materialsList ;
	}
	
	
	private C1276_HeroEquipStarDetailRespMessage getHeroEquipStarDetailRespMessage(RoleInstance role,RoleGoods roleGoods){
		StarUpgradeFormula formula = this.getNextStarUpgradeFormula(roleGoods.getGoodsId(), 
				roleGoods.getQuality(), roleGoods.getStar()) ;
		C1276_HeroEquipStarDetailRespMessage respMsg = new C1276_HeroEquipStarDetailRespMessage();
		GoodsEquipment equip = GameContext.getGoodsApp().getGoodsTemplate(GoodsEquipment.class, roleGoods.getGoodsId()) ;
		GoodsLiteNamedItem namedItem = equip.getGoodsLiteNamedItem(roleGoods) ;
		namedItem.setQualityType(formula.getQuality());
		namedItem.setStar(formula.getStar());
		respMsg.setTargetGoods(namedItem);
		respMsg.setMaterialsList(this.getMaterialsList(formula));
		respMsg.setGameMoney(formula.getGameMoney());
		StarUpgradeRate rate = this.getStarUpgradeRate(formula.getQuality(),formula.getStar());
		if(null != rate){
			respMsg.setRate(rate.getShowRate());
		}
		return respMsg ;
	}
	
	private C1277_HeroEquipMaterialFormulaRespMessage getHeroEquipStarDetailRespMessage(RoleInstance role,RoleHero roleHero,int pos){
		GoodsHero goodsHero = GameContext.getGoodsApp().getGoodsTemplate(GoodsHero.class, roleHero.getHeroId());
		int goodsId = goodsHero.getEquipId(pos);
		GoodsEquipment equip = GameContext.getGoodsApp().getGoodsTemplate(GoodsEquipment.class, goodsId) ;
		StarUpgradeFormula formula = this.getStarUpgradeFormula(goodsId, equip.getQualityType(), equip.getStar()) ;
		
		C1277_HeroEquipMaterialFormulaRespMessage respMsg = new C1277_HeroEquipMaterialFormulaRespMessage();
		respMsg.setTargetGoods(equip.getGoodsLiteNamedItem());
		respMsg.setMaterialsList(this.getMaterialsList(formula));
		respMsg.setGameMoney(formula.getGameMoney());
		return respMsg ;
	}
	
	@Override
	public Message getHeroEquipStarDetailRespMessage(RoleInstance role,int heroId,String goodsInstanceId,byte pos){
		RoleHero roleHero = null ;
		RoleGoods roleGoods = null ;
		StorageType storageType = StorageType.bag ;
		if(heroId > 0){
			roleHero = GameContext.getUserHeroApp().getRoleHero(role.getRoleId(), heroId);
			storageType = StorageType.hero ;
		}
		if(!Util.isEmpty(goodsInstanceId)){
			roleGoods = GameContext.getUserGoodsApp().getRoleGoods(role, storageType, goodsInstanceId, heroId) ;
		}
		if(null == roleHero && null == roleGoods){
			return new C0003_TipNotifyMessage(this.getText(TextId.ERROR_INPUT)) ;
		}
		if(null == roleGoods){
			//返回材料消息
			C1277_HeroEquipMaterialFormulaRespMessage respMsg = getHeroEquipStarDetailRespMessage(role,roleHero,pos) ;
			respMsg.setHeroId(heroId);
			return respMsg ;
		}
		C1276_HeroEquipStarDetailRespMessage respMsg = getHeroEquipStarDetailRespMessage(role,roleGoods) ;
		respMsg.setHeroId(heroId);
		respMsg.setGoodsInstanceId(goodsInstanceId);
		respMsg.setPos(pos);
		return respMsg ;
	}
	
	
	@Override
	public int getHeroEquipslotType(int heroId,int goodsId) {
		GoodsHero hero = GameContext.getGoodsApp().getGoodsTemplate(GoodsHero.class, heroId);
		if(null == hero){
			return -1 ;
		}
		return hero.getEquipslotType(goodsId) ;
	}

	@Override
	public byte getEquipMaxHole(RoleGoods roleGoods) {
		if (null == roleGoods) {
			return 0;
		}
		StrengLevelHoleConfig config = this.getStrengLevelHoleConfig(roleGoods.getStrengthenLevel());
		if (null == config) {
			return 0;
		}
		return config.getHole();
	}
	
	private StrengLevelHoleConfig getStrengLevelHoleConfig(int level) {
		if (Util.isEmpty(this.strengHoleMap)) {
			return null;
		}
		return this.strengHoleMap.get(level);
	}

	@Override
	public byte getOpenHoleLevel(byte hole) {
		if (Util.isEmpty(this.strengHoleMap)) {
			return 0;
		}
		for (int i = 0; i < this.strengHoleMap.size(); i++) {
			StrengLevelHoleConfig config = this.getStrengLevelHoleConfig(i);
			if (null == config) {
				continue;
			}
			if (config.getHole() >= hole + 1) {
				return (byte) config.getStrengthenLevel();
			}
		}
		return 0;
	}

	private StarUpgradeRate getStarUpgradeRate(int q,int s){
		String key = q + "_" + s ;
		return Util.fromMap(this.starUpgradeRateMap,key);
	}

}
