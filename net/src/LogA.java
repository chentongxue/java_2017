
import java.text.SimpleDateFormat;


/**
 * 日志，输出LOG并打印在文件
 */
public class LogA {
	private static final String TAG = "BB日志";
	private static LogA LOG_UTIL = null;
	private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd-hh:mm:ss:SSS");// 显示日期格式

	public static LogA inst() {
		if (LOG_UTIL == null) {
			LOG_UTIL = new LogA();
		}
		return LOG_UTIL;
	}
	
	public static void i(Object o){
		inst().doI(TAG, o);
	}
	public static void i(String tag, Object o){
		inst().doI(tag, o);
	}
	public static void d(Object o){
		inst().doD(TAG, o);
	}
	public static void d(String tag, Object o){
		inst().doD(tag, o);
	}
	public static void v(Object o){
		inst().doV(TAG, o);
	}
	public static void v(String tag, Object o){
		inst().doV(tag, o);
	}
	public static void e(Object o){
		inst().doE(TAG, o);
	}
	public static void e(String tag, Object o){
		inst().doE(tag, o);
	}
	public static void w(Object o){
		inst().doW(TAG, o);
	}
	public static void w(String tag, Object o){
		inst().doW(tag, o);
	}
	
	
	public void doI(String tag, Object o){
		String msg = o==null?"null": o.toString();
		System.out.println(tag+ ":" +msg);
		writeAndToast(tag,  msg);
	}
	public void doV(String tag, Object o){
		String msg = o==null?"null": o.toString();
		System.out.println(tag+ ":" +msg);
		writeAndToast(tag,  msg);
	}
	public void doE(String tag, Object o){
		String msg = o==null?"null": o.toString();
		System.out.println(tag+ ":" +msg);
		writeAndToast(tag,  msg);
	}
	public void doW(String tag, Object o){
		String msg = o==null?"null": o.toString();
		System.out.println(tag+ ":" +msg);
		writeAndToast(tag,  msg);
	}
	public void doD(String tag, Object o){
		String msg = o==null?"null": o.toString();
		System.out.println(tag+ ":" +msg);
		writeAndToast(tag,  msg);
	}

	private void writeAndToast(String tag, String msg) {
	}
	/** 输出函数所在行 */
	public static String getTraceInfo(Throwable t) {
		StringBuffer sb = new StringBuffer();
		StackTraceElement[] stacks = t.getStackTrace();  
		String className = stacks[0].getClassName();
		String methodName = stacks[0].getMethodName();
		String lineNum = stacks[0].getLineNumber() + "";

		className = addSpace(className, 10);
		methodName = addSpace(methodName, 6);
		lineNum = addSpace(lineNum, 4);

		sb.append("~").append(className).append(">").append(methodName).append(
				">").append(lineNum);

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
}
