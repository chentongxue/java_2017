package sacred.alliance.magic.action;
import sacred.alliance.magic.app.goods.behavior.AbstractGoodsBehavior;
import sacred.alliance.magic.app.goods.behavior.GoodsBehaviorType;
import sacred.alliance.magic.app.goods.behavior.param.UseGoodsParam;
import sacred.alliance.magic.app.goods.behavior.result.UseResult;
import sacred.alliance.magic.base.GoodsType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.MapInstance;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.push.C0007_ConfirmationNotifyMessage;
import com.game.draco.message.request.C0507_GoodsUseReqMessage;
import com.game.draco.message.response.C0002_ErrorRespMessage;

public class GoodsUseAction extends BaseAction<C0507_GoodsUseReqMessage> {
	
	@Override
	public Message execute(ActionContext context, C0507_GoodsUseReqMessage reqMsg) {
		try {
			RoleInstance role = this.getCurrentRole(context);
			if (null == role) {
				return null;
			}
			String goodsInstanceId = reqMsg.getGoodsInstanceId();
			// 获得相关物品
			RoleGoods roleGoods = role.getRoleBackpack()
					.getRoleGoodsByInstanceId(goodsInstanceId);
			if (null == roleGoods) {
				return new C0002_ErrorRespMessage(reqMsg.getCommandId(),
						this.getText(TextId.NO_GOODS));
			}
			GoodsType goodsType = GameContext.getGoodsApp().getGoodsType(
					roleGoods.getGoodsId());
			// 使用物品
			MapInstance mapInstance = role.getMapInstance();
			if (null != mapInstance
					&& !mapInstance.canUseGoods(role, roleGoods.getGoodsId())) {
				return new C0003_TipNotifyMessage(
						Status.Goods_Not_Can_Used_In_Map.getTips());
			}
			AbstractGoodsBehavior behavior = goodsType
					.getGoodsBehavior(GoodsBehaviorType.Use);
			if (null == behavior) {
				return new C0003_TipNotifyMessage(Status.GOODS_NO_USE.getTips());
			}
			UseGoodsParam param = new UseGoodsParam(role);
			param.setRoleGoods(roleGoods);
			Result result = behavior.operate(param);
			if (!result.isSuccess()) {
				return new C0003_TipNotifyMessage(result.getInfo());
			}
			if (result instanceof UseResult) {
				UseResult useResult = (UseResult) result;
				if (useResult.isMustConfirm()) {
					// 发送二次确认消息
					C0007_ConfirmationNotifyMessage confirmMsg = new C0007_ConfirmationNotifyMessage();
					confirmMsg.setAffirmCmdId(useResult.getConfirmCmdId());
					confirmMsg.setAffirmParam(useResult.getConfirmInfo());
					confirmMsg.setCancelCmdId((short) 0);
					confirmMsg.setCancelParam("");
					confirmMsg.setInfo(result.getInfo());
					confirmMsg.setTime((byte) 0);
					confirmMsg.setTimeoutCmdId((short) 0);
					confirmMsg.setTimeoutParam("");
					return confirmMsg;
				}
			}
			if (null != mapInstance) {
				//useless
				mapInstance.useGoods(roleGoods.getGoodsId());
			}
			// 调用使用物品任务接口
			GameContext.getUserQuestApp()
					.useGoods(role, roleGoods.getGoodsId());
			if(Util.isEmpty(result.getInfo())){
				return null ;
			}
			return new C0003_TipNotifyMessage(result.getInfo());
		} catch (Exception e) {
			logger.error("", e);
			return new C0002_ErrorRespMessage(reqMsg.getCommandId(),
					this.getText(TextId.SYSTEM_ERROR));
		}
	}
}
