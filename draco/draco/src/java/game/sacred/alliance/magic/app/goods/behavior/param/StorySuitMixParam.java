package sacred.alliance.magic.app.goods.behavior.param;

import sacred.alliance.magic.vo.RoleInstance;

public class StorySuitMixParam extends AbstractParam{

	public static final byte PARAM_EXCHANGE = 0;//兑换装备
	public static final byte PARAM_TAKE_NOW = 1;//马上获得
	public static final byte PARAM_TARGET_INFO = 2;//目标产物信息
	
	public StorySuitMixParam(RoleInstance role) {
		super(role);
	}

	private byte bagType;
	private String goodsInstanceId;
	private byte paramType;
	private short suitGroupId;//套装组ID
	private byte equipslotType;//装备部位
	private byte goodsLevel;//装备等级
	
	public byte getBagType() {
		return bagType;
	}
	public void setBagType(byte bagType) {
		this.bagType = bagType;
	}
	public String getGoodsInstanceId() {
		return goodsInstanceId;
	}
	public void setGoodsInstanceId(String goodsInstanceId) {
		this.goodsInstanceId = goodsInstanceId;
	}
	public byte getParamType() {
		return paramType;
	}
	public void setParamType(byte paramType) {
		this.paramType = paramType;
	}
	public short getSuitGroupId() {
		return suitGroupId;
	}
	public void setSuitGroupId(short suitGroupId) {
		this.suitGroupId = suitGroupId;
	}
	public byte getEquipslotType() {
		return equipslotType;
	}
	public void setEquipslotType(byte equipslotType) {
		this.equipslotType = equipslotType;
	}
	public byte getGoodsLevel() {
		return goodsLevel;
	}
	public void setGoodsLevel(byte goodsLevel) {
		this.goodsLevel = goodsLevel;
	}
	
}
