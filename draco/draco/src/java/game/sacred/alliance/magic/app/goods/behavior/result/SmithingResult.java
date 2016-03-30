package sacred.alliance.magic.app.goods.behavior.result;

import lombok.Data;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.domain.RoleGoods;

public @Data class SmithingResult extends Result{

	private RoleGoods equipGoods;
	private int money ;
	private String affirmParam ;
	private boolean mustConfirm ;
	private byte index ;
	private byte col ;
	private int value ;
	
}
