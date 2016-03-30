package sacred.alliance.magic.app.simulator;

import java.util.ArrayList;
import java.util.List;

import org.project.protobuf.codec.bytes.ByteProtoBuffer;

import platform.message.request.C5901_ChargeNotifyReqMessage;
import platform.message.response.C5901_ChargeNotifyRespMessage;
import sacred.alliance.magic.client.zeroc.DefaultIceClient;
import sacred.alliance.magic.codec.impl.AutoLoadMessageMapping;
import sacred.alliance.magic.codec.impl.bytes.BytesMessageParser;

public class RechargeSimulator { 

	/**
	 * 充值模拟的游戏服务器ice地址
	 */
	private static final String GAME_ICE_URI = "ice://gameServer:tcp -h 192.168.1.175 -p 10420" ;
	/**
	 * 充值模拟的帐号ID
	 */
	private static final String USERID = "277209" ;
	/**
	 * 充值模拟的角色ID
	 */
	private static final String ROLEID = "6000003" ;
	/**
	 * 充值模拟的渠道ID和渠道订单号,保证联合唯一才能模拟成功
	 */
	private static final int CHANNEL_ID = 1;
	//private static final String CHANNEL_ORDER_ID = "1123414989013";
	/** 计费中心订单号 */
	//private static final String ORDER_ID = "1213156513";
	/**
	 * 充值模拟的充值金额(单位分)
	 */
	private static final int RMB = 500 ;
	
	public static void main(String[] args) throws Exception  {
		try{
			
			AutoLoadMessageMapping mapping = new AutoLoadMessageMapping();
			List<String> pkgList = new ArrayList<String>();
			pkgList.add("platform.message.request");
			pkgList.add("platform.message.response");
			mapping.setPkgList(pkgList);
			mapping.init();
			
			BytesMessageParser parser = new BytesMessageParser();
			parser.setMapping(mapping);
			parser.setProtobufPackagePath("platform.all.message.codec");
			parser.setProtoBuffer(new ByteProtoBuffer());
			parser.init();
			
			
			C5901_ChargeNotifyReqMessage message = new C5901_ChargeNotifyReqMessage();
			message.setAppId(4);
			message.setUserId(USERID);
			message.setChannelId(CHANNEL_ID);
			//message.setChannelOrderId(CHANNEL_ORDER_ID);
			message.setChannelOrderId(String.valueOf(System.currentTimeMillis()));
			//message.setOrderId(ORDER_ID);
			message.setOrderId("" + System.currentTimeMillis());
			message.setMoney(RMB);
			message.setRoleId(ROLEID);
			message.setResult(1);
			
			DefaultIceClient client = new DefaultIceClient(GAME_ICE_URI,1,parser);
			
			C5901_ChargeNotifyRespMessage resp = (C5901_ChargeNotifyRespMessage)client.sendMessage(message);
	        if(resp != null){
	        	int resultid = resp.getResult();
	        	if(resultid == 1){
	        		System.out.println("充值请求发送成功!");
	        	}else{
	        		System.out.println("充值请求发送失败!" + resp.getResult());
	        	}
	        }else{
        		System.out.println("充值请求发送失败!" );
        	}
	        
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
