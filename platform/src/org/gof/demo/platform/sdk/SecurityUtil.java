package org.gof.demo.platform.sdk;

import java.security.MessageDigest;
import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class SecurityUtil {


	/**
	 * 唯一识别码
	 * @return
	 */
	public static String getUUID() {
		String uuid = UUID.randomUUID().toString();
		StringBuilder sb = new StringBuilder();
		sb.append(uuid.substring(0, 8));
		sb.append(uuid.substring(9, 13));
		sb.append(uuid.substring(14, 18));
		sb.append(uuid.substring(19, 23));
		sb.append(uuid.substring(24));
		return sb.toString();
	}

	/**
	 * MD5加密
	 * @param source
	 * @return
	 */
	public static String md5(String source) {
		StringBuilder sb = new StringBuilder();
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(source.getBytes("UTF-8"));
			byte bytes[] = md.digest();
			String tempStr = "";
			for (int i = 0; i < bytes.length; i++) {
				tempStr = (Integer.toHexString(bytes[i] & 0xff));
	            if (tempStr.length() == 1) {
	            	sb.append("0").append(tempStr);
	            } else {
	            	sb.append(tempStr);
	            }
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sb.toString();
	}
	
	/**
	 * 安全哈希签名
	 * @param source
	 * @return
	 */
	public static String sha1(String source){
		StringBuilder sb = new StringBuilder();
		try {
			MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
			byte[] bytes = messageDigest.digest(source.getBytes("UTF-8"));
			String tempStr = "";
			for (int i = 0; i < bytes.length; i++) {
				tempStr = (Integer.toHexString(bytes[i] & 0xff));
	            if (tempStr.length() == 1) {
	            	sb.append("0").append(tempStr);
	            } else {
	            	sb.append(tempStr);
	            }
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sb.toString();
	}
	
	/**
	 * HmacMD5
	 * @param key
	 * @param params
	 * @return
	 */
	public static String hmacMD5(String key, String... strs){
		try {
			Mac mac = Mac.getInstance("HmacMD5");
	        mac.init(new SecretKeySpec(key.getBytes(), "HmacMD5"));
	        for(String str : strs){
	            mac.update(str.getBytes());
	        }
	        return HexUtil.toHexString(mac.doFinal()); 
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	
	//计算密码的强度
	public static int getPasswordStrength(String password) {
			boolean hasNum = false;
			boolean hasUpperLetter = false;
			boolean hasLowerLetter = false;
			for(int i=0;i<password.length();i++) {
				int chr = password.charAt(i); 
				if(chr>=48 && chr<=57) {//数字
					hasNum = true;
				}
				if(chr>=65 && chr<=90) {//大写字母
					hasUpperLetter = true;
				}
				if(chr>=97 && chr<=122) {//小写字母
					hasLowerLetter = true;
				}
			}
			int count = 0;
			if(hasNum) {
				count ++;
			}
			if(hasUpperLetter) {
				count ++;
			}
			if(hasLowerLetter) {
				count ++;
			}
			if(count==3 && password.length()<9)  {
				count --;
			}
			return count;
	}

}
