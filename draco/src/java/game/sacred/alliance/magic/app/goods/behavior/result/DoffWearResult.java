package sacred.alliance.magic.app.goods.behavior.result;

import lombok.Data;
import sacred.alliance.magic.domain.RoleGoods;

public @Data class DoffWearResult extends GoodsResult{
	
	private RoleGoods wearRoleGoods;
	private RoleGoods doffRoleGoods;
	private String expiredTime;
	
	public DoffWearResult setResult(byte ret) {
		this.result = ret;
		return this;
	}
	public DoffWearResult setInfo(String info) {
		this.info = info;
		return this;
	}
}
