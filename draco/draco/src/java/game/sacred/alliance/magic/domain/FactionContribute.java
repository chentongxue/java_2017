package sacred.alliance.magic.domain;

import sacred.alliance.magic.base.SaveDbStateType;
import lombok.Data;

public @Data class FactionContribute {
	
	private String roleId;//角色ID
	private String factionId;//公会ID
	private int contribute;//贡献值
	private int totalContribute;//贡献值总数
	private SaveDbStateType saveDbStateType = SaveDbStateType.Initialize;//持久化状态（决定是否更新库）
	
}
