package sacred.alliance.magic.action;

import com.game.draco.message.request.C0558_GoodsGemMixExecReqMessage;
import com.game.draco.message.response.C0558_GoodsGemMixExecRespMessage;

import sacred.alliance.magic.app.goods.behavior.AbstractGoodsBehavior;
import sacred.alliance.magic.app.goods.behavior.GoodsBehaviorType;
import sacred.alliance.magic.app.goods.behavior.param.MixParam;
import sacred.alliance.magic.base.GoodsType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.constant.RespTypeStatus;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

public class GoodsGemMixExecAction extends BaseAction<C0558_GoodsGemMixExecReqMessage>{

	@Override
	public Message execute(ActionContext context, C0558_GoodsGemMixExecReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null ;
		}
		AbstractGoodsBehavior behavior = GoodsType.GoodsGem.getGoodsBehavior(GoodsBehaviorType.Mix);
		MixParam parm = new MixParam(role);
		parm.setGoodsId(reqMsg.getTargetId());
		parm.setMixNum(reqMsg.getNum());
		
		Result result = behavior.operate(parm);
		C0558_GoodsGemMixExecRespMessage respMsg = new C0558_GoodsGemMixExecRespMessage();
		respMsg.setStatus(RespTypeStatus.FAILURE);
		if(result.isSuccess()){
			respMsg.setStatus(RespTypeStatus.SUCCESS);
			//合成成功照样返回提示信息
			//return respMsg ;
		}
		respMsg.setInfo(result.getInfo());
		return respMsg;
	}

}
