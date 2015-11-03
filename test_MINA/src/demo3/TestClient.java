package demo3;

import java.net.InetSocketAddress;

import org.apache.log4j.Logger;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

/*
 * 模拟手机发短信
 */
public class TestClient {
	private static Logger logger = Logger.getLogger(TestClient.class);
	private static String HOST = "127.0.0.1";
	private static int PORT = 3005;

	public static void main(String[] args) {
		// 创建一个非阻塞的客户端程序
		IoConnector connector = new NioSocketConnector();
		// 设置链接超时时间
		connector.setConnectTimeout(30000);
		// 设置过滤器
		connector.getFilterChain().addLast("codec",
				new ProtocolCodecFilter(new ObjectSerializationCodecFactory()));

		// 添加业务逻辑处理器类
		connector.setHandler(new Demo1ClientHandler());
		IoSession session = null;
		try {
			ConnectFuture future = connector.connect(new InetSocketAddress(
					HOST, PORT));// 创建连接
			future.awaitUninterruptibly();// 等待连接创建完成
			session = future.getSession();// 获得session

			PhoneMessageDto sendMes = new PhoneMessageDto();
			sendMes.setSendPhone("13681803609"); // 当前发送人的手机号码
			sendMes.setReceivePhone("13721427169"); // 接收人手机号码
			sendMes.setMessage("测试发送短信，这个是短信信息哦，当然长度是有限制的哦....");

			session.write(sendMes);// 发送给移动服务端
		} catch (Exception e) {
			logger.error("客户端链接异常...", e);
		}
		session.getCloseFuture().awaitUninterruptibly();// 等待连接断开
		connector.dispose();
	}
}
