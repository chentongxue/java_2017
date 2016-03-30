package sacred.alliance.magic.app.treasure;

import lombok.Data;
import sacred.alliance.magic.base.Result;

public @Data class TreasurePosResult extends Result {
	public final static byte POS_CARETE_SUCESS = 1; //藏宝点重新生成成功
	public final static byte POS_LEGAL = 2;//藏宝点合法 
	
	private byte posType;
}
