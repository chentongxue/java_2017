package sacred.alliance.magic.app.goods.behavior.derive;

import java.util.List;
import java.util.Map;

import sacred.alliance.magic.app.attri.AttriBuffer;
import sacred.alliance.magic.app.attri.AttriItem;
import sacred.alliance.magic.app.goods.behavior.AbstractGoodsBehavior;
import sacred.alliance.magic.app.goods.behavior.GoodsBehaviorType;
import sacred.alliance.magic.app.goods.behavior.param.AbstractParam;
import sacred.alliance.magic.app.goods.behavior.param.MosaicParam;
import sacred.alliance.magic.app.goods.behavior.result.MosaicHoleResult;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.BindingType;
import sacred.alliance.magic.base.OperatorType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.domain.GoodsEquipment;
import sacred.alliance.magic.domain.GoodsRune;
import sacred.alliance.magic.domain.MosaicConfig;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.medal.MedalType;
import com.game.draco.app.rune.domain.MosaicRune;
import com.game.draco.app.target.cond.TargetCondType;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class GoodsMosaic extends AbstractGoodsBehavior {

	public GoodsMosaic() {
		this.behaviorType = GoodsBehaviorType.Mosaic;
	}

	@Override
	public MosaicHoleResult operate(AbstractParam param) {
		MosaicParam mosaicParam = (MosaicParam) param;
		RoleGoods equGoods = mosaicParam.getEquipGoods();
		RoleGoods gemGoods = mosaicParam.getGemGoods();
		RoleInstance role = mosaicParam.getRole();
		byte hole = mosaicParam.getHole();
		return this.mosaicConfirm(role, equGoods, gemGoods, mosaicParam.getTargetId(), hole);
	}

	private MosaicHoleResult mosaicConfirm(RoleInstance role, RoleGoods equGoods, RoleGoods gemGoods, int targetId, byte hole) {
		MosaicHoleResult result = this.mosaicCondition(role, equGoods, gemGoods, hole);
		if (!result.isSuccess()||result.isIgnore()) {
			return result;
		}
		RoleGoods roleEqup = result.getRoleGoods();
		byte bagType = (byte) roleEqup.getStorageType();
		return this.doMosaic(role, bagType, result, targetId);
	}

	private MosaicHoleResult doMosaic(RoleInstance role, byte bagType, MosaicHoleResult result, int targetId) {
		result.failure();
		RoleGoods roleEqup = result.getRoleGoods();
		RoleGoods roleRune = result.getRoleRune();
		int hole = result.getMatchHoleId();

		// 扣除游戏币
		if (result.getMosaicMoney() > 0) {
			GameContext.getUserAttributeApp().changeRoleMoney(role, AttributeType.gameMoney, OperatorType.Decrease, result.getMosaicMoney(), OutputConsumeType.rune_mosaic_equ);
			role.getBehavior().notifyAttribute();
		}
		// 删除宝石
		Result delResult = GameContext.getUserGoodsApp().deleteForBagByInstanceId(role, roleRune.getId(), 1, OutputConsumeType.rune_mosaic_equ);
		if (!delResult.isSuccess()) {
			result.setInfo(delResult.getInfo());
			return result;
		}
		// 都绑定
		roleEqup.setBind(BindingType.already_binding.getType());
		roleRune.setBind(BindingType.already_binding.getType());
		// 修改镶嵌信息
		MosaicRune mosaicRune = new MosaicRune();
		List<AttriItem> attriList = Lists.newArrayList();
		GoodsRune goodsRune = GameContext.getGoodsApp().getGoodsTemplate(GoodsRune.class, roleRune.getGoodsId());
		if (1 == goodsRune.getSecondType()) {
			attriList.add(new AttriItem(goodsRune.getAttrType(), goodsRune.getAttrValue(), false));
			mosaicRune.setAttriList(attriList);
		} else {
			attriList = roleRune.getAttrVarList();
			mosaicRune.setAttriList(attriList);
		}
		result.setRuneTemplate(goodsRune);
		mosaicRune.setHole((byte) result.getMatchHoleId());
		mosaicRune.setGoodsId(roleRune.getGoodsId());
		// 镶嵌到装备上
		roleEqup.getMosaicRune()[hole] = mosaicRune;
		result.setMosaicRune(mosaicRune);
		// 镶嵌不会更改基本属性
		// 装备属性的修改,通过响应消息返回,客户端自己修改,不用服务器重新PUSH整个装备的基本信息
		// 装备在身上/金身的物品,需要重新计算属性
		boolean on = isOnBattleHero(role.getRoleId(), bagType, targetId);
		if (on) {
			// 重新计算属性
			AttriBuffer buffer = mosaicRune.getRuneAttriButeBuffer();
			GameContext.getUserAttributeApp().changeAttribute(role, buffer);
			role.getBehavior().notifyAttribute();
		}
		boolean isOnSwitchHero = isOnSwitchHero(role.getRoleId(), bagType, targetId);
		if (isOnSwitchHero) {
			// 更新装备特效
			GameContext.getMedalApp().updateMedal(role, MedalType.XiangQian, roleEqup);
			//同步其他英雄的战斗力
			//当前出战的英雄已经计算了战斗力
			GameContext.getHeroApp().syncBattleScore(role, targetId,!on);
		}
		// 调用目标系统
		GameContext.getTargetApp().updateTarget(role, TargetCondType.HeroEquipMosaic);
		result.success();
		return result;
	}

	/**
	 * 镶嵌条件
	 */
	private MosaicHoleResult mosaicCondition(RoleInstance role, RoleGoods equGoods, RoleGoods runeGoods, byte holeNum) {
		MosaicHoleResult result = this.condition(role, equGoods, holeNum);
		if (!result.isSuccess()||result.isIgnore()) {
			return result;
		}
		result.failure();
		// 符文不存在
		if (null == runeGoods) {
			result.setInfo(GameContext.getI18n().getText(TextId.GOODS_NO_EXISTS));
			return result;
		}
		GoodsRune goodsRune = GameContext.getGoodsApp().getGoodsTemplate(GoodsRune.class, runeGoods.getGoodsId());
		if (null == goodsRune) {
			result.setInfo(GameContext.getI18n().getText(TextId.ERROR_INPUT));
			return result;
		}
		// 验证规则
		byte rule = GameContext.getRuneApp().getMoasicRules((byte) 0)[holeNum];
		if (rule < goodsRune.getSecondType()) {
			result.setInfo(GameContext.getI18n().getText(TextId.ERROR_INPUT));
			return result;
		}
		// 判断单属性互斥
		if (this.isMosaicSameTypeRune(equGoods.getMosaicRune(), goodsRune)) {
			result.setInfo(GameContext.getI18n().getText(TextId.GOODS_MOSAIC_SAME_TYPE));
			return result;
		}
		RoleGoods equ = result.getRoleGoods();
		GoodsEquipment equTemplate = GoodsDeriveSupport.getGoodsEquipment(equ.getGoodsId());
		result.success();
		result.setRoleRune(runeGoods);
		result.setGoodsTemplate(equTemplate);
		result.setMatchHoleId(holeNum);
		return result;
	}

	private Map<Byte, MosaicRune> getMosaicRuneMap(MosaicRune[] runes) {
		Map<Byte, MosaicRune> mosaicRuneMap = Maps.newHashMap();
		for (MosaicRune rune : runes) {
			if (null == rune) {
				continue;
			}
			mosaicRuneMap.put(rune.getHole(), rune);
		}
		return mosaicRuneMap;
	}

	// 是否镶嵌了该类型符文
	private boolean isMosaicSameTypeRune(MosaicRune[] runes, GoodsRune goodsRune) {
		// 如果符文不为单属性，不存在互斥
		if (1 != goodsRune.getSecondType()) {
			return false;
		}
		Map<Byte, MosaicRune> mosaicRuneMap = this.getMosaicRuneMap(runes);
		if (Util.isEmpty(mosaicRuneMap)) {
			return false;
		}
		// 获取单属性符文属性类型
		short runeAttriType = goodsRune.getRuneAttributeType();
		for (MosaicRune mosaicRune : mosaicRuneMap.values()) {
			short mosaicType = goodsRune.getRuneAttributeType(mosaicRune.getAttriList());
			if (runeAttriType == mosaicType) {
				return true;
			}
		}
		return false;
	}

	private MosaicHoleResult condition(RoleInstance role, RoleGoods equGoods, byte hole) {
		MosaicHoleResult result = new MosaicHoleResult();
		if (null == role) {
			// 用户不在线
			result.setInfo(GameContext.getI18n().getText(TextId.ROLE_NOT_ONLINE));
			return result;
		}
		MosaicConfig config = GameContext.getGoodsApp().getMosaicConfig();
		//【游戏币/潜能/钻石不足弹板】 判断
		Result ar = GameContext.getUserAttributeApp().getEnoughResult(role, AttributeType.gameMoney, config.getMosaicMoney());
		if(ar.isIgnore()){
			result.setIgnore(true);
			return result;
		}
		if(!ar.isSuccess()){
			result.setInfo(Status.GOODS_DERIVE_MOSAIC_MONEY_LESS.getTips());
			return result;
		}
		// 将需要的钱币放入结果对象
		result.setMosaicMoney(config.getMosaicMoney());
		// 验证孔位信息
		MosaicRune[] runes = equGoods.getMosaicRune();
		if (null == runes || 0 == runes.length) {
			result.setInfo(GameContext.getI18n().getText(TextId.ERROR_INPUT));
			return result;
		}
		// 孔位不为空
		if (null != runes[hole]) {
			result.setInfo(GameContext.getI18n().getText(TextId.ERROR_INPUT));
			return result;
		}
		result.success();
		result.setRoleGoods(equGoods);
		result.setMatchHoleId(hole);
		return result;
	}

}
