package sacred.alliance.magic.app.faction.godbeast;

import com.game.draco.GameContext;

import lombok.Data;
import sacred.alliance.magic.util.Log4jManager;

public @Data class FactionSoulInspireConfig {
	private int id;
	private int flyLevel;
	private int level;
	private String functionName;
	private short buffId;
	private String buffName;
	
	public void check(String info){
		if(this.buffId <= 0){
			this.checkFail(info + "buffId=" + this.buffId + ",buffId is error.");
		}
		if(null == GameContext.getBuffApp().getBuff(this.buffId)){
			this.checkFail(info + "buffId=" + this.buffId + ",buffId is not exist.");
		}
	}
	
	private void checkFail(String info){
		Log4jManager.CHECK.error(info);
		Log4jManager.checkFail();
	}
}
