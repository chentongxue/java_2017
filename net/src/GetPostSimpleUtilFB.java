

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
/***
 * 2015年12月8日17:20:11
 */
public class GetPostSimpleUtilFB {
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
//    	 LogA.i("bind test:"+testBind());
//    	 LogA.i("unbind test:"+testUnBind());//【解绑】 成功0 2 
//    	 LogA.i("isbind test:"+testIsBind());//是否绑定  {"code":31,"msg":""}已解绑
    	 LogA.i("quick login test:"+testQuickLogin());
     }
     public static String testBind(){
    	 
    	 String ts = "http://ec2-52-33-4-138.us-west-2.compute.amazonaws.com/account"
    	 		+ "?account=1666543123627887"
    	 		+ "&openid=21412250250"
    	 		+ "&token=CAAFDDRlIT2wBAIcRmLaZAiYZBFZA4AhZCrQtdfrY62LewGr9q1C0otb3F54jF8E2ZAnmLdWXhkm08CGEM6Lq0CGpwIA3r368i88cBt1s5sZCP0ByJgIBlTOAxLs6zLLbRTwK5ZCtlm7opvVfV28U3dgROcF7STL3sPKHYPg23ZAwrvFEOpFbK2tor6k7cMl7jUUi7m9iSmSD8wZDZD"
    	 		+ "&uniqueCode=14498064775091666543123627887"
    	 		+ "&channel=1042"
    	 		+ "&deviceType=ANDROID"
    	 		+ "&deviceCode=864895022518292-0c1dafc69140";
    	 String openid = "21412250250";
    	 String account =  "1666543123627887";
    	 String uniCode = "14498051118481666543123627887";
    	 String deviceCode = "864895022518292-0c1dafc69140";
    	 String deviceType = "ANDROID";
    	 String channel = "1042";
    	 String token = "CAAFDDRlIT2wBAIcRmLaZAiYZBFZA4AhZCrQtdfrY62LewGr9q1C0otb3F54jF8E2ZAnmLdWXhkm08CGEM6Lq0CGpwIA3r368i88cBt1s5sZCP0ByJgIBlTOAxLs6zLLbRTwK5ZCtlm7opvVfV28U3dgROcF7STL3sPKHYPg23ZAwrvFEOpFbK2tor6k7cMl7jUUi7m9iSmSD8wZDZD";
    	 Map<String,String> params = new HashMap<String, String>();
    	 params.put("openid", openid);//测试的时候随机下
    	 params.put("deviceCode", deviceCode);
    	 params.put("channel", channel);
    	 params.put("deviceType", deviceType);
    	 params.put("uniqueCode", uniCode);
    	 params.put("token", token);
    	 params.put("account", account);
    	
    	 return GetPostSimpleUtil.post(ServerInfo.bindUrl, params);
     }
     public static String testUnBind(){
    	 
    	 String ts = "12-11 11:39:02.396: I/POST(3177): >>>>"
    	 		+ "http://ec2-52-33-4-138.us-west-2.compute.amazonaws.com/account"
    	 		+ "?token=CAAFDDRlIT2wBAMRVBWVOfZA767lHmJdxFZBR33UbFXu00QMaBMSbTtAM237tNf9cQ457ndZAZCZC6GWM5AuG7ok4xwV9cpgBf534PA1rRwrRBAWDrmkLcdiC3ZC8zbNEOEkuBwQbhASexaO6NMqwEgxWClsZBuoQ76zDyDsZBVqQvrZBLd4P8HNDibjuRL4iDNZCUZD"
    	 		+ "&deviceType=ANDROID"
    	 		+ "&deviceCode=864895022518292-0c1dafc69140"
    	 		+ "&account=1666543123627887"
    	 		+ "&openid=21412250250"
    	 		+ "&type=unbind"
    	 		+ "&uniqueCode=14498051424011666543123627887"
    	 		+ "&channel=1042";
//    	 String openid = "21412250250";
    	 String account =  "1666543123627887";
    	 String uniCode = System.currentTimeMillis()+account;
    	 String deviceCode = "864895022518292-0c1dafc69140";
    	 String deviceType = "ANDROID";
    	 String channel = "1042";
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
     public static String testIsBind(){
    	 
    	 String ts = "12-11 15:18:01.046: I/POST(7901): >>>>"
    	 		+ "http://ec2-52-33-4-138.us-west-2.compute.amazonaws.com/account"
    	 		+ "?account=1666543123627887"
    	 		+ "&type=isbind"
    	 		+ "&token=CAAFDDRlIT2wBABfyk7S2vxO2jbpy9rZCunxOHGwqWaU0YgSUVIDcDOGsgKoHr3CvOjfnZADrp5Wp6giI7mInL7AsUcYEF58fRDY2VLFY1dps0MGw5QN750OMKmjhveqidqfWV0DbcPKF5pCxMrLbThr71J1XjPDcIDYRCZA2Ax8lZCoQN5oZCKgYyolNVRnUnD5DjqW2HpwZDZD"
    	 		+ "&uniqueCode=14498182810551666543123627887"
    	 		+ "&channel=1042"
    	 		+ "&deviceType=ANDROID&deviceCode=864895022518292-0c1dafc69140";
//    	 String openid = "21412250250";
    	 String account =  "1666543123627887";
    	 String uniCode = System.currentTimeMillis()+account;
    	 String deviceCode = "864895022518292-0c1dafc69140";
    	 String deviceType = "ANDROID";
    	 String channel = "1042";
    	 String token = "CAAFDDRlIT2wBAMRVBWVOfZA767lHmJdxFZBR33UbFXu00QMaBMSbTtAM237tNf9cQ457ndZAZCZC6GWM5AuG7ok4xwV9cpgBf534PA1rRwrRBAWDrmkLcdiC3ZC8zbNEOEkuBwQbhASexaO6NMqwEgxWClsZBuoQ76zDyDsZBVqQvrZBLd4P8HNDibjuRL4iDNZCUZD";
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
     /***
      * 4为 
      * 不传openid为快速登陆
      * @return
      */
     public static String testQuickLogin(){
    	 String s="http://ec2-52-33-4-138.us-west-2.compute.amazonaws.com/login"
    	 		+ "?openid=3&"
    	 		+ "deviceCode=864895022518292-0c1dafc69140"
    	 		+ "&channel=1042"
    	 		+ "&deviceType=ANDROID"
    	 		+ "&uniqueCode=14490646345"
    	 		+ "&token=14490646345";
    	 
//    	 String openid = "21412250250";
    	 String openid = "1450176210724018165";
    	 String uniCode = System.currentTimeMillis()+"";
    	 String deviceCode = "864895022518292-0c1dafc19988888";
    	 String deviceType = "ANDROID";
    	 String channel = "1042";
    	 String token = "wqe";
//    	 String token = "CAAFDDRlIT2wBAMRVBWVOfZA767lHmJdxFZBR33UbFXu00QMaBMSbTtAM237tNf9cQ457ndZAZCZC6GWM5AuG7ok4xwV9cpgBf534PA1rRwrRBAWDrmkLcdiC3ZC8zbNEOEkuBwQbhASexaO6NMqwEgxWClsZBuoQ76zDyDsZBVqQvrZBLd4P8HNDibjuRL4iDNZCUZD";
    	 Map<String,String> params = new HashMap<String, String>();
    	 params.put("openid", openid);
    	 params.put("deviceCode", deviceCode);
    	 params.put("channel", channel);
    	 params.put("deviceType", deviceType);
    	 params.put("uniqueCode", uniCode);
//    	 params.put("token", token);
    	 
    	 return GetPostSimpleUtil.post(ServerInfo.loginUrl, params);
     }
     
}

