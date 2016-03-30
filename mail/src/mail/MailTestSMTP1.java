package mail;

import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MailTestSMTP1 {

	public static void main(String[] args) {
		String sender = "346126185@qq.com";
		String password = "josehere123";
		String ss[] = new String[]{"153771472@qq.com"};
		String content = "你好";
		String title = "龙之牧场账号";
		sendMail(sender, sender, password, ss, title, content);
	}
	public static void sendMail(String srcAdrr, String senderName, String senderPassord, String[] tarAddrs, String title, String content){
		try {
			Properties props = new Properties();
			props.put("mail.smtp.auth", "true");
			props.put("mail.transport.protocol", "smtp");
			Session session = Session.getInstance(props);
			session.setDebug(true);
			Message msg = new MimeMessage(session);
			InternetAddress[] addrs = new InternetAddress[tarAddrs.length];
			for (int i = 0; i < tarAddrs.length; i++) {
				addrs[i] = new InternetAddress(tarAddrs[i]);
			}
			InternetAddress addr = new InternetAddress(srcAdrr);
			msg.setText("nihao");
			msg.setSubject(title);
			msg.setFrom(addr);
			Transport transport =session.getTransport();
			transport.connect("smtp.qq.com", 25, senderName,senderPassord);
			transport.sendMessage(msg, addrs);
			transport.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
