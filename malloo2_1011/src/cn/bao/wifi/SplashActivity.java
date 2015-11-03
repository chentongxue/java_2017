package cn.bao.wifi;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;
import android.widget.Toast;
import cn.bao.wifi.util.LogUtil;
import cn.bao.wifi.util.WifiApManager;

/**
 * @author WilliamGai
 */
public class SplashActivity extends Activity {
	private static final int NETWORK_MISS = 0;
	protected static final int WIFI_READY = 1;
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case NETWORK_MISS:
				AlertDialog.Builder builder = new AlertDialog.Builder(
						SplashActivity.this);
				builder.setTitle("没有打开WIFI？");
				builder.setPositiveButton("开启WIFI", new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					/**
					 * 2012-09-30 强制打开WIFI
					 */
					openWifi();
					}
				});
				builder.setNegativeButton("取消", new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				});
				builder.create().show();
				break;
			case WIFI_READY:
				Toast.makeText(SplashActivity.this, "WIFI已开启",1).show();
				startActivity(new Intent(SplashActivity.this,
						LocationWebViewActivity.class));
				finish();
				break;
			default:
				break;
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.splash);
		// tv_version = (TextView) this.findViewById(R.id.tv_version);
		// tv_version.setText(getAppVersion());
		/*
		 *添加动画效果，起始换面暂留 
		 */
		LinearLayout ll = (LinearLayout) this.findViewById(R.id.rl_splash);
		AlphaAnimation aa = new AlphaAnimation(0.0f, 1.0f);
		aa.setDuration(2000);
		ll.setAnimation(aa);
		String string_time = getApplicationContext().getResources().getString(
				R.string.splash_time);
		long splash_time = Long.parseLong(string_time);
		new Handler().postDelayed(new LoadMainUI(), splash_time);
	}

	private class LoadMainUI implements Runnable {
		@Override
		public void run() {
			// 检查后网络设置
			if (getNetWorkStates()) {
				LogUtil.getInstance().writeLog("WIFI ok");
			} else {
				LogUtil.getInstance().writeLog("WIFI false");
				// 弹出对话框提示用户
				Message msg = new Message();
				msg.what = NETWORK_MISS;
				handler.sendMessage(msg);
				return;
			}
			Intent intent = new Intent(SplashActivity.this, LocationWebViewActivity.class);
			startActivity(intent);
			finish();
		}
	}

	/**
	 * 检查手机的网络设置
	 * @return false no net
	 */
	private boolean getNetWorkStates() {
		/**
		 * 测试提示开启WIFI的话将return 改为 false
		 */
		return WifiApManager.getInstance(this).enableWifi();
	}
	public void openWifi() {
		new Thread() {
			@Override
			public void run() {
				WifiApManager.getInstance(SplashActivity.this).OpenWifi();
				//每50毫秒判断WIFI是否开启，直到WIFI开启 停止并发送消息
		        while (!WifiApManager.getInstance(SplashActivity.this) .enableWifi()){
		        	try {
						Thread.sleep(50);
						Log.i("zzz", "WIFI ok？？？？？？？？？？？？？");						
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
		        }
		        Log.i("zzz", "WIFI ok!!!!!!!!!!!!!!!!!!!!!");		
				Message message = new Message();
				message.what = WIFI_READY;
				handler.sendMessage(message);
			}
		}.start();
	}
}