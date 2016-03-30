package com.game.draco.app.qualify.action;

import java.util.List;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.app.count.type.CountType;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.qualify.QualifyAppImpl;
import com.game.draco.app.qualify.domain.QualifyRank;
import com.game.draco.message.item.QualifyRoleInfoItem;
import com.game.draco.message.request.C1751_QualifyInfoReqMessage;
import com.game.draco.message.response.C0002_ErrorRespMessage;
import com.game.draco.message.response.C1751_QualifyInfoRespMessage;
import com.google.common.collect.Lists;

public class QualifyInfoAction extends BaseAction<C1751_QualifyInfoReqMessage> {
	
	@Override
	public Message execute(ActionContext context, C1751_QualifyInfoReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if (null == role) {
			return null;
		}
		C1751_QualifyInfoRespMessage resp = new C1751_QualifyInfoRespMessage();
		short rank = GameContext.getQualifyApp().getRoleRank(role);
		resp.setRank(rank >= QualifyAppImpl.OUT_RANK ? 0 : rank);
		resp.setGiveGiftInfo(GameContext.getQualifyApp().getNextGiveGiftStrInfo());
		// 获得挑战对手
		List<QualifyRank> roleQualifyList = GameContext.getQualifyApp().getChallengeOpponents(role);
		if (Util.isEmpty(roleQualifyList)) {
			C0002_ErrorRespMessage message = new C0002_ErrorRespMessage();
			message.setInfo(GameContext.getI18n().getText(TextId.Sys_Error));
			return message;
		}
		List<QualifyRoleInfoItem> list = Lists.newArrayList();
		for (QualifyRank roleQualify : roleQualifyList) {
			if (null == roleQualify) {
				continue;
			}
			QualifyRoleInfoItem item = new QualifyRoleInfoItem();
			item.setBattleScore(roleQualify.getBattleScore());
			item.setHeroBattleList(GameContext.getQualifyApp().getQualifyBattleHero(roleQualify));
			item.setRank(roleQualify.getRank());
			item.setRoleId(Integer.parseInt(roleQualify.getRoleId()));
			item.setRoleName(roleQualify.getRoleName());
			item.setLevel((byte) roleQualify.getRoleLevel());
			list.add(item);
		}
		resp.setQualifyRoleInfoList(list);
		resp.setChallengeCDTime(GameContext.getQualifyApp().getChallengeCDTime(role));
		byte maxChallenge = GameContext.getQualifyApp().getMaxChallengeTimes(role);
		resp.setChallengeTimes((byte)(maxChallenge - role.getRoleCount().getRoleTimesToByte(CountType.ChallengeTimes)));//getChallengeTimes());
		resp.setMaxChallengeTimes(maxChallenge);
		resp.setShopId(GameContext.getQualifyApp().getShopId());
		return resp;
	}
	
}
