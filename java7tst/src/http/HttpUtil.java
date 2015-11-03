package http;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;



public class HttpUtil {

	public static int count = 0;
	
	public static String getContent(String url, String charset) {
		HttpURLConnection con = null;
		try {
			con = (HttpURLConnection) new URL(url).openConnection();
			con.setConnectTimeout(60000);    //设置连接超时
			con.setReadTimeout(60000);        //设置响应超时
			con.setRequestMethod("post");
			BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream(), charset));
			char[] buff = new char[1000];
			int length = -1;
			StringBuilder content = new StringBuilder();
			while ((length = reader.read(buff)) != -1) {
				content.append(buff, 0, length);
			}
			reader.close();
			count=0;
			return content.toString();
		} catch (Exception e) {
			e.printStackTrace();
			while (count<500) {
				count++;
				return getContent(url, "utf-8");
			}
			if (count==500) {
				count=0;
			}
			return null;
		} finally {
			if (con != null)
				con.disconnect();
		}
	}
}
