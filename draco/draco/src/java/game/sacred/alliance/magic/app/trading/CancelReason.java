package sacred.alliance.magic.app.trading;

import com.game.draco.GameContext;

import sacred.alliance.magic.constant.TextId;

public enum CancelReason {
	rolecancel(TextId.TRADING_CANCEL_ROLE_CANCEL),
	timeout(TextId.TRADING_CANCEL_TIME_OUT),
	logout(TextId.TRADING_CANCEL_LOGOUT),
	bagfull(TextId.TRADING_CANCEL_BAG_FULL),
	emptytrading(TextId.TRADING_CANCEL_EMPTY_TRADING)
	;
	
	private final String tips ;
	
	CancelReason(String tips){
		this.tips = tips ;
	}

	public String getTips() {
		return GameContext.getI18n().getText(tips);
	}
	
	
}
