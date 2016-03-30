package com.game.draco.app.dailyplay;

public enum DailyPlayType {
	/*
	1	老异步竞技场（一战到底）
	2	英雄试炼
	3	副本
	4	炼金
	5	好友点赞
	6	宠物抢夺
	7	公会捐献
	8	参与阵营战
	9	参与王者比赛
	10	参与神仙福地
	11	参与杀肉山
	12	抽卡
	13	好友传功
	14	日常任务
	15  竞技场（排位赛）
	16  通关某类型副本
	17  击杀藏宝图boss
	18  生存战场
	*/
	
	async_arena((byte)1),
	hero_arena((byte)2),
	copy_map((byte)3),
	alchemy((byte)4),
	friend_praise((byte)5),
	pet_plunder((byte)6),
	union_donate((byte)7),
	camp_war((byte)8),
	arena_1v1((byte)9),
	angel_chest((byte)10),
	boss_dps((byte)11),
	choice_card((byte)12),
	friend_transmission((byte)13),
	quest_poker((byte)14),
	qualify((byte)15),
	copy_type_map((byte)16),
	kill_treasure_boss((byte)17),
	survival_battle((byte)18)
	;

	private final byte type ;
	
	DailyPlayType(byte type){
		this.type = type ;
	}

	public byte getType() {
		return type;
	}
	
	
}
