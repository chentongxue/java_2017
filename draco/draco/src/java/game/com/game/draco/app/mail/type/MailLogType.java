package com.game.draco.app.mail.type;

public enum MailLogType {
	
	send(1,"发送邮件"),
	pick(2,"提取邮件"),
	del(3,"删除邮件"),
	
	;
	
	private final int type;
	private final String name;
	
	MailLogType(int type , String name){
		this.type = type;
		this.name = name;
	}

	public int getType() {
		return type;
	}

	public String getName() {
		return name;
	}
	
}
