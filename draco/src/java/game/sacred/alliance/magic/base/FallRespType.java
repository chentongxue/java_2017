package sacred.alliance.magic.base;

public enum FallRespType {
	error((byte)0),
	fall((byte)1),
	//roll((byte)2),
	;
	public byte type;

	FallRespType(byte type) {
		this.type = type;
	}

	public byte getType() {
		return type;
	}

	public static StorageType get(byte type) {
		for(StorageType st : StorageType.values()){
			if(st.getType() == type){
				return st;
			}
			
		}
		return null;
	}

}
