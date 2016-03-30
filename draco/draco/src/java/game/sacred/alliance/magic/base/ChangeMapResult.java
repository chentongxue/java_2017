package sacred.alliance.magic.base;

import com.game.draco.GameContext;

import sacred.alliance.magic.constant.TextId;

public enum ChangeMapResult {
	//1.成功 0.目标地图不存在 2.当前地图不允许离开 3.目标地图不允许进入
	ok(1,true,""),
	target_map_not_exist(0,false,TextId.target_map_not_exist),
	current_map_canot_exit(2,false,TextId.current_map_canot_exit),
	target_map_canot_enter(3,false,TextId.target_map_canot_enter)
	;
	
	
	private final int type ;
	private final boolean success ;
	private final String desc ;
	
	private ChangeMapResult(int type,boolean success,String desc){
		this.type = type ;
		this.success = success ;
		this.desc = desc ;
	}

	public boolean isSuccess() {
		return success;
	}

	public int getType() {
		return type;
	}

	public String getDesc() {
		return GameContext.getI18n().getText(this.desc);
	}
	
	
	
	
}
