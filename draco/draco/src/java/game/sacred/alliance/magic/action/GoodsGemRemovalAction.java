package sacred.alliance.magic.action;

import com.game.draco.GameContext;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.request.C0549_GoodsGemRemovalReqMessage;
import com.game.draco.message.response.C0549_GoodsGemRemovalRespMessage;

import sacred.alliance.magic.app.goods.behavior.AbstractGoodsBehavior;
import sacred.alliance.magic.app.goods.behavior.GoodsBehaviorType;
import sacred.alliance.magic.app.goods.behavior.param.RemovalPunchParam;
import sacred.alliance.magic.base.GoodsType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.base.StorageType;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.constant.RespTypeStatus;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.vo.RoleInstance;

public class GoodsGemRemovalAction extends BaseAction<C0549_GoodsGemRemovalReqMessage>{

	@Override
	public Message execute(ActionContext context, C0549_GoodsGemRemovalReqMessage reqMsg) {
		C0549_GoodsGemRemovalRespMessage respMsg = new C0549_GoodsGemRemovalRespMessage();
		respMsg.setStatus(RespTypeStatus.FAILURE);
		try {
			RoleInstance role = this.getCurrentRole(context);
			byte bagType = reqMsg.getBagType();
			String goodsInstanceId = reqMsg.getGoodsId();
			StorageType storageType = StorageType.get(bagType);
			RoleGoods equipGoods = GameContext.getUserGoodsApp().getRoleGoods(role, storageType, goodsInstanceId);
			if(equipGoods == null){
				return new C0003_TipNotifyMessage(this.getText(TextId.NO_GOODS));
			}
			GoodsType goodsType = GameContext.getGoodsApp().getGoodsType(equipGoods.getGoodsId());
			AbstractGoodsBehavior behavior = goodsType.getGoodsBehavior(GoodsBehaviorType.Removal);
			
			RemovalPunchParam param = new RemovalPunchParam(role);
			param.setBagType(bagType);
			param.setGoodsId(goodsInstanceId);
			param.setHoleId(reqMsg.getHoleId());
			Result result = behavior.operate(param);
			if (!result.isSuccess()) {
				respMsg.setInfo(result.getInfo());
				return respMsg ;
			}
			respMsg.setGoodsId(reqMsg.getGoodsId());
			respMsg.setBagType(reqMsg.getBagType());
			respMsg.setHoleId(reqMsg.getHoleId());
			respMsg.setStatus(RespTypeStatus.SUCCESS);
			respMsg.setInfo(Status.Goods_Gem_Remove_Success.getTips());
		}catch(Exception ex){
			respMsg.setInfo(this.getText(TextId.SYSTEM_ERROR));
			logger.error("",ex);
		}
		return respMsg ;
	}

}
