package sacred.alliance.magic.action;
import com.game.draco.GameContext;
import com.game.draco.message.request.C0514_GoodsConfirmApplyReqMessage;
import com.game.draco.message.response.C0002_ErrorRespMessage;

import sacred.alliance.magic.app.goods.behavior.AbstractGoodsBehavior;
import sacred.alliance.magic.app.goods.behavior.GoodsBehaviorType;
import sacred.alliance.magic.app.goods.behavior.param.UseGoodsParam;
import sacred.alliance.magic.base.GoodsType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.vo.MapInstance;
import sacred.alliance.magic.vo.RoleInstance;

public class GoodsConfirmApplyAction extends BaseAction<C0514_GoodsConfirmApplyReqMessage> {
	@Override
	public Message execute(ActionContext context,C0514_GoodsConfirmApplyReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return new C0002_ErrorRespMessage(reqMsg.getCommandId(),
					this.getText(TextId.ROLE_NOT_ONLINE));
		}
		String id = reqMsg.getInfo();
		//获得相关物品
		RoleGoods roleGoods = role.getRoleBackpack().getRoleGoodsByInstanceId(id);
		if(null == roleGoods){
			return new C0002_ErrorRespMessage(reqMsg.getCommandId(),
					this.getText(TextId.NO_GOODS));
		}
		MapInstance mapInstance = role.getMapInstance();
		if(null != mapInstance && 
				!mapInstance.canUseGoods(role, roleGoods.getGoodsId())){
			return new C0002_ErrorRespMessage(reqMsg.getCommandId(), Status.Goods_Not_Can_Used_In_Map.getTips());
		}
		GoodsType goodsType = GameContext.getGoodsApp().getGoodsType(roleGoods.getGoodsId());
		AbstractGoodsBehavior behavior = goodsType.getGoodsBehavior(GoodsBehaviorType.Use);
		if(null == behavior){
			return new C0002_ErrorRespMessage(reqMsg.getCommandId(),Status.GOODS_NO_USE.getTips());
		}
		UseGoodsParam param = new UseGoodsParam(role);
		param.setRoleGoods(roleGoods);
		//二次确认
		param.setConfirm(true);
		Result result = behavior.operate(param);
		if(!result.isSuccess()){
			return new C0002_ErrorRespMessage(reqMsg.getCommandId(),result.getInfo());
		}
		if(null != mapInstance){
			mapInstance.useGoods(roleGoods.getGoodsId());
		}
		return null;
	}
}
