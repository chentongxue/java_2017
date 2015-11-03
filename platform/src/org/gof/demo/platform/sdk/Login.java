/**
 * 
 */
package org.gof.demo.platform.sdk;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.gof.core.support.SysException;
import org.gof.core.support.log.LogCore;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class Login {

	private static final String CHECK_TOKEN_URL = "http://auth.gamebean.net/ucenter/uMember/userLoginMgr.do";
	private static final String IPID = "0002";
	
	public static final String APPID = "1001";//申请app时进行替换
	public static final String SECURITY_KEY = "d44aad69bb50d9bb321fa1298c1cdeed";//申请app时进行替换
	
	public static final int SUCCESS = 0;
	public static final int FAIL = 1;
	public static final int OTHER_ERROR = -99;

	public static void main(String[] args) {
		checkToken("0", "0b04ad2a-a071-4eb4-a64e-ad44b1785970");
	}
	
	/**
	 * @param userIdentity
	 * @param token
	 * @param appId
	 * @return
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	public static int checkToken(String userIdentity, String token) {
		
		JSONObject jo = new JSONObject();
		jo.put("interfaceId", IPID);
		jo.put("tokenId", token);
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("jsonStr", jo.toJSONString());
		
		String result = checkToken(params);
		
		LogCore.core.info("Receive the remote Indentity:["+result+"].");
		return parseChargeResult(result, userIdentity);
	}
	
	/**
	 * @param result
	 * @return
	 */
	public static int parseChargeResult(String result, String userIdentity) {
		Map<String, Object> resMap = JSON.parseObject(result);
		if(resMap != null){
			
			// 判断返回值状态
			Object code = resMap.get("status");
			int intCode = Integer.valueOf((String)code);

			//验证UserId
			boolean passed = false;
			Object userInfo = resMap.get("userInfo");
			if(userInfo != null){
				Map<String, Object> userMap = JSON.parseObject(userInfo.toString());
				String id = userMap.get("id").toString();
				if(id.equals(userIdentity)){
					passed = true;
				}
				
				LogCore.core.info("Account:[" + id + "];  Passed:[" + passed+"].");
			}
			
			if(intCode == 0 && passed){
				return SUCCESS;
			} else {
				return FAIL;
			}
		}
		return OTHER_ERROR;
	}

	/**
	 * 验证登陆信息
	 * @return
	 */
	private static String checkToken(Map<String, String> params) {
		try {
			//1 拼接地址
			StringBuilder urlSB = new StringBuilder(CHECK_TOKEN_URL);
			//1.1 有需要拼接的参数
			if(!params.isEmpty()) {
				urlSB.append("?");
			}
			
			//1.2 拼接参数
			for(Entry<String, String> entry : params.entrySet()) {
				Object value = entry.getValue();
				String v = (value == null) ? "" : URLEncoder.encode(entry.getValue().toString(), "UTF-8");
				
				urlSB.append(entry.getKey()).append("=").append(v).append("&");
			}
			
			//1.3 最终地址
			String urlStrFinal = urlSB.toString();
			
			//1.4 去除末尾的&
			if(urlStrFinal.endsWith("&")) {
				urlStrFinal = urlStrFinal.substring(0, urlStrFinal.length() - 1);
			}
			
			//请求地址
			HttpGet get = new HttpGet(urlStrFinal);
			
			//准备环境
			try(CloseableHttpClient http = HttpClients.createDefault();
				CloseableHttpResponse response = http.execute(get);) {

				//返回内容
			    HttpEntity entity = response.getEntity();
			    
			    //主体数据
			    InputStream in = entity.getContent();  
			    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			    //读取
			    StringBuilder sb = new StringBuilder();
			    String line = null;  
			    while ((line = reader.readLine()) != null) {  
			    	sb.append(line);
			    }
			    
			    return sb.toString();
			}
		} catch (Exception e) {
			throw new SysException(e);
		}
	}
}
