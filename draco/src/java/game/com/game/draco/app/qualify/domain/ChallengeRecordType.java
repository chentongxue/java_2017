package com.game.draco.app.qualify.domain;

public enum ChallengeRecordType {

	ChallengeLose((byte) 0, "挑战失败"), 
	ChallengeWin((byte) 1, "挑战胜利"),
	BeChallengeLost((byte) 2, "被挑战失败"),
	BeChallengeWin((byte) 3, "被挑战胜利")
	
	;

	private final byte type;
	private final String name;

	ChallengeRecordType(byte type, String name) {
		this.name = name;
		this.type = type;
	}

	public byte getType() {
		return type;
	}

	public String getName() {
		return name;
	}

	public static ChallengeRecordType get(byte type) {
		for (ChallengeRecordType item : ChallengeRecordType.values()) {
			if (item.getType() == type) {
				return item;
			}
		}
		return null;
	}
}
