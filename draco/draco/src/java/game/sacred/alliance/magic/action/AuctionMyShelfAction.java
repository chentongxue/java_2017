package sacred.alliance.magic.action;

import java.util.ArrayList;
import java.util.List;

import com.game.draco.GameContext;
import com.game.draco.message.item.AuctionMyShelfItem;
import com.game.draco.message.request.C0851_AuctionMyShelfReqMessage;
import com.game.draco.message.response.C0851_AuctionMyShelfRespMessage;

import platform.message.item.MercuryRecordItem;
import sacred.alliance.magic.app.auction.SearchResult;
import sacred.alliance.magic.constant.RespTypeStatus;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

public class AuctionMyShelfAction extends BaseAction<C0851_AuctionMyShelfReqMessage>{

	@Override
	public Message execute(ActionContext context, C0851_AuctionMyShelfReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null ;
		}
		C0851_AuctionMyShelfRespMessage respMsg = new C0851_AuctionMyShelfRespMessage();
		respMsg.setStatus(RespTypeStatus.FAILURE);
		
		SearchResult result = GameContext.getAuctionApp().getRoleAuctionList(role);
		if(!result.isSuccess()){
			respMsg.setInfo(result.getTips());
			return respMsg ;
		}
		
		List<AuctionMyShelfItem> auctionMyShelfList = new ArrayList<AuctionMyShelfItem>();
		for(MercuryRecordItem record : result.getRecords()){
			AuctionMyShelfItem item = GameContext.getAuctionApp().convertMyShelfItem(record);
			if(null != item){
				auctionMyShelfList.add(item);
			}
		}
		respMsg.setAuctionMyShelfList(auctionMyShelfList);
		respMsg.setStatus(RespTypeStatus.SUCCESS);
		return respMsg;
	}
	
}
