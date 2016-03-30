package sacred.alliance.magic.action;

import sacred.alliance.magic.app.goods.behavior.AbstractGoodsBehavior;
import sacred.alliance.magic.app.goods.behavior.GoodsBehaviorType;
import sacred.alliance.magic.app.goods.behavior.param.EquipUpgradeParam;
import sacred.alliance.magic.app.goods.behavior.result.EquipUpgradeResult;
import sacred.alliance.magic.base.GoodsType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.base.StorageType;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.request.C0548_GoodsUpgradeTargetInfoReqMessage;
import com.game.draco.message.response.C0504_GoodsInfoViewRespMessage;

public class GoodsUpgradeTargetInfoAction extends BaseAction<C0548_GoodsUpgradeTargetInfoReqMessage> {

	@Override
	public Message execute(ActionContext context, C0548_GoodsUpgradeTargetInfoReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null ;
		}
		byte bagType = reqMsg.getBagType();
		String goodsInstanceId = reqMsg.getGoodsInstanceId();
		try {
			StorageType storageType = StorageType.get(bagType);
			RoleGoods equipGoods = GameContext.getUserGoodsApp().getRoleGoods(role, storageType, goodsInstanceId);
			if(equipGoods == null){
				return new C0003_TipNotifyMessage(this.getText(TextId.NO_GOODS));
			}
			GoodsType goodsType = GameContext.getGoodsApp().getGoodsType(equipGoods.getGoodsId());
			AbstractGoodsBehavior goodsBehavior = goodsType.getGoodsBehavior(GoodsBehaviorType.EquipUpgrade);
			
			EquipUpgradeParam param = new EquipUpgradeParam(role);
			//升级目标信息
			param.setParamType(EquipUpgradeParam.PARAM_TARGET_INFO);
			param.setBagType(bagType);
			param.setGoodsInstanceId(goodsInstanceId);
			Result result = goodsBehavior.operate(param);
			if(!result.isSuccess()){
				return new C0003_TipNotifyMessage(result.getInfo());
			}
			C0504_GoodsInfoViewRespMessage respMsg = new C0504_GoodsInfoViewRespMessage();
			EquipUpgradeResult upgradeResult = (EquipUpgradeResult)result ;
			RoleGoods targetRolgeGoods = upgradeResult.getTargetRoleGoods();
			respMsg.setBaseItem(upgradeResult.getTargetEquipment().getGoodsBaseInfo(targetRolgeGoods));
			return respMsg ;
		}catch(Exception ex){
			logger.error("GoodsUpgradeTargetInfoAction",ex);
			return new C0003_TipNotifyMessage(this.getText(TextId.SYSTEM_ERROR));
		}
	}

}
