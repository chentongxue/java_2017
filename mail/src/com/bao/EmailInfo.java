package com.bao;

import java.io.File;
import java.io.Serializable;
import java.util.List;

public class EmailInfo implements Serializable {

	private static final long serialVersionUID = -2448933687811325694L;
	private String host;
	private Integer port;
	private String login_account;
	private String login_password;
	private String bcc;
	private String subject;

	private String sender;
	private String[] recipients; // 收件人
	private String[] ccs; // 抄送人
	private String[] bccs; // 暗送人
	private File[] attachs;
	private String[] bookingAttach;
	private List<BookingAttachment> bookingAttachList;
	private String[] attachsContentType;
	private String[] attachsFileName;
	private String Content;

	private boolean validate = true;

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public String getLogin_account() {
		return login_account;
	}

	public void setLogin_account(String loginAccount) {
		login_account = loginAccount;
	}

	public String getLogin_password() {
		return login_password;
	}

	public void setLogin_password(String loginPassword) {
		login_password = loginPassword;
	}

	public String getBcc() {
		return bcc;
	}

	public void setBcc(String bcc) {
		this.bcc = bcc;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String[] getRecipients() {
		return recipients;
	}

	public void setRecipients(String[] recipients) {
		this.recipients = recipients;
	}

	public String[] getCcs() {
		return ccs;
	}

	public void setCcs(String[] ccs) {
		this.ccs = ccs;
	}

	public String[] getBccs() {
		return bccs;
	}

	public void setBccs(String[] bccs) {
		this.bccs = bccs;
	}

	public File[] getAttachs() {
		return attachs;
	}

	public void setAttachs(File[] attachs) {
		this.attachs = attachs;
	}

	public String[] getAttachsContentType() {
		return attachsContentType;
	}

	public void setAttachsContentType(String[] attachsContentType) {
		this.attachsContentType = attachsContentType;
	}

	public String[] getAttachsFileName() {
		return attachsFileName;
	}

	public void setAttachsFileName(String[] attachsFileName) {
		this.attachsFileName = attachsFileName;
	}

	public String getContent() {
		return Content;
	}

	public void setContent(String content) {
		Content = content;
	}

	public boolean isValidate() {
		return validate;
	}

	public void setValidate(boolean validate) {
		this.validate = validate;
	}

	public String[] getBookingAttach() {
		return bookingAttach;
	}

	public void setBookingAttach(String[] bookingAttach) {
		this.bookingAttach = bookingAttach;
	}

	public List<BookingAttachment> getBookingAttachList() {
		return bookingAttachList;
	}

	public void setBookingAttachList(List<BookingAttachment> bookingAttachList) {
		this.bookingAttachList = bookingAttachList;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

}