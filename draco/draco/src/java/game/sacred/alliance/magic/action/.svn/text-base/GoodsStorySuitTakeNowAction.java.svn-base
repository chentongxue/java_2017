package sacred.alliance.magic.action;

import sacred.alliance.magic.app.goods.behavior.AbstractGoodsBehavior;
import sacred.alliance.magic.app.goods.behavior.GoodsBehaviorType;
import sacred.alliance.magic.app.goods.behavior.param.StorySuitMixParam;
import sacred.alliance.magic.base.GoodsType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.request.C0587_GoodsStorySuitTakeNowReqMessage;

public class GoodsStorySuitTakeNowAction extends BaseAction<C0587_GoodsStorySuitTakeNowReqMessage>{

	@Override
	public Message execute(ActionContext context, C0587_GoodsStorySuitTakeNowReqMessage reqMsg) {
		try {
			RoleInstance role = this.getCurrentRole(context);
			if(null == role){
				return null ;
			}
			GoodsType goodsType = GoodsType.GoodsEquHuman;
			AbstractGoodsBehavior goodsBehavior = goodsType.getGoodsBehavior(GoodsBehaviorType.StorySuitMix);
			
			StorySuitMixParam param = new StorySuitMixParam(role);
			//马上获得
			param.setParamType(StorySuitMixParam.PARAM_TAKE_NOW);
			param.setSuitGroupId(reqMsg.getSuitGroupId());
			param.setGoodsLevel(reqMsg.getGoodsLevel());
			param.setEquipslotType(reqMsg.getEquipslotType());
			
			Result result = goodsBehavior.operate(param);
			//如果是跳转到别的界面，此处不返回消息
			if(result.isIgnore()){
				return null;
			}
			if(!result.isSuccess()){
				return new C0003_TipNotifyMessage(result.getInfo());
			}
			return new C0003_TipNotifyMessage(this.getText(TextId.STORY_SUIT_TAKE_NOW_SUCCESS));
		}catch(Exception ex){
			logger.error(this.getClass().getName() + " error: ",ex);
			return new C0003_TipNotifyMessage(this.getText(TextId.SYSTEM_ERROR));
		}
	}

}
