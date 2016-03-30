package sacred.alliance.magic.action;

import com.game.draco.GameContext;
import com.game.draco.message.request.C0506_GoodsSplitReqMessage;
import com.game.draco.message.response.C0002_ErrorRespMessage;

import sacred.alliance.magic.app.goods.RoleBackpack;
import sacred.alliance.magic.app.goods.behavior.AbstractGoodsBehavior;
import sacred.alliance.magic.app.goods.behavior.GoodsBehaviorType;
import sacred.alliance.magic.app.goods.behavior.param.GoodsSplitParam;
import sacred.alliance.magic.app.goods.behavior.result.GoodsResult;
import sacred.alliance.magic.base.GoodsType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.vo.RoleInstance;

public class GoodsSplitAction extends BaseAction<C0506_GoodsSplitReqMessage> {
	@Override
	public Message execute(ActionContext context, C0506_GoodsSplitReqMessage reqMsg) {
		try {
			RoleInstance role = this.getCurrentRole(context);
			if(null == role){
				return null;
			}
			String goodsInstanceId = reqMsg.getGoodsInstanceId();
			RoleBackpack info = role.getRoleBackpack();
			if (info.isFull()) {
				return new C0002_ErrorRespMessage(reqMsg.getCommandId(),Status.Goods_Split_Backpack_Full.getTips());
			}
			RoleGoods roleGoods = info.getRoleGoodsByInstanceId(goodsInstanceId);
			if(roleGoods == null){
				return new C0002_ErrorRespMessage(reqMsg.getCommandId(),Status.GOODS_NO_FOUND.getTips());
			}
			GoodsType goodsType = GameContext.getGoodsApp().getGoodsType(roleGoods.getGoodsId());
			AbstractGoodsBehavior behavior = goodsType.getGoodsBehavior(GoodsBehaviorType.split);
			GoodsSplitParam param = new GoodsSplitParam(role);
			int splitCount = (reqMsg.getSplitCount() & 0xffff) ;
			param.setSplitNum(splitCount);
			param.setRoleGoods(roleGoods);
			Result result = behavior.operate(param);
			if(!result.isSuccess()){
				return new C0002_ErrorRespMessage(reqMsg.getCommandId(), result.getInfo());
			}
			GoodsResult goodsRst = (GoodsResult)result;
			goodsRst.syncBackpack(role,OutputConsumeType.goods_split);
			
			return null;
		} catch (Exception e) {
			logger.error("GoodsSplitAction error,goodsInstanceId = "+ reqMsg.getGoodsInstanceId(), e);
			return new C0002_ErrorRespMessage(reqMsg.getCommandId(), Status.FAILURE.getTips());
		}

	}
}
