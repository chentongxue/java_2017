package com.game.draco.app.quest.config;

import sacred.alliance.magic.constant.Cat;

public enum QuestXlsType {
	
	Goods(1,"收集物品","goods"),
    KillMonster(2,"杀怪","kill_monster"),
    ChooseMenu(3,"选择菜单","dialogue"),
    NpcFall(4,"杀怪收集物品","kill_fall"),
    TriggerEvent(5,"触发机关","trigger_event"),
    KillMonsterLimit(6,"杀怪限制","kill_limit"),
    KillRole(7,"杀人","kill_role"),
    UseGoodsType(8,"使用某类物品","use_goods_type"),
    MapEnter(9,"探索地图","map_enter"),
    CopyPass(10,"副本通关","copy_pass"),
	CopyMapPass(11,"副本地图通关","copy_map_pass"),
	
	;
	
	private final int type;
	private final String name;
	private final String sheetName;
	
	QuestXlsType(int type, String name, String sheetName){
		this.type = type;
		this.name = name;
		this.sheetName = sheetName;
	}
	
	public int getType(){
		return type;
	}
	
    public String getName() {
		return name;
	}
    
	public String getSheetName() {
		return sheetName;
	}

	public static QuestXlsType get(int type){
		for(QuestXlsType item : QuestXlsType.values()){
			if(item.getType() == type){
				return item;
			}
		}
		return null;
    }
	
	/**
	 * 获取任务配置表的sheet名称
	 * 格式：名称1,名称2,名称3
	 * @return
	 */
	public static String getSheetNames(){
		String value = "" ;
		String cat = "" ;
		for(QuestXlsType item : QuestXlsType.values()){
			value += (cat + item.getSheetName());
			cat = Cat.comma ;
		}
		return value ;
	}
	
}
