package cn.bao.wifi.domain;
/**
 * 热点信息
 * @author bao
 *
 */
public class WifiApInfo {
	
	private String bssid;
	private String ssid;
	private int rss;
	private int channel;
	private int physicalType; 

	public void setBssid(String bssid) {
		this.bssid = bssid;
	}

	public String getSsid() {
		return ssid;
	}

	public void setSsid(String ssid) {
		this.ssid = ssid;
	}

	public int getRss() {
		return rss;
	}

	public void setRss(int rss) {
		this.rss = rss;
	}

	public int getChannel() {
		return channel;
	}

	public void setChannel(int channel) {
		this.channel = channel;
	}

	public int getPhysicalType() {
		return physicalType;
	}

	public void setPhysicalType(int physicalType) {
		this.physicalType = physicalType;
	}

	public String getBssid() {
		return bssid;
	}

	@Override
	public String toString() {
		return "[bssid=" + bssid + ", ssid=" + ssid + ", rss=" + rss
				+ ", channel=" + channel + ", physicalType=" + physicalType
				+ "]";
	}
	
}
