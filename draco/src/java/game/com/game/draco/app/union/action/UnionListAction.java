package com.game.draco.app.union.action;

import java.util.ArrayList;
import java.util.List;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.util.ListPageDisplay;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.union.domain.Union;
import com.game.draco.message.item.UnionItem;
import com.game.draco.message.request.C1702_UnionListReqMessage;
import com.game.draco.message.response.C1702_UnionListRespMessage;

/**
 * 公会列表
 * @author mofun030602
 *
 */
public class UnionListAction extends BaseAction<C1702_UnionListReqMessage> {

	@Override
	public Message execute(ActionContext context, C1702_UnionListReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		C1702_UnionListRespMessage resp = new C1702_UnionListRespMessage();
		ListPageDisplay<Union> result = GameContext.getUnionApp().getUnionList(reqMsg.getPageNum(), reqMsg.getPageSize());
		List<UnionItem> unionList = new ArrayList<UnionItem>();
		if(Util.isEmpty(result.getList())){
			return resp;
		}
		
		String unionId = role.getUnionId();
		for(Union union : result.getList()){
			if(union == null){
				continue;
			}
			UnionItem item = new UnionItem();
			item.setUnionId(union.getUnionId());
			item.setUnionName(union.getUnionName());
			item.setUnionLevel(union.getUnionLevel());
			item.setLeaderName(union.getLeaderName());
			item.setMemberNum((short) union.getUnionMemberMap().size());
			item.setMaxMemberNum((short)GameContext.getUnionApp().getUnionDataAllNum(union.getUnionLevel()));
			item.setMaxPopularity(GameContext.getUnionApp().getUnionDataMaxPopualrity(union.getUnionLevel()));
			item.setUnionDesc(union.getUnionDesc());
			if(null != unionId && union.getUnionId().equals(unionId)){
				item.setSelfUnion((byte) 1);
			}
			item.setMinProgress(GameContext.getUnionInstanceApp().getUnionKillBossRecord(union.getUnionId()).size());
			item.setMaxProgress(GameContext.getUnionDataApp().getActivityMaxBossNum());
			unionList.add(item);
		}
		resp.setTotalPageNum((short)result.getTotalPages());
		resp.setFactionList(unionList);
		return resp;
	}

}
