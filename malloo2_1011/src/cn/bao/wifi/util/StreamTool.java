package cn.bao.wifi.util;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class StreamTool {
	// 把inputstream的信息转化成byte[]返回
	public static byte[] getStreamBytes(InputStream is) throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		while ((len = is.read(buffer)) != -1) {
			baos.write(buffer, 0, len);
		}
		byte[] b = baos.toByteArray();
		is.close();
		baos.close();
		return b;
	}
}
