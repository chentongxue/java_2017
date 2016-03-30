package sacred.alliance.magic.log;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.log.log.Active;
import sacred.alliance.magic.log.log.Login;
import sacred.alliance.magic.log.log.Online;
import sacred.alliance.magic.log.log.UserGrade;
import sacred.alliance.magic.util.DateUtil;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;

import com.game.draco.GameContext;
import com.game.log.util.RTSI;

public class LogAppImpl implements LogApp{
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private String productId;
	private String regionId;
	private final String DEFUALT_IP = "127.0.0.1";
	private String logFilePath = "";
	private final static String LOG_FILE_SUBFIX = ".log";
	private Date createLogDate = null;
	private Map<String, Logger> loggerMap = new ConcurrentHashMap<String, Logger>();
	private String pkgName = "";

	@Override
	public void start() {
		this.productId = GameContext.getEnvConfig().getProductId();
		this.regionId = GameContext.getEnvConfig().getRegionId();
		if(this.closePrintLog()){
			return;
		}
		createLogger();
	}
	
	
	private void createLogger(){
		try{
			List<String> fileList = getClazzNameList();
			if(Util.isEmpty(fileList)){
				return;
			}
			Map<String, Logger> _loggerMap = new HashMap<String, Logger>();
			this.createLogDate = new Date();
			String dateStr = DateUtil.date2FormatDate(this.createLogDate, "yyyyMM");
			for(String logName : fileList) {
				Logger logger = this.getLoggerFile(logName, dateStr);
				_loggerMap.put(logName, logger);
			}
			this.loggerMap = _loggerMap;
		}catch(Exception e){
			logger.error("LogApp.createLogger error",e);
		}
	}
	
	
	public List<String> getClazzNameList(){
		List<String> clazzNameList =  new ArrayList<String>();
		Set<Class> clazzList = RTSI.findClass(pkgName, sacred.alliance.magic.log.Log.class);	
		for(Class clazz : clazzList){
			clazzNameList.add(clazz.getSimpleName());
		}
		return clazzNameList;
	}
	
	private Logger getLogger(String logName){
		return this.loggerMap.get(logName);
	}
	
	private void execute(RoleInstance role, Log logInfo, Logger logger){
		try {
			if(null == logger) {
				return;
			}
			if(null != role){
				logInfo.setUserId(role.getChannelUserId());
				logInfo.setUserName(role.getChannelUserId());
				logInfo.setCharId(role.getRoleId());
				String roleIp = role.getLoginIp();
				String infoIp = logInfo.getUserIp();
				if(Util.isEmpty(infoIp)) {
					if(Util.isEmpty(roleIp)){
						logInfo.setUserIp(DEFUALT_IP);
					}else{
						logInfo.setUserIp(getHostName(roleIp));
					}	
				}
			}
			logInfo.setTime(System.currentTimeMillis()/1000);
			logInfo.setProductId(productId);
			logInfo.setRegionId(regionId);
			logger.info(logInfo.createLog());
		} catch (Exception e) {
			this.logger.error("StatLogApp.execute", e);
		}
	}
	@Override
	public void activeLog(RoleInstance role, int createdRoleNum, String ip) {
		try{
			if(closePrintLog()){
				return;
			}
			if(createdRoleNum > 0){
				return;
			}
			Active info = new Active();
			info.setUserIp(getHostName(ip));
			this.execute(role, info, getLogger(info.getClass().getSimpleName()));
		}catch(Exception e){
			logger.error("LogApp.RoleFirstCreateLog error",e);
		}
	}

	@Override
	public void onlineLog() {
		try{
			if(closePrintLog()){
				return;
			}
			Online info = new Online();
			info.setAccountNums(GameContext.getOnlineCenter().onlineUserSize());
			info.setCharNums(GameContext.getOnlineCenter().onlineUserSize());
			this.execute(null, info, getLogger(info.getClass().getSimpleName()));
		}catch(Exception e){
			logger.error("LogApp.RoleFirstCreateLog error",e);
		}
	}

	@Override
	public void userGradeLog(RoleInstance role) {
		try{
			if(closePrintLog()){
				return;
			}
			UserGrade info = new UserGrade();
			info.setGrade(role.getLevel());
			this.execute(role, info, getLogger(info.getClass().getSimpleName()));
		}catch(Exception e){
			logger.error("LogApp.RoleLevelLog error",e);
		}
	}

	@Override
	public void loginLog(RoleInstance role) {
		try{
			if(closePrintLog()){
				return;
			}
			Login info = new Login();
			this.execute(role, info, getLogger(info.getClass().getSimpleName()));
		}catch(Exception e){
			logger.error("LogApp.RoleLoginLog error",e);
		}
	}

	private String getHostName(String ipInfo){
		if(Util.isEmpty(ipInfo)){
			return "";
		}
		String[] arr = ipInfo.split(Cat.colon);
		if(null == arr || 0 == arr.length){
			return "";
		}
		return arr[0] ;
	}
	
	private boolean closePrintLog(){
		return Util.isEmpty(this.productId) || Util.isEmpty(this.regionId);
	}
	@Override
	public void setArgs(Object arg0) {
		
	}
	@Override
	public void stop() {
		
	}
	
	private Logger getLoggerFile(String log, String date) {
		String logFilePath = this.logFilePath + Cat.slash + date + Cat.slash + log.toLowerCase();
		String logName =  Cat.underline + this.productId + Cat.underline + this.regionId + Cat.underline; 
		String fileName = logFilePath + logName +  "%d{yyyyMMdd}" +LOG_FILE_SUBFIX ;
		RollingFileAppender<ILoggingEvent> appender = new RollingFileAppender<ILoggingEvent>();
		Logger logger = LoggerFactory.getLogger(logFilePath);
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        
        ch.qos.logback.classic.Logger newLogger = (ch.qos.logback.classic.Logger)logger;
        newLogger.detachAndStopAllAppenders();

        //policy
        TimeBasedRollingPolicy<ILoggingEvent> policy = new TimeBasedRollingPolicy<ILoggingEvent>();
        policy.setContext(loggerContext);
        policy.setFileNamePattern(fileName);
        policy.setParent(appender);
        
        //encoder
        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setCharset(Charset.forName("UTF-8"));
        encoder.setContext(loggerContext);
        encoder.setPattern("%m%n");
        
        //start appender
        appender.setName(logName);
//		appender.setFile(fileName);
        appender.setRollingPolicy(policy);
        appender.setContext(loggerContext);
        appender.setEncoder(encoder);
//        appender.setPrudent(true); //support that multiple JVMs can safely write to the same file.
        policy.start();
        encoder.start();
        /**
         * appender.start()方法必须放最后
         */
        appender.start();
        
        newLogger.addAppender(appender);
        //setup level
        newLogger.setLevel(Level.INFO);
        //remove the appenders that inherited 'ROOT'.
        newLogger.setAdditive(true);
		return newLogger;
	}
	
	@Override
	public void createNewLogger(){
		try{
			if(this.closePrintLog()){
				return;
			}
			Date now = new Date();
			if(DateUtil.isSameMonth(now, this.createLogDate)){
				return;
			}
			this.createLogger();
		}catch(Exception e){
			logger.error("LogApp.createNewLogger error",e);
		}
	}

	public void setLogFilePath(String logFilePath) {
		this.logFilePath = logFilePath;
	}


	public void setPkgName(String pkgName) {
		this.pkgName = pkgName;
	}
}
