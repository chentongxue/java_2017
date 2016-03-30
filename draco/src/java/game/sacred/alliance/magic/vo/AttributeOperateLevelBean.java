package sacred.alliance.magic.vo;

import lombok.Data;
import sacred.alliance.magic.base.AttributeType;

public @Data class AttributeOperateLevelBean {

	private AttributeType attrType;
	private int value;
	private float precValue;
	private int minLevel;
	private int maxLevel;
	
	public AttributeOperateLevelBean(AttributeType attrType, int value,int minLevel,int maxLevel){
		this.attrType = attrType;
		this.value = value;
		this.minLevel = minLevel;
		this.maxLevel = maxLevel;
	}
	
	public AttributeOperateLevelBean(AttributeType attrType, int value, float precValue){
		this.attrType = attrType;
		this.value = value;
		this.precValue = precValue;
	}
	
}
