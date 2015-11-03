package org.gof.demo.platform.main;

import org.gof.core.Node;
import org.gof.demo.platform.C;
import org.gof.demo.platform.Log;
import org.gof.demo.platform.LoginPort;
import org.gof.demo.platform.LoginService;

public class PlatformStartup {

	public static void main(String[] args) {
		//设置日志文件名称
		System.setProperty("logFileName", "platform");
		
		//创建Node
		Node node = new Node(C.NODE_ID, C.NODE_ADDR);

		//启动验证Port
		for(int i = 0; i < C.PORT_STARTUP_NUM_LOGIN; i++) {
			LoginPort loginPort = new LoginPort(C.PORT_LOGIN_PREFIX + i);
			loginPort.startup(node);
			
			//启动验证服务
			LoginService loginServ = new LoginService(loginPort);
			loginServ.startup();
			loginPort.addService(loginServ);
		}

		//启动Node
		node.startup();
		
		//启动日志信息
		Log.platform.info("================================================");
		Log.platform.info(C.NODE_ID + " started.");
		Log.platform.info("Listen:" + C.NODE_ADDR);
		Log.platform.info("================================================");
		
		//系统关闭时进行清理
		Runtime.getRuntime().addShutdownHook(new Thread() { 
			public void run() { 
				
			} 
		});
	}
}
