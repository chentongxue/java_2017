package sacred.alliance.magic.action;

import sacred.alliance.magic.app.goods.behavior.AbstractGoodsBehavior;
import sacred.alliance.magic.app.goods.behavior.GoodsBehaviorType;
import sacred.alliance.magic.app.goods.behavior.param.DoffWearParam;
import sacred.alliance.magic.app.goods.behavior.result.DoffWearResult;
import sacred.alliance.magic.base.GoodsType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.base.StorageType;
import sacred.alliance.magic.constant.RespTypeStatus;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.vo.MapInstance;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.request.C0510_EquipOffReqMessage;
import com.game.draco.message.response.C0002_ErrorRespMessage;
import com.game.draco.message.response.C0510_EquipOffRespMessage;

public class EquipOffAction extends BaseAction<C0510_EquipOffReqMessage> {
	@Override
	public Message execute(ActionContext context,
			C0510_EquipOffReqMessage reqMsg) {

		C0510_EquipOffRespMessage respMsg = new C0510_EquipOffRespMessage();
		respMsg.setType((byte) RespTypeStatus.FAILURE);

		try {
			RoleInstance role = this.getCurrentRole(context);
			MapInstance mapInstance = role.getMapInstance();
			if (null == mapInstance) {
				return respMsg;
			}
			if (!mapInstance.canDoffWearEquip()) {
				return null;
			}
			
			byte bagType = reqMsg.getStorageType() ;
			StorageType st = StorageType.get(bagType);
			if(null == st || !st.isCanOnEquip()){
				respMsg.setInfo(GameContext.getI18n().getText(TextId.ERROR_INPUT));
				return respMsg;
			}
			
			String goodsInstanceId = reqMsg.getGoodsInstanceId();
			RoleGoods roleGoods = GameContext.getUserGoodsApp().getRoleGoods(
					role, st, goodsInstanceId,reqMsg.getTargetId());

			if (roleGoods == null) {
				respMsg.setInfo(Status.GOODS_NO_FOUND.getTips());
				return respMsg;
			}
			GoodsType goodsType = GameContext.getGoodsApp().getGoodsType(
					roleGoods.getGoodsId());

			AbstractGoodsBehavior goodsBehavior = goodsType
					.getGoodsBehavior(GoodsBehaviorType.Doff);
			DoffWearParam param = new DoffWearParam(role);
			param.setDoffWearGoods(roleGoods);
			param.setStorageType(st);
			param.setTargetId(reqMsg.getTargetId());
			
			Result result = goodsBehavior.operate(param);
			if (!result.isSuccess()) {
				respMsg.setInfo(result.getInfo());
				return respMsg;
			}
			DoffWearResult doffWearResult = (DoffWearResult) result;
			respMsg.setType(doffWearResult.getResult());
			respMsg.setGoodsInstanceId(goodsInstanceId);
			respMsg.setBagGridIndex((byte) roleGoods.getGridPlace());
			respMsg.setStorageType(reqMsg.getStorageType());
			respMsg.setReqTargetId(reqMsg.getTargetId());
			return respMsg;
		} catch (Exception e) {
			logger.error("", e);
		}
		return new C0002_ErrorRespMessage(reqMsg.getCommandId(),
				this.getText(TextId.SYSTEM_ERROR));
	}

}
