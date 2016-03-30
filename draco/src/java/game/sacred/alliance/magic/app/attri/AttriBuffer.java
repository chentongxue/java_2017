package sacred.alliance.magic.app.attri;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import sacred.alliance.magic.base.AttributeType;
public class AttriBuffer {
	private static final float ZERO_VALUE = 0.0f ;
	private Map<Byte,AttriItem> map = new HashMap<Byte,AttriItem>();
	
	public interface Filter{
		boolean filter(byte attriType);
	}
	
	public static AttriBuffer build(String str){
		if(null == str || 0 == str.trim().length()){
			return null ;
		}
		//1:2:2;3:4:5
		String[] ss = str.split(";");
		if(null == ss || 0 == ss.length){
			return null ;
		}
		AttriBuffer buffer = AttriBuffer.createAttriBuffer();
		for(String s:ss){
			AttriItem item = AttriItem.build(s);
			if(null == item){
				continue ;
			}
			buffer.append(item);
		}
		return buffer ;
	}
	
	public String toStoreString(){
		if(null == map || 0==map.size()){
			return "" ;
		}
		StringBuffer buffer = new StringBuffer("");
		String cat = "" ;
		for(AttriItem item : map.values()){
			if(null == item){
				continue ;
			}
			buffer.append(cat).append(item.toStoreString());
			cat = ";" ;
		}
		return buffer.toString();
	}
	
	public AttriItem getAttriItem(AttributeType attriType){
		if(null == attriType){
			return null ;
		}
		return this.map.get(attriType.getType());
	}
	
	public AttriItem removeAttriItem(AttributeType attriType){
		if(null == attriType){
			return null ;
		}
		return this.map.remove(attriType.getType());
	}
	
	public AttriBuffer filter(Filter filter){
		AttriBuffer buffer = AttriBuffer.createAttriBuffer() ;
		if(null == filter){
			return buffer.append(this) ;
		}
		 
		for(AttriItem item : map.values()){
			if(null == item || !filter.filter(item.getAttriTypeValue())){
				continue ;
			}
			buffer.append(item);
		}
		return buffer ;
	}
	
	public boolean isEmpty(){
		return null == map || 0 == map.size() ;
	}
	public AttriBuffer remove(AttributeType attriType){
		if(null == attriType){
			return this ;
		}
		return this.remove(attriType.getType());
	}
	
	public AttriBuffer remove(byte attriType){
		map.remove(attriType);
		return this ;
	}
	
	public AttriBuffer clear(){
		map.clear();
		return this;
	}

	public AttriBuffer append(AttributeType attriType,float changeValue){
		return append(attriType.getType(), changeValue);
	}
	
	public AttriBuffer append(byte attriTypeValue,float changeValue){
		return this.append(attriTypeValue, changeValue, 0);
	}
	/**
	 * 
	 * @param attriType
	 * @param changeValue
	 * @param isPrec 是否是百分比
	 * @return
	 */
	public AttriBuffer append(AttributeType attriType,float changeValue,boolean isPrec){
		return this.append(attriType.getType(), changeValue, isPrec);
	}
	
	public AttriBuffer append(Collection<AttriItem> list){
		if(null == list){
			return this ;
		}
		for(AttriItem ai : list){
			this.append(ai);
		}
		return this ;
	}
	
	public AttriBuffer append(AttriItem ai){
		if(null == ai || ai.isEmpty() ){
			return this ;
		}
		this.append(ai.getAttriTypeValue(), ai.getValue(), ai.getPrecValue());
		return this ;
	}
	
	public AttriBuffer append(byte attriTypeValue,float changeValue,boolean isPrec){
		if( this.isZero(changeValue)){
			return this ;
		}
		if(isPrec){
			append(attriTypeValue, 0, changeValue);
		}else{
			append(attriTypeValue, changeValue, 0);
		}
		return this;
	}
	
	private boolean isZero(float value){
		return ZERO_VALUE == value ;
	}
	
	public AttriBuffer reverse(){
		for(AttriItem item : map.values()){
			item.setValue(item.getValue()*-1);
			item.setPrecValue(item.getPrecValue()*-1);
		}
		return this ;
	}
	
	public AttriBuffer rate(float rate){
		for(AttriItem item : map.values()){
			item.setValue(item.getValue()*rate);
			item.setPrecValue(item.getPrecValue()*rate);
		}
		return this ;
	}
	
	public AttriBuffer precToValue(){
		for(AttriItem item : map.values()){
			float prec = item.getPrecValue() ;
			if(prec <=0.0){
				continue ;
			}
			item.setValue(item.getValue()*(1+ prec));
			item.setPrecValue(0);
		}
		return this ;
	}
	
	
	public AttriBuffer append(AttributeType attriType,float value,float precValue){
		if(null == attriType || (this.isZero(value) && this.isZero(precValue))){
			return this ;
		}
		AttriItem attriItem = map.get(attriType.getType());
		if(null != attriItem){
			attriItem.setValue(attriItem.getValue() + value);
			attriItem.setPrecValue(attriItem.getPrecValue() + precValue);
		}else{
			map.put(attriType.getType(), new AttriItem(attriType.getType(),value,precValue));
		}
		return this;
	}
	
	public AttriBuffer append(byte attriTypeValue,float value,float precValue){
		AttributeType attriType = AttributeType.get(attriTypeValue);
		return this.append(attriType, value, precValue);
	}
	
	public static AttriBuffer createAttriBuffer(){
		return new AttriBuffer();
	}
	
	public AttriBuffer append(AttriBuffer buffer){
		if(null == buffer || buffer.isEmpty()){
			return this;
		}
		for(AttriItem item : buffer.getMap().values()){
			this.append(item.getAttriTypeValue(), item.getValue(), item.getPrecValue());
		}
		return this ;
	}
	
	public Map<Byte, AttriItem> getMap() {
		return map;
	}
	
	
	
}
