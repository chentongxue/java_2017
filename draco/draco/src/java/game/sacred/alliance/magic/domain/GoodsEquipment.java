package sacred.alliance.magic.domain;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import sacred.alliance.magic.app.attri.AttriItem;
import sacred.alliance.magic.app.goods.MosaicHole;
import sacred.alliance.magic.app.goods.RoleGoodsHelper;
import sacred.alliance.magic.app.goods.Util;
import sacred.alliance.magic.app.goods.derive.EquipRecatingAttrWeightConfig;
import sacred.alliance.magic.app.goods.derive.RecatingBoundBean;
import sacred.alliance.magic.app.goods.wing.WingGrid;
import sacred.alliance.magic.app.goods.wing.WingGridConfig;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.EquipslotType;
import sacred.alliance.magic.base.GoodsType;
import sacred.alliance.magic.util.DateUtil;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.item.GoodsBaseEquGoddessItem;
import com.game.draco.message.item.GoodsBaseEquHumanItem;
import com.game.draco.message.item.GoodsBaseEquItem;
import com.game.draco.message.item.GoodsBaseFashionItem;
import com.game.draco.message.item.GoodsBaseItem;
import com.game.draco.message.item.GoodsDerivativeAttriItem;
import com.game.draco.message.item.GoodsEquGemItem;

/**
 * 装备属性(固定装备、随机装备通用)
 * @author Administrator
 *
 */
