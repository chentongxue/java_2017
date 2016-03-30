package com.game.draco.app.mail.type;

public enum MailFreezeType {
	
	Unfreeze(0,"非冻结"),
	Freeze(1,"已冻结"),
	
	;
	
	private final int type;
	private final String name;
	
	MailFreezeType(int type , String name){
		this.type = type;
		this.name = name;
	}

	public int getType() {
		return type;
	}

	public String getName() {
		return name;
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
