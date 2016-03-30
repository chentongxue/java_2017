package sacred.alliance.magic.base;

public enum PathType {

	FORWARD(0),
	GOBACK(1),
	CIRCLE(2),
	;
	
	int type;
	
	PathType(int type){
		this.type = type;
	}
	
	public int getType(){
		return type;
	}
	
	public static PathType getByType(int type){
		switch(type){
		case 0: 
			return FORWARD;
		case 1:
			return GOBACK ;
		case 2:
			return CIRCLE ;
		default :
			return FORWARD;
		}
	}
	public static String toString(int type){
		switch(type){
		case 0:
			return "单向";
		case 1:
			return "往返";	
		case 2:
			return "循环";			
		default:
			return "其它";
		}
	}
	
	public static String[] getAllType(){
		String[] ret = new String[values().length];
		for(int i=0;i<values().length;i++){
			ret[i] = toString(values()[i].getType());
		}
		return ret;
	}
	
	
}
