package com.game.draco.app.mail;

import com.game.draco.app.mail.domain.Mail;

import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.vo.RoleInstance;

public interface MailDisplayContextLogic {

	public String getDisplayContext(Mail mail,RoleInstance role);
	
	public OutputConsumeType getMailSource() ;
}
