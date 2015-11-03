package cn.bao.wifi.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.bao.wifi.domain.LocationResult;
import cn.bao.wifi.domain.WebLocationRequest;
import cn.bao.wifi.domain.WifiApInfo;
/**
 * 拼接和解析数据
 * @author bao
 *
 */
public class JsonParser {
	
	/**
	 * generate jsonString
	 * @param 
	 * @return
	 * @throws JSONException
	 */
	public static String wifiApRequest(WebLocationRequest webLocationRequest) throws JSONException {
		
		JSONObject rootObj = new JSONObject();
		rootObj.put("RequestDeviceId",webLocationRequest.getRequestDeviceId());
		rootObj.put("RequestUserId", webLocationRequest.getRequestUserId());
		rootObj.put("ApplicationId", webLocationRequest.getApplicationId());
		rootObj.put("OSModel", webLocationRequest.getOSModel());
		rootObj.put("HardwareModel", webLocationRequest.getHardwareModel());
		
		JSONArray bssArray = new JSONArray();
		for(WifiApInfo info:webLocationRequest.getInfos()) {
			JSONObject tmpObj = new JSONObject();
			tmpObj.put("Bssid",info.getBssid());
			tmpObj.put("Ssid", info.getSsid());
			tmpObj.put("Rss", info.getRss());
			tmpObj.put("Channel", info.getChannel());
			tmpObj.put("PhysicalType", info.getPhysicalType());
			
			bssArray.put(tmpObj);
		}
		rootObj.put("BssList",bssArray);
		return rootObj.toString();
	}
	
	/**
	 * 解析
	 * @param resultData
	 * @return
	 * @throws JSONException 
	 */
	public static LocationResult parsedWifiAp(String resultData) throws JSONException {
		LocationResult result = new LocationResult();
		JSONObject jsonObj = new JSONObject(resultData);
//		result.setAltitude(jsonObj.optDouble("Altitude"));
//		result.setBuildingAlias(jsonObj.optString("BuildingAlias"));
//		result.setBuildingId(jsonObj.optString("BuildingId"));
//		result.setFloor(jsonObj.optInt("Floor"));
//		result.setFloorAlias(jsonObj.optString("FloorAlias"));
//		result.setLatitude(jsonObj.optDouble("Latitude"));
//		result.setLongitude(jsonObj.optDouble("Longitude"));
		result.setBuildingAlias(jsonObj.optString("BuildingAlias"));//BuildingAlias
		result.setBuildingId(jsonObj.optString("BuildingId"));//BuildingId
		result.setFloor(jsonObj.optInt("Floor"));//setFloor
		result.setFloorAlias(jsonObj.optString("FloorAlias"));//FloorAlias
		result.setInfo(jsonObj.optBoolean("info"));//info
		result.setMessage(jsonObj.optString("message"));//message
		result.setX(jsonObj.optDouble("x"));//x
		result.setY(jsonObj.optDouble("y"));
		return result;
	}
}
