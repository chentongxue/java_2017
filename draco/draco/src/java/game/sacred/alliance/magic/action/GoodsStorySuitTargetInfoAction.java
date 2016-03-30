package sacred.alliance.magic.action;

import sacred.alliance.magic.app.goods.behavior.AbstractGoodsBehavior;
import sacred.alliance.magic.app.goods.behavior.GoodsBehaviorType;
import sacred.alliance.magic.app.goods.behavior.param.StorySuitMixParam;
import sacred.alliance.magic.app.goods.behavior.result.StorySuitMixResult;
import sacred.alliance.magic.base.GoodsType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.request.C0585_GoodsStorySuitTargetInfoReqMessage;
import com.game.draco.message.response.C0504_GoodsInfoViewRespMessage;

public class GoodsStorySuitTargetInfoAction extends BaseAction<C0585_GoodsStorySuitTargetInfoReqMessage>{

	@Override
	public Message execute(ActionContext context, C0585_GoodsStorySuitTargetInfoReqMessage reqMsg) {
		try {
			RoleInstance role = this.getCurrentRole(context);
			if(null == role){
				return null ;
			}
			GoodsType goodsType = GoodsType.GoodsEquHuman;
			AbstractGoodsBehavior goodsBehavior = goodsType.getGoodsBehavior(GoodsBehaviorType.StorySuitMix);
			
			StorySuitMixParam param = new StorySuitMixParam(role);
			//目标装备信息
			param.setParamType(StorySuitMixParam.PARAM_TARGET_INFO);
			param.setBagType(reqMsg.getBagType());
			param.setGoodsInstanceId(reqMsg.getGoodsInstanceId());
			param.setSuitGroupId(reqMsg.getSuitGroupId());
			param.setGoodsLevel(reqMsg.getGoodsLevel());
			param.setEquipslotType(reqMsg.getEquipslotType());
			
			Result result = goodsBehavior.operate(param);
			if(!result.isSuccess()){
				return new C0003_TipNotifyMessage(result.getInfo());
			}
			C0504_GoodsInfoViewRespMessage respMsg = new C0504_GoodsInfoViewRespMessage();
			StorySuitMixResult suitMixResult = (StorySuitMixResult) result;
			respMsg.setBaseItem(suitMixResult.getTargetBaseItem());
			return respMsg ;
		}catch(Exception ex){
			logger.error(this.getClass().getName() + " error: ",ex);
			return new C0003_TipNotifyMessage(this.getText(TextId.SYSTEM_ERROR));
		}
	}

}
