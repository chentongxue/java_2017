package com.game.draco.app.team.vo;

import java.util.List;

import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.team.PlayerTeam;
import com.game.draco.message.item.TeamPanelTargetDetailItem;
import com.game.draco.message.response.C0002_ErrorRespMessage;

public class FieldTargetTypeLogic implements TargetTypeLogic {
	
	@Override
	public boolean countEnough(RoleInstance role, short targetId) {
		return true;
	}

	@Override
	public List<TeamPanelTargetDetailItem> getTeamPanelTargetDetailItemList() {
		return null;
	}

	@Override
	public TeamPanelTargetType getTeamPanelTargetType() {
		return TeamPanelTargetType.field;
	}

	@Override
	public Message targetForword(RoleInstance role, PlayerTeam team) {
		TeamTargetConfig config = GameContext.getTeamApp().getTeamTargetConfig(this.getTeamPanelTargetType().getType(), team.getTargetId());
		if (null == config) {
			C0002_ErrorRespMessage message = new C0002_ErrorRespMessage();
			message.setInfo(GameContext.getI18n().getText(TextId.ERROR_INPUT));
			return message;
		}
		// 前往逻辑
		GameContext.getForwardApp().forward(role, Short.parseShort(config.getParam()));
		return null;
	}

	@Override
	public String getTargetName(short targetId) {
		return this.getTeamPanelTargetType().getName();
	}

}
