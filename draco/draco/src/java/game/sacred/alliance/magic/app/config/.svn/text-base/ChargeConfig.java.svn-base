package sacred.alliance.magic.app.config;

public class ChargeConfig extends PropertiesConfig {
	
	/**
	 * 获取充值比例（人民币单位：分）
	 * （如果1元=10元宝，应该配0.1，即100分*0.1=10元宝）
	 * @return
	 */
	public float getFeeRatioFen(){
		return Float.valueOf(this.getConfig("payRatioFen"));
	}
	
	/**
	 * 获取充值比例（人民币单位：元）
	 * 若1元=10元宝，则返回10
	 * @return
	 */
	public short getFeeRatioYuan(){
		return (short) (this.getFeeRatioFen() * 100);
	}
	
	/**
	 * 充值是否开启
	 * @return
	 */
	public boolean isPayOpen(){
		return 1 == Integer.valueOf(this.getConfig("payOpen"));
	}
	
	/**
	 * 充值是否进行白名单判断
	 * @return
	 */
	public boolean isPayWhiteIpVerify(){
		return 1 == Integer.valueOf(getConfig("payWhiteIpVerify"));
	}
	
	/**
	 * 充值记录显示的条数
	 * @return
	 */
	public int getChargeRecordShowSize(){
		return Integer.valueOf(this.getConfig("chargeRecordShowSize"));
	}
	
	/**
	 * 使用moogame流水号的渠道
	 * @return
	 */
	public String getUseMoogameIdChannels(){
		return this.getConfig("useMoogameIdChannels");
	}
	
	public String getRecordShowGameMoneyChannels(){
		return this.getConfig("recordShowGameMoneyChannels");
	}
	
	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}
	
	
}
