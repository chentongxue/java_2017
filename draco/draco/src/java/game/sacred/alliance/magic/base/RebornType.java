package sacred.alliance.magic.base;



public enum RebornType {
	
	rebornPoint((byte)0,"墓地复活"),
	place((byte)1,"原地复活"),
	//skill((byte)2,"技能复活"),
	
	;
	
	private final byte id;
	
	private final String name;
	
	RebornType(byte id, String name){
		this.name=name;
		this.id= id;
	}

	public static RebornType get(byte type){
		for(RebornType mt : values()){
			if(mt.getId() == type){
				return mt ;
			}
		}
		return null ;
	}
	
	public byte getId(){
		return id;
	}
	
	public String getName() {
		return name;
	}
}
