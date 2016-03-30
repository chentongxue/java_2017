package com.game.draco.app.rank.action;

import java.util.ArrayList;
import java.util.List;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.rank.domain.RankGroup;
import com.game.draco.app.rank.domain.RankInfo;
import com.game.draco.app.rank.domain.RankLayout;
import com.game.draco.app.rank.domain.RankWorld;
import com.game.draco.message.item.RankGroupItem;
import com.game.draco.message.item.RankListRankItem;
import com.game.draco.message.item.RankListRankSonItem;
import com.game.draco.message.request.C0810_RankListReqMessage;
import com.game.draco.message.response.C0810_RankListRespMessage;
/**
 * 获得排行榜显示项列表
 * 0810
 */
public class RankListAction extends BaseAction<C0810_RankListReqMessage>{

	@Override
	public Message execute(ActionContext context, C0810_RankListReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null;
		}
		C0810_RankListRespMessage respMsg = new C0810_RankListRespMessage();
		List<RankGroup> rankGroupList = GameContext.getRankApp().getRankGroupList();
		if(Util.isEmpty(rankGroupList)){
			return respMsg;
		}
		List<RankGroupItem> groupItems = new ArrayList<RankGroupItem>();
		respMsg.setGroupItems(groupItems);
		//
		for(RankGroup group : rankGroupList){
			if(null == group){
				continue;
			}
			List<RankWorld> rankWorldList = group.getRankWorldList();
			if(Util.isEmpty(rankWorldList)){
				continue ;
			}
			RankGroupItem groupItem = new RankGroupItem();
			groupItem.setGroupName(group.getGroupName());
			
			for(RankWorld rankWorld : rankWorldList){
				RankListRankItem rankItem = new RankListRankItem();
				rankItem.setName(rankWorld.getName());
				RankLayout layout = GameContext.getRankApp().getRankLayout(rankWorld.getRankType().getType());
				if(null != layout){
					rankItem.setLayoutItemList(layout.getLayoutItemList());
				}
				for(RankInfo rankInfo : rankWorld.getRankInfoList()){
					RankListRankSonItem sonItem = new RankListRankSonItem();
					sonItem.setRankId(rankInfo.getId());
//					sonItem.setTagResId(rankInfo.getTagResId());
					sonItem.setRankName(rankInfo.getName());
					rankItem.getSonItems().add(sonItem);
				}
				groupItem.getRankItems().add(rankItem);
			}
			groupItems.add(groupItem);
		}
		respMsg.setRankId(reqMsg.getRankId());
		return respMsg;
	}

}
