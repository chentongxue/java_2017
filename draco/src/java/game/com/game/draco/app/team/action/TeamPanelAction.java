package com.game.draco.app.team.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.team.PlayerTeam;
import com.game.draco.app.team.Team;
import com.game.draco.app.team.vo.ApplyInfo;
import com.game.draco.app.team.vo.TeamPanelTargetType;
import com.game.draco.message.request.C1307_TeamPanelReqMessage;
import com.game.draco.message.request.C1316_TeamPanelIntimateReqMessage;
import com.game.draco.message.response.C1307_TeamPanelRespMessage;

public class TeamPanelAction extends BaseAction<C1307_TeamPanelReqMessage> {

	@Override
	public Message execute(ActionContext context, C1307_TeamPanelReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if (null == role) {
			return null;
		}
		C1307_TeamPanelRespMessage resp = new C1307_TeamPanelRespMessage();
		// 获取目标列表
		resp.setTargetList(GameContext.getTeamApp().getTeamPanelTargetTypeList(role));
		Team team = role.getTeam();
		if (null == team) {
			// 设置最大队伍人数为系统默认最大人数
			resp.setTargetType(TeamPanelTargetType.every.getType());
			resp.setNumber((byte) PlayerTeam.MAX_MEMBERS_NUM);
			return resp;
		}
		// 队伍最大人数
		resp.setNumber((byte) team.getMaxPlayerNum());
		PlayerTeam playerTeam = (PlayerTeam) team;
		resp.setTargetType(playerTeam.getTargetType());
		resp.setTargetId(playerTeam.getTargetId());
		// 判断队伍是否在发布中
		if (playerTeam.isPublish()) {
			resp.setPublish((byte) 1);
		}
		ApplyInfo applyInfo = GameContext.getTeamApp().getMatchApplyInfo(team.getTeamId());
		if (null != applyInfo) {
			resp.setMatching((byte) 1);
		}
		// 请求亲密度加成
		try {
			C1316_TeamPanelIntimateReqMessage messsage = new C1316_TeamPanelIntimateReqMessage();
			role.getBehavior().addCumulateEvent(messsage);
		} catch (Exception e) {
		}
		return resp;
	}

}
