package cn.bao.wifi;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import cn.bao.wifi.domain.LocationResult;
import cn.bao.wifi.domain.WebLocationRequest;
import cn.bao.wifi.domain.WifiApInfo;
import cn.bao.wifi.net.HttpReq;
import cn.bao.wifi.util.App;
import cn.bao.wifi.util.JsonParser;
import cn.bao.wifi.util.LogUtil;
import cn.bao.wifi.util.WifiApManager;

/**
 * @author malloo team
 */
public class LocationWebViewActivity extends Activity {
	private Button bt_show_log;
	private Button bt_hide_log;
	private WebView webView;
	private TextView tv_log;// 日志信息显示文本
	private ScrollView sv;
	private ProgressBar pb;
	private AlertDialog dialog;
	public SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");// 显示日期格式
	LocationResult result = null;
	// LocationResult result_his = null;
	LocationResult result_his = null;
	List<WifiApInfo> infos = null;
	MAPService mapservice = null;
	public long scan_inteval = 5000;
	protected String url;
	private String logString = "详细信息";
	private String logToastString = "Toast信息";
	// 指南针传感器
	protected SensorManager manager;
	protected MySensorListener mySensorListener;
	protected float degree = 0;
	private Button ButtonShow;
	protected Timer timer;
	public boolean showflag = true;
	private Handler pbhandler, handler;
	public boolean showexception = false;
	public boolean netcode = true;
	public String exception = "";
	private LinearLayout l_search;
	public int count;
	public int threadId = 0;
	public int toastId = 0;// 土司ID
	public int notconnectCount = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.webview);
		scan_inteval = Long.parseLong(getResources().getString(
				R.string.scan_inteval));
		boolean writeNote = Boolean.parseBoolean(getResources().getString(
				R.string.write_note));
		LogUtil.setValid(writeNote);
		l_search = (LinearLayout) this.findViewById(R.id.l_search);
		/*
		 * 2012-09-23 开始的时候隐藏软键盘
		 */
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		initHandler();

		ButtonShow = (Button) this.findViewById(R.id.ButtonShow);
		tv_log = (TextView) this.findViewById(R.id.tv_log);
		bt_show_log = (Button) this.findViewById(R.id.ButtonShowLog);
		bt_hide_log = (Button) this.findViewById(R.id.ButtonHideLog);
		sv = (ScrollView) this.findViewById(R.id.view_log);
		tv_log.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				Intent intent = new Intent(LocationWebViewActivity.this,
						LogMarkDialogActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("log", logString);
				intent.putExtras(bundle);
				startActivity(intent);

				return false;
			}
		});

		pb = (ProgressBar) this.findViewById(R.id.pb_load_map);
		pb.setMax(100);
		webView = (WebView) this.findViewById(R.id.webview);
		webView.setWebChromeClient(new WebChromeClient() {
			public void onProgressChanged(WebView view, int progress) {
				// Activity和Webview根据加载程度决定进度条的进度大小
				addLog("载入" + progress + "%");
				// 发送给进度条
				Message message = new Message();
				message.obj = progress;
				pbhandler.sendMessage(message);
			}
		});
		// 为了捕获载入的错误
		webView.setWebViewClient(new WebViewClient() {

			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				super.onReceivedError(view, errorCode, description, failingUrl);
				Message message = new Message();
				message.obj = "showexception";
				handler.sendMessage(message);
				exception = "载入商场地图异常";
				netcode = false;
			}
		});
		webView.invokeZoomPicker();
		webView.getSettings().setJavaScriptEnabled(true);
		webView.addJavascriptInterface(new JSInterface(), "jsobject");
		// 页面调用的接口，传递参数：upoi（x,y,level,head），x， y坐标，单位是像素，楼层，行进方向。

		// 设置加载进来的页面自适应手机屏幕
		webView.getSettings().setUseWideViewPort(true);
		webView.getSettings().setLoadWithOverviewMode(true);
		webView.setClickable(true);// no use
		webView.getSettings().setSupportZoom(true);// 支持变焦
		webView.getSettings().setBuiltInZoomControls(true);// 支持缩放
		webView.loadUrl("http://www.mracket.com/jd/jd.php");
		// webView.loadUrl("javascript:hello()");

		// 指南针传感器
		// 获取sensor的服务
		manager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);

		// 找到方向传感器
		Sensor sensor = manager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
		// 给方向传感器注册监听事件
		mySensorListener = new MySensorListener();
		manager.registerListener(mySensorListener, sensor,
				SensorManager.SENSOR_DELAY_NORMAL);

	}

	public void initHandler() {
		// 这里只处理 整数
		pbhandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				int progress = (Integer) msg.obj;
				pb.setProgress(progress);
				if (progress == 100) {
					pb.setVisibility(View.INVISIBLE);
					if (netcode) {
						l_search.setVisibility(View.VISIBLE);
						// 过一会儿再消失
						timer = new Timer();

						TimerTask task = new TimerTask() {
							@Override
							public void run() {
								// 发送给进度条
								Message message = new Message();
								if (showflag) {
									message.obj = "buttonshow";
									handler.sendMessage(message);
									showflag = false;
								} else {
									message.obj = "buttongone";
									handler.sendMessage(message);
									showflag = true;
								}
								// timer.cancel();
							}
						};
						// 任务 延迟的时间
						timer.schedule(task, 100, 500);
					}
				}// ==100
			}
		};

		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);

				Object text = msg.obj;
				if (text.toString().equalsIgnoreCase("logresult")) {
					// 测试
					// int x = (int) (Math.random()*100);
					// int y = (int) (Math.random()*100);
//					double[][] ds = { { 35.501, 39.561 }, { 25.082, 39.561 },
//							{ 0, 42.616 }, { 80.759, 3.892 },
//							{ 24.616, 35.866 }, { 45.183, 24.066 },
//							{ 92.651, 18.371 }, { 80.759, 25.790 },
//							{ 96.331, 34.996 }, { 44.685, 13.305 },
//							{ 53.456, 13.305 }, { 82.928, 34.654 },
//							{ 75.402, 13.305 }, { 77.911, 13.305 },
//							{ 86.587, 13.305 }, { 87.176, 9.981 },
//							{ 97.940, 9.981 } };
//					tempt_count = (tempt_count + 1) % (ds.length);
//					int x = (int) ds[tempt_count][0];
//					int y = (int) (ds[tempt_count][1] + 7.784);
//					url = "javascript:upoi(" + x + "," + y + ","
//							+ result_his.getFloor() + "," + (int) degree + ")";
					// 正常
					 url = "javascript:upoi(" + (int) result_his.getX() + ","
					 + (int) result_his.getY() + ","
					 + result_his.getFloor() + "," + (int) degree + ")";

					// logToastString +="第"+(tempt_count+1)+"个"+url;
//					 Toast.makeText(LocationWebViewActivity.this,
//					 "第"+(tempt_count+1)+"次"+url, Toast.LENGTH_SHORT).show();
					webView.loadUrl(url);
					addLog(result_his.markString() + "\n" + url + "指南针参数"
							+ degree);
				} else if (text.toString().equalsIgnoreCase("logclear")) {
					logString = "";
					tv_log.setText("");
				} else if (text.toString().equalsIgnoreCase("log")) {
					tv_log.setText(logString);
				} else if (text.toString().equalsIgnoreCase("buttonshow")) {
					ButtonShow.setVisibility(View.VISIBLE);
				} else if (text.toString().equalsIgnoreCase("buttongone")) {
					ButtonShow.setVisibility(View.GONE);
				} else if (text.toString().equalsIgnoreCase("showexception")) {
					showExceptionDialog("网络异常", exception);
				} else if (text.toString().equalsIgnoreCase("toastlog")) {
					Toast.makeText(LocationWebViewActivity.this,
							logToastString, Toast.LENGTH_SHORT).show();
					logString = logString + logToastString;
					tv_log.setText(logString);
				}
			}
		};
	}

	public class JSInterface {
		public void upoi() {
			// mapservice = new MAPService();
			// mapservice.start();
		}
	}

	public void show_map(View view) {
		Toast.makeText(this, R.string.show_map, 1).show();
	}

	public void go_click(View view) {
		Toast.makeText(this, "导航功能暂无", 1).show();
		addLog("点击" + "GO");
		Intent intent = new Intent(LocationWebViewActivity.this,
				NavSearchActivity.class);
		startActivity(intent);
	}

	public void go_search(View view) {
		Toast.makeText(this, "搜索功能暂无", Toast.LENGTH_SHORT).show();
		addLog("点击" + "搜索");
		// Intent intent = new Intent(LocationWebViewActivity.this,
		// LocationWebViewActivity.class);
		// startActivity(intent);
		// finish();
	}

	public void go_location(View view) {
		addLog("点击" + "AT");
		if (timer != null)
			timer.cancel();
		ButtonShow.setVisibility(View.GONE);
		startMapThread();
	}

	/**
	 * 日志的显示
	 * 
	 * @param view
	 */
	public void hidelog(View view) {
		bt_show_log.setVisibility(View.VISIBLE);
		view.setVisibility(View.GONE);
		sv.setVisibility(View.GONE);
	}

	public void showlog(View view) {
		bt_hide_log.setVisibility(View.VISIBLE);
		view.setVisibility(View.GONE);
		sv.setVisibility(View.VISIBLE);
	}

	/**
	 * 回退键
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {

			AlertDialog.Builder builder = new Builder(this);
			builder.setCancelable(false);// 这样click后退键不可以取消
			builder.setTitle("确定退出？");
			builder.setIcon(R.drawable.icon_easy_platform);
			builder.setNegativeButton("取消", new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
			});
			builder.setPositiveButton("确定", new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					addLog("退出");
					finish();
				}
			});
			dialog = builder.create();
			dialog.show();
		}
		return super.onKeyDown(keyCode, event);
	}

	// 请求REQ 获得位置信息 以后将在这儿捕获大多数异常
	public void requestResult() {
		try {
			// 1 扫描WIF 得到列表 如果列表大小大于1 则，进行第二步
			infos = WifiApManager.getInstance(LocationWebViewActivity.this)
					.scanWifi();
			LogUtil.getInstance().writeLog("1 扫描WIF 得到列表 如果列表大小大于1 则，进行第二步",
					infos);
			if (infos != null && infos.size() >= 1) {
				// 2 转化为数据结构 以便上传给REQ
				WebLocationRequest webLocationRequest = new WebLocationRequest(
						infos);
				// LogUtil.getInstance().writeLog("2 转化为数据结构 以便上传给REQ");
				// 3 将数据结构封装成JSON，以便上传给REQ
				String requestStr = JsonParser
						.wifiApRequest(webLocationRequest);
				// requestStr = "df";
				LogUtil.getInstance().writeLog("3 将数据结构封装成JSON，以便上传给REQ",
						requestStr);
				// 4 请求得到REQ的JSON字符串
				String resultData = null;
				try {
					resultData = HttpReq.post(LocationWebViewActivity.this,
							requestStr);
					notconnectCount = 0;
				} catch (Exception e) {
					e.printStackTrace();
					Message message = new Message();
					message.obj = "toastlog";
					handler.sendMessage(message);
					logToastString = "获取打点信息异常";
					notconnectCount++;
					if (notconnectCount == 5) {// 输出连接错误
						message.obj = "showexception";
						handler.sendMessage(message);
						exception = "连接服务器异常";
					}
				}
				LogUtil.getInstance().writeLog("4 请求得到REQ的JSON字符串", resultData);
				// 5将结果解析为 位置信息
				if (resultData != null)
					result = JsonParser.parsedWifiAp(resultData);
				// LogUtil.getInstance().writeLog("5 将结果解析为 位置信息", result);
				if (result != null
						&& (!result.getBuildingAlias().equals("null"))) {
					result_his = JsonParser.parsedWifiAp(resultData);
				}
			}
			// 得到REQ数据完毕开始请求
		} catch (Exception e) {
			e.printStackTrace();
			addLog("REQ函数异常", e.toString());
			Message message = new Message();
			message.obj = "toastlog";
			handler.sendMessage(message);
			logToastString = "得到异常";
			LogUtil.getInstance().writeLog("与MAP交互过程中异常", e.fillInStackTrace());
		}
  	}

	/**
	 * 每5秒将得到的请求REQ　并发送给　MAP
	 */
	public class MAPService extends Thread {
		public boolean stop = false;

		public void run() {
			// 计数器，从0到12循环
			int i = 0;
			// 在控制台中用
			count = 0;
			// 用以记录时间
			// 线程ID
			threadId++;
			long long_start, long_interval;
			while (!stop) {
				count++;
				long_start = System.currentTimeMillis();
				requestResult();
				// ---
				if (true) {
					// if (result_his != null
					// && (!result_his.getBuildingAlias().equals("null"))) {
					// url = "javascript:upoi(" + result_his.getX() + ","
					// + result_his.getX() + "," + result_his.getFloor()
					// + "," + 200 + ")";
					// webView.loadUrl(url);
					// tv_log.append("@" + result_his.markString());//不可以在子线程
					// 操作UI
					Message message = new Message();
					message.obj = "logresult";
					handler.sendMessage(message);
				}// +++

				try {
					long_interval = scan_inteval - System.currentTimeMillis()
							+ long_start;
					if (long_interval > 0)
						sleep(long_interval);
					// 每记录12次日志，测清空客户端的控制台
					i = (i + 1) % 12;
					if (i == 0) {
						Message message = new Message();
						message.obj = "logclear";
						handler.sendMessage(message);
					}
					addToastLog("<" + i + ">" + "完成一次请求" + threadId + ","
							+ count);
				} catch (InterruptedException e) {
					e.printStackTrace();
					addToastLog("线程睡眠失败" + count);
				}
			}// while
			addLog("请求MAP", "线程结束");
			addToastLog("请求MAP停止");
		}

		// 结束线程
		public void setStop() {
			this.stop = true;
		}
	}

	// 有焦点 继续线程 调试阶段不可用
	@Override
	protected void onResume() {
		super.onResume();
		// mapservice = new MAPService();
		// mapservice.start();
		// tv_log.append("点击" + "super.onResume();");
	}

	// 无焦点 中断线程 调试阶段不可用
	@Override
	protected void onPause() {
		super.onPause();
		// mapservice.setStop();
	}

	// 界面不可见的时候
	@Override
	protected void onStop() {
		super.onStop();
		stopMapThread();
	}

	// 停止请求MAP的线程
	public void stopMapThread() {
		if (mapservice != null) {
			try {
				mapservice.setStop();
				mapservice.stop();
				mapservice = null;
				addToastLog("停止请求MAP的线程");
			} catch (Exception e) {

			}
		}
	}

	// 停止请求MAP的线程
	public void startMapThread() {
		addToastLog("请求MAP的线程开启");
		// if (can_lanch) {
		if (mapservice != null) {
			mapservice.setStop();
			// mapservice.destroy();
		}
		mapservice = null;
		mapservice = new MAPService();
		mapservice.start();
		// }
	}

	public class MySensorListener implements SensorEventListener {

		// 当和传感器捕获的数据状态发生改变的时候调用的代码
		@Override
		public void onSensorChanged(SensorEvent event) {
			// 当前手机的朝向 离北的偏移量event.values[0];
			// 0=North, 90=East, 180=South, 270=West
			degree = event.values[0];
		}

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}
	}


	// 日志操作
	public void addLog(String s, Object ob) {
		if (ob != null) {
			logString = logString + "\n" + getSimpleTime()
					+ (s + ob.toString());
			LogUtil.getInstance().writeLog(s, ob);
			Message message = new Message();
			message.obj = "log";
			handler.sendMessage(message);
		}
	}

	public void addLog(String s) {
		logString = logString + "\n" + getSimpleTime() + (s);
		LogUtil.getInstance().writeLog(s);
		Message message = new Message();
		message.obj = "log";
		handler.sendMessage(message);
	}

	private void addToastLog(String s) {
		toastId++;
		logToastString = getSimpleTime() + s + "(" + toastId + ")";
		Message message = new Message();
		message.obj = "toastlog";
		handler.sendMessage(message);
	}

	public String getSimpleTime() {
		long time = System.currentTimeMillis(); // 获得当前时间
		return sdf.format(new Date(time)); // 显示当前时间
	}

	// public void sendHandlerMessage(int commandId) {
	// Message message = new Message();
	// message.obj = commandId;
	// handler.sendMessage(message);
	// }

	// public void showException()
	// 显示异常对话框 退出的时候会发生异常
	public void showExceptionDialog(String title, String ct) {
		try {
			if (!showexception) {
				AlertDialog.Builder builder = new AlertDialog.Builder(
						App.context);
				builder.setCancelable(false);// 这样click后退键不可以取消
				builder.setTitle(title);
				builder.setMessage(ct);
				builder.setPositiveButton("重新载入", new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						startActivity(new Intent(LocationWebViewActivity.this,
								SplashActivity.class));
						finish();
					}
				});
				builder.setNegativeButton("退出", new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				});
				builder.create().show();
			}
			;
		} catch (Exception e) {
		}
		showexception = true;
	}
}
