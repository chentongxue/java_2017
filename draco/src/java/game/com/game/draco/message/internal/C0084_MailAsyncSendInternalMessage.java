package com.game.draco.message.internal;

import com.game.draco.app.mail.domain.Mail;

import lombok.Data;

public @Data class C0084_MailAsyncSendInternalMessage extends InternalMessage {
	
	public C0084_MailAsyncSendInternalMessage() {
		this.commandId = 84;
	}
	
	private Mail mail;
	
}
