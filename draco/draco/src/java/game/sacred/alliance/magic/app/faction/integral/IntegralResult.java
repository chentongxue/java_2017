package sacred.alliance.magic.app.faction.integral;

import lombok.Data;
import sacred.alliance.magic.base.Result;

public @Data class IntegralResult extends Result{
    
	public static final byte CONFIRM = -1 ;
	private int  effectValue ;
	
	public boolean isMustConfirm(){
		return this.result == CONFIRM ;
	}
	
	public void setMustConfirm(){
		this.result = CONFIRM ;
	}
}
