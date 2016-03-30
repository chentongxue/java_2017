package com.game.draco.app.forward.logic;

public enum ForwardLogicType {

	/*
	1	打开副本面板
	2	打开活动面板
	3	点击菜单进入功能
	4	打开公会副本ID
	5	神秘商店
	6	兑换
	7	打开坐骑面板
	8	打开宠物面板
	9	客户端处理
	10	好友
	11	公会捐献
	12	宠物抢夺
	13  抽卡
	14  活跃度
	15  寻路
	16  充值面板
	17  副本类型
	18  公会拍卖
	19  文本提升
	20  随身商店
	*/
	
	copy((byte)1),
	active((byte)2),
	menu((byte)3),
	union_copy((byte)4),
	shop_secret((byte)5),
	exchange((byte)6),
	horse_panel((byte)7),
	pet_panel((byte)8),
	client_logic((byte)9),
	friend((byte)10),
	union_donate((byte)11),
	pet_plunder((byte)12),
	choice_card((byte)13),
	daily_play((byte)14),
	path_to_point((byte)15),
	charge_panel((byte)16),
	copy_type((byte)17),
	union_Auction((byte)18),
	text_tips((byte)19),
	shop_anytime((byte)20),
	;
	
	
	private final byte type ;
	
	ForwardLogicType(byte type){
		this.type = type ;
	}

	public byte getType() {
		return type;
	}

	
}
