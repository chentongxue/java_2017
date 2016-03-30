package com.game.draco.app.qualify.domain;

import java.util.List;

import com.google.common.collect.Lists;

import lombok.Data;

public @Data class RoleQualifyRecord {

	private String roleId;
	private List<ChallengeRecord> challengeRecordList = Lists.newArrayList();
	
	public void addChallengeRecord(ChallengeRecord record) {
		if (this.challengeRecordList.size() >= 10) {
			this.challengeRecordList.remove(0);
		}
		this.challengeRecordList.add(record);
	}
	
}
