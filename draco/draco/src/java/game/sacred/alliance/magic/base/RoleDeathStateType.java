package sacred.alliance.magic.base;

public enum RoleDeathStateType{
	
	   ALIVE(0),
	   JUST_DIED(1),
	   CORPSE(2),
	   DEAD(3),
	   JUST_ALIVED(4)
	;
	public int type;
	
	RoleDeathStateType(int type){
		this.type = type;
	}
	
	public int getType(){
		return type;
	}
	
	public static String toString(int type){
		switch(type){
		case 0:
			return "活着";
		case 1:
			return "刚死亡";	
		case 2:
			return "尸体";
		case 3:
			return "死亡";
		case 4:
		default:
			return "刚复活";
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