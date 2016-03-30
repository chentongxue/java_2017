package com.game.draco.app.unionbattle.action;

import java.util.List;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.union.domain.Union;
import com.game.draco.app.unionbattle.domain.UnionIntegralRank;
import com.game.draco.message.item.UnionIntegralBattleItem;
import com.game.draco.message.request.C2542_UnionIntegralListReqMessage;
import com.game.draco.message.response.C2542_UnionIntegralListRespMessage;
import com.google.common.collect.Lists;

/**
 * 查看公会积分列表
 */
public class UnionIntegralListAction extends BaseAction<C2542_UnionIntegralListReqMessage> {

	@Override
	public Message execute(ActionContext context, C2542_UnionIntegralListReqMessage req) {
		RoleInstance role = this.getCurrentRole(context);
		if(role == null){
			return null;
		}
		
		C2542_UnionIntegralListRespMessage respMsg = new C2542_UnionIntegralListRespMessage();
		
		List<UnionIntegralRank> list = GameContext.getUnionIntegralBattleApp().getUnionIntegralRankList();
		
		List<UnionIntegralBattleItem> integralItemList = Lists.newArrayList();
		
		for(UnionIntegralRank record : list){
			UnionIntegralBattleItem item = new UnionIntegralBattleItem();
			String unionName = GameContext.getI18n().getText(TextId.UNION_INTEGRAL_FIGHT_BYE);
			if(record.getIntegral() == 0){
				continue;
			}
			item.setIntegral(record.getIntegral());
			Union union = GameContext.getUnionApp().getUnion(record.getUnionId());
			if(union == null){
				continue;
			}
			unionName = union.getUnionName();
			item.setUnionLeaderName(union.getLeaderName());
			item.setUnionName(unionName);
			integralItemList.add(item);
		}
		
		respMsg.setList(integralItemList);
		
		return respMsg;
	}

}
