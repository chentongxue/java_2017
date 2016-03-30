package sacred.alliance.magic.app.goods.behavior.param;

import lombok.Data;
import sacred.alliance.magic.vo.RoleInstance;

public @Data class EquipUpgradeStarParam extends AbstractParam{

	public EquipUpgradeStarParam(RoleInstance role) {
		super(role);
	}
	
	private byte bagType ;
	private String goodsInstanceId ;
	private int targetId ;
	
}
