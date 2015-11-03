package http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map.Entry;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;


public class HttpClient {

	public static final String METHOD_GET = "GET";
	public static final String METHOD_POST = "POST";

	/**
	 * 发送Get请求
	 * 
	 * @param urlString
	 * @param timeout
	 * @return
	 */
	public static String doGet(String urlString) {
		return doGet(urlString, 16000);
	}

	/**
	 * 发送GET请求
	 * 
	 * @param urlString
	 * @param timeout
	 * @return
	 */
	public static String doGet(String urlString, Integer timeout) {
		long starttime = System.currentTimeMillis();
		InputStream is = null;
		BufferedReader br = null;
		HttpURLConnection conn = null;

		try {
			URL url = new URL(urlString);
			conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(timeout);
			conn.setReadTimeout(timeout);
			conn.setRequestMethod(METHOD_GET);
			is = conn.getInputStream();
			br = new BufferedReader(new InputStreamReader(is, "utf8"));
			String line = null;
			StringBuffer sb = new StringBuffer();
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			return sb.toString();
		} catch (Exception e) {
			try {
				InputStream errorIs = conn.getErrorStream();
				if (errorIs != null) {
					BufferedReader errorBr = new BufferedReader(new InputStreamReader(errorIs));
					StringBuffer errorSb = new StringBuffer();
					String line = null;
					while (null != (line = errorBr.readLine())) {
						errorSb.append(line);
					}
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			} catch (Exception exc) {
				exc.printStackTrace();
			}
		} finally {
			long usetime = System.currentTimeMillis() - starttime;
			if (usetime > 2000) {
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

	/**
	 * 发送GET请求
	 * 
	 * @param urlString
	 * @param timeout
	 * @return
	 */
	public static String doGet(String urlString, HashMap<String, String> cookies) {
		org.apache.commons.httpclient.HttpClient httpClient = new org.apache.commons.httpclient.HttpClient();
		GetMethod getMethod = new GetMethod(urlString);
		StringBuilder buffer = new StringBuilder(128);
		// 设置cookie
		if (cookies != null && !cookies.isEmpty()) {
			for (Entry<String, String> map: cookies.entrySet()) {
				buffer.append(map.getKey()).append("=").append(map.getValue()).append(";");
			}
			getMethod.getParams().setCookiePolicy(CookiePolicy.IGNORE_COOKIES);
			getMethod.setRequestHeader("Cookie", buffer.toString());
		}
		// 设置建立连接超时时间
		httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(16000);
		// 设置读数据超时时间
		httpClient.getHttpConnectionManager().getParams().setSoTimeout(16000);
		// 设置编码
		getMethod.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");
		// 使用系统提供的默认的恢复策略
		getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,new DefaultHttpMethodRetryHandler());
		try {
			try {
				int statusCode = httpClient.executeMethod(getMethod);
				if (statusCode != HttpStatus.SC_OK) {
//					logger.error("Request failed");
				}
				System.out.println(statusCode);
				// 读取内容
				byte[] responseBody = getMethod.getResponseBody();
				return new String(responseBody, "UTF-8");
			} finally {
//				logger.warn("Request closed");
				// 释放链接
				getMethod.releaseConnection();
			}
		} catch (HttpException e) {
//			logger.error("Request exception1:"+e.getMessage());
		} catch (IOException e) {
//			logger.error("Request exception2:"+e.getMessage());
		}
		return null;
	}
	/**
	 * Post方法
	 * 
	 * @param urlString
	 * @param params
	 * @return
	 */
	public static String doPost(String urlString, String params) {
		return doPost(urlString, params, 16000);
	}

	/**
	 * Post方法
	 * 
	 * @param urlString
	 * @param params
	 * @param timeout
	 * @return
	 */
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
//			logger.warn(e.toString());
			try {
				InputStream errorIs = conn.getErrorStream();
				if (null != errorIs) {
					BufferedReader errorBr = new BufferedReader(new InputStreamReader(errorIs));
					StringBuffer errorSb = new StringBuffer();
					String line = null;
					while (null != (line = errorBr.readLine())) {
						errorSb.append(line);
					}
//					logger.warn(errorSb.toString());
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			} catch (Exception exc) {
				exc.printStackTrace();
			}
		} finally {
			long usetime = System.currentTimeMillis() - starttime;
			if (usetime > 2000) {
//				logger.info(String.format("ResponseTime:%s,URL:%s", usetime, urlString));
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
