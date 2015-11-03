package logfile;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class WriteLogFileInstance {
	private final static String FORMAT = "yyyy-MM-dd-HH-mm";
	private final static String DATE_FORMAT = "yyyy-MM-dd-HH-mm-ss";
	private final static SimpleDateFormat sdf = new SimpleDateFormat(
			DATE_FORMAT);
	private final static String logFilePath = "D://log_test2//";
	private final static String logName = "log_name_test.log";
	public static final String LOG_SWITCH_FLAG = "///";

	private final static WriteLogFileInstance instance = new WriteLogFileInstance();

	private WriteLogFileInstance() {

	}

	public static WriteLogFileInstance getInstance() {
		return instance;
	}

	public static void main(String[] args) {
		String s1 = "haha";
		try {
			File f = new File(logFilePath + logName);

			if (f.exists()) {
				System.out.print("文件存在");
			} else {
				System.out.print("文件不存在");
				f.createNewFile();// 不存在则创建
			}
			BufferedWriter bw = new BufferedWriter(new FileWriter(f, true));
			int num = 0;
			while (num++ < 100) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						int n = 0;
						while (n++ < 100) {
							getInstance().writeLogFile((logFilePath + logName),
									"HAHA2");
						}
					}
				}).start();
			}
			bw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void writeLogFile(String fileName, String logStr) {
		try {
			File f = new File(fileName);

			if (!f.exists()) {
				f.createNewFile();// 不存在则创建
			}
			BufferedWriter bw = new BufferedWriter(new FileWriter(f, true));
			String timeStr = sdf.format(new Date());
			bw.write(timeStr + logStr);
			bw.newLine();
			bw.flush();
			bw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
