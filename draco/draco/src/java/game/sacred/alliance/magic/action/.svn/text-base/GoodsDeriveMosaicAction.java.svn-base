package sacred.alliance.magic.action;

import com.game.draco.GameContext;
import com.game.draco.message.item.GoodsEquGemItem;
import com.game.draco.message.request.C0543_GoodsMosaicReqMessage;
import com.game.draco.message.response.C0543_GoodsMosaicRespMessage;

import sacred.alliance.magic.app.goods.behavior.AbstractGoodsBehavior;
import sacred.alliance.magic.app.goods.behavior.GoodsBehaviorType;
import sacred.alliance.magic.app.goods.behavior.param.MosaicParam;
import sacred.alliance.magic.app.goods.behavior.result.MosaicHoleResult;
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

/**
 * 装备衍生：执行镶嵌 543
 */
public class GoodsDeriveMosaicAction extends BaseAction<C0543_GoodsMosaicReqMessage>{

	@Override
	public Message execute(ActionContext context, C0543_GoodsMosaicReqMessage reqMsg) {
		C0543_GoodsMosaicRespMessage respMsg = new C0543_GoodsMosaicRespMessage();
		respMsg.setStatus(RespTypeStatus.FAILURE);
		try{
			byte bagType = reqMsg.getPositionType();
			String equId = reqMsg.getInstanceId();
			String gemId = reqMsg.getGemId();
			RoleInstance role = this.getCurrentRole(context);
			
			RoleGoods equipGoods = this.getRoleGoods(role, bagType, equId);
			RoleGoods gemGoods = this.getRoleGoods(role, StorageType.bag.getType(), gemId);
			if(equipGoods == null || gemGoods == null){
				respMsg.setInfo(this.getText(TextId.GOODS_NO_EXISTS));
				return respMsg ;
			}
			
			MosaicParam param = new MosaicParam(role);
			param.setEquipGoods(equipGoods);
			param.setGemGoods(gemGoods);
			param.setOperateType(MosaicParam.MOSAIC);
			
			GoodsType goodsType = this.getGoodsType(equipGoods.getGoodsId());
			AbstractGoodsBehavior goodsBehavior = goodsType.getGoodsBehavior(GoodsBehaviorType.Mosaic);
			
			Result result = goodsBehavior.operate(param);
			
			MosaicHoleResult mosaicHoleResult = (MosaicHoleResult)result;
			if(!mosaicHoleResult.isSuccess()){
				respMsg.setInfo(mosaicHoleResult.getInfo());
				return respMsg ;
			}
			
			//将本次镶嵌对装备的影响返回给客户端
			respMsg.setPositionType(bagType);
			respMsg.setInstanceId(equId);
			respMsg.setEquBindType(equipGoods.getBind());
			
			GoodsEquGemItem equGemItem = new GoodsEquGemItem();
			equGemItem.setHoleIndex((byte)mosaicHoleResult.getMatchHoleId());
			equGemItem.setImageId(mosaicHoleResult.getGemTemplate().getImageId());
			equGemItem.setGemQuality(mosaicHoleResult.getGemTemplate().getQualityType());
			equGemItem.setGemName(mosaicHoleResult.getGemTemplate().getName());
			equGemItem.setLevel((byte)mosaicHoleResult.getGemTemplate().getLevel());
			equGemItem.setAttriItems(mosaicHoleResult.getGemTemplate().getDisplayAttriItem());
			
			respMsg.setEquGemItem(equGemItem);
			
			//respMsg.setHoleIndex((byte)mosaicHoleResult.getMatchHoleId());
			//respMsg.setGemImage((byte)mosaicHoleResult.getGemTemplate().getImageId());
			//respMsg.setGemQuality(mosaicHoleResult.getGemTemplate().getQualityType());
			//respMsg.setGemName(mosaicHoleResult.getGemTemplate().getName());
			//respMsg.setGemAttributes(mosaicHoleResult.getGemTemplate().getDisplayAttriItem());
			
			respMsg.setStatus(RespTypeStatus.SUCCESS);
			respMsg.setInfo(Status.Goods_Mosaic_Success.getTips());
			return respMsg;
		}catch(Exception e){
			logger.error("GoodsDeriveMosaicAction ", e);
			respMsg.setInfo(this.getText(TextId.SYSTEM_ERROR));
		}
		return respMsg ;
	}
	
	
	private RoleGoods getRoleGoods(RoleInstance role ,byte bagType,String goodsInstanceId){
		StorageType storageType = StorageType.get(bagType);
		return GameContext.getUserGoodsApp().getRoleGoods(role, storageType, goodsInstanceId);
	}
	
	private GoodsType getGoodsType(int goodsId){
		return GameContext.getGoodsApp().getGoodsType(goodsId);
	}
}
