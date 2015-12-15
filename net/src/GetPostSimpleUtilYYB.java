

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
/***
 * 2015年12月8日17:20:11
 */
public class GetPostSimpleUtilYYB {
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
    	 LogA.i("hah");
    	 test2();
     }
     public static void test(){
    	 //"openid":"123456",
    	 //"channel":"10431",
    	 //"uniqueCode":"1449640512405123456",
    	 //"token":"","deviceType":"867080027807175"}
    	String bindurl = "http://119.29.69.78/tencent/login";
     	Map<String,String> params = new HashMap<String, String>();
        String openid = "78973423";
        String uniqueCode = System.currentTimeMillis()+openid+"";
     	params.put("openid", openid);
     	params.put("deviceCode", "864895022518292-0c1dafc69140");
     	params.put("channel", "10430");
     	params.put("deviceType", "ANDROID");
     	params.put("uniqueCode", uniqueCode);
//     	params.put("uniqueCode", uniqueCode);
     	params.put("token", "aa");
     	String rs = GetPostSimpleUtilYYB.post(bindurl, params);
     	LogA.i("++"+rs);
     }
     public static void test2(){
    	String s=" http://119.29.69.78/tencent/login"
    			+ "?openid=TENCENT_123456"
    			+ "&token=90225f440d05b3b035d91fb289b5e173"
    			+ "&uniqueCode=1449644904123867080027807175"
    			+ "&channel=1043"
    			+ "&deviceType=ANDROID"
    			+ "&deviceCode=867080027807175";
    	 String bindurl = "http://119.29.69.78/tencent/login";
    	 Map<String,String> params = new HashMap<String, String>();
    	 String openid = "TENCENT_123456";
    	 String uniqueCode = System.currentTimeMillis()+openid+"";
    	 params.put("openid", openid);
    	 params.put("deviceCode", "864895022518292-0c1dafc69140");
    	 params.put("channel", "10430");
    	 params.put("deviceType", "ANDROID");
    	 params.put("uniqueCode", uniqueCode);
//     	params.put("uniqueCode", uniqueCode);
    	 params.put("token", "90225f440d05b3b035d91fb289b5e173");
    	 String rs = GetPostSimpleUtilYYB.post(bindurl, params);
    	 LogA.i("++"+rs);
     }
}

