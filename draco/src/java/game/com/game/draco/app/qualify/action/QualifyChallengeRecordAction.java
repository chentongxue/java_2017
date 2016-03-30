package com.game.draco.app.qualify.action;

import java.util.Date;
import java.util.List;

import com.game.draco.GameContext;
import com.game.draco.app.qualify.QualifyAppImpl;
import com.game.draco.app.qualify.domain.ChallengeRecord;
import com.game.draco.message.item.QualifyRecordItem;
import com.game.draco.message.request.C1756_QualifyChallengeRecordReqMessage;
import com.game.draco.message.response.C1756_QualifyChallengeRecordRespMessage;
import com.google.common.collect.Lists;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

public class QualifyChallengeRecordAction extends BaseAction<C1756_QualifyChallengeRecordReqMessage> {

	private static final int TIME_INT = 1000;
	private Date nowDate;

	@Override
	public Message execute(ActionContext context, C1756_QualifyChallengeRecordReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if (null == role) {
			return null;
		}
		List<ChallengeRecord> recordList = GameContext.getQualifyApp().getChallengeRecordList(role);
		C1756_QualifyChallengeRecordRespMessage resp = new C1756_QualifyChallengeRecordRespMessage();
		resp.setQualifyRecordList(this.getQualifyRecordList(recordList));
		return resp;
	}

	private List<QualifyRecordItem> getQualifyRecordList(List<ChallengeRecord> recordList) {
		if (Util.isEmpty(recordList)) {
			return null;
		}
		// 获得服务器当前时间
		this.nowDate = new Date();
		List<QualifyRecordItem> itemList = Lists.newArrayList();
		for (int i = recordList.size() - 1; i >= 0; i--) {
			ChallengeRecord record = recordList.get(i);
			if (null == record) {
				continue;
			}
			itemList.add(this.createQualifyRecordItem(record));
		}
		return itemList;
	}

	private QualifyRecordItem createQualifyRecordItem(ChallengeRecord record) {
		QualifyRecordItem item = new QualifyRecordItem();
		item.setChallengeTime(this.getChallengeTimeToNow(record.getChallengeTime()));
		short rank = record.getCurrRank();
		item.setRank(rank >= QualifyAppImpl.OUT_RANK ? 0 : rank);
		item.setRoleName(record.getRoleName());
		item.setStatus(record.getStatus());
		item.setType(record.getType());
		return item;
	}

	private int getChallengeTimeToNow(int challengeTime) {
		return (int) (nowDate.getTime() / TIME_INT) - challengeTime;
	}

}
