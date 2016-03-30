package com.game.draco.app.rank.action;


import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.rank.domain.RankInfo;
import com.game.draco.app.rank.domain.RankLogData;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.request.C0811_RankDetailReqMessage;
import com.game.draco.message.response.C0811_RankDetailRespMessage;
/**
 * 0811
 * 根据排行榜页码获取排行榜
 */
public class RankDetailAction extends BaseAction<C0811_RankDetailReqMessage>{

	@Override
	public Message execute(ActionContext context, C0811_RankDetailReqMessage reqMsg) {
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
		//客户端页码从1开始
		int page = reqMsg.getPage()-1 ;
		RankLogData rankLogData = GameContext.getRankApp().getPageData(reqMsg.getRankId(),page);
		if(null == rankLogData){
			C0811_RankDetailRespMessage noDateMsg = new C0811_RankDetailRespMessage();
			return noDateMsg;
		}
		C0811_RankDetailRespMessage respMsg = new C0811_RankDetailRespMessage();
		respMsg.setCurPage((short)(rankLogData.getCurPage() + 1));
		respMsg.setPageTotal((short)(rankLogData.getTotalPage()));
		respMsg.setDetaiItems(rankLogData.getRdItemList());
		respMsg.setKeyType(rankInfo.getRankType().getActorType().getType());
		return respMsg;
	}

}
