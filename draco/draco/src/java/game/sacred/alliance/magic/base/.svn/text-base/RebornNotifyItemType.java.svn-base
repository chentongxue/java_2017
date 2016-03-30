package sacred.alliance.magic.base;



public enum RebornNotifyItemType {
	place((byte)0,"原地复活",(byte)1),
	rebornSameMap((byte)1,"入口复活",(byte)0),
	rebornNotSameMap((byte)2,"墓地复活",(byte)0)
	;
	
	private final byte type;
	
	private final String name;
	
	private final byte rebornType;
	
	RebornNotifyItemType(byte type, String name, byte rebornType){
		this.name=name;
		this.type= type;
		this.rebornType = rebornType;
	}

	public static RebornNotifyItemType get(byte type){
		for(RebornNotifyItemType mt : values()){
			if(mt.getType() == type){
				return mt ;
			}
		}
		return null ;
	}
	
	public byte getType(){
		return type;
	}
	
	public String getName() {
		return name;
	}

	public byte getRebornType() {
		return rebornType;
	}
}
