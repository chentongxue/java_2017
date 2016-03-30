

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;

import com.alibaba.fastjson.JSONObject;
public class GetPostSimpleUtilDragonXianWang {
	private static final String URL = "http://119.29.151.177";
	/**openid签名验证加密串*/
	public static final String SIG_OPENID_KEY = "dragon@sincetimes.com123!";
	/**快速登陆*/
	public static String LOGIN_URL = URL+"/login";
	public static String LAST_SERVERS_URL = "http://119.29.151.177:8081/MobileGame/lastservers_beta.jsp";
	//修改密码
	public static String RESET_PSW_URL =  URL+"/huaqing/passwordmodify";
	public static String BIND_OPENID_URL =  URL+"/huaqing/account";
	/**账号登陆*/
	public static String ACCOUNT_LOGIN_URL =  URL+"/huaqing/account/login";
	public static String MAIL_BIND_URL =  URL+"/huaqing/mailbind";
	public static String ACCOUNT_REGIST_URL =  URL+"/huaqing/accountRegister";
	public static String PAY_URL =  URL+"/ios/pay";
	public static String FIND_PSW_URL =  URL+"/account/password/find";

//	public static String PAY_URL = "http://192.168.2.181/googleplay/payCallback";

	
	//苹果支付
	public static final String IOS_APP_PAY_URL = "https://buy.itunes.apple.com/verifyReceipt";
	public static final String IOS_APP_PAY_URL_SANDBOX = "https://sandbox.itunes.apple.com/verifyReceipt";
	public static final String METHOD_GET = "GET";
	public static final String METHOD_POST = "POST";
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
    	 String openid = "1453192920559012336";
    	 //1453087942521015657
    	 //获取服务器列表  1453192920559012336  http://119.29.151.177/lastservers.jsp
//    	 testLogin(LOGIN_URL, "", "1002", "1003", "21201", "1005", "324143214", "", "1008");
//    	 testLastServer("");
    	     	 testPay(PAY_URL, "123", "456","789");
//    	 testAcountRegist("gbn", "123", "gbn1002");
//    	 testMailBind("gbn5", "abc", "346126185@qq.com");
    	 
//    	 accountLogin("gbn5", "123");
//    	 accountLogin("gbn5", "456");
//    	 findPsw(FIND_PSW_URL,"21201","gbn","346126185@qq.com");
//    	 restPsw("1453190538248012468", "789", "123");
    	     	 
    	     	 
//    	 String receipt = getFileString("a.txt").trim();
//    	 String s =  Base64.encodeBase64String(receipt.getBytes());
//    	 System.err.println(s);
//    	 JSONObject postData = new JSONObject();
//    	 System.out.println("a:"+receipt);
//		 postData.put("receipt-data", receipt);
//		 System.out.println("b："+postData.toString());
//		 testIOS_CALL_BACK(IOS_APP_PAY_URL_SANDBOX, postData.toString());
     }
     public static String getFileString(String fileName){
    	 BufferedReader br;
 		try {
 			File file = new File(fileName);
 			br = new BufferedReader(new FileReader(file));
 			String line = null;
 			String s = "";
 			while ((line = br.readLine()) != null) {
 				s+=line;
 			   System.out.println(line);
 			}
 			br.close();
 			return s;
 		} catch (IOException e) {
 			e.printStackTrace();
 			return null;
 		}
 	
     }
     public static void testIOS_CALL_BACK(String url, String post){
//    	 GetPostSimpleUtil.sendPost(path, content)
    	 String a = doPost(url, post, 50000);
    	 System.out.println("json result ="+a);
     }
 	public static String doPost(String urlString, String params, Integer timeout) {

		long starttime = System.currentTimeMillis();
		InputStream is = null;
		BufferedReader br = null;
		HttpURLConnection conn = null;

		try {
			URL url = new URL(urlString);
			conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(timeout);
			conn.setReadTimeout(timeout);
			conn.setRequestMethod(METHOD_POST);
			conn.setDoOutput(true);

			if (params != null) {
				OutputStreamWriter oswriter = new OutputStreamWriter(conn.getOutputStream());
				oswriter.write(params);
				oswriter.flush();
				oswriter.close();
			}

			is = conn.getInputStream();
			br = new BufferedReader(new InputStreamReader(is, "utf8"));
			String line = null;
			StringBuffer sb = new StringBuffer();
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			return sb.toString();
		} catch (Exception e) {
			System.out.println(e.toString());
			try {
				InputStream errorIs = conn.getErrorStream();
				if (null != errorIs) {
					BufferedReader errorBr = new BufferedReader(new InputStreamReader(errorIs));
					StringBuffer errorSb = new StringBuffer();
					String line = null;
					while (null != (line = errorBr.readLine())) {
						errorSb.append(line);
					}
					System.out.println(errorSb.toString());
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			} catch (Exception exc) {
				exc.printStackTrace();
			}
		} finally {
			long usetime = System.currentTimeMillis() - starttime;
			if (usetime > 2000) {
				System.out.println(String.format("ResponseTime:%s,URL:%s", usetime, urlString));
			}
			try {
				if (is != null) {
					is.close();
				}
				if (br != null) {
					br.close();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			if (conn != null) {
				conn.disconnect();
			}
		}
		return null;
	}

     public static void testLastServer(String openid){
    	 String sign = md5(openid + "hqfy");
    	 System.err.println(sign);
    	 String rst = GetPostSimpleUtil.sendGet(LAST_SERVERS_URL+"?openid="+openid+"&sign="+sign);
    	 System.out.println(rst);
     }
     public static void restPsw(String openid, String oldPassword, String newPassword){
    	 Map<String,String> params= new LinkedHashMap<String, String>();
    	 params.put("openid", openid);
    	 params.put("oldPassword", oldPassword);
    	 params.put("newPassword", newPassword);
    	 GetPostSimpleUtil.post(RESET_PSW_URL, params);
     }
     public static void findPsw(String url,String channel,String account,String mail){
    	 Map<String,String> params= new LinkedHashMap<String, String>();
    	 params.put("mail", mail);
    	 params.put("account", account);
    	 params.put("channel", channel);
    	 GetPostSimpleUtil.post(url, params);
     }
     public static String accountLogin(String account, String password){
    	 Map<String,String> params = new LinkedHashMap<String, String>();
    	 params.put("account", account);//测试的时候随机下
    	 params.put("password", password);
    	 params.put("deviceType", "ipad");
    	 params.put("channel", "21201");
    	 params.put("uniqueCode", "72347234");
    	 params.put("deviceId", "24234234");
    	 return GetPostSimpleUtil.post(ACCOUNT_LOGIN_URL, params);

     }
     public static String testBingOpenid(){
//    	 BIND_OPENID_URL'
    	 return null;
     }
     public static String testAcountRegist(String account, String password,String deviceId){
    	 Map<String,String> params = new LinkedHashMap<String, String>();
    	 params.put("account", account);//测试的时候随机下
    	 params.put("password", password);
    	 params.put("deviceId", deviceId);
    	 params.put("channel", "21201");
    	 params.put("deviceType", "ipad");
    	 return GetPostSimpleUtil.post(ACCOUNT_REGIST_URL, params);
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

