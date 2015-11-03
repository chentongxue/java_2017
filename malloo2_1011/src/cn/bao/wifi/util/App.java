package cn.bao.wifi.util;

import android.app.Application;
import android.content.Context;
import android.telephony.TelephonyManager;


public class App extends Application {

	public static String RequestDeviceId = "22:11:cd:x3:23";
	public static String RequestUserId = "123456";
	public static String ApplicationId = "124235";
	public static String OSModel = "Android";
	public static String HardwareModel = "HTC";
	public static Context context = null;
	@Override
	public void onCreate() {
		super.onCreate();
		App.context = getApplicationContext();
		init();
	}
	
	// 初使化上传数据
	private void init() {
		// 简单取
		TelephonyManager teleManger = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
//		 RequestDeviceId = teleManger.getDeviceId();
		 RequestDeviceId = WifiApManager.getInstance(context).getMAC();
		HardwareModel  = android.os.Build.DEVICE;
	}
	
	// 登录用户ID
	public void setRequestUserId(String userId) {
		RequestUserId = userId;
	}
	public Context getContext(){
		return context;
	}
}
