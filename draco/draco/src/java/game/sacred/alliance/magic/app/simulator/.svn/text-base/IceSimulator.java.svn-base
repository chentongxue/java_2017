package sacred.alliance.magic.app.simulator;
import java.util.ArrayList;
import java.util.List;

import org.project.protobuf.codec.bytes.ByteProtoBuffer;

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
	private static final String GAME_ICE_URI = "ice://gameServer:tcp -h 127.0.0.1 -p 10420" ;
	
	/**
	 * 具体的请求逻辑,需要的时候自己实现即可
	 * @return
	 */
	public static Message bulidReqMessage(){
		C10029_ReloadResourceReqMessage reqMsg = new C10029_ReloadResourceReqMessage();
		reqMsg.setType((byte)ResourceReloadType.Skill.getType());
		return reqMsg ;
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
			pkgList.add("sacred.alliance.magic.debug.message.request");
			pkgList.add("sacred.alliance.magic.debug.message.response");
			mapping.setPkgList(pkgList);
			mapping.init();
			
			BytesMessageParser parser = new BytesMessageParser();
			parser.setMapping(mapping);
			parser.setProtobufPackagePath("sacred.alliance.magic.debug.message.codec");
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
