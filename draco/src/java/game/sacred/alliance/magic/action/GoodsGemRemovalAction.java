package sacred.alliance.magic.action;

import sacred.alliance.magic.app.goods.behavior.AbstractGoodsBehavior;
import sacred.alliance.magic.app.goods.behavior.GoodsBehaviorType;
import sacred.alliance.magic.app.goods.behavior.param.MosaicParam;
import sacred.alliance.magic.base.GoodsType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.base.StorageType;
import sacred.alliance.magic.constant.RespTypeStatus;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.request.C0549_GoodsGemRemovalReqMessage;
import com.game.draco.message.response.C0549_GoodsGemRemovalRespMessage;

public class GoodsGemRemovalAction extends BaseAction<C0549_GoodsGemRemovalReqMessage> {

	@Override
	public Message execute(ActionContext context, C0549_GoodsGemRemovalReqMessage reqMsg) {
		C0549_GoodsGemRemovalRespMessage resp = new C0549_GoodsGemRemovalRespMessage();
		resp.setStatus(RespTypeStatus.FAILURE);
		try {
			RoleInstance role = this.getCurrentRole(context);
			if (null == role) {
				return null;
			}
			byte bagType = reqMsg.getBagType();// 背包类型
			int targetId = reqMsg.getTargetId();// 英雄ID
			String goodsInstanceId = reqMsg.getGoodsId();// 装备实例ID
			// 获得装备
			StorageType storageType = StorageType.get(bagType);
			RoleGoods equipGoods = GameContext.getUserGoodsApp().getRoleGoods(role, storageType, goodsInstanceId, targetId);
			if (equipGoods == null) {
				return new C0003_TipNotifyMessage(this.getText(TextId.NO_GOODS));
			}
			MosaicParam param = new MosaicParam(role);
			param.setEquipGoods(equipGoods);
			param.setHole(reqMsg.getHoleId());
			param.setTargetId(targetId);
			// 执行卸下
			GoodsType goodsType = GameContext.getGoodsApp().getGoodsType(equipGoods.getGoodsId());
			AbstractGoodsBehavior behavior = goodsType.getGoodsBehavior(GoodsBehaviorType.Removal);
			Result result = behavior.operate(param);
			if(result.isIgnore()){
				return null;
			}
			// 处理结果
			if (!result.isSuccess()) {
				resp.setInfo(result.getInfo());
				return resp;
			}
			resp.setBagType(reqMsg.getBagType());
			resp.setGoodsId(reqMsg.getGoodsId());
			resp.setHoleId(reqMsg.getHoleId());
			resp.setTargetId(targetId);
			resp.setInfo(Status.Goods_Gem_Remove_Success.getTips());
			resp.setStatus(result.getResult());
		} catch (Exception ex) {
			resp.setInfo(this.getText(TextId.SYSTEM_ERROR));
			logger.error("", ex);
		}
		return resp;
	}

}
