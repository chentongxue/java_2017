package sacred.alliance.magic.action;
import sacred.alliance.magic.app.goods.RoleGoodsHelper;
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
import sacred.alliance.magic.domain.GoodsEquipment;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.vo.MapInstance;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.request.C0501_EquipOnReqMessage;
import com.game.draco.message.response.C0002_ErrorRespMessage;
import com.game.draco.message.response.C0501_EquipOnRespMessage;



public class EquipOnAction extends BaseAction<C0501_EquipOnReqMessage> {
	@Override
	public Message execute(ActionContext context, C0501_EquipOnReqMessage reqMsg) {
		C0501_EquipOnRespMessage respMsg = new C0501_EquipOnRespMessage();
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

			byte bagType = reqMsg.getTargetBagType() ;
			StorageType targetStorageType = StorageType.get(bagType);
			if(null == targetStorageType || !targetStorageType.isCanOnEquip()){
				respMsg.setInfo(GameContext.getI18n().getText(TextId.ERROR_INPUT));
				return respMsg;
			}
			
			String goodsInstanceId = reqMsg.getGoodsInstanceId();
			RoleGoods roleGoods = GameContext.getUserGoodsApp().getRoleGoods(
					role, StorageType.bag, goodsInstanceId,reqMsg.getTargetId());
			if (roleGoods == null) {
				respMsg.setInfo(Status.GOODS_NO_FOUND.getTips());
				return respMsg;
			}
			if (RoleGoodsHelper.isExpired(roleGoods)) {
				respMsg.setInfo(Status.GOODS_IS_EXPIRED.getTips());
				return respMsg;
			}
			GoodsEquipment equipment = GameContext.getGoodsApp()
					.getGoodsTemplate(GoodsEquipment.class,
							roleGoods.getGoodsId());
			if (equipment == null) {
				respMsg.setInfo(Status.GOODS_NO_FOUND.getTips());
				return respMsg;
			}
			
			short offGridIndex = roleGoods.getGridPlace() ;
			
			GoodsType goodsType = GameContext.getGoodsApp().getGoodsType(
					roleGoods.getGoodsId());

			AbstractGoodsBehavior goodsBehavior = goodsType
					.getGoodsBehavior(GoodsBehaviorType.Wear);
			DoffWearParam param = new DoffWearParam(role);
			param.setDoffWearGoods(roleGoods);
			param.setStorageType(targetStorageType);
			param.setTargetId(reqMsg.getTargetId());
			
			Result result = goodsBehavior.operate(param);
			if (!result.isSuccess()) {
				respMsg.setInfo(result.getInfo());
				return respMsg;
			}
			DoffWearResult doffWearResult = (DoffWearResult) result;
			String goodsDoffInstanceid = null;
			RoleGoods doffGoods = doffWearResult.getDoffRoleGoods();
			if (doffGoods != null) {
				goodsDoffInstanceid = doffGoods.getId();
			}
			respMsg.setType(doffWearResult.getResult());
			//脱下物品的信息
			respMsg.setOffInstanceid(goodsDoffInstanceid);
			respMsg.setOffGridIndex((byte)offGridIndex);
			//穿上物品的信息
			respMsg.setEndDateTime(doffWearResult.getExpiredTime());
			respMsg.setOnBagType(reqMsg.getTargetBagType());
			respMsg.setOnGridIndex((byte)roleGoods.getGridPlace());
			respMsg.setOnInstanceId(roleGoods.getId());
			respMsg.setReqTargetId(reqMsg.getTargetId());
			return respMsg;
		} catch (Exception e) {
			logger.error("", e);
		}
		return new C0002_ErrorRespMessage(reqMsg.getCommandId(),
				this.getText(TextId.SYSTEM_ERROR));
	}
	
}
