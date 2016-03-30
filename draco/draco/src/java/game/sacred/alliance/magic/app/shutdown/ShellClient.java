package sacred.alliance.magic.app.shutdown;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.project.protobuf.codec.bytes.ByteProtoBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;

import platform.message.request.C5700_ShutdownOffLineNoticeReqMessage;
import platform.message.request.C5701_ShutDownOffLineRolesReqMessage;
import platform.message.request.C5702_ShutdownRejectReqMessage;
import sacred.alliance.magic.client.Client;
import sacred.alliance.magic.client.zeroc.DefaultIceClient;
import sacred.alliance.magic.codec.impl.AutoLoadMessageMapping;
import sacred.alliance.magic.codec.impl.bytes.BytesMessageParser;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.module.i18n.DefaultLanguageHandler;

public class ShellClient {
	private static final Logger logger = LoggerFactory.getLogger(ShellClient.class);
	private static final String NOTICE_CMDID = String.valueOf(new C5700_ShutdownOffLineNoticeReqMessage().getCommandId());
	private static final String OFFLINE_CMDID = String.valueOf(new C5701_ShutDownOffLineRolesReqMessage().getCommandId());
	private static final String REJECT_CMDID = String.valueOf(new C5702_ShutdownRejectReqMessage().getCommandId());
	
	static class SingletonHolder {
		static ShellClient singleton = new ShellClient();
	}

	public static ShellClient getInstance() {
		return SingletonHolder.singleton;
	}
	
	private Client client ;
	private BytesMessageParser messageParser ;
	private DefaultLanguageHandler i18n ;
	
	private ShellClient() {
		// 构建消息解析器
		try {
			AutoLoadMessageMapping mapping = new AutoLoadMessageMapping();
			List<String> pkgList = new ArrayList<String>();
			pkgList.add("platform.message.request");
			pkgList.add("platform.message.response");
			mapping.setPkgList(pkgList);
			mapping.init();

			messageParser = new BytesMessageParser();
			messageParser.setProtobufPackagePath("platform.all.message.codec");
			messageParser.setProtoBuffer(new ByteProtoBuffer());
			messageParser.setMapping(mapping);
			messageParser.init();
			
			i18n = new DefaultLanguageHandler();
			i18n.setLangName("vn");
			i18n.setI18nResource(new FileSystemResource("i18n/game_i18n.xls"));
			i18n.start();
		} catch (Exception ex) {
			logger.error("",ex);
		}
	}

	
	private Client getClient() throws Exception{
		if(null != this.client){
			return client ;
		}
		URL url = Thread.currentThread().getContextClassLoader().getResource("shutdown-ice.client");
		this.client = new DefaultIceClient(url.getFile(),messageParser);
		return this.client;
	}
	

	
	public void offLineNotice(int minutes) throws Exception{
		C5700_ShutdownOffLineNoticeReqMessage reqMsg = new C5700_ShutdownOffLineNoticeReqMessage();
		String content = "" ;
		if(1 == minutes - 1){
			content = i18n.messageFormat(TextId.SHUT_DOWN_REJECT_TIPS, String.valueOf(minutes-1));
		}else{
			content = i18n.messageFormat(TextId.SHUT_DOWN_TIPS, String.valueOf(minutes-1));
		}
		reqMsg.setMinutes(minutes);
		reqMsg.setContent(content);
		this.sendMessage(reqMsg);
	}
	
	public void offLineRoles(byte type) throws Exception{
		C5701_ShutDownOffLineRolesReqMessage reqMsg = new C5701_ShutDownOffLineRolesReqMessage();
		reqMsg.setType(type);
		this.sendMessage(reqMsg);
	}
	
	public void rejectReq(byte type) throws Exception{
		C5702_ShutdownRejectReqMessage reqMsg = new C5702_ShutdownRejectReqMessage();
		reqMsg.setType(type);
		this.sendMessage(reqMsg);
	}
	
	private void sendMessage(Message reqMsg){
		try{
			Message resp = this.getClient().sendMessage(reqMsg);
			System.out.println("run " + reqMsg.getCommandId() + " success");
		}catch(Exception e){
			System.out.println("run " + reqMsg.getCommandId() + " error");
		}
	}
	
	public static void main(String[] args) throws Exception {
		try {
			String commandId = args[0];
			if (null == commandId || 0 == commandId.trim().length()) {
				return;
			}
			if (NOTICE_CMDID.equals(commandId)) {
				ShellClient.getInstance().offLineNotice(Integer.parseInt(args[1]));
				return;
			}
			if (OFFLINE_CMDID.equals(commandId)) {
				ShellClient.getInstance().offLineRoles(Byte.parseByte(args[1]));
				return;
			}
			if (REJECT_CMDID.equals(commandId)) {
				ShellClient.getInstance().rejectReq(Byte.parseByte(args[1]));
				return ;
			}
			System.out.println("commandId not found");
		} catch (Exception ex) {
			logger.error("ShellClient main error",ex);
			System.err.println(ex);
		}finally{
			System.exit(0);
		}
	}
}
