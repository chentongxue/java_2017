package sacred.alliance.magic.app.trading;

import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.vo.RoleInstance;

public class TradingResult extends Result{

	protected RoleInstance who ;

	public RoleInstance getWho() {
		return who;
	}

	public void setWho(RoleInstance who) {
		this.who = who;
	}
	
	
}
