package sacred.alliance.magic.action;

import java.util.ArrayList;
import java.util.List;

import com.game.draco.GameContext;
import com.game.draco.message.item.AuctionSearchItem;
import com.game.draco.message.request.C0850_AuctionSearchReqMessage;
import com.game.draco.message.response.C0850_AuctionSearchRespMessage;

import platform.message.item.MercuryRecordItem;
import sacred.alliance.magic.app.auction.SearchResult;
import sacred.alliance.magic.constant.RespTypeStatus;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

public class AuctionSearchAction extends BaseAction<C0850_AuctionSearchReqMessage>{

	@Override
	public Message execute(ActionContext context, C0850_AuctionSearchReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null ;
		}
		C0850_AuctionSearchRespMessage respMsg = new C0850_AuctionSearchRespMessage();
		respMsg.setStatus(RespTypeStatus.FAILURE);
		SearchResult result = GameContext.getAuctionApp().search(role, reqMsg);
		if(!result.isSuccess()){
			respMsg.setInfo(result.getTips());
			return respMsg ;
		}
		
		if(reqMsg.getCurrPage() <=0 
				&& reqMsg.getBigType() <=-1 
				&& reqMsg.getSmallType() <=-1
				&& Util.isEmpty(reqMsg.getName())
				&& -1 == reqMsg.getQualityType()
				&& 0 == reqMsg.getUsable()
				&& -1 == reqMsg.getMoneyType()
				&& 3 == reqMsg.getSortFiled()
				&& 0 == reqMsg.getDesc()){
			//首次默认搜索的才提示
			//通知是否有过期物品
			GameContext.getAuctionApp().notifyHaveExpiredGoods(role);
		}
		
		List<AuctionSearchItem> resultList = new ArrayList<AuctionSearchItem>();
		for( MercuryRecordItem record  : result.getRecords()){
			AuctionSearchItem item = GameContext.getAuctionApp().convertSearchItem(record);
			if(null != item){
				resultList.add(item);
			}
		}
		
		respMsg.setStatus(RespTypeStatus.SUCCESS);
		respMsg.setTotalPage((short)result.getTotalPage());
		respMsg.setAuctionSearchList(resultList);
		respMsg.setCurrPage(reqMsg.getCurrPage());
		return respMsg;
	}
	
}
