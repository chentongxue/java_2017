package sacred.alliance.magic.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpUtil {
	
	private static final Logger logger = LoggerFactory.getLogger(HttpUtil.class);
	
	private static final String EQU = "=" ;
	private static final String AND = "&" ;
	private static final String EMPTY_STR = "" ;
	private static final String HTTPS = "https" ;
	private static final int HTTPS_PORT = 443 ;
	private static final String SSL = "SSL" ;
	private static final String TLS = "TLS" ;
	private static final String SSL_CONTEXT_STR = SSL ;
	private static final String UTF8 = "UTF-8";
	
	/**
	 * 执行一个HTTP POST请求，返回请求响应的内容
	 * @param url 请求的URL地址
	 * @param params 请求的查询参数,可以为null
	 * @return 返回请求响应的内容
	 */
	public static String doPost(String url, String body, int timeout) {
		DefaultHttpClient httpclient = null ;
		try {
			httpclient = getHttpClient(url);
			HttpParams params = httpclient.getParams();
			HttpConnectionParams.setConnectionTimeout(params, timeout);
			HttpConnectionParams.setSoTimeout(params, timeout);
			HttpPost httpPost = new HttpPost(url);
			httpPost.setHeader("Content-Type", "text/plain; charset=utf-8");
			httpPost.setEntity(new ByteArrayEntity(body.getBytes(UTF8)));
			HttpResponse response = httpclient.execute(httpPost);
			return EntityUtils.toString(response.getEntity(), UTF8);
		} catch (Exception e) {
			logger.error(HttpUtil.class.getName() + ".doPost error: ", e);
			return null;
		}finally{
			if(null != httpclient){
				httpclient.getConnectionManager().shutdown() ;
			}
		}
	}
	
	/**
	 * 执行一个HTTP POST请求，返回请求响应的内容
	 * @param url 请求的URL地址
	 * @param body 消息体
	 * @param timeout 超时时间
	 * @return
	 */
	public static String doPost(String url, byte[] body, int timeout) {
		DefaultHttpClient httpclient = null ;
		try {
			httpclient = getHttpClient(url);
			HttpParams params = httpclient.getParams();
			HttpConnectionParams.setConnectionTimeout(params, timeout);
			HttpConnectionParams.setSoTimeout(params, timeout);
			HttpPost httpPost = new HttpPost(url);
			httpPost.setEntity(new ByteArrayEntity(body));
			HttpResponse response = httpclient.execute(httpPost);
			//return EntityUtils.toByteArray(response.getEntity());
			return EntityUtils.toString(response.getEntity(), UTF8);
		} catch (Exception e) {
			logger.error(HttpUtil.class.getName() + ".doPost error: ", e);
			return null;
		}finally{
			if(null != httpclient){
				httpclient.getConnectionManager().shutdown() ;
			}
		}
	}
	
	/**
	 * 执行一个HTTP GET请求，返回请求响应的内容
	 * @param url 请求的URL地址
	 * @param params 请求的查询参数,可以为null
	 * @return 返回请求响应的内容
	 */
	public static String doGet(String url, int timeout) {
		DefaultHttpClient httpclient = null ;
		try {
			httpclient = getHttpClient(url);
			HttpParams params = httpclient.getParams();
			HttpConnectionParams.setConnectionTimeout(params, timeout);
			HttpConnectionParams.setSoTimeout(params, timeout);
			HttpGet httpGet = new HttpGet(url);
			httpGet.setHeader("Content-Type", "text/plain; charset=utf-8");
			HttpResponse response = httpclient.execute(httpGet);
			return EntityUtils.toString(response.getEntity(), UTF8);
		} catch (Exception e) {
			logger.error(HttpUtil.class.getName() + ".doGet error: ", e);
			return null;
		}finally{
			if(null != httpclient){
				httpclient.getConnectionManager().shutdown() ;
			}
		}
	}
	
	public static DefaultHttpClient getHttpClient(String url) throws Exception{
		DefaultHttpClient httpclient = new DefaultHttpClient();
		if(!url.startsWith(HTTPS)){
			return httpclient;
		}
		SSLContext sslcontext = SSLContext.getInstance(SSL_CONTEXT_STR);
        sslcontext.init(null, new TrustManager[]{new TrustAnyTrustManager()}, null);
        SSLSocketFactory socketFactory = new SSLSocketFactory(sslcontext,SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        Scheme sch = new Scheme(HTTPS, HTTPS_PORT, socketFactory);
        httpclient.getConnectionManager().getSchemeRegistry().register(sch);
        return httpclient ;
	}
	
	/**
	* 编译URL
	* @param baseUrl    URL地址
	* @param paramsMap  参数（KEY=参数名,VALUE=参数值）
	* @return String
	*/
	public static String getUrl(String baseUrl, Map<String,String> paramsMap) {
		try {
			if(null == paramsMap || 0 == paramsMap.size()){
				return EMPTY_STR;
			}
			StringBuffer buffer = new StringBuffer();
			String cat = "";
			for(Entry<String, String> entry : paramsMap.entrySet()){
				String value = URLEncoder.encode(entry.getValue(), UTF8);
				buffer.append(cat)
					.append(entry.getKey())
					.append(EQU)
					.append(value);
				cat = AND;
			}
			return baseUrl + buffer.toString();
		} catch (UnsupportedEncodingException e) {
			return EMPTY_STR;
		}
	}
	
	
	/**
	 * 构建参数信息
	 * @param paramsMap
	 * @return
	 */
	public static String buildParamInfo(Map<String,String> paramsMap){
		try {
			StringBuffer buffer = new StringBuffer();
			String and = "";
			for(Entry<String, String> entry : paramsMap.entrySet()){
				String value = URLEncoder.encode(entry.getValue(), UTF8);
				buffer.append(and)
					.append(entry.getKey())
					.append(EQU)
					.append(value);
				and = AND;
			}
			return buffer.toString();
		} catch (UnsupportedEncodingException e) {
			logger.error(HttpUtil.class.getName() + ".buildParams error: ", e);
			return "";
		}
	}
	
	public static Map<String,String> getParamsMap(String body){
		Map<String,String> paramsMap = new HashMap<String,String>();
			String[] infos = body.split("&");
			for(String item : infos){
				if(null == item){
					continue;
				}
				String[] par = item.split("=");
				if(null == par || 2 != par.length){
					continue;
				}
				paramsMap.put(par[0], par[1]);
			}
			return paramsMap;
	}
	
}

