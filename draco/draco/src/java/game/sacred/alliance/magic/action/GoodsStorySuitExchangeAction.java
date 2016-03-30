package sacred.alliance.magic.action;

import sacred.alliance.magic.app.goods.behavior.AbstractGoodsBehavior;
import sacred.alliance.magic.app.goods.behavior.GoodsBehaviorType;
import sacred.alliance.magic.app.goods.behavior.param.StorySuitMixParam;
import sacred.alliance.magic.base.GoodsType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.constant.RespTypeStatus;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.message.request.C0586_GoodsStorySuitExchangeReqMessage;
import com.game.draco.message.response.C0586_GoodsStorySuitExchangeRespMessage;

public class GoodsStorySuitExchangeAction extends BaseAction<C0586_GoodsStorySuitExchangeReqMessage>{

	@Override
	public Message execute(ActionContext context, C0586_GoodsStorySuitExchangeReqMessage reqMsg) {
		C0586_GoodsStorySuitExchangeRespMessage resp = new C0586_GoodsStorySuitExchangeRespMessage();
		resp.setType(RespTypeStatus.FAILURE);
		try {
			RoleInstance role = this.getCurrentRole(context);
			if(null == role){
				return null ;
			}
			GoodsType goodsType = GoodsType.GoodsEquHuman;
			AbstractGoodsBehavior goodsBehavior = goodsType.getGoodsBehavior(GoodsBehaviorType.StorySuitMix);
			
			StorySuitMixParam param = new StorySuitMixParam(role);
			//兑换装备
			param.setParamType(StorySuitMixParam.PARAM_EXCHANGE);
			param.setBagType(reqMsg.getBagType());
			param.setGoodsInstanceId(reqMsg.getGoodsInstanceId());
			param.setSuitGroupId(reqMsg.getSuitGroupId());
			param.setGoodsLevel(reqMsg.getGoodsLevel());
			param.setEquipslotType(reqMsg.getEquipslotType());
			
			Result result = goodsBehavior.operate(param);
			if(!result.isSuccess()){
				resp.setInfo(result.getInfo());
				return resp;
			}
			resp.setType(RespTypeStatus.SUCCESS);
			resp.setInfo(this.getText(TextId.STORY_SUIT_EXCHANGE_SUCCESS));
			return resp;
		}catch(Exception ex){
			logger.error(this.getClass().getName() + " error: ",ex);
			resp.setInfo(this.getText(TextId.SYSTEM_ERROR));
			return resp;
		}
	}

}
