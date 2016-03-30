package com.game.draco.app.team.vo;

import java.util.List;

import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.copy.config.CopyConfig;
import com.game.draco.app.copy.vo.CopyType;
import com.game.draco.app.team.PlayerTeam;
import com.game.draco.message.item.TeamPanelTargetDetailItem;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.response.C0002_ErrorRespMessage;

public class CopyTargetTypeLogic implements TargetTypeLogic {
	
	@Override
	public boolean countEnough(RoleInstance role, short targetId) {
		CopyConfig copyConfig = GameContext.getCopyLogicApp().getCopyConfig(targetId);
		if (null == copyConfig) {
			return false;
		}
		return GameContext.getCopyLogicApp().isEnterCountEnough(role, copyConfig);
	}

	@Override
	public List<TeamPanelTargetDetailItem> getTeamPanelTargetDetailItemList() {
		return GameContext.getTeamApp().getTeamPanelTargetDetailList(this.getTeamPanelTargetType().getType());
	}

	@Override
	public TeamPanelTargetType getTeamPanelTargetType() {
		return TeamPanelTargetType.copy;
	}

	@Override
	public Message targetForword(RoleInstance role, PlayerTeam team) {
		// 获取目标配置
		TeamTargetConfig config = GameContext.getTeamApp().getTeamTargetConfig(this.getTeamPanelTargetType().getType(), team.getTargetId());
		if (null == config) {
			C0002_ErrorRespMessage message = new C0002_ErrorRespMessage();
			message.setInfo(GameContext.getI18n().getText(TextId.ERROR_INPUT));
			return message;
		}
		// 如果是队长，打开活动列表并弹出指定活动
		if (!team.isLeader(role)) {
			C0003_TipNotifyMessage message = new C0003_TipNotifyMessage();
			message.setMsgContext(config.getBroadCast());
			return message;
		}
		// 如果是队长，打开活动列表并弹出指定活动
		return GameContext.getCopyLogicApp().getCopyPanelRespMessage(role, team.getTargetId(), CopyType.team.getType());
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
