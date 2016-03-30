package mail;

import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MailTest {
	public static void sendMail(String fromMail, String user, String password,
			String toMail, String mailTitle, String mailContent)
			throws Exception {
		Properties props = new Properties(); 
		props.put("mail.smtp.host", "smtp.exmail.qq.com");//
		props.put("mail.smtp.auth", "true");
//		Security.addProvider(new Provider(); 
//		props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.setProperty("mail.smtp.port", "25");
//		props.setProperty("mail.smtp.socketFactory.port", "465");
		Session session = Session.getInstance(props);
		session.setDebug(true); 

		MimeMessage message = new MimeMessage(session);
		message.setFrom(new InternetAddress(fromMail));
		message.setRecipient(Message.RecipientType.TO, new InternetAddress(
				toMail));
		message.setSubject(mailTitle);
		 message.setText("nihao"); 
		message.setContent(mailContent, "text/html;utf-8");
		message.setSentDate(new Date());
		message.saveChanges();

		 Transport transport = session.getTransport("smtp");
//		Transport transport = session.getTransport();
		transport.connect(user, password);
		transport.sendMessage(message, message.getAllRecipients());
		transport.close();
	}

	public static void main(String[] args) {
		try {
			sendMail("testtianyue@sina.com", "testtianyue", "uiojkl",  
			        "346126185@qq.com",  
			        "Java mail test",  
			        "test context");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
	}
}
