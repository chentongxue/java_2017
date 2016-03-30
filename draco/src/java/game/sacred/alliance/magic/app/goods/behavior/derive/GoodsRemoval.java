package sacred.alliance.magic.app.goods.behavior.derive;

import java.util.ArrayList;
import java.util.List;

import sacred.alliance.magic.app.attri.AttriBuffer;
import sacred.alliance.magic.app.attri.AttriItem;
import sacred.alliance.magic.app.goods.behavior.AbstractGoodsBehavior;
import sacred.alliance.magic.app.goods.behavior.GoodsBehaviorType;
import sacred.alliance.magic.app.goods.behavior.param.AbstractParam;
import sacred.alliance.magic.app.goods.behavior.param.MosaicParam;
import sacred.alliance.magic.app.goods.behavior.result.GoodsResult;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.BindingType;
import sacred.alliance.magic.base.OperatorType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.domain.GoodsRune;
import sacred.alliance.magic.domain.MosaicConfig;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.medal.MedalType;
import com.game.draco.app.rune.domain.MosaicRune;
import com.game.draco.app.target.cond.TargetCondType;
import com.google.common.collect.Lists;

public class GoodsRemoval extends AbstractGoodsBehavior {

	public GoodsRemoval() {
		this.behaviorType = GoodsBehaviorType.Removal;
	}

	@Override
	public GoodsResult operate(AbstractParam param) {
		GoodsResult result = new GoodsResult();
		MosaicParam removalParam = (MosaicParam) param;
		RoleInstance role = removalParam.getRole();
		int targetId = removalParam.getTargetId();
		RoleGoods roleEqup = removalParam.getEquipGoods();
		MosaicRune[] runes = roleEqup.getMosaicRune();
		if (null == runes) {
			return result.setInfo(GameContext.getI18n().getText(TextId.ERROR_INPUT));
		}
		int holeId = removalParam.getHole();
		if (holeId < 0 || holeId >= runes.length) {
			return result.setInfo(GameContext.getI18n().getText(TextId.ERROR_INPUT));
		}
		// 判断游戏币是否足够
		MosaicConfig config = GameContext.getGoodsApp().getMosaicConfig();
		//【游戏币/潜能/钻石不足弹板】 判断
		Result ar = GameContext.getUserAttributeApp().getEnoughResult(role, AttributeType.gameMoney, config.getExciseMoney());
		if(ar.isIgnore()){//弹板
			result.setIgnore(true);
			return result;
		}
		if(!ar.isSuccess()){//不足
			return result.setInfo(Status.GOODS_DERIVE_REMOVE_GEM_MONEY_LESS.getTips());
		}
		result.failure();
		// 判断 holeId上是否有镶嵌的相关宝石
		MosaicRune mosaicRune = runes[holeId];
		if (null == mosaicRune) {
			result.setInfo(GameContext.getI18n().getText(TextId.GOODS_REMOVAL_FAIL));
			return result;
		}
		// 生成镶嵌的宝石的实例（不添加）
		RoleGoods mosaicRoleRune = this.createRoleGoods(role, mosaicRune);
		// 不存在该物品（容错）
		if (null == mosaicRoleRune) {
			result.setInfo(GameContext.getI18n().getText(TextId.GOODS_NO_EXISTS));
			return result;
		}
		// 判断背包是否可添加
		if (!this.canPutGoods(role, mosaicRoleRune)) {
			result.setInfo(GameContext.getI18n().getText(TextId.Bag_Is_Full));
			return result;
		}
		// 扣除游戏币
		if (config.getExciseMoney() > 0) {
			GameContext.getUserAttributeApp().changeRoleMoney(role, AttributeType.gameMoney, OperatorType.Decrease, config.getExciseMoney(),
					OutputConsumeType.rune_discharge_equ_consume);
			role.getBehavior().notifyAttribute();
		}
		// 摘除宝石
		runes[holeId] = null;
		// 添加拆除的物品
		GoodsResult goodsRes = this.addRoleGoods(role, mosaicRoleRune);
		if (!goodsRes.isSuccess()) {
			return goodsRes;
		}
		// 实时入库（防止出现异常刷宝石）
		roleEqup.offlineSaveDb();
		// 发送更新
		// 摘除不会更改基本属性
		// 装备的影响通过响应消息返回,服务器不需要PUSH装备的整个基本信息
		byte bagType = roleEqup.getStorageType();
		boolean on = isOnBattleHero(role.getRoleId(), bagType, targetId);
		if (on) {
			// 重新计算属性
			AttriBuffer buffer = mosaicRune.getRuneAttriButeBuffer();
			buffer.reverse();
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
	 * 添加物品
	 * @param role
	 * @param roleGoods
	 * @return
	 */
	private GoodsResult addRoleGoods(RoleInstance role, RoleGoods roleGoods) {
		return GameContext.getUserGoodsApp().addGoodsForBag(role, roleGoods, OutputConsumeType.rune_discharge_equ);
	}
	
	/**
	 * 背包是否可添加
	 * @param role
	 * @param roleGoods
	 * @return
	 */
	private boolean canPutGoods(RoleInstance role, RoleGoods roleGoods) {
		List<RoleGoods> goodsList = Lists.newArrayList();
		goodsList.add(roleGoods);
		return GameContext.getUserGoodsApp().canPutGoods(role, goodsList);
	}
	
	/**
	 * 创建宝石物品实例
	 * @param role
	 * @param mosaicRune
	 * @return
	 */
	private RoleGoods createRoleGoods(RoleInstance role, MosaicRune mosaicRune) {
		GoodsRune goodsRune = GameContext.getGoodsApp().getGoodsTemplate(GoodsRune.class, mosaicRune.getGoodsId());
		if (null == goodsRune) {
			return null;
		}
		RoleGoods roleGoods = goodsRune.createSingleRoleGoods(role.getRoleId(), 1);
		roleGoods.setBind(BindingType.already_binding.getType());
		// 如果是多属性宝石，将之前属性赋值给宝石
		if (1 != goodsRune.getSecondType()) {
			roleGoods.setAttrVarList((ArrayList<AttriItem>) mosaicRune.getAttriList());
		}
		return roleGoods;
	}
	
}
