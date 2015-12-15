package http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class HTTP3SLG {
	public static final String METHOD_GET = "GET";
	public static final String METHOD_POST = "POST";

	/****
	 * ²ÎÊýÃû
	openid
	deviceCode
	channel
	uniqueCode
	deviceType
	token

	 * @param args
	 */
	public static void main(String args[]) {
		String path = "http://ec2-52-33-4-138.us-west-2.compute.amazonaws.com/login";
		String params = "openid=2342&deviceCode=8625&channel=1042&deviceType=ANDROID&uniqueCode=148&token=123";
		String s = sendMsg(path,METHOD_POST, params, 1000);
		System.out.println(s);

	}

	public static String sendMsg(String urlString, String method, String params, Integer timeout) {

		long starttime = System.currentTimeMillis();
		InputStream is = null;
		BufferedReader br = null;
		HttpURLConnection conn = null;

		try {
			URL url = new URL(urlString);
			conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(timeout);
			conn.setReadTimeout(timeout);
			conn.setRequestMethod(method);
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
