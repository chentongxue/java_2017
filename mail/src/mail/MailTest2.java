package mail;

import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MailTest2 {

	public static void main(String[] args) {
		sendMail();
	}
	public static void sendMail(){
		try {
			Properties props = new Properties();
			props.put("mail.smtp.auth", "true");
//			props.put("mail.smtp.starttls.enable","false"); 
			props.put("mail.transport.protocol", "smtp");
//			props.put("mail.smtp.user", "testtianyue@sina.com");
//			props.put("mail.smtp.password", "uiojkl");
//			props.put("mail.smtp.protocol", "smtp");
//			props.put("mail.smtp.host", "smtp.sina.com");
//			props.put("mail.smtp.port", "25");
			Session session = Session.getInstance(props);
			session.setDebug(true);
			Message msg = new MimeMessage(session);
			InternetAddress addr = new InternetAddress("testtianyue@sina.com");
			msg.setText("nihao");
			msg.setFrom(addr);
			Transport transport =session.getTransport();
			transport.connect("smtp.sina.com", 25, "testtianyue@sina.com","uiojkl");
			transport.sendMessage(msg, new Address[]{new InternetAddress("153771472@qq.com")});
			transport.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
