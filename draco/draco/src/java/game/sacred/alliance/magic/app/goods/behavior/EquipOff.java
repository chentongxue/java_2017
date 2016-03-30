package sacred.alliance.magic.app.goods.behavior;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.attri.AttriBuffer;
import sacred.alliance.magic.app.goods.BaseEquipBackpack;
import sacred.alliance.magic.app.goods.DefaultBackpack;
import sacred.alliance.magic.app.goods.RoleGoodsHelper;
import sacred.alliance.magic.app.goods.behavior.param.AbstractParam;
import sacred.alliance.magic.app.goods.behavior.param.DoffWearParam;
import sacred.alliance.magic.app.goods.behavior.result.DoffWearResult;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.domain.GoodsEquipment;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.medal.MedalType;


/**
 * 脱装备
 * @author wang.k
 *
 */
public class EquipOff extends AbstractGoodsBehavior{
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	public EquipOff() {
		this.behaviorType = GoodsBehaviorType.Doff;
	}
	
	@Override
	public DoffWearResult operate(AbstractParam param) {

		DoffWearResult result = new DoffWearResult();
		DoffWearParam doffWearParam = (DoffWearParam) param;
		RoleGoods doffGoods = doffWearParam.getDoffWearGoods();
		RoleInstance role = param.getRole();

		int goodsId = doffGoods.getGoodsId();
		GoodsEquipment equipment = GameContext.getGoodsApp().getGoodsTemplate(
				GoodsEquipment.class, goodsId);
		if (equipment == null) {
			return result.setInfo(GameContext.getI18n()
					.getText(TextId.NO_GOODS));
		}
		// 判断背包容量
		boolean bagFull = role.getRoleBackpack().isFull();
		if (bagFull) {
			return result.setInfo(Status.GOODS_BACKPACK_FULL.getTips());
		}

		// 获得目标容器
		DefaultBackpack pack = GameContext.getUserGoodsApp().getStorage(role,
				doffWearParam.getStorageType());
		if (null == pack || !(pack instanceof BaseEquipBackpack)) {
			return result.setInfo(GameContext.getI18n().getText(
					TextId.ERROR_INPUT));
		}

		RoleGoods doffRoleGoods = ((BaseEquipBackpack) pack).doff(doffGoods);
		if (null == doffRoleGoods) {
			return result.setInfo(Status.GOODS_BACKPACK_FULL.getTips());
		}

		result.setDoffRoleGoods(doffRoleGoods);
		AttriBuffer doffBuff = RoleGoodsHelper.getAttriBuffer(doffRoleGoods);
		// 重新计算属性，只计算单个装备对玩家属性的影响
		this.singleTrail(role, doffBuff.reverse());

		try {
			// 更新装备特效
			// 强化
			if (doffRoleGoods.getStarNum() > 0) {
				GameContext.getMedalApp().updateMedal(role,
						MedalType.QiangHua, doffRoleGoods);
			}
			// 洗练
			if (!Util.isEmpty(doffRoleGoods.getAttrVarList())) {
				GameContext.getMedalApp().updateMedal(role,
						MedalType.XiLian, doffRoleGoods);
			}

			if (RoleGoodsHelper.hadMosaicGem(doffRoleGoods)) {
				// 镶嵌
				GameContext.getMedalApp().updateMedal(role,
						MedalType.XiangQian, doffRoleGoods);
			}
		} catch (Exception ex) {
			logger.error("", ex);
		}
		// 套装
		GameContext.getSuitApp().suitChanged(role, null, equipment);
		return result.setResult(DoffWearResult.SUCCESS);
	}
	
	
	private void singleTrail(RoleInstance role, AttriBuffer attriBuff){
		// 属性计算
		GameContext.getUserAttributeApp().changeAttribute(role, attriBuff);
		// 通知属性
		role.getBehavior().notifyAttribute();
	}
	
}
