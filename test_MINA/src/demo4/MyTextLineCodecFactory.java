package demo4;

//import org.apache.mina.common.IoSession;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

public class MyTextLineCodecFactory implements ProtocolCodecFactory {

	public ProtocolDecoder getDecoder(IoSession session) throws Exception {
		return new MyTextLineCodecDecoder();
	}

	public ProtocolEncoder getEncoder(IoSession session) throws Exception {
		return new MyTextLineCodecEncoder();
	}
}
