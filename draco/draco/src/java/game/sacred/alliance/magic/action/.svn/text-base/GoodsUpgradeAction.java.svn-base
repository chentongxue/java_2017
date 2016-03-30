package sacred.alliance.magic.action;

import com.game.draco.GameContext;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.request.C0546_GoodsUpgradeReqMessage;
import com.game.draco.message.response.C0546_GoodsUpgradeRespMessage;

import sacred.alliance.magic.app.goods.behavior.AbstractGoodsBehavior;
import sacred.alliance.magic.app.goods.behavior.GoodsBehaviorType;
import sacred.alliance.magic.app.goods.behavior.param.EquipUpgradeParam;
import sacred.alliance.magic.base.GoodsType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.base.StorageType;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.constant.RespTypeStatus;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.vo.RoleInstance;

public class GoodsUpgradeAction extends BaseAction<C0546_GoodsUpgradeReqMessage>{

	@Override
	public Message execute(ActionContext context, C0546_GoodsUpgradeReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null ;
		}
		byte bagType = reqMsg.getBagType();
		String goodsInstanceId = reqMsg.getGoodsInstanceId();
		
		C0546_GoodsUpgradeRespMessage respMsg = new C0546_GoodsUpgradeRespMessage();
		respMsg.setStatus(RespTypeStatus.FAILURE);
		try {
			StorageType storageType = StorageType.get(bagType);
			RoleGoods equipGoods = GameContext.getUserGoodsApp().getRoleGoods(role, storageType, goodsInstanceId);
			if(equipGoods == null){
				return new C0003_TipNotifyMessage(this.getText(TextId.NO_GOODS));
			}
			GoodsType goodsType = GameContext.getGoodsApp().getGoodsType(equipGoods.getGoodsId());
			AbstractGoodsBehavior goodsBehavior = goodsType.getGoodsBehavior(GoodsBehaviorType.EquipUpgrade);
			
			EquipUpgradeParam param = new EquipUpgradeParam(role);
			//升级操作
			param.setParamType(EquipUpgradeParam.PARAM_EXEC);
			param.setBagType(bagType);
			param.setGoodsInstanceId(goodsInstanceId);
			Result result = goodsBehavior.operate(param);
			if(!result.isSuccess()){
				respMsg.setInfo(result.getInfo());
				return respMsg ;
			}
			respMsg.setStatus(RespTypeStatus.SUCCESS);
			respMsg.setInfo(this.getText(TextId.EQUIP_UPGRADE_SUCCESS));
		}catch(Exception ex){
			logger.error("GoodsUpgradeAction",ex);
			respMsg.setInfo(this.getText(TextId.SYSTEM_ERROR));
		}
		return respMsg ;
	}

}
