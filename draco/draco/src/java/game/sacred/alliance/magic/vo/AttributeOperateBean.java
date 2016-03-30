package sacred.alliance.magic.vo;

import lombok.Data;
import sacred.alliance.magic.base.AttributeType;

public @Data class AttributeOperateBean {

	private AttributeType attrType;
	private int value;
	private float precValue;
	
	public AttributeOperateBean(AttributeType attrType, int value){
		this.attrType = attrType;
		this.value = value;
	}
	
	public AttributeOperateBean(AttributeType attrType, int value, float precValue){
		this.attrType = attrType;
		this.value = value;
		this.precValue = precValue;
	}
	
}
