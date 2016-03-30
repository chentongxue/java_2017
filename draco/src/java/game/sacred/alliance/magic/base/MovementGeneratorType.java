package sacred.alliance.magic.base;

public enum MovementGeneratorType {
    IDLE_MOTION_TYPE(0),
    RANDOM_MOTION_TYPE(1),
    WAYPOINT_MOTION_TYPE(2),
    ANIMAL_RANDOM_MOTION_TYPE(3),
    TARGETED_MOTION_TYPE(4),
    HOME_MOTION_TYPE(5),
    POINT_MOTION_TYPE(6),
    ;
    
    MovementGeneratorType(int type){
    	this.type = type;
    }
    
    public int type;
    
	public int getType(){
		return type;
	}
	
	public static String toString(int type){
		switch(type){
		case 0:
			return "空闲";
		case 1:
			return "随机";
		case 2:
			return "寻路";
		case 3:
			return "动物随机";
		case 4:
			return "目标靠近";
		case 5:
			return "回家";
		case 6:
			return "点移动";
		default:
			return "停止当前行动";
		}
	}
	
}
