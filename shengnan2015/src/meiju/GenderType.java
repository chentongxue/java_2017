package meiju;

public enum GenderType {  
	UNKNOWN((byte)0),
	MALE((byte)1),
	FEMALE((byte)2), 
	;
	
	private byte value;
	
	GenderType(byte type) {
		this.value = type; 
	} 

    public int getType() {
		return value;
	}

	public static GenderType valueOf(byte value) { 
		GenderType userType = null; 
		switch (value) { 
		case 1: 
			userType = MALE;
            break;
		case 2: 
			userType = FEMALE; 
			break;
		default:
			userType = UNKNOWN;
            break;
		} 
		return userType;
	}
	public static GenderType valueOf2(byte value){
		for(GenderType v : values()){
			if(value == v.getType()){
				return v;
			}
		}
		return null;
	}
    public static void main(String[] args) throws InterruptedException
	{
    	GenderType t = GenderType.valueOf("MALE");
    	System.out.println(t);
//    	GenderType t2 = GenderType.valueOf(1);
    	System.out.println(t);
	}
	public String getDescription() { 
		String desc = null;
        switch (this) {
		case MALE: 
			desc = "男性"; 
			break; 
		case FEMALE:   
			desc = "女性";   
			break; 
		default:    
			desc = "未知用户"; 
			break; 
		}
        return desc;
	}
}



