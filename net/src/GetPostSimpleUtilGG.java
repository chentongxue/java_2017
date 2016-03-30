

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;
/***
 * 2015年12月22日13:06:39 测试谷歌
 */
public class GetPostSimpleUtilGG {
	public static String LOGIN_KEY = "slg@sincetimes.com123!";
	public  static String RESET_URL = "http://ec2-52-33-4-138.us-west-2.compute.amazonaws.com/restart";
	private static String CHANNEL_ID = "21102";// googleplayer
	private static String CHANNEL_ID_FB = "21101";// facebook

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
    /**
     * 使用GET方法向服务器提交数据
     * @throws Exception
     */
     public static String sendGet(String path){
          
		try {
			URL url = new URL(path);
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
	          conn.setConnectTimeout(5000);
	          conn.setRequestMethod("GET");
	          if(conn.getResponseCode()==200){
	              InputStream is = conn.getInputStream();//因为这里是从服务器得到信息
	              byte[] data = getStreamBytes(is);
	              LogA.i(new String(data));
	              return new String(data);
	          }
		} catch (Exception e) {
			LogA.i(e.toString());
			e.printStackTrace();
		}
        return null;
     }
     public static String post(String url, Map<String,String> params){
    	 String content = "";
    	 for(Map.Entry<String, String> entry:params.entrySet()){
    		 String key = entry.getKey();
    		 String value = entry.getValue();
    		 content += key + "=" + value + "&";
    	 }
    	 
    	 content = content.substring(0, content.length() -1);
    	 System.err.println(content);
    	 return sendPost(url, content);
     }
     public static String sendPost(String path, String content){
    	 int resultCode = -1;
    	 try {
    		 URL url = new URL(path);
    		 HttpURLConnection conn = (HttpURLConnection)url.openConnection();
    		 conn.setConnectTimeout(5000);
    		 conn.setRequestMethod("POST");
    		 conn.setRequestProperty("Content-Type", " application/x-www-form-urlencoded");
//        	 String content = "name="+name+"&password="+password;
        	 byte[] data = content.getBytes();
        	 conn.setRequestProperty("Content-Length", data.length+"");
    		 conn.setDoOutput(true);
        	 conn.getOutputStream().write(data);
    		 resultCode = conn.getResponseCode();
    		 if(resultCode == 200){
    			 InputStream is = conn.getInputStream();//因为这里是从服务器得到信息
    			 byte[] d = getStreamBytes(is);
    			 LogA.i("通过POST:\n"+new String(d));
    			 return new String(d);
    		 }else{
    			 LogA.i("通过POST 失败"+conn.getResponseCode());
    		 }
    	 } catch (Exception e) {
    		 LogA.i("通过POST shibai"+resultCode+e.toString());
    	 }
    	 LogA.i("通过POST" +"end");
    	 return null;
     }
     public static void main(String args[]){
    	 String openid = "1452062127207017793";
    	 String account =  "1662048590744007";
//    	 String account =  "105537450312123635243";
    	 String account2 =  "00457719987502410042";
    	 //quick login params, openid=,deviceCode=358239052723139-00904c50f8ec,channel=1042,deviceType=ANDROID
    	 String token = "CAAXQCPptFdwBAPCXJCdDdSsUdP7ZA2J5A211ouTnxKWEoS6XK5hPOURlFIhaJCA9tnysvGgRR4R1Bsu0a0wBznizZBrltp4V5Fuhk0PTwgODonRDTSpSUEKQTu8PTL9wNHVmwcBJs8n79lRyPScB6xy8GhHuyYxtJU1ZBVvtbX2pQGECkSZBZAvMRRJuVjwwZD";
    	 String deviceCode="00u4209020356741-88e3ab774d004";
//    	 String deviceCode="00158239052723139-00904c50f8ec";
//    	 LogA.i("bind test:"+testBind(openid, account2));
//    	 LogA.i("bind test:"+testBind(CHANNEL_ID_FB, openid, accoundt2));//5:没有传openid
//    	 LogA.i("unbind test:"+testUnBind(account));//【解绑】 成功0 2 ry
    	 LogA.i("isbind test:"+testIsBind(account, CHANNEL_ID_FB, token));//31:已解绑; 10，从未绑定,0,32已经绑定的
    	 LogA.i("isbind test:"+testIsBind2(openid,CHANNEL_ID_FB));//31,,33未绑定       32:已绑定
//    	 LogA.i("quick login test:"+testQuickLogin(deviceCode));
//    	 LogA.i("quick login test:"+testReset());e4
     }
     public static String testBind(String openid, String account){
    	 
    	 String ts = "http://ec2-52-33-4-138.us-west-2.compute.amazonaws.com/account?"
    	 		+ "account=101577199875024042795&"
    	 		+ "openid=1450268118013016874&"
    	 		+ "token=ya29.VAJa1pTOnTTE5bJ-w8bTMxW6fYgrNdJe3gP78Grw6WVt4CfwGRI9VHva8aY7juN19B0jvw&"
    	 		+ "uniqueCode=1450919525905&"
    	 		+ "channel=21102&"
    	 		+ "deviceType=ANDROID&"
    	 		+ "deviceCode=864895022518292-0c1dafc10086";
    	
    	 String uniCode = "14498051118481666543123627887";
    	 String deviceCode = "864895022518292-0c1dafc69140";
    	 String deviceType = "ANDROID";
//    	 String channel = "1042";
    	 String token = "CAAFDDRlIT2wBAIcRmLaZAiYZBFZA4AhZCrQtdfrY62LewGr9q1C0otb3F54jF8E2ZAnmLdWXhkm08CGEM6Lq0CGpwIA3r368i88cBt1s5sZCP0ByJgIBlTOAxLs6zLLbRTwK5ZCtlm7opvVfV28U3dgROcF7STL3sPKHYPg23ZAwrvFEOpFbK2tor6k7cMl7jUUi7m9iSmSD8wZDZD";
    	 Map<String,String> params = new HashMap<String, String>();
    	 params.put("openid", openid);//测试的时候随机下
    	 params.put("deviceCode", deviceCode);
    	 params.put("channel", CHANNEL_ID);
    	 params.put("deviceType", deviceType);
    	 params.put("uniqueCode", uniCode);
    	 params.put("token", token);
    	 params.put("account", account);
    	
    	 return GetPostSimpleUtil.post(ServerInfo.bindUrl, params);
     }
     public static String testBind(String channelId, String openid, String account){
    	 
    	 String ts = "http://ec2-52-33-4-138.us-west-2.compute.amazonaws.com/account?"
    			 + "account=101577199875024042795&"
    			 + "openid=1450268118013016874&"
    			 + "token=ya29.VAJa1pTOnTTE5bJ-w8bTMxW6fYgrNdJe3gP78Grw6WVt4CfwGRI9VHva8aY7juN19B0jvw&"
    			 + "uniqueCode=1450919525905&"
    			 + "channel=21102&"
    			 + "deviceType=ANDROID&"
    			 + "deviceCode=864895022518292-0c1dafc10086";
    	 
    	 String uniCode = "14498051118481666543123627887";
    	 String deviceCode = "864895022518292-0c1dafc69140";
    	 String deviceType = "ANDROID";
//    	 String channel = "1042";
    	 String token = "CAAFDDRlIT2wBAIcRmLaZAiYZBFZA4AhZCrQtdfrY62LewGr9q1C0otb3F54jF8E2ZAnmLdWXhkm08CGEM6Lq0CGpwIA3r368i88cBt1s5sZCP0ByJgIBlTOAxLs6zLLbRTwK5ZCtlm7opvVfV28U3dgROcF7STL3sPKHYPg23ZAwrvFEOpFbK2tor6k7cMl7jUUi7m9iSmSD8wZDZD";
    	 Map<String,String> params = new HashMap<String, String>();
    	 params.put("openid", openid);//测试的时候随机下
    	 params.put("deviceCode", deviceCode);
    	 params.put("channel", channelId);
    	 params.put("deviceType", deviceType);
    	 params.put("uniqueCode", uniCode);
    	 params.put("token", token);
    	 params.put("account", account);
    	 
    	 return GetPostSimpleUtil.post(ServerInfo.bindUrl, params);
     }
     public static String testUnBind(String account){
    	 
    	 String ts = "12-11 11:39:02.396: I/POST(3177): >>>>"
    	 		+ "http://ec2-52-33-4-138.us-west-2.compute.amazonaws.com/account"
    	 		+ "?token=CAAFDDRlIT2wBAMRVBWVOfZA767lHmJdxFZBR33UbFXu00QMaBMSbTtAM237tNf9cQ457ndZAZCZC6GWM5AuG7ok4xwV9cpgBf534PA1rRwrRBAWDrmkLcdiC3ZC8zbNEOEkuBwQbhASexaO6NMqwEgxWClsZBuoQ76zDyDsZBVqQvrZBLd4P8HNDibjuRL4iDNZCUZD"
    	 		+ "&deviceType=ANDROID"
    	 		+ "&deviceCode=864895022518292-0c1dafc69140"
    	 		+ "&account=1666543123627888"
    	 		+ "&openid=21412250250"
    	 		+ "&type=unbind"
    	 		+ "&uniqueCode=14498051424011666543123627887"
    	 		+ "&channel=1042";
//    	 String openid = "21412250250";
    	 String uniCode = System.currentTimeMillis()+account;
    	 String deviceCode = "864895022518292-0c1dafc69140";
    	 String deviceType = "ANDROID";
    	 String channel = CHANNEL_ID;
    	 String token = "CAAFDDRlIT2wBAMRVBWVOfZA767lHmJdxFZBR33UbFXu00QMaBMSbTtAM237tNf9cQ457ndZAZCZC6GWM5AuG7ok4xwV9cpgBf534PA1rRwrRBAWDrmkLcdiC3ZC8zbNEOEkuBwQbhASexaO6NMqwEgxWClsZBuoQ76zDyDsZBVqQvrZBLd4P8HNDibjuRL4iDNZCUZD";
    	 Map<String,String> params = new HashMap<String, String>();
//    	 params.put("openid", openid);
    	 params.put("deviceCode", deviceCode);
    	 params.put("channel", channel);
    	 params.put("deviceType", deviceType);
    	 params.put("uniqueCode", uniCode);
    	 params.put("token", token);
    	 params.put("type", "unbind");
    	 params.put("account", account);
    	
    	 return GetPostSimpleUtil.post(ServerInfo.bindUrl, params);
     }
     public static String testIsBind(String account,String channel,String token){
    	 
//    	 String openid = "21412250250";
//    	 String account =  "101577199875024042795";
    	 String uniCode = System.currentTimeMillis()+account;
    	 String deviceCode = "864895022518292-0c1dafc69140";
    	 String deviceType = "ANDROID";
    	 Map<String,String> params = new HashMap<String, String>();
//    	 params.put("openid", openid+"1");
    	 params.put("deviceCode", deviceCode);
    	 params.put("channel", channel);
    	 params.put("deviceType", deviceType);
    	 params.put("uniqueCode", uniCode);
    	 params.put("token", token);
    	 params.put("type", "isbind");
    	 params.put("account", account);
    	 
    	 return GetPostSimpleUtil.post(ServerInfo.bindUrl, params);
     }
     public static String testIsBind2(String openid,String channel){
    	 
//    	 String openid = "21412250250";
    	 String uniCode = System.currentTimeMillis()+"testisbind";
    	 String deviceCode = "864895022518292-0c1dafc69140";
    	 String deviceType = "ANDROID";
    	 String token = "CAAFDDRlIT2wBAMRVBWVOfZA767lHmJdxFZBR33UbFXu00QMaBMSbTtAM237tNf9cQ457ndZAZCZC6GWM5AuG7ok4xwV9cpgBf534PA1rRwrRBAWDrmkLcdiC3ZC8zbNEOEkuBwQbhASexaO6NMqwEgxWClsZBuoQ76zDyDsZBVqQvrZBLd4P8HNDibjuRL4iDNZCUZD";
    	 Map<String,String> params = new HashMap<String, String>();
    	 params.put("openid", openid);
    	 params.put("channel", channel);
    	 
    	 return GetPostSimpleUtil.post(ServerInfo.isbindUrl, params);
     }
     /***
      * 4为 
      * 不传openid为快速登陆
      * @return
      */
     public static String testQuickLogin(String deviceCode){
    	 String s="http://ec2-52-33-4-138.us-west-2.compute.amazonaws.com/login"
    	 		+ "?openid=3&"
    	 		+ "deviceCode=864895022518292-0c1dafc69140"
    	 		+ "&channel=1042"
    	 		+ "&deviceType=ANDROID"
    	 		+ "&uniqueCode=14490646345"
    	 		+ "&token=14490646345";
    	 
//    	 String openid = "21412250250";
    	 String openid = "";
    	 String uniCode = System.currentTimeMillis()+"test001";
    	 String deviceType = "ANDROID";
    	 String token = "wqe";
 		String key = md5(LOGIN_KEY+openid);
//    	 String token = "CAAFDDRlIT2wBAMRVBWVOfZA767lHmJdxFZBR33UbFXu00QMaBMSbTtAM237tNf9cQ457ndZAZCZC6GWM5AuG7ok4xwV9cpgBf534PA1rRwrRBAWDrmkLcdiC3ZC8zbNEOEkuBwQbhASexaO6NMqwEgxWClsZBuoQ76zDyDsZBVqQvrZBLd4P8HNDibjuRL4iDNZCUZD";
    	 Map<String,String> params = new HashMap<String, String>();
    	 params.put("openid", openid);
    	 params.put("deviceCode", deviceCode);
    	 params.put("channel", CHANNEL_ID);
    	 params.put("deviceType", deviceType);
    	 params.put("uniqueCode", uniCode);
    	 params.put("key", key);
    	 System.err.println(key);
    	 System.err.println(key.length());
    	 return GetPostSimpleUtil.post(ServerInfo.loginUrl, params);
     }
     public static String testReset(){

    	 String s="http://ec2-52-33-4-138.us-west-2.compute.amazonaws.com/login"
    			 + "?openid=3&"
    			 + "deviceCode=864895022518292-0c1dafc69140"
    			 + "&channel=1042"
    			 + "&deviceType=ANDROID"
    			 + "&uniqueCode=14490646345"
    			 + "&token=14490646345";
    	 
    	 String uniCode = System.currentTimeMillis()+"";
    	 String deviceCode = "864895022518292-0c1dafc199AAA";
    	 String deviceType = "ANDROID";
    	 String channel = "1042";
//    	 String token = "CAAFDDRlIT2wBAMRVBWVOfZA767lHmJdxFZBR33UbFXu00QMaBMSbTtAM237tNf9cQ457ndZAZCZC6GWM5AuG7ok4xwV9cpgBf534PA1rRwrRBAWDrmkLcdiC3ZC8zbNEOEkuBwQbhASexaO6NMqwEgxWClsZBuoQ76zDyDsZBVqQvrZBLd4P8HNDibjuRL4iDNZCUZD";
    	 Map<String,String> params = new HashMap<String, String>();
    	 params.put("deviceCode", deviceCode);
    	 params.put("channel", channel);
    	 params.put("deviceType", deviceType);
    	 params.put("uniqueCuode", uniCode);
    	 
    	 return GetPostSimpleUtil.post(RESET_URL, params);
     }
	  public static String md5(String source) {
		    try {
		      MessageDigest md = MessageDigest.getInstance("MD5");
		      md.update(source.getBytes("UTF-8"));
		      byte[] bytes = md.digest();

		      char[] chars = new char[32];
		      char[] hexDigits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
		      int k = 0;
		      for (int i = 0; i < 16; i++) {
		        byte byte0 = bytes[i];
		        chars[(k++)] = hexDigits[(byte0 >>> 4 & 0xF)];
		        chars[(k++)] = hexDigits[(byte0 & 0xF)];
		      }
		      return new String(chars);
		    }
		    catch (Exception e) {
		      e.printStackTrace();
		    }return null;
		  }
}

