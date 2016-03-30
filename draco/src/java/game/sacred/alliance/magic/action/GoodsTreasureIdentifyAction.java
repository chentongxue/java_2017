package sacred.alliance.magic.action;

import com.game.draco.GameContext;
import com.game.draco.message.request.C0513_GoodsTreasureIdentifyReqMessage;
import com.game.draco.message.response.C0513_GoodsTreasureIdentifyRespMessage;

import sacred.alliance.magic.app.goods.behavior.AbstractGoodsBehavior;
import sacred.alliance.magic.app.goods.behavior.GoodsBehaviorType;
import sacred.alliance.magic.app.goods.behavior.param.UseGoodsParam;
import sacred.alliance.magic.base.GoodsType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.constant.RespTypeStatus;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.vo.RoleInstance;
/**
 * 使用虚空漩涡（藏宝图）物品
 */
public class GoodsTreasureIdentifyAction extends BaseAction<C0513_GoodsTreasureIdentifyReqMessage> {

	@Override
	public Message execute(ActionContext context, C0513_GoodsTreasureIdentifyReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null ;
		}
		C0513_GoodsTreasureIdentifyRespMessage respMsg = new C0513_GoodsTreasureIdentifyRespMessage();
		respMsg.setStatus(RespTypeStatus.FAILURE);
		RoleGoods roleGoods = role.getRoleBackpack().getRoleGoodsByInstanceId(reqMsg.getGoodsId());
		if(roleGoods == null){
			respMsg.setInfo(Status.GOODS_NO_FOUND.getTips());
			return respMsg ;
		}
		GoodsType goodsType = GameContext.getGoodsApp().getGoodsType(roleGoods.getGoodsId());
		AbstractGoodsBehavior behavior = goodsType.getGoodsBehavior(GoodsBehaviorType.treasureIdentify);
		UseGoodsParam param = new UseGoodsParam(role);
		param.setRoleGoods(roleGoods);
		Result result = behavior.operate(param);
		if(!result.isSuccess()){
			respMsg.setInfo(result.getInfo());
			return respMsg ;
		}
		respMsg.setStatus(Status.SUCCESS.getInnerCode());
		respMsg.setInfo(Status.Goods_Identify_Success.getTips());
		return respMsg;
	}

}
