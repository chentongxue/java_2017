package sacred.alliance.magic.app.goods.behavior.param;

import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.vo.RoleInstance;

public class MosaicParam extends AbstractParam{
	public final static byte MOSAIC = 0;//获得二次确认信息
	public final static byte MOSAIC_EXEC = 1;//镶嵌操作
	
	private RoleGoods equipGoods;
	private RoleGoods gemGoods;
	private String param; // 二次确认字符串
	private byte operateType;
	
	public MosaicParam(RoleInstance role) {
		super(role);
	}

	public RoleGoods getEquipGoods() {
		return equipGoods;
	}
	public void setEquipGoods(RoleGoods equipGoods) {
		this.equipGoods = equipGoods;
	}
	public RoleGoods getGemGoods() {
		return gemGoods;
	}
	public void setGemGoods(RoleGoods gemGoods) {
		this.gemGoods = gemGoods;
	}
	public byte getOperateType() {
		return operateType;
	}
	public void setOperateType(byte operateType) {
		this.operateType = operateType;
	}
	public String getParam() {
		return param;
	}
	public void setParam(String param) {
		this.param = param;
	}
}
