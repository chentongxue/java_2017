package demo4_3;

import java.nio.charset.Charset;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

public class MyTextLineCodecEncoderII implements ProtocolEncoder {
	private Charset charset; // �����ʽ

	private String delimiter; // �ı��ָ���

	public MyTextLineCodecEncoderII(Charset charset, String delimiter) {
		this.charset = charset;
		this.delimiter = delimiter;
	}

	public void encode(IoSession session, Object message,
			ProtocolEncoderOutput out) throws Exception {
		if (delimiter == null || "".equals(delimiter)) { // ����ı����з�δָ����ʹ��Ĭ��ֵ
			delimiter = "\r\n";
		}
		if (charset == null) {
			charset = Charset.forName("utf-8");
		}

		String value = message.toString();
		IoBuffer buf = IoBuffer.allocate(value.length()).setAutoExpand(true);
		buf.putString(value, charset.newEncoder()); // ��ʵ����
		buf.putString(delimiter, charset.newEncoder()); // �ı����з�
		buf.flip();
		out.write(buf);
	}

	public void dispose(IoSession session) throws Exception {
	}
}
