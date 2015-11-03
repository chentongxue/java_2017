package demo4;

import java.nio.charset.Charset;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

public class MyTextLineCodecDecoder implements ProtocolDecoder {
	private Charset charset = Charset.forName("UTF-8");

	IoBuffer buf = IoBuffer.allocate(100).setAutoExpand(true);

	public void decode(IoSession session, IoBuffer in, ProtocolDecoderOutput out)
			throws Exception {
		while (in.hasRemaining()) {
		
			byte b = in.get();
			buf.put(b);
			if (b == '\n') {
				/**
				 * limit=position , position=0,����mask��
				 * Ϊ�˶�ȡ����׼����һ���ǽ���buf��������bufд�������ʱ���ã�
				 * �������Ҫ���ã������п���position!=limit�� ����position����û�����ݣ�
				 * ÿ��д�����ݵ������ʱ������ȷ��position=limit
				 */
				buf.flip();
				byte[] msg = new byte[buf.limit()];
				buf.get(msg);
				String message = new String(msg, charset);
				// ����ɹ�����buf����
				/**
				 * ����IoBufferʵ��
				 * ��һ������ָ����ʼ������
				 * �ڶ�������ָ��ʹ��ֱ�ӻ���������JAVA
				 * �ڴ�ѵĻ�������Ĭ��Ϊfalse��
				 */
				buf = IoBuffer.allocate(100).setAutoExpand(true);
				out.write(message);
			}
		}

	}

	public void dispose(IoSession session) throws Exception {
	}

	public void finishDecode(IoSession session, ProtocolDecoderOutput out)
			throws Exception {
	}
}
