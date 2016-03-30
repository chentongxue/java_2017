package sacred.alliance.magic.app.goods.behavior.param;

import lombok.Data;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.vo.RoleInstance;

public @Data class StrengthenParam extends AbstractParam{
	//获得强化信息
	public final static byte STRENGTHEN_INFO = 0;
	public final static byte STRENGTHEN_EXEC = 1;//操作 
	
	private byte operateType;
	private String paramInfo; //二次确认串
	private RoleGoods equipGoods;
	//private int strengthentype;
	private int targetId ;
	
	public StrengthenParam(RoleInstance role) {
		super(role);
	}

	
}
