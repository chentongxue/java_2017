package http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class HTTP_justTets {
	 public static void main(String args[]){
		 String uid = "819238912893";
		 String key = "sdf";
//    	 String path = "http://203.195.180.143:8090/cdkey/ios";
    	 String md5 = MD5Util.md5WithUTF8("abf3e3b4f7d0f1685ef1fa0641cb8d2c"+uid+key);
//    	 String params = "uid="+uid+"&key="+key+"&sign="+md5;
    	 String params = null;
    	 String search = post("http://app100616028.qzone.qzoneapp.com/joy_top.war?type=4&openid=FC0AFEC639A3DB43DC9EF1D232803E43&openkey=BFC803F879F45C3D3F00C942812022EEC3E1EFF54A04C89D308556C9463EB3D36FDC6241FA0D4F327F3A6BDFF8BBBB06C1FB750B834656B3283F66446B9EFB5AAC0C33EFDE58AD9B60E8978BF22419CB47E3EF762C8AA621&pfkey=b740a8ac7f658ed0bdcada491cb6b45b&region=1&uid=649712628150002&abc=735", params);
    	 System.out.println(params);
    	 System.out.println(search);
//    	 String search = post("http://203.195.180.143:8090/cdkey/ios", "uid=72620548291132686&key=9f46kPnyuZrpg&sign=" + MD5Util.md5WithUTF8("abf3e3b4f7d0f1685ef1 fa0641cb8d2c726205482911326869f46kPnyuZrpgW"));
// 		System.out.println(search);

		
	 }
	 public static String post(String strURL, String params){
		 try {
				URL url = new URL(strURL);// ��������
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.setDoOutput(true);
				connection.setDoInput(true);
				connection.setUseCaches(false);
				connection.setInstanceFollowRedirects(true);
				connection.setRequestMethod("POST"); // ��������ʽ
//				connection.setRequestProperty("Accept", "application/json"); // ���ý������ݵĸ�ʽ
				connection.setRequestProperty("Content-Type", " application/x-www-form-urlencoded"); // ���÷������ݵĸ�ʽ
				connection.connect();
				OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream(), "UTF-8"); // utf-8����
				out.append(params);
				out.flush();
				out.close();
				// ��ȡ��Ӧ
				int length = (int) connection.getContentLength();// ��ȡ����
				InputStream is = connection.getInputStream();
				if (length != -1) {
					byte[] data = new byte[length];
					byte[] temp = new byte[512];
					int readLen = 0;
					int destPos = 0;
					while ((readLen = is.read(temp)) > 0) {
						System.arraycopy(temp, 0, data, destPos, readLen);
						destPos += readLen;
					}
					String result = new String(data, "UTF-8"); // utf-8����
					System.err.println(result);
					return result;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return "error"; // �Զ��������Ϣ
	 }

}
