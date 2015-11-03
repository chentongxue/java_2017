package http;

import java.security.MessageDigest;

public class MD5Util {
	
	public static String md5(String source){
		try{
			//System.out.println(str.getBytes("UTF-8"));
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(source.getBytes("UTF-8"));
			byte[] bytes = md.digest();
			//System.out.println(new String(bytes));
			char[] chars = new char[16*2];
			char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',  'e', 'f'};			
			int k=0;
			for(int i=0;i<16;i++){
				byte byte0 = bytes[i];                 
			    chars[k++] = hexDigits[byte0 >>> 4 & 0xf];		                      
			    chars[k++] = hexDigits[byte0 & 0xf];			    
			}					
		    return new String(chars);
		}
		catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	public static void main(String[] args){
		System.out.println(md5("bao"+"hqfy"));
	}
	
	
}
