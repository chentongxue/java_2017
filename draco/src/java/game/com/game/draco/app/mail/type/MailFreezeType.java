package com.game.draco.app.mail.type;

public enum MailFreezeType {
	
	Unfreeze(0),//非冻结
	Freeze(1),//已冻结
	
	;
	
	private final int type;
	
	MailFreezeType(int type ){
		this.type = type;
	}

	public int getType() {
		return type;
	}

	public static MailFreezeType get(int type){
		for(MailFreezeType item : MailFreezeType.values()){
			if(item.getType() == type){
				return item;
			}
		}
		return null;
	}
	
}
