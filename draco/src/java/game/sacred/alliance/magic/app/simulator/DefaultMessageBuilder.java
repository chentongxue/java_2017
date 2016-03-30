package sacred.alliance.magic.app.simulator;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.Date;

import sacred.alliance.magic.codec.impl.MessageMapping;
import sacred.alliance.magic.core.Message;

public class DefaultMessageBuilder implements MessageBuilder{

	private MessageMapping messageMapping ;
	
	@Override
	public Message buildMessage(String[] cmdlines) {
		String cmdId = cmdlines[0];
		Class<Message> clazz = null ;
		try {
			clazz = messageMapping.getCommandClass(cmdId);
		} catch(ClassNotFoundException e){
			//System.err.println("不存在的命令: commandId=" + cmdId);
		}catch (Exception e) {
			e.printStackTrace();
			return null ;
		}
		//给message赋值
		return this.messageAssignment(clazz, cmdlines);
	}

	
	public void setMessageMapping(MessageMapping messageMapping) {
		this.messageMapping = messageMapping;
	}

	private Message messageAssignment(Class<Message> clazz,String[] values){
		try{
			if(null == clazz){
				return null ;
			}
			Message message = clazz.newInstance();
			Field[] field = getFields(clazz);
			//field[0] = commandId
			for(int i=1;i<field.length;i++){
				String propertyName=field[i].getName();
				PropertyDescriptor prop=new PropertyDescriptor(propertyName,message.getClass());
				String type=prop.getPropertyType().getSimpleName();
				if(type.equals("int")){
					prop.getWriteMethod().invoke(message,new Object[]{new Integer(values[i])});
				}else if(type.equals("long")){
					prop.getWriteMethod().invoke(message,new Object[]{new Long(values[i])});
				}else if(type.equals("String")){
					prop.getWriteMethod().invoke(message,new Object[]{values[i]});
				}else if(type.equals("Date")){
					prop.getWriteMethod().invoke(message,new Object[]{new Date(values[i])});
				}else if(type.equals("short")){
					prop.getWriteMethod().invoke(message,new Object[]{new Short(values[i])});
				}else if(type.equals("byte")){
					prop.getWriteMethod().invoke(message,new Object[]{new Byte(values[i])});
				}else if(type.equals("boolean")){
					prop.getWriteMethod().invoke(message,new Object[]{new Boolean(values[i])});
				}else if(type.equals("double")){
					prop.getWriteMethod().invoke(message,new Object[]{new Double(values[i])});
				}else if(type.equals("float")){
					prop.getWriteMethod().invoke(message,new Object[]{new Float(values[i])});
				}else if(type.equals("byte[]")){
					String[] vs = values[i].split(",");
					byte[] buffer=new byte[vs.length];
					for(int index=0;index<vs.length;index++){
						buffer[i] = Byte.parseByte(vs[i]);
					}
					prop.getWriteMethod().invoke(message,new Object[]{buffer});
				}else if(type.equals("short[]")){
					String[] vs = values[i].split(",");
					short[] buffer=new short[vs.length];
					for(int index=0;index<vs.length;index++){
						buffer[i] = Short.parseShort(vs[i]);
					}
					prop.getWriteMethod().invoke(message,new Object[]{buffer});
				}else if(type.equals("int[]")){
					String[] vs = values[i].split(",");
					int[] buffer=new int[vs.length];
					for(int index=0;index<vs.length;index++){
						buffer[i] = Integer.parseInt(vs[i]);
					}
					prop.getWriteMethod().invoke(message,new Object[]{buffer});
				}else if(type.equals("string[]")){
					String[] vs = values[i].split(",");
					prop.getWriteMethod().invoke(message,new Object[]{vs});
				}else{
					System.err.println("不支持的数据类型:" + type);
					return null ;
				}
			}
			return message ;
		}catch(Exception ex){
			ex.printStackTrace();
			return null ;
		}
	}
	
	
	private  Field[] getFields(Class obj){
		Field[] field;
		if(obj.getName().equals("sacred.alliance.magic.core.Item")){
			return obj.getDeclaredFields();
		}else if(obj.getName().equals("java.lang.Object")){
			return obj.getDeclaredFields();
		}else{
			Field[] superField=getFields(obj.getSuperclass());
			Field[] currField=obj.getDeclaredFields();
			field=new Field[superField.length+currField.length];
			System.arraycopy(superField,0,field,0,superField.length);
			System.arraycopy(currField,0,field,superField.length,currField.length);
			return field;
		}
	}
}
