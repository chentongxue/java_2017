package http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class HTTP3 {
	public static final String METHOD_GET = "GET";
	public static final String METHOD_POST = "POST";

	public static void main(String args[]) {
		String uid = "819238912893";
		String key = "sdf";
		String path = "http://active.gz.1251006671.clb.myqcloud.com:8001/cdkey/ios";
		String md5 = MD5Util.md5EncryptWithUTF8("abf3e3b4f7d0f1685ef1fa0641cb8d2c" + uid + key);
		String params = "uid=" + uid + "&key=" + key + "&sign=" + md5;
		String s = doPost(path, params, 1000);
		System.out.println(s);

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
				OutputStreamWriter oswriter = new OutputStreamWriter(
						conn.getOutputStream());
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
			System.err.println(e.toString());
			try {
				InputStream errorIs = conn.getErrorStream();
				if (null != errorIs) {
					BufferedReader errorBr = new BufferedReader(
							new InputStreamReader(errorIs));
					StringBuffer errorSb = new StringBuffer();
					String line = null;
					while (null != (line = errorBr.readLine())) {
						errorSb.append(line);
					}
					System.err.println(errorSb.toString());
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			} catch (Exception exc) {
				exc.printStackTrace();
			}
		} finally {
			long usetime = System.currentTimeMillis() - starttime;
			if (usetime > 2000) {
				System.err.println(String.format("ResponseTime:%s,URL:%s", usetime,urlString));
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
}
