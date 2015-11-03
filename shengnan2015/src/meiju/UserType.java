package meiju;

public enum UserType {     
	STUDENT(1), TEACHER(2), PARENT(3), SCHOOL_ADMIN(4), UNKNOWN(-1);
	
	private int value;
	
	UserType(int value) {
		this.value = value; 
	} 

    public int getValue() {
		return value;
	}

	public String getStringValue() {
		return String.valueOf(value);
	}

	public static UserType valueOf(int value) { 
		UserType userType = null; 
		switch (value) { 
		case 1: 
			userType = STUDENT;
            break;
		case 2: 
			userType = TEACHER; 
			break;
		case 3:
			userType = PARENT;
            break; 
		case 4: 
			userType = SCHOOL_ADMIN;
            break; 
		default:
			userType = UNKNOWN;
            break;
		} 
		return userType;
	}
	public static UserType get(int type){
		for(UserType v : values()){
			if(type == v.getValue()){
				return v;
			}
		}
		return null;
	}
	public String getDescription() { 
		String desc = null;
        switch (this) {
        case STUDENT: 
			desc = "学生"; 
			break;
		case TEACHER: 
			desc = "老师"; 
			break; 
		case PARENT:   
			desc = "家长";   
			break; 
		case SCHOOL_ADMIN:   
			desc = "学校管理员";  
			break;        
		default:    
			desc = "未知用户"; 
			break; 
		}
        return desc;
	}
}



