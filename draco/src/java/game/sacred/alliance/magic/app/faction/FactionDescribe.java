package sacred.alliance.magic.app.faction;

import lombok.Data;

public @Data class FactionDescribe {
	
	/**
	 * 门派说明类型
	 * 0：门派信息 1：门派成员 2：门派列表 3：申请信息
	 */
	private byte type;
	private String describe;//说明信息
}
