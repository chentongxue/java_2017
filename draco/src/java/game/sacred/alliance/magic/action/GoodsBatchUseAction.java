package sacred.alliance.magic.action;

import com.game.draco.GameContext;
import com.game.draco.message.push.C0007_ConfirmationNotifyMessage;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.request.C0519_GoodsBatchUseReqMessage;
import com.game.draco.message.response.C0002_ErrorRespMessage;

import sacred.alliance.magic.app.goods.behavior.AbstractGoodsBehavior;
import sacred.alliance.magic.app.goods.behavior.GoodsBehaviorType;
import sacred.alliance.magic.app.goods.behavior.param.UseGoodsParam;
import sacred.alliance.magic.app.goods.behavior.result.UseResult;
import sacred.alliance.magic.base.GoodsType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.vo.RoleInstance;

/**
 * 批量使用物品
 */
public class GoodsBatchUseAction extends BaseAction<C0519_GoodsBatchUseReqMessage>{

	
	@Override
	public Message execute(ActionContext context, C0519_GoodsBatchUseReqMessage reqMsg) {
		int useCount = reqMsg.getCount() & 0xffff;
		RoleInstance role = this.getCurrentRole(context);
		String goodsInstanceId = reqMsg.getGoodsInstanceId();
		//获得相关物品
		RoleGoods roleGoods = role.getRoleBackpack().getRoleGoodsByInstanceId(goodsInstanceId);
		if(null == roleGoods){
			return new C0002_ErrorRespMessage(reqMsg.getCommandId(),this.getText(TextId.NO_GOODS));
		}
		if(roleGoods.getCurrOverlapCount() < useCount){
			return new C0002_ErrorRespMessage(reqMsg.getCommandId(),this.getText(TextId.ERROR_INPUT));
		}
		GoodsType goodsType = GameContext.getGoodsApp().getGoodsType(roleGoods.getGoodsId());
		AbstractGoodsBehavior behavior = goodsType.getGoodsBehavior(GoodsBehaviorType.Use);
		if(null == behavior){
			return new C0003_TipNotifyMessage(Status.GOODS_NO_USE.getTips());
		}
		UseGoodsParam param = new UseGoodsParam(role);
		param.setRoleGoods(roleGoods);
		param.setUseCount(useCount);
		Result result = behavior.operate(param);
		if(!result.isSuccess()){
			return new C0003_TipNotifyMessage(result.getInfo());
		}
		//二次确认
		if(result instanceof UseResult){
			UseResult useResult = (UseResult)result ;
			if(useResult.isMustConfirm()){
				//发送二次确认消息
				C0007_ConfirmationNotifyMessage confirmMsg = new C0007_ConfirmationNotifyMessage();
				confirmMsg.setAffirmCmdId(useResult.getConfirmCmdId());
				confirmMsg.setAffirmParam(useResult.getConfirmInfo());
				confirmMsg.setCancelCmdId((short)0);
				confirmMsg.setCancelParam("");
				confirmMsg.setInfo(result.getInfo());
				confirmMsg.setTime((byte)0);
				confirmMsg.setTimeoutCmdId((short)0);
				confirmMsg.setTimeoutParam("");
				return confirmMsg;
			}
		}
		return null;
	}
	
}
