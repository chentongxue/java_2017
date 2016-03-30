package sacred.alliance.magic.app.goods.behavior.param;

import lombok.Data;
import sacred.alliance.magic.base.StorageType;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.vo.RoleInstance;

public @Data class DoffWearParam extends AbstractParam{

	public DoffWearParam(RoleInstance role) {
		super(role);
	}
	
	private RoleGoods doffWearGoods;
	private StorageType storageType ;
	private int targetId ;
	
}
