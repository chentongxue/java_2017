package sacred.alliance.magic.app.goods.behavior.param;

import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.vo.RoleInstance;

public class StrengthenParam extends AbstractParam{
	//获得强化信息
	public final static byte STRENGTHEN_INFO = 0;
	public final static byte STRENGTHEN_EXEC = 1;//操作 
	public final static byte STRENGTHEN_ONEKEY = 2; //一键操作
	
	private byte operateType;
	private String paramInfo; //二次确认串
	private RoleGoods equipGoods;
	private int strengthentype;
	
	public StrengthenParam(RoleInstance role) {
		super(role);
	}

	public RoleGoods getEquipGoods() {
		return equipGoods;
	}
	public void setEquipGoods(RoleGoods equipGoods) {
		this.equipGoods = equipGoods;
	}
	public int getStrengthentype() {
		return strengthentype;
	}
	public void setStrengthentype(int strengthentype) {
		this.strengthentype = strengthentype;
	}
	public String getParamInfo() {
		return paramInfo;
	}
	public void setParamInfo(String paramInfo) {
		this.paramInfo = paramInfo;
	}
	public byte getOperateType() {
		return operateType;
	}
	public void setOperateType(byte operateType) {
		this.operateType = operateType;
	}
}
