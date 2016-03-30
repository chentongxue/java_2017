package sacred.alliance.magic.app.goods;

import sacred.alliance.magic.base.StorageType;
import sacred.alliance.magic.vo.RoleInstance;

public class GoddessEquipBackpack extends BaseEquipBackpack{

	public GoddessEquipBackpack(RoleInstance role, int gridCount) {
		super(role, gridCount);
	}

	@Override
	protected StorageType getStorageType(){
		return StorageType.goddess ;
	}

}
