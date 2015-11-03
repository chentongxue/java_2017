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
				 * limit=position , position=0,重置mask，
				 * 为了读取做好准备，一般是结束buf操作，将buf写入输出流时调用；
				 * 这个必须要调用，否则极有可能position!=limit， 导致position后面没有数据；
				 * 每次写入数据到输出流时，必须确保position=limit
				 */
				buf.flip();
				byte[] msg = new byte[buf.limit()];
				buf.get(msg);
				String message = new String(msg, charset);
				// 解码成功，把buf重置
				/**
				 * 创建IoBuffer实例
				 * 第一个参数指定初始化容量
				 * 第二个参数指定使用直接缓冲区还是JAVA
				 * 内存堆的缓存区，默认为false。
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
