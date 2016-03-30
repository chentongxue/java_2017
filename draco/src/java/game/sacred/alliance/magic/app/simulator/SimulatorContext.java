package sacred.alliance.magic.app.simulator;

import java.util.ArrayList;
import java.util.List;

import org.apache.mina.core.filterchain.IoFilter;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.project.protobuf.codec.bytes.ByteProtoBuffer;

import sacred.alliance.magic.client.http.HttpStreamClient;
import sacred.alliance.magic.codec.impl.AutoLoadMessageMapping;
import sacred.alliance.magic.codec.impl.bytes.BytesMessageParser;
import sacred.alliance.magic.codec.impl.bytes.DefaultMinaV2Security;
import sacred.alliance.magic.codec.impl.bytes.IoBufferMessageParser;
import sacred.alliance.magic.codec.impl.bytes.MinaV2CodecFactory;

public class SimulatorContext {

	static class SingletonHolder {
		static SimulatorContext singleton = new SimulatorContext();
	}

	public static SimulatorContext getInstance() {
		return SingletonHolder.singleton;
	}
	
	private DefaultMessageBuilder messageBuilder ;
	private PrintMessageFilter printMessageFilter ;
	private IoFilter codecFilter ;
	private HttpStreamClient gateWayClient ;
	private DefaultMinaV2Security minaSecurity ;
	
	private SimulatorContext() {
		try {
			
			AutoLoadMessageMapping messageMapping = new AutoLoadMessageMapping();
			List<String> pkgList = new ArrayList<String>();
			pkgList.add("platform.message.request");
			pkgList.add("platform.message.response");
//			pkgList.add("sacred.alliance.magic.message.request");
//			pkgList.add("sacred.alliance.magic.message.response");
//			pkgList.add("sacred.alliance.magic.message.push");
//			pkgList.add("sacred.alliance.magic.gateway.message.request");
//			pkgList.add("sacred.alliance.magic.gateway.message.response");
//			pkgList.add("sacred.alliance.magic.gateway.message.c2g.request");
//			pkgList.add("sacred.alliance.magic.gateway.message.c2g.response");
//			pkgList.add("sacred.alliance.magic.gateway.message.g2a.request");
//			pkgList.add("sacred.alliance.magic.gateway.message.g2a.response");
//			pkgList.add("sacred.alliance.magic.gateway.message.g2u.request");
//			pkgList.add("sacred.alliance.magic.gateway.message.g2u.response");
//			pkgList.add("sacred.alliance.magic.gateway.message.g2v.request");
//			pkgList.add("sacred.alliance.magic.gateway.message.g2v.response");
			messageMapping.setPkgList(pkgList);
			messageMapping.init();
			
			ByteProtoBuffer byteProtoBuffer = new ByteProtoBuffer() ; 
			
			BytesMessageParser gateWayMessageParser = new BytesMessageParser();
			gateWayMessageParser.setMapping(messageMapping);
			gateWayMessageParser.setProtoBuffer(byteProtoBuffer);
			gateWayMessageParser.setProtobufPackagePath("platform.all.message.codec");
			gateWayMessageParser.init();

			gateWayClient = new HttpStreamClient();
			gateWayClient.setMessageParser(gateWayMessageParser);
			gateWayClient.setConnectionTimeout(30000);
			gateWayClient.setSoTimeout(30000);
			//urlMapping蔚婓俋醱扢离
			//gateWayClient.setUrlMapping(urlMapping);
			
			IoBufferMessageParser messageParser = new IoBufferMessageParser();
			messageParser.setMapping(messageMapping);
			messageParser.setProtoBuffer(byteProtoBuffer);
			messageParser
					.setProtobufPackagePath("sacred.alliance.magic.message.codec");
			messageParser.init();

			this.minaSecurity = new DefaultMinaV2Security();
			this.minaSecurity.setMessageParser(messageParser);
			MinaV2CodecFactory minaV2CodecFactory = new MinaV2CodecFactory();
			minaV2CodecFactory.setMessageParser(messageParser);
			minaV2CodecFactory.setSecurity(minaSecurity);
			
			codecFilter = new ProtocolCodecFilter(minaV2CodecFactory);
			printMessageFilter = new PrintMessageFilter();
			printMessageFilter.setProtoBuffer(byteProtoBuffer);
			
			messageBuilder = new DefaultMessageBuilder();
			messageBuilder.setMessageMapping(messageMapping);
			
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	

	public DefaultMessageBuilder getMessageBuilder() {
		return messageBuilder;
	}


	public PrintMessageFilter getPrintMessageFilter() {
		return printMessageFilter;
	}

	public IoFilter getCodecFilter() {
		return codecFilter;
	}


	public HttpStreamClient getGateWayClient() {
		return gateWayClient;
	}



	public DefaultMinaV2Security getMinaSecurity() {
		return minaSecurity;
	}

	
	
}
