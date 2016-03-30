package com.game.draco.app.mail.type;

public enum MailLogType {
	
	send(1),//发送邮件
	pick(2),//提取邮件
	del(3),//删除邮件
	
	;
	
	private final int type;
	
	MailLogType(int type){
		this.type = type;
	}

	public int getType() {
		return type;
	}

}
