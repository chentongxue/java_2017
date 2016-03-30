package sacred.alliance.magic.app.faction.godbeast;

import com.game.draco.GameContext;

import lombok.Data;
import sacred.alliance.magic.util.Log4jManager;

public @Data class FactionSoulInspireBuffConfig {
	private short buffId;
	private int buffLevel;
	private byte inspireType;
	private byte costType;
	private int costValue;
	private int ratio;
	private String successInfo;
	private String failInfo;
	private String curDetail;
	private String nextDetail;
	private int needLevel;
	
	public void check(String info){
		if(null == FactionSoulInspireCostType.get(this.costType)){
			this.checkFail(info + "costType=" + this.costType + ",costType is not exist.");
		}
		if(null == FactionSoulInspireType.get(this.inspireType)){
			this.checkFail(info + "inspireType=" + this.inspireType + ",inspireType is not exist.");
		}
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
