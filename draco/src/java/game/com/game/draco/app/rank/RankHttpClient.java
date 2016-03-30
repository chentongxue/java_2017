package com.game.draco.app.rank;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.util.HttpUtil;


public class RankHttpClient {
	public static final String RESP_NOT_SC_OK = "!200";
	public static final String RESP_ERROR = "";
	private static Logger logger = LoggerFactory.getLogger(RankHttpClient.class);
	private static final int connectionTimeout = 1000*2 ;
	private static final int soTimeout = 1000*2 ;
	private static final String charsetName = "UTF-8" ;
	
	public static String get(String url) {
		DefaultHttpClient httpclient = null ;
		try {
			httpclient = HttpUtil.getHttpClient(url);
			HttpParams params = httpclient.getParams();
			HttpConnectionParams.setConnectionTimeout(params, connectionTimeout);
			HttpConnectionParams.setSoTimeout(params, soTimeout);
			HttpGet httpGet = new HttpGet(url);
			HttpResponse response = httpclient.execute(httpGet);
			int code = response.getStatusLine().getStatusCode();
			if (code < HttpStatus.SC_OK
					|| code >= HttpStatus.SC_MULTIPLE_CHOICES) {
				logger.error("rankApp getPageData error, code: " + code + ", url: " + url);
		      	return RESP_NOT_SC_OK;
			}
			return EntityUtils.toString(response.getEntity(), charsetName);
		} catch (Exception e) {
			logger.error("", e);
			return RESP_ERROR;
		}finally{
			if(null != httpclient){
				httpclient.getConnectionManager().shutdown() ;
			}
		}
	}

}
