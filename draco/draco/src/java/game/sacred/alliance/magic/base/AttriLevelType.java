package sacred.alliance.magic.base;

public enum AttriLevelType {
	orig("原始属性",false),
	added("附加属性",true),
	flag("标识属性",false),
	;
	private final String name;
	private final boolean reCalct ;
	
	AttriLevelType(String name,boolean reCalct){
		this.name = name;
		this.reCalct = reCalct;
	}
	public boolean verify(AttributeType attriType){
		return (null != attriType && attriType.getAttriLevelType() == this);
	}
	
	public boolean verify(byte type){
		return this.verify(AttributeType.get(type));
	}
	
	public String getName() {
		return name;
	}
	public boolean isReCalct() {
		return reCalct;
	}
	
}
