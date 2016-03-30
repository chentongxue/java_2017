package com.bao;

import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

public class MailTest {

	public static void main(String[] args) {
		sendEmail(null);
	}
	public static boolean sendEmail(EmailInfo emailInfo){
		boolean flag = false;
		MyAuthenticator authenticator = null;
		Properties pro = new Properties();
		pro.put("mail.smtp.host", emailInfo.getHost());
		pro.put("mail.smtp.port", emailInfo.getPort());
		pro.put("mail.smtp.auth", emailInfo.isValidate() ? "true" : "false");
		if (emailInfo.isValidate()) {
			authenticator = new MyAuthenticator(emailInfo.getLogin_account(), emailInfo.getLogin_password());
		}
		Session sendMailSession = Session.getInstance(pro, authenticator);
		try {
			Message mailMessage = new MimeMessage(sendMailSession);
			mailMessage.setSubject(emailInfo.getSubject());
			mailMessage.setSentDate(new Date());
			//发件人
			Address from = new InternetAddress(emailInfo.getSender());
			mailMessage.setFrom(from);
			
			//多个收件人
			Address[] recs = new InternetAddress[emailInfo.getRecipients().length];
			for(int i=0;i<emailInfo.getRecipients().length;i++){
				recs[i] = new InternetAddress(emailInfo.getRecipients()[i]);
			}
			mailMessage.setRecipients(Message.RecipientType.TO, recs);
			
			//多个抄送人
			if(null != emailInfo.getCcs() && emailInfo.getCcs().length > 0){
				Address[] ccs = new InternetAddress[emailInfo.getCcs().length];
				for(int i=0;i<emailInfo.getCcs().length;i++){
					ccs[i] = new InternetAddress(emailInfo.getCcs()[i]);
				}
				mailMessage.setRecipients(Message.RecipientType.CC, ccs);
			}
			
			//多个密送人
			if(null != emailInfo.getBccs() && emailInfo.getBccs().length > 0){
				Address[] bccs = new InternetAddress[emailInfo.getBccs().length];
				for(int i=0;i<emailInfo.getBccs().length;i++){
					if(emailInfo.getBccs()[i] != ""){
						bccs[i] = new InternetAddress(emailInfo.getBccs()[i]);
					}
				}
				mailMessage.setRecipients(Message.RecipientType.BCC, bccs);
			}
			
			//发送HTML的内容
			Multipart mainPart = new MimeMultipart();
			BodyPart html = new MimeBodyPart();
			html.setContent(emailInfo.getContent(), "text/html; charset=utf-8");
			mainPart.addBodyPart(html);
			
			//多个附件
			//--input file 选择的附件
			if(null != emailInfo.getAttachs() && emailInfo.getAttachs().length > 0){
				for(int i=0;i<emailInfo.getAttachs().length;i++){
					BodyPart filePart= new MimeBodyPart();
		            DataSource source = new FileDataSource(emailInfo.getAttachs()[i]);
		            filePart.setDataHandler(new DataHandler(source));
		            filePart.setFileName(MimeUtility.encodeText(emailInfo.getAttachsFileName()[i]));
		            mainPart.addBodyPart(filePart);
				}
			}
            mailMessage.setContent(mainPart);
            Transport trans = sendMailSession.getTransport("smtp");
            trans.connect(emailInfo.getHost(), emailInfo.getLogin_account(), emailInfo.getLogin_password());
            Transport.send(mailMessage);
			trans.close();
			flag = true;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return flag;
	}

}
