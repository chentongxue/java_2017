package sacred.alliance.magic.app.map;

public enum MapProperty {
	
	canRolePk(0),
	showExit(1),
	canOnHorse(2),
	canSwitchHero(3),//是否可以更换出战英雄
	canUseFood(4),
	canHpHealth(5),
	canChange3Hero(6), //是否可英雄上阵
	canShowFatigue(7), //是否显示疲劳度
	canHook(8),//是否可挂机
	canQuickVoice(9), //是否可快捷语音
	canShowQuest(10),//是否显示任务
	canShowTeam(11), //是否显示队伍
    showWorldMap(12),//是否显示世界地图
    showMenu(13),//是否显示功能菜单
    showFriendNpcHp(14),//是否显示友好npc的血条
	;

	private final int type ;
	
	private MapProperty(int type){
		this.type = type ;
	}

	public int getType() {
		return type;
	}
	
	
}
