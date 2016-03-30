package com.game.draco.app.mail.domain;

import java.util.Date;

import lombok.Data;
/**
 * mail-attachment 
 */
public @Data class MailAccessory {
	private String mailId;
	private String roleId;
	private int templateId;
	private int num;
	private int bind;
	private String instanceId;
	private Date sendTime;
}
