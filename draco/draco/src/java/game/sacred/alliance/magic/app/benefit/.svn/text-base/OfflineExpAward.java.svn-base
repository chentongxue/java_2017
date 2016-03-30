package sacred.alliance.magic.app.benefit;

import lombok.Data;
import sacred.alliance.magic.base.AttributeType;

public @Data class OfflineExpAward {
	
	public static final float Multiple_Rat = 100f;//倍数的放大比例
	private short multiple;//倍数
	private byte attriType;//属性ID
	private float moneyMultiple;//消耗货币倍数 0表示免费领取
	private String btnName;//按钮名称
	private AttributeType attributeType;
	
	public void init(){
		this.attributeType = AttributeType.get(this.attriType);
	}
	
	/**
	 * 真实倍数（配置表中是放大100被填写的）
	 * @return
	 */
	public float getRealMultiple(){
		return this.multiple / Multiple_Rat;
	}
	
	/**
	 * 需要支付的钱数
	 * @param baseMoney 钱币基数
	 * @param offlineTime 离线时间
	 * @return
	 */
	public int getDeductMoney(float baseMoney , int offlineTime){
		double value = Math.ceil(baseMoney * offlineTime) * this.moneyMultiple;
		if(value > Integer.MAX_VALUE){
			value = Integer.MAX_VALUE;
		}
		return (int) value;
	}
	
	/**
	 * 需要支付的钱数
	 * @param config
	 * @param offlineTime
	 * @return
	 */
	public int getDeductMoney(OfflineExpConfig config, int offlineTime){
		return this.getDeductMoney(config.getBaseMoney(this.attributeType), offlineTime);
	}
	
	/**
	 * 需要付费领取
	 * @return
	 */
	public boolean needPayMoney(){
		return this.moneyMultiple > 0;
	}
}
