package com.game.draco.app.team.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.team.PlayerTeam;
import com.game.draco.app.team.Team;
import com.game.draco.app.team.vo.ApplyInfo;
import com.game.draco.message.request.C1308_TeamPanelLockReqMessage;
import com.game.draco.message.response.C1308_TeamPanelLockRespMessage;

public class TeamPanelLockAction extends BaseAction<C1308_TeamPanelLockReqMessage> {

	@Override
	public Message execute(ActionContext context, C1308_TeamPanelLockReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if (null == role) {
			return null;
		}
		int maxNumber = reqMsg.getNumber();
		C1308_TeamPanelLockRespMessage resp = new C1308_TeamPanelLockRespMessage();
		// 验证数据的合法性（不能小于两个人，不能大于四个人）
		if (maxNumber < 2 || maxNumber > PlayerTeam.MAX_MEMBERS_NUM) {
			resp.setInfo(GameContext.getI18n().getText(TextId.ERROR_INPUT));
			return resp;
		}
		// 如果玩家没有队伍
		Team team = role.getTeam();
		if (null == team || team.getPlayerNum() > reqMsg.getNumber()) {
			resp.setInfo(GameContext.getI18n().getText(TextId.ERROR_INPUT));
			return resp;
		}
		if (!team.isLeader(role)) {
			resp.setInfo(GameContext.getI18n().getText(TextId.Team_Copy_Lader_Can_Apply));
			return resp;
		}
		// 如果队伍已发布或在匹配队列中，不允许修改队伍人数（客户端控制）
		PlayerTeam playerTeam = (PlayerTeam) team;
		if (playerTeam.isPublish()) {
			resp.setInfo(GameContext.getI18n().getText(TextId.ERROR_INPUT));
			return resp;
		}
		ApplyInfo applyInfo = GameContext.getTeamApp().getMatchApplyInfo(team.getTeamId());
		if (null != applyInfo) {
			resp.setInfo(GameContext.getI18n().getText(TextId.ERROR_INPUT));
			return resp;
		}
		// 改变队伍最大人数
		playerTeam.setNumber(maxNumber);
		resp.setStatus((byte) 1);// 标记成功
		resp.setNumber((byte) maxNumber);
		return resp;
	}

}
