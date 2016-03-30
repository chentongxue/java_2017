package sacred.alliance.magic.base;

public enum ChallengeResultType {
	
	Default((byte)-1),//无胜负
	Win((byte)0),
	Lose((byte)1)
	;
	
	private final byte type;
	
	private ChallengeResultType(byte type) {
		this.type = type;
	}

	public byte getType() {
		return type;
	}
	
	public static ChallengeResultType get(byte type){
		for(ChallengeResultType resultType : ChallengeResultType.values()){
			if(resultType.getType() == type){
				return resultType;
			}
		}
		return null;
	}
	
	public static ChallengeResultType getOther(ChallengeResultType type){
		if(type == ChallengeResultType.Win) {
			return Lose;
		}
		return Win;
	}
}
