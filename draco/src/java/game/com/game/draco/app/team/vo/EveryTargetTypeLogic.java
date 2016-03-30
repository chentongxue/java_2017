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

public class EveryTargetTypeLogic implements TargetTypeLogic {
	
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
		return TeamPanelTargetType.every;
	}

	@Override
	public Message targetForword(RoleInstance role, PlayerTeam team) {
		TeamTargetConfig config = GameContext.getTeamApp().getTeamTargetConfig(this.getTeamPanelTargetType().getType(), team.getTargetId());
		if (null == config) {
			C0002_ErrorRespMessage message = new C0002_ErrorRespMessage();
			message.setInfo(GameContext.getI18n().getText(TextId.ERROR_INPUT));
			return message;
		}
		C0003_TipNotifyMessage message = new C0003_TipNotifyMessage();
		message.setMsgContext(config.getBroadCast());
		return message;
	}

	@Override
	public String getTargetName(short targetId) {
		return this.getTeamPanelTargetType().getName();
	}

}
