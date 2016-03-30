package sacred.alliance.magic.app.menu;

import lombok.Data;

public @Data class HintConfig {

	private short id;//编号
	private byte level;//开启等级
	private byte hintType;//开启时是否提示 1：提示  0：不提示
	private String hintInfo;//开启提示信息
	
	/**
	 * 功能开启时是否提示
	 * @return
	 */
	public boolean isOpenHint(){
		return 1 == this.hintType;
	}
	
}
