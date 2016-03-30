package sacred.alliance.magic.app.goods.behavior;

import com.game.draco.GameContext;

import sacred.alliance.magic.app.goods.behavior.param.AbstractParam;
import sacred.alliance.magic.app.goods.behavior.param.DiscardGoodsParam;
import sacred.alliance.magic.app.goods.behavior.result.GoodsResult;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.vo.RoleInstance;


/**
 * 丢弃物品
 * @author Wang.K
 *
 */
public class DiscardGoods extends AbstractGoodsBehavior{
	
	public DiscardGoods() {
		this.behaviorType = GoodsBehaviorType.Discard;
	}

	@Override
	public GoodsResult operate(AbstractParam param) {
		DiscardGoodsParam discardGoodsParam = (DiscardGoodsParam) param;
		RoleGoods roleGoods = discardGoodsParam.getRoleGoods();
		RoleInstance role = discardGoodsParam.getRole();
		if (role == null || roleGoods == null) {
			return new GoodsResult().setInfo(GameContext.getI18n().getText(TextId.NO_GOODS));
		}
		int goodsId = roleGoods.getGoodsId();
		GoodsBase goodsBase = GameContext.getGoodsApp().getGoodsBase(goodsId);
		if (goodsBase == null) {
			return new GoodsResult().setInfo(GameContext.getI18n().getText(TextId.NO_GOODS));
		}

		if (!goodsBase.hasDiscard()) {
			return new GoodsResult().setInfo(Status.GOODS_NO_DISCARD
					.getTips());
		}

		GoodsResult result = GameContext.getUserGoodsApp().discardGoods(role, roleGoods, OutputConsumeType.role_discard_goods);
		
		return result;
	}

	
}
