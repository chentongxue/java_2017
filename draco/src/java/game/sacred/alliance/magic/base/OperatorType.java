/**
Author zhong by 2008-4-29
zhongmingyu@msn.com 
**/
package sacred.alliance.magic.base;

public enum OperatorType {

	Add(0),
	Decrease(1),
	//Multiply(2),
	//Divde(3),
	Equal(4),
	//Mod(5),
	;
	
	int type;
	
	OperatorType(int type){
		this.type = type;
	}
	
	public int getType() {
		return type;
	}
	
	public static String toString(int type){
		switch(type){
		case 0:
			return "+";
		case 1:
			return "-";
		case 2:
			return "*";
		case 3:
			return "/";
		case 4:
			return "=";
		default:
			return "%";
		}
	}
	public static OperatorType get(int type){
		switch(type){
		case 0:
			return Add;
		case 1:
			return Decrease;
		case 4:
			return Equal;	
		default:
			return null;
		}
	}
	
	
	public static String[] getAllType(){
		OperatorType[] type = OperatorType.values();
		String[] ret = new String[type.length];
		for(int i=0;i<type.length;i++){
			ret[i] = toString(type[i].getType());
		}
		return ret;
	}
	
	public static int compute(int attributeValue, OperatorType operatorType, int operateValue){
		if(operatorType==OperatorType.Add){
			attributeValue += operateValue;
		}else if(operatorType==OperatorType.Decrease){
			attributeValue -= operateValue;
		/*}else if(operatorType==OperatorType.Multiply){
			attributeValue *= operateValue;
		}else if(operatorType==OperatorType.Divde){
			attributeValue /= operateValue;*/
		}else if(operatorType==OperatorType.Equal){
			attributeValue = operateValue;
		/*}else if(operatorType==OperatorType.Mod){
			attributeValue %= operateValue;*/
		}
		return attributeValue;
	}
	

}
