package com.game.draco.app.union.action;

import java.util.ArrayList;
import java.util.List;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.union.domain.UnionMember;
import com.game.draco.message.item.UnionApplyJoinItem;
import com.game.draco.message.request.C1706_UnionApplyJoinListReqMessage;
import com.game.draco.message.response.C1706_UnionApplyJoinListRespMessage;

/**
 * 查看申请列表
 * @author mofun030602
 *
 */
public class UnionApplyJoinListAction extends BaseAction<C1706_UnionApplyJoinListReqMessage> {

	@Override
	public Message execute(ActionContext context, C1706_UnionApplyJoinListReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		List<UnionMember> list = GameContext.getUnionApp().getApplyJoinList(role.getUnionId());
		List<UnionApplyJoinItem> unionJoinList = new ArrayList<UnionApplyJoinItem>();
		for(UnionMember member : list){
			if(null == member){
				continue;
			}
			UnionApplyJoinItem item = new UnionApplyJoinItem();
			item.setRoleId(member.getRoleId());
			item.setRoleName(member.getRoleName());
			item.setRoleLevel((short)member.getLevel());
			item.setCareer(member.getOccupation());
			unionJoinList.add(item);
		}
		C1706_UnionApplyJoinListRespMessage resp = new C1706_UnionApplyJoinListRespMessage();
		resp.setUnionJoinList(unionJoinList);
		return resp;
	}

}
