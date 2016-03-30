package sacred.alliance.magic.action;

import sacred.alliance.magic.app.goods.behavior.AbstractGoodsBehavior;
import sacred.alliance.magic.app.goods.behavior.GoodsBehaviorType;
import sacred.alliance.magic.app.goods.behavior.param.MosaicParam;
import sacred.alliance.magic.app.goods.behavior.result.MosaicHoleResult;
import sacred.alliance.magic.base.BindingType;
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
import com.game.draco.message.item.GoodsEquRuneItem;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.request.C0543_GoodsMosaicReqMessage;
import com.game.draco.message.response.C0543_GoodsMosaicRespMessage;

/**
 * 装备衍生：执行镶嵌 543
 */
public class GoodsMosaicAction extends BaseAction<C0543_GoodsMosaicReqMessage> {

	@Override
	public Message execute(ActionContext context, C0543_GoodsMosaicReqMessage reqMsg) {
		C0543_GoodsMosaicRespMessage resp = new C0543_GoodsMosaicRespMessage();
		resp.setStatus(RespTypeStatus.FAILURE);
		try {
			RoleInstance role = this.getCurrentRole(context);
			if (null == role) {
				return null;
			}
			byte bagType = reqMsg.getPositionType();// 背包类型
			String equId = reqMsg.getInstanceId();// 装备实例ID
			String runeId = reqMsg.getRuneId();// 符文实例ID
			int targetId = reqMsg.getTargetId();// 英雄ID
			byte hole = reqMsg.getHole();// 孔位
			// 验证物品是否存在
			RoleGoods equipGoods = this.getRoleGoods(role, bagType, equId, targetId);// 获得装备
			int maxHole = GameContext.getEquipApp().getEquipMaxHole(equipGoods) - 1;
			if (maxHole < hole) {
				C0003_TipNotifyMessage message = new C0003_TipNotifyMessage();
				String msgContext = GameContext.getI18n().messageFormat(TextId.GOODS_STRENGTHEN_MOSAIC_LV, GameContext.getEquipApp().getOpenHoleLevel(hole));
				message.setMsgContext(msgContext);
				return message;
			}
			RoleGoods roleRune = this.getRoleGoods(role, StorageType.bag.getType(), runeId, targetId); // 获得符文
			if (equipGoods == null || roleRune == null) {
				resp.setInfo(this.getText(TextId.GOODS_NO_EXISTS));
				return resp;
			}
			// 封装镶嵌符文参数
			MosaicParam param = new MosaicParam(role);
			param.setEquipGoods(equipGoods);
			param.setGemGoods(roleRune);
			param.setTargetId(targetId);
			param.setHole(hole);
			// 执行镶嵌
			GoodsType goodsType = this.getGoodsType(equipGoods.getGoodsId());
			AbstractGoodsBehavior goodsBehavior = goodsType.getGoodsBehavior(GoodsBehaviorType.Mosaic);
			Result result = goodsBehavior.operate(param);
			if(result.isIgnore()){
				return null;
			}
			// 处理镶嵌结果
			MosaicHoleResult mosaicHoleResult = (MosaicHoleResult) result;
			if (!mosaicHoleResult.isSuccess()) {
				resp.setInfo(mosaicHoleResult.getInfo());
				return resp;
			}
			// 将本次镶嵌对装备的影响返回给客户端
			resp.setPositionType(bagType);// 容器
			resp.setInstanceId(equId);// 装备
			resp.setTargetId(targetId);// 英雄ID
			// 将镶嵌的符文信息返回
			GoodsEquRuneItem equRuneItem = new GoodsEquRuneItem();
			equRuneItem.setHole((byte) mosaicHoleResult.getMatchHoleId());
			equRuneItem.setGoodsLiteItem(mosaicHoleResult.getMosaicRune().getGoodsLiteItem());
			equRuneItem.setRuneName(mosaicHoleResult.getRuneTemplate().getName());
			equRuneItem.setAttriItems(mosaicHoleResult.getMosaicRune().getAttriStrValueList());
			resp.setEquGemItem(equRuneItem);
			resp.setEquBindType(BindingType.already_binding.getType());// 绑定类型（镶嵌成功必绑定）
			resp.setInfo(Status.Goods_Mosaic_Success.getTips());
			resp.setStatus(RespTypeStatus.SUCCESS);
			return resp;
		} catch (Exception e) {
			logger.error("GoodsDeriveMosaicAction ", e);
			resp.setInfo(this.getText(TextId.SYSTEM_ERROR));
		}
		return resp;
	}

	private RoleGoods getRoleGoods(RoleInstance role, byte bagType, String goodsInstanceId, int targetId) {
		StorageType storageType = StorageType.get(bagType);
		return GameContext.getUserGoodsApp().getRoleGoods(role, storageType, goodsInstanceId, targetId);
	}

	private GoodsType getGoodsType(int goodsId) {
		return GameContext.getGoodsApp().getGoodsType(goodsId);
	}
}
