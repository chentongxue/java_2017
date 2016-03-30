package sacred.alliance.magic.app.treasure;

import lombok.Data;
import sacred.alliance.magic.base.Result;
/**
 * 虚空漩涡（藏宝图）地点
 */
public @Data class TreasurePosResult extends Result {
	public final static byte POS_CARETE_SUCESS = 1; //藏宝点重新生成成功
	public final static byte POS_LEGAL = 2;//藏宝点合法 
	
	private byte posType;
}
