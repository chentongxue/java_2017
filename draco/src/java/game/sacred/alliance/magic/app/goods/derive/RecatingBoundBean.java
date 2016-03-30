package sacred.alliance.magic.app.goods.derive;

import lombok.Data;
import sacred.alliance.magic.util.Util;

public @Data class RecatingBoundBean {
	
	private int minValue;//最小值
	private int maxValue;//最大值
	private int qualityType;//品质
	
	public RecatingBoundBean(){
		
	}
	
	public RecatingBoundBean(int minValue, int maxValue, int qualityType){
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.qualityType = qualityType;
	}
	
	public boolean isSuitValue(int value){
		return value >= this.minValue && value <= this.maxValue;
	}
	
	public int randomValue(){
		return Util.randomInt(this.minValue, this.maxValue);
	}
	
}
