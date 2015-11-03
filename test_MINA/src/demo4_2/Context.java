package demo4_2;

import org.apache.mina.core.buffer.IoBuffer;

public class Context {
	private IoBuffer buf;

	public Context() {
		buf = IoBuffer.allocate(100).setAutoExpand(true);
	}

	public IoBuffer getBuf() {
		return buf;
	}

}
