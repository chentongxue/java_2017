package mail;

import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MailTest2ask {

	public static void main(String[] args) {
		sendMail();
	}
	public static void sendMail(){
		try {
			String host = "smtp.sina.com", user = "testtianyue", pass = "uiojkl";

		    String to = "153771472@qq.com";
		    String from = "testtianyue@sina.com";
//		    String subject = subj;
		    String messageText = "hello";
		    boolean sessionDebug = true;

		    Properties props = System.getProperties();
		    props.put("mail.smtp.host", host);
		    props.put("mail.debug", "true");
		    props.put("mail.transport.protocol", "smtp");
		    props.put("mail.smtp.auth", "true");
		    props.put("mail.smtp.port", "25");

		    Session mailSession = Session.getDefaultInstance(props, null);
		    mailSession.setDebug(sessionDebug);

		    Message msg = new MimeMessage(mailSession);
		    msg.setFrom(new InternetAddress(from));
		    InternetAddress[] address = {new InternetAddress(to)};
		    msg.setRecipients(Message.RecipientType.TO, address);
//		    msg.setSubject(subject);
		    msg.setContent(messageText, "text/html");

		    Transport transport = mailSession.getTransport("smtp");
		    transport.connect(host, user, pass);

		    try {
		        transport.sendMessage(msg, msg.getAllRecipients());
		    }
		    catch (Exception e) {
		        System.out.println("Error" + e.getMessage());
		    }
		    transport.close();
		} catch (Exception e) {
			e.toString();
		}
	}

}
