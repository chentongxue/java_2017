package sacred.alliance.magic.app.charge;

import com.game.draco.GameContext;

import sacred.alliance.magic.constant.TextId;

public enum ChargeStatus {
	
	Fail(0,TextId.CHARGE_FAILURE),
	Success(1,TextId.CHARGE_SUCCESS),
	;
	
	private final int type;
	private final String name;
	
	ChargeStatus(int type, String name){
		this.type = type;
		this.name = name;
	}

	public int getType() {
		return type;
	}

	public String getName() {
		return GameContext.getI18n().getText(this.name);
	}
	
	public static ChargeStatus get(int type){
		for(ChargeStatus item : ChargeStatus.values()){
			if(item.getType() == type){
				return item;
			}
		}
		return Fail;
	}
	
}
