package demo3;

import java.io.Serializable;

/*
 * �ֻ����ŷ���DTO
 */
public class PhoneMessageDto implements Serializable {
	private String sendPhone; // �������ֻ���

	private String receivePhone; // �������ֻ���

	private String message; // ������Ϣ

	public String getSendPhone() {
		return sendPhone;
	}

	public void setSendPhone(String sendPhone) {
		this.sendPhone = sendPhone;
	}

	public String getReceivePhone() {
		return receivePhone;
	}

	public void setReceivePhone(String receivePhone) {
		this.receivePhone = receivePhone;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
