package sacred.alliance.magic.app.pk;

import lombok.Data;
import sacred.alliance.magic.base.Result;

public @Data class PkFallResult extends Result{
	private int goodsId;
	private int goodsNum;
	private byte bindType;
}
