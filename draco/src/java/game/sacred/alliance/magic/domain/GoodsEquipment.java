package sacred.alliance.magic.domain;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import sacred.alliance.magic.app.attri.AttriItem;
import sacred.alliance.magic.app.goods.RoleGoodsHelper;
import sacred.alliance.magic.app.goods.Util;
import sacred.alliance.magic.app.goods.derive.EquipRecatingAttrWeightConfig;
import sacred.alliance.magic.app.goods.derive.RecatingBoundBean;
import sacred.alliance.magic.base.StorageType;
import sacred.alliance.magic.util.DateUtil;

import com.game.draco.GameContext;
import com.game.draco.app.rune.domain.MosaicRune;
import com.game.draco.message.item.GoodsBaseEquItem;
import com.game.draco.message.item.GoodsBaseItem;
import com.game.draco.message.item.GoodsDerivativeAttriItem;
import com.game.draco.message.item.GoodsEquRuneItem;

/**
 * 装备属性(固定装备、随机装备通用)
 * @author Administrator
 * 
 */
public @Data class GoodsEquipment extends GoodsBase {

	private static final String DEFAULTSTR = "";
	
	private int equipslotType;//装备位置(没有作用)
	private short maxStrengthenLevel;//最高强化等级
	private byte attriNum;//洗练属性个数
	private byte sex;//-1:通用0：男1：女

	/** 允许此性别的使用 */
	public boolean isAllowSex(int sex) {
		return this.sex == -1 || this.sex == sex;
	}

	/** 加载调用* */
	@Override
	public void init(Object initData) {

	}

	/**
	 * 获得装备评分
	 * @return
	 */
	private int getEquipScore() {
		return 0;
	}

	@Override
	public List<AttriItem> getAttriItemList() {
		//!!! 必须返回null
		//基本属性通过 EquipApp AttriBuffer getBaseAttriBuffer(int goodsId,int quality,int star)获取
		return null ;
	}

	@Override
	public RoleGoods createSingleRoleGoods(String roleId, int overlapCount) {
		RoleGoods rg = super.createSingleRoleGoods(roleId, overlapCount);
		rg.setQuality(this.qualityType);
		rg.setStar(this.star);
		// 洗练属性一开始不创建
		rg.setAttrVar(DEFAULTSTR);
		return rg;
	}

	private int calculateScore(RoleGoods roleGoods) {
		if (roleGoods == null) {
			return this.getEquipScore();
		}
		return RoleGoodsHelper.getEquipScore(roleGoods);
	}

	public GoodsBaseItem getGoodsBaseInfo(RoleGoods roleGoods) {
		GoodsBaseEquItem item = new GoodsBaseEquItem();
		this.setGoodsBaseItem(roleGoods, item);
		item.setStar(this.star);
		item.setSecondType(this.secondType);
		item.setStrengthenLevel((byte)0);
		item.setMaxStrengthenLevel((byte)this.maxStrengthenLevel);
		item.setQualityType(this.getQualityType());
		item.setLvLimit((byte) this.getLvLimit());
		item.setDesc(this.getDesc());
		item.setActivateType(this.getActivateType());
		item.setResId((short) this.getResId());
		item.setDeadline(this.getDeadline());
		item.setSex((byte) this.getSex());
		item.setExpireType(this.getExpireType());
		item.setMaxAttriNum((byte) this.attriNum);
		// 装备评分
		item.setGs(this.calculateScore(roleGoods));
		// 基本属性+强化属性
		item.setBaseAttriItem(GameContext.getEquipApp().getBaseAttriItem(roleGoods, this));
		if (null == roleGoods) {
			// 装备模板信息
			return item;
		}
		item.setQualityType(roleGoods.getQuality());
		item.setStar(roleGoods.getStar());
		// 实例存在
		if (!Util.isEmpty(roleGoods.getExpiredTime())) {
			item.setExpiredTime(DateUtil.getMinFormat(roleGoods.getExpiredTime()));
		}
		if (roleGoods.getDeadline() > 0) {
			item.setDeadline(roleGoods.getDeadline());
		}
		item.setExpired((byte) (RoleGoodsHelper.isExpired(roleGoods) ? 1 : 0));
		item.setStrengthenLevel((byte)roleGoods.getStrengthenLevel());

		// 获得衍生属性
		item.setDerivativeItems(this.getRecatingAttriList(roleGoods));
		// 镶嵌 相关
		// 装备开启孔位
		item.setMaxHole(GameContext.getEquipApp().getEquipMaxHole(roleGoods));
		// 装备镶嵌的符文
		List<GoodsEquRuneItem> runeItems = this.getMosaicItem(roleGoods);
		if (!Util.isEmpty(runeItems)) {
			item.setRuneItems(runeItems);
		}
		// 设置英雄id
		if (StorageType.hero.getType() == roleGoods.getStorageType()) {
			item.setHeroId(Integer.parseInt(roleGoods.getOtherParm()));
		}
		return item;
	}

	private List<GoodsDerivativeAttriItem> getRecatingAttriList(RoleGoods roleGoods) {
		if (Util.isEmpty(roleGoods.getAttrVarList())) {
			return null;
		}
		int quality = this.getQualityType();
		int star = this.star;
		if (null != roleGoods) {
			quality = roleGoods.getQuality();
			star = roleGoods.getStar();
		}
		List<GoodsDerivativeAttriItem> derivativeItems = new ArrayList<GoodsDerivativeAttriItem>();
		for (AttriItem ai : roleGoods.getAttrVarList()) {
			if (null == ai) {
				continue;
			}
			byte attriType = ai.getAttriTypeValue();
			EquipRecatingAttrWeightConfig awc = GameContext.getGoodsApp().getEquipRecatingAttrWeightConfig(attriType, quality, star);
			if (null == awc) {
				continue;
			}
			int value = (int) ai.getValue();
			RecatingBoundBean bean = awc.getRecatingBoundBean(value);
			if (null == bean) {
				continue;
			}
			GoodsDerivativeAttriItem rii = new GoodsDerivativeAttriItem();
			rii.setType(attriType);
			rii.setValue(value);
			rii.setQuality((byte) bean.getQualityType());
			derivativeItems.add(rii);
		}
		return derivativeItems;
	}

	/**
	 * 获取镶嵌的item
	 * @param roleGoods
	 * @return
	 */
	private List<GoodsEquRuneItem> getMosaicItem(RoleGoods roleGoods) {
		// 获得宝石镶嵌属性
		MosaicRune[] runes = roleGoods.getMosaicRune();
		if (null == runes || runes.length <= 0) {
			return null;
		}
		List<GoodsEquRuneItem> runeItemList = new ArrayList<GoodsEquRuneItem>();
		for (int index = 0; index < runes.length; index++) {
			MosaicRune rune = runes[index];
			if (null == rune) {
				continue;
			}
			int runeId = rune.getGoodsId();
			GoodsRune gb = GameContext.getGoodsApp().getGoodsTemplate(GoodsRune.class, runeId);
			if (null == gb) {
				continue;
			}
			GoodsEquRuneItem runeItem = new GoodsEquRuneItem();
			runeItem.setHole(rune.getHole());
			runeItem.setGoodsLiteItem(rune.getGoodsLiteItem());
			runeItem.setRuneName(gb.getName());
			runeItem.setAttriItems(rune.getAttriStrValueList());
			runeItemList.add(runeItem);
		}
		return runeItemList;
	}
	
}
