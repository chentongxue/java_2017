package sacred.alliance.magic.base;



public enum RebornType {
	situ((byte)0,"原地复活"),
	rebornPoint((byte)1,"墓地复活"),
	soul((byte)2,"灵魂复活"),
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
