package com.game.draco.app.quest.base;

public enum QuestTermType {
	
	UnKnow((byte)-1, false, "未知"),
	Goods((byte)0, true, "收集物品"),//检测背包
	KillMonster((byte)1, false, "杀怪"),
	Map((byte)2, false, "探索地图"),
	CollectPoint((byte)3, false, "采集物品"),//检测背包
	KillNpcFall((byte)4,false,"杀怪计数"),//杀怪时按几率计数，其实不掉落物品（提高性能）
	TriggerEvent((byte)5, false, "机关采集"),
	KillMonsterLimit((byte)6, false, "杀怪限制"),
	Attribute((byte)7, true, "特性"),//判断角色属性
	Role((byte)8, false, "敌对玩家"),
	KillMonsterCollect((byte)9, true, "杀怪收集物品"),//检测背包
	ChooseMenu((byte)10, false, "进行对话"),
	UseGoodsType((byte)11, false, "某点使用某类物品"),
	CopyPass((byte)12, false, "副本通关"),
	CopyMapPass((byte)13, false, "副本某地图通关"),
	MapRefreshNpc((byte)14, false, "地图刷怪波次"),
	
	//MapPoint((byte)4, false, "探索地图点"),
	//UseGoodsOnPoint((byte)6, false, "某点使用物品"),
	/*Strengthen((byte)11, false, "装备强化"),
	MountTrain((byte)12,false,"坐骑培养"),
	MountQuality((byte)13,false,"坐骑进阶"),
	Gemsynthesis((byte)14, false, "合成宝石"),
	Trans((byte)15, false, "使用变身"),
	SoulPractice((byte)16, false, "神兽元神修炼"),
	SoulEvolution((byte)17, false, "神兽进化"),
	ArenaJoin((byte)19, false, "挑战擂台赛"),
	ArenaWin((byte)20, false, "挑战擂台赛获胜"),
	FriendAdd((byte)21, false, "添加好友"),
	FriendNumber((byte)22, true, "好友数量"),//查找好友数量
	TowerEnter((byte)23, false, "挑战九重天"),
	TowerPass((byte)24, false, "通过九重天任意层"),
	TowerPassLimit((byte)25, false, "通过九重天第几关第几层"),*/
	//Recasting((byte)30, false, "装备洗练"),
	//KillCopyMonster((byte)31, false, "副本杀怪"),
	//RmQuestComplete((byte)33, true, "完成随机任务"),//查找当前随机任务进度
	//CopyCount((byte)35, true, "副本当日次数"),//查找副本今日完成数量
	//DegreeCount((byte)36, true, "活跃度当前数"),//查找活跃度今日完成数量
	
	;
	
	private final byte type ;
	private final boolean special;//任务进度需要特殊处理 QuestTerm.getCurrCount()
	private final String name;
	
	QuestTermType(byte type, boolean special, String name){
		this.type = type ;
		this.special = special;
		this.name = name;
	}
	
	public  byte getType(){
		return this.type ;
	}

	public String getName() {
		return name;
	}
	
	/**
	 * 任务进度需要特殊处理
	 * 不能直接从任务日志中取值
	 * @return
	 */
	public boolean isSpecial() {
		return special;
	}

	public static QuestTermType get(byte type){
		for(QuestTermType item : QuestTermType.values()){
			if(item.getType() == type){
				return item;
			}
		}
		return null;
	}
	
	/**
	 * 任务阶段是否需要收集物品
	 * @return
	 */
	public boolean isNeedGoods(){
		return CollectPoint == this || Goods == this || KillMonsterCollect == this;
	}
	
}
