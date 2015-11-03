package encode;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String s = "%22%E6%8B%89%E5%8D%A1%C2%B7%E5%88%80%E9%94%8B%22";
		String name;
		try {
//			name = new String( s.getBytes("iso8859-1"),"utf-8");
			
			String ss = URLDecoder.decode(s,"utf-8");
			System.out.println("name = " + ss);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	        

	}

}
