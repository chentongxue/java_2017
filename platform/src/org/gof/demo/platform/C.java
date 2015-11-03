package org.gof.demo.platform;

import java.util.Properties;

import org.gof.core.support.Utils;

/**
 * 系统参数
 */
public class C {
	//配置文件名称
	private static final String CONFIG_NAME = "gofDistr.properties";
	
	//前缀
	public static final String PORT_LOGIN_PREFIX = "login";	//登陆服务Port前缀
	
	//配置
	public static final String NODE_ID = "platform";	//NodeID
	public static final String NODE_ADDR;				//Node地址
	
	//服务
	public static final String SERV_LOGIN = "login";		//登陆服务ID
	
	//验证服务实例数
	public static final int PORT_STARTUP_NUM_LOGIN;				
	
	static {
		//获取配置
		Properties prop = Utils.readProperties(CONFIG_NAME);
		
		//读取配置
		NODE_ADDR = prop.getProperty("node.addr.platform");
		PORT_STARTUP_NUM_LOGIN = Utils.intValue(prop.getProperty("port.startup.num.platform.login"));
	}
}