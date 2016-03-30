package sacred.alliance.magic.app.attri.config;

import sacred.alliance.magic.constant.ParasConstant;
import sacred.alliance.magic.util.KeySupport;
import lombok.Data;

public @Data class AttriBattleScore implements KeySupport<Byte>{
	private byte attriType; //属性类型
	private float factor; //系数A
	
	public void init() {
		this.factor /= ParasConstant.PERCENT_BASE_VALUE;
	}
	
	@Override
	public Byte getKey() {
		return this.attriType;
	}
}
