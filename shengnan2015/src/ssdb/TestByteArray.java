package ssdb;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Arrays;

public class TestByteArray {
	public static void main(String args[]) throws UnsupportedEncodingException{
//		String s = "hello";
		String s1 = "你好";
//		byte[] a = s.getBytes();
		byte[] b = s1.getBytes();
		byte[] b2 = s1.getBytes("UTF-8");
		byte[] b3 = s1.getBytes("GBK");
//		System.out.println(Arrays.toString(a));
//		System.out.println(a.length);
		System.err.println(Arrays.toString(b));
		System.err.println(b.length);
		System.err.println(Arrays.toString(b2));
		System.err.println(b2.length);
		System.err.println(Arrays.toString(b3));
		System.err.println(b3.length);
		
		System.out.println(printByte2Char(b));
		System.out.println(printByte2Char(b2));
		System.out.println(printByte2Char(b3));
		
		String t = "你好";
		byte[] bt = t.getBytes("UTF-8");
		String tt = new String(bt);
		System.out.println(tt);
		
		String csn = Charset.defaultCharset().name();
		System.out.println(csn);
	}
	private static String printByte2Char(byte[] b){
		StringBuilder s = new StringBuilder();
		for (int i = 0; i < (char)b.length; i++) {
			char c = (char)b[i];
			s.append(c);
		}
		return s.toString();
	}

}
