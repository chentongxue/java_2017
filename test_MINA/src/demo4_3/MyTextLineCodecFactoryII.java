package demo4_3;

import java.nio.charset.Charset;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

public class MyTextLineCodecFactoryII implements ProtocolCodecFactory {
	private Charset charset; // �����ʽ

	private String delimiter; // �ı��ָ���

	public MyTextLineCodecFactoryII(Charset charset, String delimiter) {
		this.charset = charset;
		this.delimiter = delimiter;
	}

	public ProtocolDecoder getDecoder(IoSession session) throws Exception {
		return new MyTextLineCodecDecoderII(charset, delimiter);
	}

	public ProtocolEncoder getEncoder(IoSession session) throws Exception {
		return new MyTextLineCodecEncoderII(charset, delimiter);
	}
}
