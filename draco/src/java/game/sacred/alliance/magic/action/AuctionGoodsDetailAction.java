package sacred.alliance.magic.action;

import platform.message.item.MercuryRecordItem;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.item.GoodsBaseItem;
import com.game.draco.message.request.C0857_AuctionGoodsDetailReqMessage;
import com.game.draco.message.response.C0002_ErrorRespMessage;
import com.game.draco.message.response.C0504_GoodsInfoViewRespMessage;

public class AuctionGoodsDetailAction extends BaseAction<C0857_AuctionGoodsDetailReqMessage>{
	
	@Override
	public Message execute(ActionContext context, C0857_AuctionGoodsDetailReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null;
		}
		try {
			MercuryRecordItem item = GameContext.getAuctionApp().getRecord(reqMsg.getId());
			if(null == item){
				return new C0002_ErrorRespMessage(reqMsg.getCommandId(),Status.Auction_Error_Goods_Not_Exist.getTips());
			}
			RoleGoods roleGoods = GameContext.getAuctionApp().toRoleGoods(item) ;
			if(null == roleGoods){
				return new C0002_ErrorRespMessage(reqMsg.getCommandId(),Status.Auction_Error_Goods_Not_Exist.getTips());
			}
			GoodsBase goodsBase = GameContext.getGoodsApp().getGoodsBase(roleGoods.getGoodsId());
			if(null == goodsBase){
				return new C0002_ErrorRespMessage(reqMsg.getCommandId(),Status.Auction_Error_Goods_Not_Exist.getTips());
			}
			GoodsBaseItem baseItem = goodsBase.getGoodsBaseInfo(roleGoods);
			if(null == baseItem ){
				return new C0002_ErrorRespMessage(reqMsg.getCommandId(),Status.Auction_Error_Goods_Not_Exist.getTips());
			}
			
			//绑定类型
			baseItem.setBindType(GameContext.getAuctionApp().getBindType(goodsBase));
			
			C0504_GoodsInfoViewRespMessage baseMsg = new C0504_GoodsInfoViewRespMessage();
			baseMsg.setId(roleGoods.getId());
			baseMsg.setBaseItem(baseItem);
			return baseMsg ;
			
		} catch (Exception e) {
			logger.error("", e);
			return new C0002_ErrorRespMessage(reqMsg.getCommandId(),
					this.getText(TextId.SYSTEM_ERROR));
		}
	
	}
	
}
