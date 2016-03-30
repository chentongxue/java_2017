package sacred.alliance.magic.action;

import sacred.alliance.magic.app.goods.RoleBackpack;
import sacred.alliance.magic.app.goods.behavior.AbstractGoodsBehavior;
import sacred.alliance.magic.app.goods.behavior.GoodsBehaviorType;
import sacred.alliance.magic.app.goods.behavior.param.GoodsDecomposeParam;
import sacred.alliance.magic.app.goods.decompose.DecomposeConfig;
import sacred.alliance.magic.base.GoodsType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.request.C0560_GoodsDecomposeReqMessage;
import com.game.draco.message.response.C0002_ErrorRespMessage;
import com.game.draco.message.response.C0560_GoodsDecomposeRespMessage;
/**
 * 物品分解
 */
public class GoodsDecomposeAction extends
		BaseAction<C0560_GoodsDecomposeReqMessage> {

	@Override
	public Message execute(ActionContext context,
			C0560_GoodsDecomposeReqMessage reqMsg) {
		short num = reqMsg.getNum();//可回收价格
		if(num <= 0){
			return new C0002_ErrorRespMessage(reqMsg.getCommandId(),
					this.getText(TextId.DECOMPOSE_NUM_ERR));
		}
		try {
			RoleInstance role = this.getCurrentRole(context);
			if (role == null) {
				return null;
			}
			String goodsInstanceId = reqMsg.getGoodsId();// 得到要分解物品的ID
			RoleBackpack info = role.getRoleBackpack();
			RoleGoods roleGoods = info.getRoleGoodsByInstanceId(goodsInstanceId);
			if (roleGoods == null) {
				return new C0002_ErrorRespMessage(reqMsg.getCommandId(),
						this.getText(TextId.GOODS_NO_FOUND));
			}
			if(num > roleGoods.getCurrOverlapCount()){
				return new C0002_ErrorRespMessage(reqMsg.getCommandId(),
						this.getText(TextId.DECOMPOSE_NUM_ERR));
			}
			GoodsBase goodsBase = GameContext.getGoodsApp().getGoodsBase(
					roleGoods.getGoodsId());
			if (goodsBase == null) {
				return new C0002_ErrorRespMessage(reqMsg.getCommandId(),
						this.getText(TextId.GOODS_NO_FOUND));
			}
			//判断roleGoods是否在分解表中
			int inputGoodsId = roleGoods.getGoodsId();
			DecomposeConfig decomposeConfig = GameContext.getGoodsApp().getDecomposeConfig(inputGoodsId);
			if(decomposeConfig == null){
				return new C0002_ErrorRespMessage(reqMsg.getCommandId(),
						this.getText(TextId.GOODS_CANOT_DECOMPOSE));
			}
			GoodsType goodsType = GameContext.getGoodsApp().getGoodsType(
					roleGoods.getGoodsId());

			AbstractGoodsBehavior behavior = goodsType
					.getGoodsBehavior(GoodsBehaviorType.Decompose);
			GoodsDecomposeParam param = new GoodsDecomposeParam(role);
			param.setGoodsBase(goodsBase);
			param.setRoleGoods(roleGoods);
			param.setNum(num);
			Result result = behavior.operate(param);
			if (!result.isSuccess()) {
				return new C0002_ErrorRespMessage(reqMsg.getCommandId(),
						result.getInfo());
			}
			C0560_GoodsDecomposeRespMessage msg = new C0560_GoodsDecomposeRespMessage();
			msg.setType(result.getResult());
			msg.setInfo(result.getInfo());
			return msg;

		} catch (Exception e) {
			logger.error("GoodsDecomposeAction error", e);
			return new C0002_ErrorRespMessage(reqMsg.getCommandId(),
					Status.FAILURE.getTips());
		}

	}

}
