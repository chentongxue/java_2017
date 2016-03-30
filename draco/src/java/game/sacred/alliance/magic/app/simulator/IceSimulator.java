package sacred.alliance.magic.app.simulator;
import java.util.ArrayList;
import java.util.List;

import org.project.protobuf.codec.bytes.ByteProtoBuffer;

import platform.message.request.C5050_RolemeetUpdateReqMessage;
import platform.message.request.C5051_RolemeetQueryReqMessage;

import com.game.draco.GameContext;
import com.game.draco.debug.base.ResourceReloadType;
import com.game.draco.debug.message.request.C10029_ReloadResourceReqMessage;

import sacred.alliance.magic.client.zeroc.DefaultIceClient;
import sacred.alliance.magic.codec.impl.AutoLoadMessageMapping;
import sacred.alliance.magic.codec.impl.bytes.BytesMessageParser;
import sacred.alliance.magic.core.Message;

public class IceSimulator { 

	/**
	 * 服务器ice地址
	 */
	private static final String GAME_ICE_URI = "ice://rolemeetServer:tcp -h 192.168.0.28 -p 17820" ;
	
	/**
	 * 具体的请求逻辑,需要的时候自己实现即可
	 * @return
	 */
	public static Message bulidReqMessage(){
		byte[] data = new byte[1];
//		C5050_RolemeetUpdateReqMessage reqMsg = new C5050_RolemeetUpdateReqMessage();
//		reqMsg.setAppId(GameContext.getAppId());
//		reqMsg.setServerId(String.valueOf(GameContext.getServerId()));
//		reqMsg.setUserId("12121212asd");
//		reqMsg.setData(data);
//		return reqMsg;
		
		C5051_RolemeetQueryReqMessage reqMsg = new C5051_RolemeetQueryReqMessage();
		reqMsg.setAppId(5);
		reqMsg.setUserId("12121212asd");
		return reqMsg;
	}
	
	public static void main(String[] args) throws Exception  {
		try{
			
			 Message reqMsg = bulidReqMessage();
			 if (null == reqMsg) {
				 System.err.println("************* build reqMsg is null ****************");
				 return  ;
			}
			AutoLoadMessageMapping mapping = new AutoLoadMessageMapping();
			List<String> pkgList = new ArrayList<String>();
			pkgList.add("platform.message.request");
			pkgList.add("platform.message.response");
			mapping.setPkgList(pkgList);
			mapping.init();
			
			BytesMessageParser parser = new BytesMessageParser();
			parser.setMapping(mapping);
			parser.setProtobufPackagePath("platform.all.message.codec");
			ByteProtoBuffer protoBuffer = new ByteProtoBuffer() ;
			parser.setProtoBuffer(protoBuffer);
			parser.init();
			System.out.println("===========================================");
			System.out.println("================== REQ MESSAGE =====================");
			System.out.println(protoBuffer.toString(reqMsg));
			System.out.println("");
			DefaultIceClient client = new DefaultIceClient(GAME_ICE_URI,1,parser);
			Message respMsg = client.sendMessage(reqMsg);
			System.out.println("================== RESP MESSAGE =====================");
	        if(null == respMsg){
	        	System.out.println("********************* NULL*************************");
	        }else{
	        	System.out.println(protoBuffer.toString(respMsg));
	        }
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
