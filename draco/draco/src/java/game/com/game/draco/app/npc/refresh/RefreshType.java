package com.game.draco.app.npc.refresh;

public enum RefreshType {
	original, //原始状态(新创建时)
	init, //初始状态
	death, //死亡刷新
	doing, //刷怪
	disappear,//到时消失
	none, //不做操作
	;
}
