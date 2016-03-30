package sacred.alliance.magic.base;


public enum SaveDbStateType {
	Initialize((byte)0,"初始状态"),//从数据库中读取，没有任何改动，不需要改动数据库
	Insert((byte)1,"新增数据"),//数据库中没有
	Update((byte)2,"修改数据"),//数据库中有，修改了设置
	
	;
	
	private final byte type;
	private final String name;
	
	SaveDbStateType(byte type, String name){
		this.type = type;
		this.name = name;
	}

	public byte getType() {
		return type;
	}

	public String getName() {
		return name;
	}
	
	public static SaveDbStateType get(byte type){
		for(SaveDbStateType item : SaveDbStateType.values()){
			if(item.getType() == type){
				return item;
			}
		}
		return null;
	}
}
