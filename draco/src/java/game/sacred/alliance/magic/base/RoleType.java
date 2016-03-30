package sacred.alliance.magic.base;

public enum RoleType {

	PLAYER(0),
	NPC(1),
	//SOUL(2),
	GM(3),
	COPY(4),//分身
	PET(5),// 宠物
	;
	
	public int type;
	
	RoleType(int type){
		this.type = type;
	}
	
	public int getType(){
		return type;
	}
	
	
	public static RoleType getRoleType(int type){
		for(RoleType rt : values()){
			if(rt.getType() == type){
				return rt ;
			}
		}
		return null ;
	}
	
}
