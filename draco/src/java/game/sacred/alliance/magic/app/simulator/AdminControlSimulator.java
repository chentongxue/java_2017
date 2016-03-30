package sacred.alliance.magic.app.simulator;
import java.util.ArrayList;
import java.util.List;

import org.project.protobuf.codec.bytes.ByteProtoBuffer;

import com.game.draco.debug.message.request.C10063_AdminControlReqMessage;
import com.game.draco.debug.message.response.C10063_AdminControlRespMessage;

import sacred.alliance.magic.client.zeroc.DefaultIceClient;
import sacred.alliance.magic.codec.impl.AutoLoadMessageMapping;
import sacred.alliance.magic.codec.impl.bytes.BytesMessageParser;

public class AdminControlSimulator { 


	private static final String GAME_ICE_URI = "ice://gameServer:tcp -h 127.0.0.1 -p 10420" ;
	
	
	public static void main(String[] args) throws Exception  {
		try{
			
			AutoLoadMessageMapping mapping = new AutoLoadMessageMapping();
			List<String> pkgList = new ArrayList<String>();
			pkgList.add("sacred.alliance.magic.debug.message.request");
			pkgList.add("sacred.alliance.magic.debug.message.response");
			mapping.setPkgList(pkgList);
			mapping.init();
			
			BytesMessageParser parser = new BytesMessageParser();
			parser.setMapping(mapping);
			parser.setProtobufPackagePath("sacred.alliance.magic.debug.message.codec");
			parser.setProtoBuffer(new ByteProtoBuffer());
			parser.init();
			
			
			C10063_AdminControlReqMessage reqMsg = new C10063_AdminControlReqMessage();
			reqMsg.setJarName("xxxxxx.jar");
			reqMsg.setArgs(new String[]{"a","b"});
			
			DefaultIceClient client = new DefaultIceClient(GAME_ICE_URI,1,parser);
			
			C10063_AdminControlRespMessage respMsg = (C10063_AdminControlRespMessage)client.sendMessage(reqMsg);
	        if(null != respMsg){
	        	System.out.println(respMsg.getInfo());
	        }else{
        		System.out.println("respMsg is null" );
        	}
	        
		}catch(Exception e){
			e.printStackTrace();
		}
		System.exit(1);
	}

}
