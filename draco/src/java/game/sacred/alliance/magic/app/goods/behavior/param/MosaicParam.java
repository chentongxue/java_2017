package sacred.alliance.magic.app.goods.behavior.param;

import lombok.Data;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.vo.RoleInstance;

public @Data class MosaicParam extends AbstractParam{
	
	public MosaicParam(RoleInstance role) {
		super(role);
	}
	
	private RoleGoods equipGoods;// 装备
	private RoleGoods gemGoods;// 符文
	private int targetId;// 英雄ID
	private byte hole;// 孔位
	
}
