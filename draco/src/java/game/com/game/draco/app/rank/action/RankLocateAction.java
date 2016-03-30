package com.game.draco.app.rank.action;

import java.util.ArrayList;
import java.util.List;

import com.game.draco.GameContext;
import com.game.draco.app.rank.domain.RankInfo;
import com.game.draco.app.rank.domain.RankLogData;
import com.game.draco.app.rank.logic.RankLogic;
import com.game.draco.message.item.RankDetailItem;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.request.C0813_RankLocateReqMessage;
import com.game.draco.message.response.C0811_RankDetailRespMessage;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;
/**
 * 0813，根据排名来获取
 * 获得某一页的排行榜结果
 */
public class RankLocateAction extends BaseAction<C0813_RankLocateReqMessage> {

	@Override
	public Message execute(ActionContext context, C0813_RankLocateReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null;
		}
		RankInfo rankInfo = GameContext.getRankApp().getRankInfo(reqMsg.getRankId());
		if(null == rankInfo){
			C0003_TipNotifyMessage tipMsg = new C0003_TipNotifyMessage();
			tipMsg.setMsgContext(this.getText(TextId.ERROR_INPUT));
			return tipMsg;
		}
		
		short rank = reqMsg.getRank();
		//如果请求的排名超出要求显示的排名
		if(rank > rankInfo.getDisCount()){
			C0003_TipNotifyMessage tipMsg = new C0003_TipNotifyMessage();
			tipMsg.setMsgContext(Status.Rank_Role_Null.getTips());
			return tipMsg;
		}
		//客户端页码从1开始
		RankLogData rankLogData = GameContext.getRankApp().getPageData(reqMsg.getRankId(), 
				(reqMsg.getRank()-1) / RankLogic.PRE_PAGE_COUNT -1);
		//如果取到数据
		if(null == rankLogData){
			C0003_TipNotifyMessage tipMsg = new C0003_TipNotifyMessage();
			tipMsg.setMsgContext(Status.Rank_Role_Null.getTips());
			return tipMsg;
		}
		List<RankDetailItem> rdItemList = null;
		for(RankDetailItem srcItem : rankLogData.getRdItemList()){
			if(null == srcItem){
				continue;
			}
			if(null == rdItemList){
				rdItemList = new ArrayList<RankDetailItem>();
			}
			rdItemList.add(srcItem);
		}
		C0811_RankDetailRespMessage respMsg = new C0811_RankDetailRespMessage();
		respMsg.setCurPage((short)(rankLogData.getCurPage() + 1));
		respMsg.setPageTotal((short)(rankLogData.getTotalPage()));
		respMsg.setDetaiItems(rankLogData.getRdItemList());
		respMsg.setKeyType(rankInfo.getRankType().getActorType().getType());
		return respMsg;
	}

}
