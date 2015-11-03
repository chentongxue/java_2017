package http;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class MessageService {
    /**
     * ʹ��GET������������ύ����
     * @throws Exception
     */
     public static boolean sendMessage(String path){
//         String basepath = "http://187.187.40.230:8080/web/LoginServlet?";
//          String path = basepath + "name="+URLEncoder.encode(name)+"&password="+password;
          
		try {
			URL url = new URL(path);
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
	          conn.setConnectTimeout(5000);
	          conn.setRequestMethod("GET");
	          if(conn.getResponseCode()==200){
	              InputStream is = conn.getInputStream();//��Ϊ�����Ǵӷ������õ���Ϣ
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
      * ʹ��POST������������ύ����
      * @throws Exception
      */
     public static boolean sendPostMessage(String name,String password)throws Exception{
          String path = "http://187.187.40.230:8080/web/LoginServlet";
          URL url = new URL(path);
          HttpURLConnection conn = (HttpURLConnection)url.openConnection();
          conn.setConnectTimeout(5000);
          //��POST��ʽ�ύ����
          conn.setRequestMethod("POST");
          //����httpЭ����������
          //Content-Type: application/x-www-form-urlencoded
          conn.setRequestProperty("Content-Type", " application/x-www-form-urlencoded");
          
          String content = "name="+name+"&password="+password;
          byte[] data = content.getBytes();
          
          //Content-Length: 27  ��������ʵ��ĳ���
          conn.setRequestProperty("Content-Length", data.length+"");
          
          //����httpЭ����������Ϣ ��Ϊ��Ҫ��ʵ����Ϣд��������
          conn.setDoOutput(true);
          //������д��������
          conn.getOutputStream().write(data);//д�������������ǲ�û������д������д�ڻ�����
          //�ύ�ɹ��Ļ�
          if(conn.getResponseCode()==200){
              InputStream is = conn.getInputStream();//��Ϊ�����Ǵӷ������õ���Ϣ
              byte[] d = StreamTool.getStreamBytes(is);
              System.out.println("ͨ��POST"+new String(d));
              return true;
          }
          return false;
     }
     public static boolean send(String path)throws Exception{
    	 URL url = new URL(path);
    	 HttpURLConnection conn = (HttpURLConnection)url.openConnection();
    	 conn.setConnectTimeout(5000);
    	 conn.setRequestMethod("POST");
    	 conn.setRequestProperty("Content-Type", " application/x-www-form-urlencoded");
//    	 String content = "name="+name+"&password="+password;
//    	 byte[] data = content.getBytes();
//    	 conn.setRequestProperty("Content-Length", data.length+"");
    	 conn.setDoOutput(true);
//    	 conn.getOutputStream().write(data);
    	 if(conn.getResponseCode()==200){
    		 InputStream is = conn.getInputStream();//��Ϊ�����Ǵӷ������õ���Ϣ
             byte[] d = StreamTool.getStreamBytes(is);
             System.out.println("ͨ��POST:\n"+new String(d));
             return true;
    	 }
    	 System.out.println("ͨ��POST"+conn.getResponseCode());
    	 return false;
     }
     public static void main(String args[]){
    	 System.out.println("hah");
    	 sendMessage("http://192.168.2.169:8060/1111.html");
    	 try {
			send("http://192.168.2.169:8060/version");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
     }
}

