

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
/***
 * 2015年12月22日13:06:39 测试谷歌
 */
public class GetPostSimpleUtilDragonLocalHost {
	/**openid签名验证加密串*/
	public static final String SIG_OPENID_KEY = "dragon@sincetimes.com123!";
	/**快速登陆*/
	public static String LOGIN_URL = "http://192.168.2.181/login";
	/**账号登陆*/
	public static String LOGIN_ACCOUNT_URL = "http://192.168.2.181/huaqing/account/login";
	public static String MAIL_BIND_URL = "http://192.168.2.181/huaqing/mailbind";
	public static String PAY_URL = "http://192.168.2.181/ios/payCallback";
//	public static String PAY_URL = "http://192.168.2.181/googleplay/payCallback";

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
    	 testLogin(LOGIN_URL, "", "1002", "1003", "21201", "1005", "324143214", "1007", "1008");
//    	 testPay(PAY_URL, "123", "456","789");
     }
     public static String testMailBind(String account, String password, String mail){
    	 Map<String,String> params = new LinkedHashMap<String, String>();
    	 params.put("account", account);//测试的时候随机下
    	 params.put("password", password);
    	 params.put("mail", mail);
    	 return GetPostSimpleUtil.post(MAIL_BIND_URL, params);
     }
     public static String testLogin(String url, String openid, String uniqueCode, String deviceType, String channel, String origin,
    		   String deviceCode, String region, String token){
    	 Map<String,String> params = new LinkedHashMap<String, String>();
    	 params.put("openid", openid);//测试的时候随机下
    	 params.put("uniqueCode", uniqueCode);
    	 params.put("deviceType", deviceType);
    	 params.put("channel", channel);
    	 params.put("origin", origin);
    	 params.put("region", region);
    	 params.put("deviceCode", deviceCode);
    	 params.put("token", token);
    	 params.put("key", md5(SIG_OPENID_KEY+openid));
    	 return GetPostSimpleUtil.post(url, params);
     }
	
     public static String testPay(String url, String openid,String uid, String receipt){
    	 String deviceType = "ANDROID";
    	 String channel = "23";
    	 String login_channel = "21201";
    	 String region = "1";
    	 Map<String,String> params = new HashMap<String, String>();
    	 params.put("openid", openid);//测试的时候随机下
    	 params.put("uid", uid);
    	 params.put("receipt", receipt);
    	 params.put("deviceType", deviceType);
    	 params.put("region", region);
    	 params.put("channel", channel);
    	 params.put("login_channel", login_channel);
    	 return GetPostSimpleUtil.post(url, params);
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

