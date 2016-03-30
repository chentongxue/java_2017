package sacred.alliance.magic.action;
import sacred.alliance.magic.app.goods.behavior.AbstractGoodsBehavior;
import sacred.alliance.magic.app.goods.behavior.GoodsBehaviorType;
import sacred.alliance.magic.app.goods.behavior.param.DiscardGoodsParam;
import sacred.alliance.magic.base.GoodsType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.base.StorageType;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.request.C0509_GoodsDiscardReqMessage;
import com.game.draco.message.response.C0002_ErrorRespMessage;

public class GoodsDiscardAction extends BaseAction<C0509_GoodsDiscardReqMessage> {
	
	@Override
	public Message execute(ActionContext context, C0509_GoodsDiscardReqMessage reqMsg) {
		try {
			RoleInstance role = this.getCurrentRole(context);
			if (null == role) {
				return null;
			}
			byte containerType = reqMsg.getBagType();
			if(containerType != StorageType.bag.getType() 
					&& containerType != StorageType.warehouse.getType()){
				return new C0002_ErrorRespMessage(reqMsg.getCommandId(),
						this.getText(TextId.NO_GOODS));
			}
			String goodsInstanceId = reqMsg.getGoodsInstanceId();
			// 获得相关物品
			RoleGoods roleGoods = GameContext.getUserGoodsApp().getRoleGoods(role, 
					StorageType.get(containerType), goodsInstanceId,0) ;
			if (null == roleGoods) {
				return new C0002_ErrorRespMessage(reqMsg.getCommandId(),
						this.getText(TextId.NO_GOODS));
			}
			GoodsType goodsType = GameContext.getGoodsApp().getGoodsType(
					roleGoods.getGoodsId());

			AbstractGoodsBehavior behavior = goodsType
					.getGoodsBehavior(GoodsBehaviorType.Discard);
			if (null == behavior) {
				return new C0002_ErrorRespMessage(reqMsg.getCommandId(),
						Status.GOODS_NO_DISCARD.getTips());
			}
			DiscardGoodsParam param = new DiscardGoodsParam(role);
			param.setRoleGoods(roleGoods);
			Result result = behavior.operate(param);
			if (!result.isSuccess()) {
				return new C0002_ErrorRespMessage(reqMsg.getCommandId(),
						result.getInfo());
			}
		} catch (Exception e) {
			logger.error("", e);
			return new C0002_ErrorRespMessage(reqMsg.getCommandId(),
					this.getText(TextId.SYSTEM_ERROR));
		}
		return null;
	}
}
