package sacred.alliance.magic.app.attri;

public class AttriItem implements Cloneable {
	private byte attriTypeValue;//属性类型
	private float value;//属性改变量(具体值)
	private float precValue;//属性改变量(百分比[0-1])
	
	public AttriItem() {
		super();
	}

	@Override
	public AttriItem clone(){
		return new AttriItem(this.attriTypeValue,this.value,this.precValue);
	}
	
	public static AttriItem build(String str){
		if(null == str|| 0==str.trim().length()){
			return null ;
		}
		String[] ss = str.split(":");
		if(null == ss || ss.length != 3){
			return null ;
		}
		return new AttriItem(Byte.parseByte(ss[0]),
				Float.parseFloat(ss[1]),Float.parseFloat(ss[2]));
	}
	
	public String toStoreString(){
		return attriTypeValue + ":" + value + ":" + precValue ;
	}
	
	public AttriItem(byte attriTypeValue,float value,float precValue){
		this.precValue = precValue;
		this.value = value;
		this.attriTypeValue = attriTypeValue;
	} 
	
	public boolean isEmpty(){
		return value==0 && this.precValue == 0 ;
	}
	/**
	 * 
	 * @param attriTypeValue
	 * @param value
	 * @param isPrec: true:表示value是个值;false:表示value是个百分比
 	 */
	public AttriItem(byte attriTypeValue,int value,boolean isPrec){
		if(isPrec){
			this.precValue = value;
		}else{
			this.value = value;
		}
		this.attriTypeValue = attriTypeValue;
	}
	
	
	public AttriItem(byte attriTypeValue,float value,boolean isPrec){
		if(isPrec){
			this.precValue = value;
		}else{
			this.value = (int)value;
		}
		this.attriTypeValue = attriTypeValue;
	}
	
	/**
	 * 是否包括概率
	 * @return
	 */
	public boolean isContainPrec(){
		return (float)0 != this.precValue ;
	}
	
	
	public static AttriItem merge(AttriItem... attriItems){
		int value = 0;
		float precValue = 0;
		byte attriTypeValue = 0 ;
		boolean has = false ;
		for(AttriItem attriItem : attriItems){
			if(null == attriItem){
				continue;
			}
			has = true ;
			attriTypeValue = attriItem.getAttriTypeValue();
			value += attriItem.getValue();
			precValue += attriItem.getPrecValue();
		}
		return has?new AttriItem(attriTypeValue,value,precValue):null;
	}
	
	public static AttriItem merge(byte attriTypeValue,float origValue,AttriItem... attriItems){
		int value = 0;
		float precValue = 0;
		for(AttriItem attriItem : attriItems){
			if(null == attriItem){
				continue;
			}
			value += attriItem.getValue();
			precValue += attriItem.getPrecValue();
		}
		value += origValue;
		return new AttriItem(attriTypeValue,value,precValue);
	}
	
	public int calctValue(){
		float calctValue = this.value;
		calctValue *= (1 + this.precValue);
		return (int)calctValue;
	}
	public byte getAttriTypeValue() {
		return attriTypeValue;
	}
	public void setAttriTypeValue(byte attriTypeValue) {
		this.attriTypeValue = attriTypeValue;
	}
	public float getValue() {
		return value;
	}
	public void setValue(float value) {
		this.value = value;
	}
	public float getPrecValue() {
		return precValue;
	}
	public void setPrecValue(float precValue) {
		this.precValue = precValue;
	}
}