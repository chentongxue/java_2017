

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

//import com.hcgtech.aoe.util.GameContext;

public class Udid {
	// 鏂板畨瑁呯殑璁惧--鐢ㄦ埛
	public static boolean isNewInstallDevice;
	private static final String KEY_UUID = "uuid";

	/**
	 * 璇诲彇UUID
	 * 
	 * @return
	 */
	private static String getUuid() {
		String uuid = "";
		uuid = Settings.System.getString(Jni.getGameActivity()
				.getContentResolver(), KEY_UUID);
		return uuid;
	}

	/**
	 * 瀛樺偍UUID
	 * 
	 * @param uuid
	 */
	public static void saveUid(String uuid) {
		Settings.System.putString(Jni.getGameActivity().getContentResolver(),
				KEY_UUID, uuid);
	}

	/**
	 * 鍙栧緱鎵嬫満鍞竴IMEI鍙�
	 * 
	 * @return String
	 */
	public static String getUid() {
		// 鍙栧緱瀛樺偍璁惧鐨刄UID
		String cardUid = getUuid();
		if (TextUtils.isEmpty(cardUid)) {
			isNewInstallDevice = true;
			// 鍙栧緱UUID
			cardUid = generateUUID(Jni.getGameActivity());
			// 淇濆瓨鍒版湰鍦版枃浠�
			saveUid(cardUid);
		}
		return cardUid;
	}

	/**
	 * 鍙栧緱鎵嬫満UUID
	 * 
	 * @param context
	 * @return
	 */
	public static String generateUUID(Context context) {
		String deviceUUID;
		SharedPreferences pref = context.getSharedPreferences("xcuuid", 0);
		if ((pref != null) && (pref.getString("uuid", "") != null)
				&& (pref.getString("uuid", "").trim().length() > 0)) {
			return pref.getString("uuid", "");
		}

		TelephonyManager tm = (TelephonyManager) context
				.getSystemService("phone");
		String deviceId = tm.getDeviceId();

		UUID uuid = UUID.randomUUID();
		String uuidStr = uuid.toString().replaceAll("-", "").substring(0, 15);
		String wifiMAC = getMacAddr(context);
		if (wifiMAC != null) {
			wifiMAC = wifiMAC.replaceAll("\\.|:", "");
		}
		if ((deviceId == null) || (TextUtils.isEmpty(deviceId.trim()))) {
			if ((wifiMAC != null) && (!(TextUtils.isEmpty(wifiMAC)))) {
				deviceUUID = "-" + wifiMAC;
			} else {
				deviceUUID = uuidStr;
			}

		} else if ((wifiMAC != null) && (!(TextUtils.isEmpty(wifiMAC)))) {
			deviceUUID = deviceId + "-" + wifiMAC;
		} else {
			deviceUUID = deviceId + "-" + uuidStr;
		}

		return deviceUUID;
	}

	/**
	 * 鍙栧緱mac鍦板潃
	 * 
	 * @param context
	 * @return
	 */
	public static String getMacAddr(Context context) {
		WifiManager wifi = (WifiManager) context.getSystemService("wifi");
		WifiInfo info = wifi.getConnectionInfo();
		return info.getMacAddress();
	}
	
	/**
	 * 浜ゅ弶鎺ㄥ箍闇�瑕佺敤鍒扮殑鏂规硶
	 * 
	 * @return
	 */
	public static String getUidForCpb() {
		return "uuid";
//		TelephonyManager tm = (TelephonyManager) GameContext
//				.getActivityInstance().getSystemService(
//						Context.TELEPHONY_SERVICE);
//		// gets the imei (GSM) or MEID/ESN (CDMA)
//		String uid = tm.getDeviceId();
//		if (TextUtils.isEmpty(uid)) {
//			// 璁惧涓嶈兘鎵撶數璇濓紵濂藉惂锛屾潵鐪嬬湅wifi鏈変笉
//			// requires ACCESS_WIFI_STATE
//			WifiManager wm = (WifiManager) GameContext.getActivityInstance()
//					.getSystemService(Context.WIFI_SERVICE);
//			// gets the MAC address
//			if (wm.isWifiEnabled()) {
//				// 鑳芥墦鐢佃瘽灏辫偗瀹氭湁 TelephonyId锛屽惁鍒欏彧鑳絯ifi涓婄綉鐜╂父鎴忥紝閭ｅ氨鑲畾鏈墂ifi鐨剈id
//				uid = wm.getConnectionInfo().getMacAddress();
//			}
//		}
//		// 淇濆瓨涓嬫潵
//
//		return SHA1(uid + "uuid");

	}

	public static String SHA1(String s) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-1");
			digest.update(s.getBytes());
			byte messageDigest[] = digest.digest();
			return toHexString(messageDigest);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return "";
	}

	public static String toHexString(byte[] keyData) {
		if (keyData == null) {
			return null;
		}
		int expectedStringLen = keyData.length * 2;
		StringBuilder sb = new StringBuilder(expectedStringLen);
		for (int i = 0; i < keyData.length; i++) {
			String hexStr = Integer.toString(keyData[i] & 0x00FF, 16);
			if (hexStr.length() == 1) {
				hexStr = "0" + hexStr;
			}
			sb.append(hexStr);
		}
		return sb.toString();
	}
	public static void main(String args[]){
		System.out.println("aa");
	}

}
