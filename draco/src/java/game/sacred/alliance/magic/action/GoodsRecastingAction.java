package sacred.alliance.magic.action;

import com.game.draco.GameContext;
import com.game.draco.message.request.C0555_GoodsRecastingReqMessage;
import com.game.draco.message.response.C0555_GoodsRecastingRespMessage;

import sacred.alliance.magic.app.goods.behavior.AbstractGoodsBehavior;
import sacred.alliance.magic.app.goods.behavior.GoodsBehaviorType;
import sacred.alliance.magic.app.goods.behavior.param.RecastingParam;
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

public class GoodsRecastingAction extends BaseAction<C0555_GoodsRecastingReqMessage>{

	@Override
	public Message execute(ActionContext context, C0555_GoodsRecastingReqMessage reqMsg) {
		C0555_GoodsRecastingRespMessage respMsg = new C0555_GoodsRecastingRespMessage();
		respMsg.setStatus(RespTypeStatus.FAILURE);
		try{
			RoleInstance role = this.getCurrentRole(context);
			if(null == role){
				return null;
			}
			
			byte positionType = reqMsg.getPositionType();
			String instanceId = reqMsg.getInstanceId();
			int targetId = reqMsg.getTargetId() ;
			StorageType storageType = StorageType.get(positionType);
			RoleGoods equipGoods = GameContext.getUserGoodsApp().getRoleGoods(role, storageType, instanceId,targetId);
			if(equipGoods == null){
				respMsg.setInfo(this.getText(TextId.NO_GOODS));
				return respMsg;
			}
			GoodsType goodsType = GameContext.getGoodsApp().getGoodsType(equipGoods.getGoodsId());
			AbstractGoodsBehavior goodsBehavior = goodsType.getGoodsBehavior(GoodsBehaviorType.Recasting);
			RecastingParam param = new RecastingParam(role);
			param.setEquipGoods(equipGoods);
			param.setLockIndex(reqMsg.getLockIndex());
			param.setType(reqMsg.getType());//0=普通洗练，1=钻石洗练
			param.setTargetId(targetId);
			
			Result result = goodsBehavior.operate(param);
			if(result.isIgnore()){
				return null;
			}
			if(!result.isSuccess()){
				respMsg.setInfo(result.getInfo());
				return respMsg ;
			}
			respMsg.setStatus(RespTypeStatus.SUCCESS);
			respMsg.setInfo(Status.Goods_Recasting_Success.getTips());
		}catch(Exception e){
			logger.error("GoodsDeriveRecastingAction ", e);
			respMsg.setStatus(RespTypeStatus.FAILURE);
			respMsg.setInfo(this.getText(TextId.SYSTEM_ERROR));
		}
		return respMsg;
	}

	
}
