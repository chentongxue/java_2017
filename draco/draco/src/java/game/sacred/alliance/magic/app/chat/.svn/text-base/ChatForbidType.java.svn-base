package sacred.alliance.magic.app.chat;

public enum ChatForbidType {
	
	None(0,"不禁言"),
	All(1,"全部禁言"),
	WordAndCamp(2,"禁言世界和阵营频道"),
	
	;
	
	private final int type;
	private final String name;
	
	ChatForbidType(int type, String name){
		this.type = type;
		this.name = name;
	}

	public int getType() {
		return type;
	}

	public String getName() {
		return name;
	}
	
	public static ChatForbidType getChatForbidType(int type){
		for(ChatForbidType item : ChatForbidType.values()){
			if(item.getType() == type){
				return item;
			}
		}
		return None;
	}
	
}
