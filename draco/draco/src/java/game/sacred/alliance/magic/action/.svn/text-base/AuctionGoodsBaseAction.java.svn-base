package sacred.alliance.magic.action;

import com.game.draco.GameContext;
import com.game.draco.message.item.GoodsBaseItem;
import com.game.draco.message.request.C0856_AuctionGoodsBaseReqMessage;
import com.game.draco.message.response.C0856_AuctionGoodsBaseRespMessage;
import com.game.draco.message.response.C0002_ErrorRespMessage;

import platform.message.item.MercuryRecordItem;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.domain.RoleGoods;

public class AuctionGoodsBaseAction extends BaseAction<C0856_AuctionGoodsBaseReqMessage>{

	@Override
	public Message execute(ActionContext context, C0856_AuctionGoodsBaseReqMessage reqMsg) {
		MercuryRecordItem item = GameContext.getAuctionApp().getRecord(reqMsg.getId());
		if(null == item){
			return new C0002_ErrorRespMessage(reqMsg.getCommandId(),Status.Auction_Error_Goods_Not_Exist.getTips());
		}
		RoleGoods roleGoods = GameContext.getAuctionApp().toRoleGoods(item) ;
		if(null == roleGoods){
			return new C0002_ErrorRespMessage(reqMsg.getCommandId(),Status.Auction_Error_Goods_Not_Exist.getTips());
		}
		
		GoodsBase goodsBase = GameContext.getGoodsApp().getGoodsBase(roleGoods.getGoodsId());
		if(goodsBase == null){
			return new C0002_ErrorRespMessage(reqMsg.getCommandId(),Status.Auction_Error_Goods_Not_Exist.getTips());
		}
		
		C0856_AuctionGoodsBaseRespMessage resp = new C0856_AuctionGoodsBaseRespMessage();
		resp.setCount((byte)roleGoods.getCurrOverlapCount());
		resp.setId(roleGoods.getId());
		GoodsBaseItem baseItem = goodsBase.getGoodsBaseInfo(roleGoods) ;
		
		//绑定类型
		baseItem.setBindType(GameContext.getAuctionApp().getBindType(goodsBase));
		
		resp.setBaseItem(baseItem);
		return resp ;
	}
	
}