public @Data class GoodsEquipment extends GoodsBase {
	
	private static final String DEFAULTSTR = "";
	
	private int equipslotType;//装备位置
	private byte gemType;//孔颜色
	private byte maxStarLevel;//最高强化等级
	private byte attriNum;//洗练属性个数
	
	private int atk;//攻击力
	private int rit;//防御力
	private int baseMaxHP;//基础血量上限(护甲专用)
	private int baseMaxMP;
	private int hit;//命中
	private int dodge;//闪避
	private int critAtk;//暴击
	private int critRit;//韧性
	
	/**
	 * 升级模板ID
	 */
	private int upgradeId ;
	private byte sex;//-1:通用0：男1：女

	private int gs;//装备评分
	
	/**允许此性别的使用*/
	public boolean isAllowSex(int sex){
		return this.sex == -1 || this.sex == sex;
	}
	
	/**加载调用**/
	@Override
	public void init(Object initData){
		
	}

	/**
	 * 获得装备评分
	 * @return
	 */
	private int getEquipScore(){
		//战斗力=(生命/15+攻击+防御+暴击+闪避+命中+抗暴)/5
		int score = this.baseMaxHP/15 
					+ this.atk
					+ this.rit ;
		score = score/5 ;
		return score ;
	}

	@Override
	public List<AttriItem> getAttriItemList() {
		List<AttriItem> attrList = new ArrayList<AttriItem>();
		//获得装备主要属性列表
		attrList.add(new AttriItem(AttributeType.atk.getType(), this.atk, 0f));
		attrList.add(new AttriItem(AttributeType.rit.getType(), this.rit, 0f));
		attrList.add(new AttriItem(AttributeType.maxHP.getType(), this.baseMaxHP, 0f));
		attrList.add(new AttriItem(AttributeType.maxMP.getType(), this.baseMaxMP, 0f));
		attrList.add(new AttriItem(AttributeType.hit.getType(), this.hit, 0f));
		attrList.add(new AttriItem(AttributeType.dodge.getType(), this.dodge, 0f));
		attrList.add(new AttriItem(AttributeType.critAtk.getType(), this.critAtk, 0f));
		attrList.add(new AttriItem(AttributeType.critRit.getType(), this.critRit, 0f));
		return attrList;
	}
	
	@Override
	public RoleGoods createSingleRoleGoods(String roleId, int overlapCount){
		RoleGoods rg = super.createSingleRoleGoods(roleId, overlapCount);
		//洗练属性一开始不创建
		rg.setAttrVar(DEFAULTSTR);
		
		//如果是翅膀并 初始化翅膀命格
		if(this.getDeadline() <= 0 && this.equipslotType == EquipslotType.wing.getType()) {
			GameContext.getWingApp().initRoleGoodsWingGird(rg, this.getOpenHoleNum());
		}
		return rg ;
	}
	
	private int calculateScore(RoleGoods roleGoods){
		if(roleGoods == null){
			return this.getEquipScore() ;
		}
		return RoleGoodsHelper.getEquipScore(roleGoods);
	}

	
	public GoodsBaseItem getGoodsBaseInfo(RoleGoods roleGoods){
		GoodsBaseEquItem item = null ;
		if(this.goodsType == GoodsType.GoodsEquHuman.getType()){
			item = new GoodsBaseEquHumanItem();
		}else if(this.goodsType == GoodsType.GoodsEquGoddess.getType()){
			item = new GoodsBaseEquGoddessItem();
		}else if(this.goodsType == GoodsType.GoodsFashion.getType()){
			item = new GoodsBaseFashionItem();
		}
		this.setGoodsBaseItem(roleGoods, item);
		item.setCarrerType(this.career);
		item.setSecondType(this.secondType);
		item.setStrengthenLevel((byte)0);
		item.setMaxStrengthenLevel(this.maxStarLevel);
		item.setAtk(this.atk);
		item.setRit(this.rit);
		item.setMaxHP(this.baseMaxHP);
		item.setMaxMP(this.baseMaxMP);
		item.setHit(this.hit);
		item.setDodge(this.dodge);
		item.setCritAtk(this.critAtk);
		item.setCritRit(this.critRit);
		item.setGemType(this.gemType);
		item.setQualityType(this.getQualityType());
		item.setLvLimit((byte) this.getLvLimit());
		item.setDesc(this.getDesc());
		item.setActivateType(this.getActivateType());
		item.setResId((short) this.getResId());
		item.setDeadline(this.getDeadline());
		item.setSex((byte) this.getSex());
		item.setExpireType(this.getExpireType());
		//套装ID
		item.setSuitId(this.suitId);
		item.setSuitGroupId(this.suitGroupId);
		item.setCanUpgrade(this.upgradeId>0?(byte)1:(byte)0);
		item.setMaxAttriNum((byte) this.attriNum);
		item.setHoleNum(this.getOpenHoleNum());
		//装备评分
		item.setGs(this.calculateScore(roleGoods));
		if (null == roleGoods) {
			//装备模板信息
			return item;
		}
		
		//实例存在
		if(!Util.isEmpty(roleGoods.getExpiredTime())){
			item.setExpiredTime(DateUtil.getMinFormat(roleGoods.getExpiredTime()));
		}
		if(roleGoods.getDeadline()>0){
			item.setDeadline(roleGoods.getDeadline());
		}
		item.setExpired((byte) (RoleGoodsHelper.isExpired(roleGoods)?1:0));
		item.setStrengthenLevel((byte)roleGoods.getStarNum());
		//获得强化加成
		EquStrengthenEffect effect = GameContext.getGoodsApp().getStrengthenEffect(roleGoods.getStarNum(), qualityType);
		if(null != effect){
			item.setStrengthenRate1(effect.getAddRate1());
			item.setStrengthenRate2(effect.getAddRate2());
		}
		//获得衍生属性
		if(!Util.isEmpty(roleGoods.getAttrVarList())){
			List<GoodsDerivativeAttriItem> derivativeItems = new ArrayList<GoodsDerivativeAttriItem>();
			for(AttriItem ai : roleGoods.getAttrVarList()){
				if(null == ai){
					continue;
				}
				byte attriType = ai.getAttriTypeValue() ;
				EquipRecatingAttrWeightConfig awc = GameContext.getGoodsApp().getEquipRecatingAttrWeightConfig(attriType, this.getQualityType());
				int value = (int)ai.getValue();
				RecatingBoundBean bean = awc.getRecatingBoundBean(value);
				if(null == bean){
					continue;
				}
				GoodsDerivativeAttriItem rii = new GoodsDerivativeAttriItem();
				rii.setType(attriType);
				rii.setValue(value);
				rii.setQuality((byte) bean.getQualityType());
				derivativeItems.add(rii);
			}
			item.setDerivativeItems(derivativeItems);
		}
		
		//镶嵌 命格 相关
		List<GoodsEquGemItem> gemItems = null;
		if(this.equipslotType != EquipslotType.wing.getType()) {
			//装备
			gemItems = getMosaicItem(roleGoods);
		}else{
			//翅膀
			gemItems = getWingGridItem(roleGoods);
		}
		if(!Util.isEmpty(gemItems)) {
			item.setGemItems(gemItems);
		}
		return item;
	
	}
	
	/**
	 * 获取镶嵌的item
	 * @param roleGoods
	 * @return
	 */
	private List<GoodsEquGemItem> getMosaicItem(RoleGoods roleGoods) {
		List<GoodsEquGemItem> gemItems = null;
		//获得宝石镶嵌属性
		MosaicHole[] holes = roleGoods.getMosaicHoles();
		if(null == holes || 0 == holes.length){
			return null ;
		}
		gemItems = new ArrayList<GoodsEquGemItem>();
		for(int index = 0 ;index < holes.length;index++){
			MosaicHole hole = holes[index];
			if(null == hole){
				continue ;
			}
			int gemId = hole.getGoodsId();
			GoodsGem gb = GameContext.getGoodsApp().getGoodsTemplate(GoodsGem.class,gemId);
			if(null == gb){
				continue ;
			}
			GoodsEquGemItem gemItem = new GoodsEquGemItem();
			gemItem.setHoleIndex((byte)index);
			gemItem.setImageId(gb.getImageId());
			gemItem.setLevel((byte)gb.getLevel());
			gemItem.setGemQuality(gb.getQualityType());
			gemItem.setGemName(gb.getName());
			gemItem.setAttriItems(gb.getDisplayAttriItem());
			gemItems.add(gemItem);
		}
		return gemItems;
	}
	
	/**
	 * 获取翅膀命格的item
	 * @param roleGoods
	 * @return
	 */
	private List<GoodsEquGemItem> getWingGridItem(RoleGoods roleGoods) {
		List<GoodsEquGemItem> gemItems = null;
		//获得宝石镶嵌属性
		WingGrid[] wingGrids = roleGoods.getWingGrids();
		if(null == wingGrids || 0 == wingGrids.length){
			return null ;
		}
		gemItems = new ArrayList<GoodsEquGemItem>();
		for(int index = 0 ;index < wingGrids.length;index++){
			WingGrid wingGrid = wingGrids[index];
			if(null == wingGrid){
				continue ;
			}
			WingGridConfig config = GameContext.getWingApp().getWingGrid((byte)wingGrid.getWingGridId(), wingGrid.getLevel());
			if(null == config){
				continue ;
			}
			GoodsEquGemItem gemItem = new GoodsEquGemItem();
			gemItem.setHoleIndex((byte)index);
			gemItem.setImageId(config.getImageId());
			gemItem.setLevel((byte)wingGrid.getLevel());
			gemItem.setGemQuality(config.getQualityType());
			gemItem.setGemName(config.getName());
			gemItem.setAttriItems(config.getDisplayAttriItemList());
			gemItems.add(gemItem);
		}
		return gemItems;
	}
	
}
