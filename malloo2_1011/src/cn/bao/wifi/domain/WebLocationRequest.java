package cn.bao.wifi.domain;

import java.util.List;
import cn.bao.wifi.util.App;

/**
 * 所有AP的信号强度，保存在本数据结构WebLocationRequest
 * @author bao
 * */
public class WebLocationRequest {
	private String RequestDeviceId;
	private String RequestUserId;
	private String ApplicationId;
	private String OSModel;
	private String HardwareModel;

	private List<WifiApInfo> infos;

	public WebLocationRequest(List<WifiApInfo> infos) {
		this.RequestDeviceId = App.RequestDeviceId;
		this.RequestUserId = App.RequestUserId;
		this.ApplicationId = App.ApplicationId;
		this.OSModel = App.OSModel;
		this.HardwareModel = App.HardwareModel;

		this.infos = infos;
	}

	public String getRequestDeviceId() {
		return RequestDeviceId;
	}

	public void setRequestDeviceId(String requestDeviceId) {
		RequestDeviceId = requestDeviceId;
	}

	public String getRequestUserId() {
		return RequestUserId;
	}

	public void setRequestUserId(String requestUserId) {
		RequestUserId = requestUserId;
	}

	public String getApplicationId() {
		return ApplicationId;
	}

	public void setApplicationId(String applicationId) {
		ApplicationId = applicationId;
	}

	public String getOSModel() {
		return OSModel;
	}

	public void setOSModel(String oSModel) {
		OSModel = oSModel;
	}

	public String getHardwareModel() {
		return HardwareModel;
	}

	public void setHardwareModel(String hardwareModel) {
		HardwareModel = hardwareModel;
	}

	public List<WifiApInfo> getInfos() {
		return infos;
	}

	public void setInfos(List<WifiApInfo> infos) {
		this.infos = infos;
	}

	@Override
	public String toString() {
		return "WebLocationRequest [RequestDeviceId=" + RequestDeviceId
				+ ", RequestUserId=" + RequestUserId + ", ApplicationId="
				+ ApplicationId + ", OSModel=" + OSModel + ", HardwareModel="
				+ HardwareModel + ", infos=" + infos + "]";
	}

}
