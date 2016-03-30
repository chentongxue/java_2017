package sacred.alliance.magic.action;

import java.util.ArrayList;
import java.util.List;

import com.game.draco.GameContext;
import com.game.draco.message.item.GoodsLiteItem;
import com.game.draco.message.request.C0523_GoodsGiftPanelReqMessage;
import com.game.draco.message.response.C0523_GoodsGiftPanelRespMessage;

import sacred.alliance.magic.app.goods.GoodsOperateBean;
import sacred.alliance.magic.base.BindingType;
import sacred.alliance.magic.constant.RespTypeStatus;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.domain.GoodsGift;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.vo.RoleInstance;

public class GoodsGiftPanelAction extends BaseAction<C0523_GoodsGiftPanelReqMessage>{

	@Override
	public Message execute(ActionContext context, C0523_GoodsGiftPanelReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null ;
		}
		C0523_GoodsGiftPanelRespMessage respMsg = new C0523_GoodsGiftPanelRespMessage();
		respMsg.setStatus(RespTypeStatus.FAILURE);
		
		String goodsInstanceId = reqMsg.getGoodsInstanceId();
		//获得相关物品
		RoleGoods roleGoods = role.getRoleBackpack().getRoleGoodsByInstanceId(goodsInstanceId);
		if(null == roleGoods){
			//没有相关物品
			return respMsg ;
		}
		int goodsId = roleGoods.getGoodsId() ;
		GoodsGift gift = GameContext.getGoodsApp().getGoodsTemplate(GoodsGift.class, goodsId);
		if(null == gift){
			return respMsg ;
		}
		respMsg.setGoodsInstanceId(goodsInstanceId);
		respMsg.setGoodsName(gift.getName());
		int nextId = gift.getNextId() ;
		if(nextId > 0){
			GoodsGift nextGift = GameContext.getGoodsApp().getGoodsTemplate(GoodsGift.class, nextId);
			if(null != nextGift){
				respMsg.setNextLevel((byte)nextGift.getLvLimit());
				respMsg.setNextName(nextGift.getName());
			}
		}
		//获得内部物品
		List<GoodsLiteItem> items = new ArrayList<GoodsLiteItem>();
		List<GoodsOperateBean> goodsList = gift.getGoodsList();
		for(GoodsOperateBean bean : goodsList){
			GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(bean.getGoodsId());
			if(null == gb){
				continue ;
			}
			GoodsLiteItem item = gb.getGoodsLiteItem() ;
			item.setGoodsId(bean.getGoodsId());
			item.setNum((short)bean.getGoodsNum());
			if(null == bean.getBindType() || BindingType.template == bean.getBindType()){
				item.setBindType(gb.getBindType());
			}else{
				item.setBindType(bean.getBindType().getType());
			}
			items.add(item);
		}
		respMsg.setItems(items);
		respMsg.setStatus(RespTypeStatus.SUCCESS);
		return respMsg ;
	}

}
