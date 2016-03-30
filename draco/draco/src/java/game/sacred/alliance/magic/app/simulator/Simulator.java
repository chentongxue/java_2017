package sacred.alliance.magic.app.simulator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import platform.message.item.AreaItem;
import platform.message.item.ServerItem;
import platform.message.request.C5201_UserLoginReqMessage;
import platform.message.response.C5201_UserLoginRespMessage;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.util.StringUtil;


public class Simulator {
	private static final int APPID = 4 ;
	private static final byte PASSPORTTYPE = 0 ;
	private String tokenId = "" ;
	private IoSession session = null;
	private IoConnector connector = null ;
	private int appId = APPID ;
	
	//游戏服务器列表
	private Map<String,ServerItem> gameServerMap = new LinkedHashMap<String,ServerItem>();
	private BufferedReader reader = new BufferedReader(new InputStreamReader(
			System.in));
	
	public static void main(String[] args) throws Exception {
		if (null == args || 0 == args.length || args[0].length() == 0) {
			System.err.println("请输入网关地址. eg: http://gw.moogame.cn");
			System.exit(1);
		}
		Simulator simulator = new Simulator();
		if(2 <= args.length){
			simulator.setAppId(Integer.parseInt(args[1]));
		}
		simulator.simulator(args[0]);
		System.exit(0);
	}
	
	public void simulator(String gwURL) throws Exception{
		try{
			loginGateway(gwURL);
			loginGame();
			actionSimulator();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			destory();
		}
	}
	
	private void destory(){
		try {
			if(null != reader){
				reader.close();
			}
		} catch (IOException e) {
		}
		if(null != session){
			session.close();
		}
		if(null != connector){
			connector.dispose();
		}
	}
	
	private void loginGateway(String gwURL) throws Exception {
		// 设置网关地址
		SimulatorContext.getInstance().getGateWayClient().setUrl(gwURL);

		while (true) {
			System.out.println("userName:");
			// System.out.print(">> ");
			String userName = reader.readLine();
			System.out.println("password:");
			// System.out.print(">> ");
			String password = reader.readLine();
			System.out.println(userName + "," + password);
			// 构建登录命令发送到网关
		//	sacred.alliance.magic.uc.auth.message.request.LoginReqMessage reqMsg = new sacred.alliance.magic.uc.auth.message.request.LoginReqMessage();
			C5201_UserLoginReqMessage reqMsg = new C5201_UserLoginReqMessage();
			reqMsg.setAppId(appId);
			reqMsg.setPassportId(userName);
			reqMsg.setPasswd(password);
//			reqMsg.setPassportType(PASSPORTTYPE);
			//reqMsg.setMid("SonyEricssonK800");
			reqMsg.setFirstLogin((byte)0);
//			reqMsg.setPassPortId(userName);
//			reqMsg.setPassWord(password);
//			reqMsg.setPassPortType(PASSPORTTYPE);
//			reqMsg.setIsFirstLogin((byte)0);
//			reqMsg.setMobileUA("SonyEricssonK800");
			
			C5201_UserLoginRespMessage respMsg = (C5201_UserLoginRespMessage) SimulatorContext
					.getInstance().getGateWayClient().sendMessage(reqMsg);
			if (null == respMsg) {
				System.err.println("登录网关无返回或返回为NULL");
				tokenId = "";
				continue;
			}
			if (1 != respMsg.getStatus()) {
				System.err.println("登录网关失败:" + respMsg.getInfo() + "("
						+ respMsg.getStatus() + ")");
				tokenId = "";
				continue;
			}
			tokenId = respMsg.getTokenId();
			List<AreaItem> areaList = respMsg.getAreas();
			if (null != areaList) {
				for (AreaItem ai : areaList) {
					List<ServerItem> serverList = ai.getServers();
					if (null != serverList) {
						for (ServerItem si : serverList) {
							gameServerMap.put(si.getServerId(), si);
						}
					}
				}
			}
			printServer();
			break;
		}
	}
	
	private void loginGame() throws Exception {
		connector = new NioSocketConnector();
		connector.getFilterChain().addLast("codec", SimulatorContext.getInstance().getCodecFilter());
		connector.getFilterChain().addLast("print", SimulatorContext.getInstance().getPrintMessageFilter());
		connector.setHandler(new SimulatorIoHandler());
		ConnectFuture future = null ;
		ServerItem si = null ;
		while(true){
			String serverId = reader.readLine();
			si = gameServerMap.get(serverId);
			if(null == si){
				System.err.println("选择的服务器不存在");
				printServer();
				continue ;
			}
			//发送登录命令
			try{
				future = connector.connect(new InetSocketAddress(si.getIp(),si.getPort()));
				future.awaitUninterruptibly();
				session = future.getSession();
			}catch(Exception ex){
				if(null != session){
					session.close();
				}
				session = null ;
			}
			if(null == session){
				System.err.println("连接服务器:" + si.getServerId() + " 失败");
				printServer();
				continue ;
			}
			//发送路由消息
			byte[] routeData = si.getRouteFlag();
			if(null != routeData && routeData.length >0){
				IoBuffer buffer = IoBuffer.allocate(routeData.length);
				buffer.put(routeData);
				buffer.flip();
				session.write(buffer);
			}
			break ;
		}
		System.out.println("连接服务器:" + si.getServerId() + " 成功,请使用下面tokentId进行登录");
		System.out.println(tokenId);
	}
	
	private void actionSimulator() {
		DefaultMessageBuilder messageBuilder = SimulatorContext.getInstance()
				.getMessageBuilder();
		String line = "";
		while (true) {
			try {
				System.out.print(">> ");
				line = reader.readLine();
				line = line.trim();
				if (0 == line.length()) {
					continue;
				}
				if (line.equals("exit") || line.equals("quit")) {
					System.out.println("byte~~");
					break;
				}
				if (line.equals("help")) {
					System.out.println("笨蛋,这里没有帮助信息");
					continue;
				}
				String[] cmdlines = StringUtil.splitString(line, " |\t");
				if (cmdlines[0].equals("pa") || cmdlines[0].equals("pr")) {
					// print filter 设置
					boolean isAdd = cmdlines[0].equals("pa");
					for (int i = 1; i < cmdlines.length; i++) {
						if (isAdd) {
							SimulatorContext.getInstance()
									.getPrintMessageFilter()
									.getFilterCommandSet().add(cmdlines[i]);
						} else {
							SimulatorContext.getInstance()
									.getPrintMessageFilter()
									.getFilterCommandSet().remove(cmdlines[i]);
						}
					}
					continue;
				}
				Message reqMsg = messageBuilder.buildMessage(cmdlines);
				if (null == reqMsg) {
					System.err.println("错误的命令,请检查输入参数");
					continue;
				}
				session.write(reqMsg);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private  void printServer(){
		System.out.println("请输入需要登录的游戏服务器ID: eg: 010");
		System.out.println("=======================");
		for(Iterator<Map.Entry<String,ServerItem>> it = gameServerMap.entrySet().iterator();it.hasNext();){
			Map.Entry<String, ServerItem> entry = it.next();
			System.out.println(entry.getKey() + "  " + entry.getValue().getServerName() +"(" + entry.getValue().getOpenFlag() + ")");
		}
		System.out.println("=======================");
	}

	public void setAppId(int appId) {
		this.appId = appId;
	}
}
