package com.game.draco.app.qualify.action;

import java.util.List;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.qualify.domain.QualifyRank;
import com.game.draco.message.item.QualifyRoleInfoItem;
import com.game.draco.message.request.C1755_QualifyShowRankReqMessage;
import com.game.draco.message.response.C0002_ErrorRespMessage;
import com.game.draco.message.response.C1755_QualifyShowRankRespMessage;
import com.google.common.collect.Lists;

public class QualifyShowRankAction extends BaseAction<C1755_QualifyShowRankReqMessage> {

	@Override
	public Message execute(ActionContext context, C1755_QualifyShowRankReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if (null == role) {
			return null;
		}
		List<QualifyRank> roleQualifyList = GameContext.getQualifyApp().getRoleQualifyList(reqMsg.getPage());
		if (Util.isEmpty(roleQualifyList)) {
			C0002_ErrorRespMessage message = new C0002_ErrorRespMessage();
			message.setInfo(GameContext.getI18n().getText(TextId.Sys_Error));
			return message;
		}
		C1755_QualifyShowRankRespMessage resp = new C1755_QualifyShowRankRespMessage();
		resp.setPage(reqMsg.getPage());
		resp.setMaxPage(GameContext.getQualifyApp().getMaxRankPage());
		resp.setQualifyRoleInfoList(this.getQualifyRoleInfoList(roleQualifyList));
		return resp;
	}
	
	private List<QualifyRoleInfoItem> getQualifyRoleInfoList(List<QualifyRank> roleQualifyList) {
		List<QualifyRoleInfoItem> qualifyRoleInfoList = Lists.newArrayList();
		for (QualifyRank roleQualify : roleQualifyList) {
			if (null == roleQualify) {
				continue;
			}
			QualifyRoleInfoItem item = new QualifyRoleInfoItem();
			item.setBattleScore(roleQualify.getBattleScore());
			item.setHeroBattleList(GameContext.getQualifyApp().getQualifyHeroList(roleQualify));
			item.setRank(roleQualify.getRank());
			item.setRoleId(Integer.parseInt(roleQualify.getRoleId()));
			item.setRoleName(roleQualify.getRoleName());
			qualifyRoleInfoList.add(item);
		}
		return qualifyRoleInfoList;
	}

}
