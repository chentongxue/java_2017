package cn.bao.wifi.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.bao.wifi.domain.WifiApInfo;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;

/**
 * WIFI相关操作
 * 
 * @author bao
 * 
 */
public class WifiApManager {

	private static WifiApManager wifiApManager;
	private Context context;
	private WifiManager wifiManager;

	private WifiApManager(Context context) {
		this.context = context;
		wifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
	}

	public static WifiApManager getInstance(Context context) {
		if (wifiApManager == null) {
			wifiApManager = new WifiApManager(context);
		}
		return wifiApManager;
	}

	/**
	 * @return true:WIFI可用 false:WIFI不可用
	 */
	public boolean enableWifi() {
		return wifiManager.isWifiEnabled();
	}

	/**
	 * 打开WIFI
	 */
	public boolean OpenWifi() {
			return wifiManager.setWifiEnabled(true);
	}

	/**
	 * 返回扫描结果
	 * 
	 * @return
	 */
	public List<WifiApInfo> scanWifi() {
		List<WifiApInfo> infos = new ArrayList<WifiApInfo>();
		if (wifiManager.startScan()) {
			List<ScanResult> results = wifiManager.getScanResults();
			if (results != null) {
				int count = results.size();
				// int count = results.size() > 10?10:results.size();
				for (int i = 0; i < count; ++i) {
					ScanResult result = results.get(i);
					if (result != null) {
						WifiApInfo info = new WifiApInfo();
						info.setBssid(result.BSSID);
						info.setPhysicalType(0);
						// 信号强度
						info.setRss(result.level);
						info.setSsid(result.SSID);
						info.setChannel(i);
						infos.add(info);
					}
				}
			}
		}
		return infos;
	}
	/**
	 * 测试用
	 */
	public String getScanStringsWifiJust4Test() {
		List<ScanResult> results = wifiManager.getScanResults();
		return Arrays.toString(results.toArray());
	}
	public String getWifiConfigurationString4Test() {
		List<WifiConfiguration> list  = wifiManager.getConfiguredNetworks();
		wifiManager.getConnectionInfo().toString();
		return Arrays.toString(list.toArray())+"\n---下面是ConnectionInfo------" +
				"\n"+wifiManager.getConnectionInfo().toString();
	}
	public String getMAC(){
		return wifiManager.getConnectionInfo().getMacAddress();
	}
}
