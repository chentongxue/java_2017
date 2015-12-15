package http;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
/***
 * 测试SLG使用
 * @author BAO
 *
 */
public class GetPostSimpleTest {
    /**
     * 使用GET方法向服务器提交数据
     * @throws Exception
     */
     public static boolean sendGet(String path){
          
		try {
			URL url = new URL(path);
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
	          conn.setConnectTimeout(5000);
	          conn.setRequestMethod("GET");
	          if(conn.getResponseCode()==200){
	              InputStream is = conn.getInputStream();//因为这里是从服务器得到信息
	              byte[] data = StreamTool.getStreamBytes(is);
	              System.out.println(new String(data));
	              return true;
	          }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
          
          return false;
     }
     /**
      * 使用POST方法向服务器提交数据
      * @throws Exception
      */
     public static boolean sendPostMessage(String name,String password)throws Exception{
          String path = "http://187.187.40.230:8080/web/LoginServlet";
          URL url = new URL(path);
          HttpURLConnection conn = (HttpURLConnection)url.openConnection();
          conn.setConnectTimeout(5000);
          //以POST方式提交数据
          conn.setRequestMethod("POST");
          //设置http协议的请求参数
          //Content-Type: application/x-www-form-urlencoded
          conn.setRequestProperty("Content-Type", " application/x-www-form-urlencoded");
          
          String content = "name="+name+"&password="+password;
          byte[] data = content.getBytes();
          
          //Content-Length: 27  设置请求实体的长度
          conn.setRequestProperty("Content-Length", data.length+"");
          
          //允许http协议对外输出信息 因为需要把实体信息写给服务器
          conn.setDoOutput(true);
          //把数据写给服务器
          conn.getOutputStream().write(data);//写给服务器，但是并没有立即写，而是写在缓存中
          //提交成功的话
          if(conn.getResponseCode()==200){
              InputStream is = conn.getInputStream();//因为这里是从服务器得到信息
              byte[] d = StreamTool.getStreamBytes(is);
              System.out.println("通过POST"+new String(d));
              return true;
          }
          return false;
     }
     public static boolean sendPost(String path){
    	 int resultCode = -1;
    	 try {
    		 URL url = new URL(path);
        	 HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        	 conn.setConnectTimeout(5000);
        	 conn.setRequestMethod("POST");
        	 conn.setRequestProperty("Content-Type", " application/x-www-form-urlencoded");
//        	 String content = "name="+name+"&password="+password;
//        	 byte[] data = content.getBytes();
//        	 conn.setRequestProperty("Content-Length", data.length+"");
        	 conn.setDoOutput(true);
//        	 conn.getOutputStream().write(data);
        	 resultCode = conn.getResponseCode();
        	 if(resultCode == 200){
        		 InputStream is = conn.getInputStream();//因为这里是从服务器得到信息
                 byte[] d = StreamTool.getStreamBytes(is);
                 System.out.println("通过POST:\n"+new String(d));
                 return true;
        	 }else{
        		 System.out.println("通过POST 失败"+conn.getResponseCode());
        	 }
		} catch (Exception e) {
			 System.out.println("通过POST shibai"+resultCode);
		}
    	 System.out.println("通过POST" +"end");
    	 return false;
     }
     public static boolean sendPost(String path, String content){
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
    			 byte[] d = StreamTool.getStreamBytes(is);
    			 System.out.println("通过POST:\n"+new String(d));
    			 return true;
    		 }else{
    			 System.out.println("通过POST 失败"+conn.getResponseCode());
    		 }
    	 } catch (Exception e) {
    		 System.out.println("通过POST shibai"+resultCode);
    	 }
    	 System.out.println("通过POST" +"end");
    	 return false;
     }
     public static void main(String args[]){
    	 System.out.println("hah");
    	 String path = "http://ec2-52-33-4-138.us-west-2.compute.amazonaws.com/login";
    	 String content = "openid=wrwe&deviceCode=864895022518292-0c1dafc69140&channel=1042&deviceType=ANDROID&uniqueCode=14490646345&token=123";
    	 String url = "http://ec2-52-33-4-138.us-west-2.compute.amazonaws.com/login?openid=&deviceCode=864895022518292-0c1dafc69140&channel=1042&deviceType=ANDROID&uniqueCode=14490646345&token=123";
    	 sendGet(url);
//    	 sendPost(path, content);
//    	 sendPost("http://192.168.2.169:8060/version");
//    	 String content= "subPackage=0&deviceType=windows&region=1&channel=10001&"
//    	 		+ "bundleId=com.st.tank.windows&openid=10001201509281624012961811&deviceId=47615b7483bcf9fa8743c8e04de65c01";
//    	 sendPost("http://192.168.2.169:8060/mix/login");
//    	 sendPost("http://192.168.2.169:8060/mix/login",content);
     }
}

