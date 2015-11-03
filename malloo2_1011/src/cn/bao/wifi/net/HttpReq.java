package cn.bao.wifi.net;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.net.ConnectivityManager;
import cn.bao.wifi.R;
/**
 * Post 请求
 * @author Administrator
 *
 */
public class HttpReq {
	
	private static int SO_TIMEOUT = 5000;
	private static int CONNECTION_TIMEOUT = 5000;
	private static String URL;
	
	private static boolean enableNetwork(Context context) {
		ConnectivityManager manager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		return manager.getActiveNetworkInfo() != null && manager.getActiveNetworkInfo().isAvailable()?true:false;
	} 

	public static String post(Context context,Object data) throws Exception{
		if(!enableNetwork(context)) {
			throw new Exception("网络异常");
		}
		URL = context.getResources().getString(R.string.post_url);
		HttpPost post = new HttpPost(URL);
		 post.setHeader(HTTP.CONTENT_TYPE, "application/json");
		DefaultHttpClient client = new DefaultHttpClient();
		setParams(client);
		HttpEntity entity;
		if(data instanceof String) {
			entity = new StringEntity((String)data,HTTP.UTF_8);
			post.setEntity(entity);
		}
		
		HttpResponse response = client.execute(post);
		return EntityUtils.toString(response.getEntity());
	}
	
	private static void setParams(DefaultHttpClient client) {
		HttpParams params = client.getParams();
		HttpConnectionParams.setSoTimeout(params,SO_TIMEOUT);
		HttpConnectionParams.setConnectionTimeout(params,CONNECTION_TIMEOUT);
		client.setParams(params);
	}
}
