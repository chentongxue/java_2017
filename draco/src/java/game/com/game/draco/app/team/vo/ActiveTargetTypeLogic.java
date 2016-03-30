package com.game.draco.app.team.vo;

import java.util.List;

import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.team.PlayerTeam;
import com.game.draco.message.item.TeamPanelTargetDetailItem;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.response.C0002_ErrorRespMessage;
import com.game.draco.message.response.C2300_ActivePanelRespMessage;

public class ActiveTargetTypeLogic implements TargetTypeLogic {
	
	@Override
	public boolean countEnough(RoleInstance role, short targetId) {
		return true;
	}

	@Override
	public List<TeamPanelTargetDetailItem> getTeamPanelTargetDetailItemList() {
		return GameContext.getTeamApp().getTeamPanelTargetDetailList(this.getTeamPanelTargetType().getType());
	}

	@Override
	public TeamPanelTargetType getTeamPanelTargetType() {
		return TeamPanelTargetType.active;
	}

	@Override
	public Message targetForword(RoleInstance role, PlayerTeam team) {
		// 如果是队长，打开活动列表并弹出指定活动
		if (team.isLeader(role)) {
			C2300_ActivePanelRespMessage message = (C2300_ActivePanelRespMessage) GameContext.getActiveApp().createActivePanelListMsg(role);
			message.setActiveId(team.getTargetId());
			return message;
		}
		// 获取目标配置
		TeamTargetConfig config = GameContext.getTeamApp().getTeamTargetConfig(this.getTeamPanelTargetType().getType(), team.getTargetId());
		if (null == config) {
			C0002_ErrorRespMessage message = new C0002_ErrorRespMessage();
			message.setInfo(GameContext.getI18n().getText(TextId.ERROR_INPUT));
			return message;
		}
		// 如果队员提示类型是提示
		if (config.getForwardType() == TeamPanelForwardType.hint.getType()) {
			C0003_TipNotifyMessage message = new C0003_TipNotifyMessage();
			message.setMsgContext(config.getBroadCast());
			return message;
		} else if (config.getForwardType() == TeamPanelForwardType.forward.getType()) {
			// 前往逻辑
			GameContext.getForwardApp().forward(role, Short.parseShort(config.getParam()));
			return null;
		}
		// 获取活动列表
		C2300_ActivePanelRespMessage message = (C2300_ActivePanelRespMessage) GameContext.getActiveApp().createActivePanelListMsg(role);
		message.setActiveId(team.getTargetId());
		return message;
	}

	@Override
	public String getTargetName(short targetId) {
		TeamTargetConfig config = GameContext.getTeamApp().getTeamTargetConfig(this.getTeamPanelTargetType().getType(), targetId);
		if (null == config) {
			return "";
		}
		return config.getTargetName();
	}

}
