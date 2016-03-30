package sacred.alliance.magic.admin;

import java.io.File;
import java.net.URL;

import org.slf4j.Logger;

import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;

public class AdminAppImpl implements AdminApp{
	
	private final Logger logger = Log4jManager.ADMIN_LOG ;
	private final static String DOT_JAR = ".jar" ;

	private String libPath = "lib/control/" ;
	public void setLibPath(String libPath) {
		this.libPath = libPath;
	}

	@Override
	public String control(String jarName,String[] args) {
		try {
			// 加载jar
			if (Util.isEmpty(jarName) || !jarName.endsWith(DOT_JAR)) {
				logger.error("jarName is empty or not endsWith .jar");
				return "ERROR:INPUT";
			}
			String jarFileName = libPath + jarName;
			File jarFile = new File(jarFileName);
			if (null == jarFile || !jarFile.isFile()) {
				logger.error("jar file not exist:{} " , jarFileName);
				return "ERROR:FILE_NOT_EXIST";
			}
			String className = jarName.substring(0, jarName.length()-DOT_JAR.length());
			AdminClassLoader classLoader = new AdminClassLoader(new URL[0],
					this.getClass().getClassLoader());
			classLoader.addURL(jarFile.toURI().toURL());
			Class clazz = classLoader.loadClass(className);
			final Control control = (Control) clazz.newInstance();
			final String[] cargs = args ;
			//异步执行
			Thread t = new Thread(new Runnable(){
				@Override
				public void run() {
					String result = control.execute(cargs);
					logger.info("exec result={}" , result);
				}
			}) ;
			t.start();
			return "RECV:SUCCESS" ;
		}catch(Exception ex){
			logger.error("control error",ex);
			return  "ERROR:" + ex.getMessage() ;
		}
	}
	
	
}
