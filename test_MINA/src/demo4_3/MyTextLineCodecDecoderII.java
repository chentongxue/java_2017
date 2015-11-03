package demo4_3;

import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

public class MyTextLineCodecDecoderII implements ProtocolDecoder {
	private Charset charset; // �����ʽ

	private String delimiter; // �ı��ָ���

	private IoBuffer delimBuf; // �ı��ָ��ƥ��ı���

	// ���峣��ֵ����Ϊÿ��IoSession�б�����������keyֵ
	private static String CONTEXT = MyTextLineCodecDecoderII.class.getName()
			+ ".context";
	// ���캯��������ָ��Charset���ı��ָ���
	public MyTextLineCodecDecoderII(Charset charset, String delimiter) {
		this.charset = charset;
		this.delimiter = delimiter;
	}

	public void decode(IoSession session, IoBuffer in, ProtocolDecoderOutput out)
			throws Exception {
		Context ctx = getContext(session);
		if (delimiter == null || "".equals(delimiter)) { // ����ı����з�δָ����ʹ��Ĭ��ֵ
			delimiter = "\r\n";
		}
		if (charset == null) {
			charset = Charset.forName("utf-8");
		}
		decodeNormal(ctx, in, out);
	}

	// ��IoSession�л�ȡContext����
	private Context getContext(IoSession session) {
		Context ctx;
		ctx = (Context) session.getAttribute(CONTEXT);
		if (ctx == null) {
			ctx = new Context();
			session.setAttribute(CONTEXT, ctx);
		}
		return ctx;
	}

	// ����
	private void decodeNormal(Context ctx, IoBuffer in,
			ProtocolDecoderOutput out) throws CharacterCodingException {
		// ȡ��δ����������Ѿ�ƥ����ı����з��ĸ���
		int matchCount = ctx.getMatchCount();

		// ����ƥ���ı����з���IoBuffer����
		if (delimBuf == null) {
			IoBuffer tmp = IoBuffer.allocate(2).setAutoExpand(true);
			tmp.putString(delimiter, charset.newEncoder());
			tmp.flip();
			delimBuf = tmp;
		}

		int oldPos = in.position(); // �����IoBuffer�����ݵ�ԭʼ��Ϣ
		int oldLimit = in.limit();
		while (in.hasRemaining()) { // ���������IoBuffer
			byte b = in.get();
			if (delimBuf.get(matchCount) == b) { // ƥ���matchCountλ���з��ɹ�
				matchCount++;
				if (matchCount == delimBuf.limit()) { // ��ǰƥ�䵽�ֽڸ������ı����з��ֽڸ�����ͬ��ƥ�����
					int pos = in.position(); // ��õ�ǰƥ�䵽��position��positionǰ����������Ч��
					in.limit(pos);
					in.position(oldPos); // position�ص�ԭʼλ��

					ctx.append(in); // ׷�ӵ�Context����δ������ݺ���

					in.limit(oldLimit); // in��ƥ�������ʣ������
					in.position(pos);

					IoBuffer buf = ctx.getBuf();
					buf.flip();
					buf.limit(buf.limit() - matchCount);// ȥ��ƥ�������е��ı����з�
					try {
						out.write(buf.getString(ctx.getDecoder())); // �����������
					} finally {
						buf.clear(); // �ͷŻ���ռ�
					}

					oldPos = pos;
					matchCount = 0;
				}
			} else {
				// ���matchCount==0�������ƥ��
				// ���matchCount>0��˵��û��ƥ�䵽�ı����з����е�ǰһ��ƥ��ɹ��ֽڵ���һ���ֽڣ�
				// ��ת��ƥ��ʧ���ַ���������matchCount=0������ƥ��
				in.position(in.position() - matchCount);
				matchCount = 0; // ƥ��ɹ���matchCount�ÿ�
			}
		}

		// ��in��δ�������ݷŻ�buf��
		in.position(oldPos);
		ctx.append(in);

		ctx.setMatchCount(matchCount);
	}

	public void dispose(IoSession session) throws Exception {

	}

	public void finishDecode(IoSession session, ProtocolDecoderOutput out)
			throws Exception {
	}

	// �ڲ��࣬����IoSession����ʱδ��ɵ�����
	private class Context {
		private CharsetDecoder decoder;
		private IoBuffer buf; // ������ʵ��������
		private int matchCount = 0; // ƥ�䵽���ı����з�����

		private Context() {
			decoder = charset.newDecoder();
			buf = IoBuffer.allocate(80).setAutoExpand(true);
		}

		// ����
		public void reset() {
			matchCount = 0;
			decoder.reset();
		}

		// ׷������
		public void append(IoBuffer in) {
			getBuf().put(in);
		}

		// ======get/set����=====================
		public CharsetDecoder getDecoder() {
			return decoder;
		}

		public IoBuffer getBuf() {
			return buf;
		}

		public int getMatchCount() {
			return matchCount;
		}

		public void setMatchCount(int matchCount) {
			this.matchCount = matchCount;
		}
	} // end class Context;
}
