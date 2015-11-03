package cn.bao.wifi.util;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

/**
 * 输出日志到本地
 * 
 * @author gaibaoning
 */
public class LogUtil {
	private static LogUtil logUtil = null;
	private static Context context;
	private static String tag = "logfile";
	private static boolean valid = true;

	private LogUtil() {
		// init
		context = App.context;
	}

	public static void setValid(boolean flag) {
		valid = flag;
	}

	public static LogUtil getInstance() {
		if (logUtil == null) {
			logUtil = new LogUtil();
		}
		return logUtil;
	}

	public void writeLog(Object obj) {
		if (valid) {
			String logText = obj == null ? "null" : obj.toString();
			// 得到当前时间
			long time = System.currentTimeMillis(); // 获得当前时间
			SimpleDateFormat sdf = new SimpleDateFormat(
					"yyyy-MM-dd hh:mm:ss:SSS");// 显示日期格式
			String str_time = sdf.format(new Date(time)); // 显示当前时间
			String s = str_time + "  " + logText;
			String file_name = str_time.substring(0, 10) + "log.txt";
			Log.v(tag, logText);
			// try {
			// save("log.txt", s);
			// Toast.makeText(context, "写"+"微硬盘成功", Toast.LENGTH_SHORT).show();
			// } catch (Exception e1) {
			// Toast.makeText(context, "写"+"微硬盘失败", Toast.LENGTH_SHORT).show();
			// e1.printStackTrace();
			// }
			try {
				if (saveSD(file_name, s)) {
					// Toast.makeText(context, "写"+"SD成功",
					// Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(context, "写" + "SD盘失败", Toast.LENGTH_SHORT)
							.show();
				}
			} catch (Exception e) {
				e.printStackTrace();
				Toast.makeText(context, "写" + "SD盘失败2", Toast.LENGTH_SHORT)
						.show();
			}
		}
	}

	public void writeLog(String mark, Object obj) {
		if (valid) {
			String logContext = obj == null ? "null" : obj.toString();
			String logText = mark + "\n" + logContext;
			// 得到当前时间
			long time = System.currentTimeMillis(); // 获得当前时间
			SimpleDateFormat sdf = new SimpleDateFormat(
					"yyyy-MM-dd hh:mm:ss:SSS");// 显示日期格式
			String str_time = sdf.format(new Date(time)); // 显示当前时间
			String s = str_time + "  " + logText;
			String file_name = str_time.substring(0, 10) + "log.txt";
			Log.v(tag, logText);
			try {
				if (saveSD(file_name, s)) {
					// Toast.makeText(context, "写"+"SD成功",
					// Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(context, "写" + "SD盘失败", Toast.LENGTH_SHORT)
							.show();
				}
			} catch (Exception e) {
				e.printStackTrace();
				Toast.makeText(context, "写" + "SD盘失败2", Toast.LENGTH_SHORT)
						.show();
			}
		}
	}

	public void writeLog(String mark, Object obj, Throwable t) {
		{
			String logContext = obj == null ? "null" : obj.toString();
			String logText = mark + getTraceInfo(t) + "\n" + logContext;
			// 得到当前时间
			long time = System.currentTimeMillis(); // 获得当前时间
			SimpleDateFormat sdf = new SimpleDateFormat(
					"yyyy-MM-dd hh:mm:ss:SSS");// 显示日期格式
			String str_time = sdf.format(new Date(time)); // 显示当前时间
			String s = str_time + "  " + logText;
			String file_name = str_time.substring(0, 10) + "log.txt";
			Log.v(tag, logText);
			try {
				if (saveSD(file_name, s)) {
				} else {
					Toast.makeText(context, "写" + "SD盘失败", Toast.LENGTH_SHORT)
							.show();
				}
			} catch (Exception e) {
				e.printStackTrace();
				Toast.makeText(context, "写" + "SD盘失败2", Toast.LENGTH_SHORT)
						.show();
			}
		}
	}

	// 输出函数所在行
	public static String getTraceInfo(Throwable t) {
		StringBuffer sb = new StringBuffer();
		StackTraceElement[] stacks = t.getStackTrace();
		String className = stacks[0].getClassName();
		String methodName = stacks[0].getMethodName();
		String lineNum = stacks[0].getLineNumber() + "";

		className = addSpace(className, 10);
		methodName = addSpace(methodName, 6);
		lineNum = addSpace(lineNum, 4);

		sb.append("~").append(className).append(">").append(methodName)
				.append(">").append(lineNum);

		return sb.toString();
	}

	public static String addSpace(String s, int len) {
		StringBuffer sb = new StringBuffer();
		sb.append(s);
		for (int i = 0; i < len - s.length(); i++) {
			sb.append(" ");
		}
		return new String(sb);
	}

	/**
	 * 写在微硬盘
	 * 
	 * @throws Exception
	 */
	public static void save(String filename, String content) throws Exception {
		// 文件的操作模式有 控制文件是否可以被外界访问，写的时候是否追加
		// 其中私有模式不被外界访问，写的时候是覆盖
		FileOutputStream outStream;
		outStream = context.openFileOutput(filename, Context.MODE_APPEND);
		// outStream.write(content.getBytes("UTF-8"));
		outStream.write(content.getBytes());
		outStream.flush();
		outStream.close();
	}

	/**
	 * 写在SD卡
	 */
	public static boolean saveSD(String filename, String content)
			throws Exception {
		// 判断用户是否安装了sd卡
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			File file = new File(Environment.getExternalStorageDirectory(),
					filename);
			FileOutputStream fos = new FileOutputStream(file, true) {
			};
			String s = "\n" + content;
			fos.write(s.getBytes());
			fos.flush();
			fos.close();
			return true;
		} else {
			return false;
		}
	}
}
