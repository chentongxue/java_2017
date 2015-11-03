package base64;

import org.apache.commons.codec.binary.Base64;

public class Base64Test {
	public static void main(String args[]){
		Base64 bs = new Base64();
		String s = "Ð¡±¦.";
		String ss = bs.encodeAsString(s.getBytes());
		System.out.println(ss);
		String s2 = new String(bs.decode(ss.getBytes()));
		System.out.println(s2);
	}
}
