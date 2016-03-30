package mail;

import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MailTest0 {

	public static void main(String[] args) {
		sendMail();
	}
	public static void sendMail(){
		try {
			Properties props = new Properties();
			props.put("mail.smtp.auth", "true");
			props.put("mail.transport.protocol", "smtp");
			Session session = Session.getInstance(props);
			session.setDebug(true);
			Message msg = new MimeMessage(session);
			InternetAddress addr = new InternetAddress("346126185@qq.com");
			msg.setText("nihao");
			msg.setFrom(addr);
			Transport transport =session.getTransport();
			transport.connect("smtp.qq.com", 25, "346126185@qq.com","josehere123");
			transport.sendMessage(msg, new Address[]{new InternetAddress("153771472@qq.com")});
			transport.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
