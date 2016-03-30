package sacred.alliance.magic.app.notify;

import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.game.draco.GameContext;

import sacred.alliance.magic.app.config.ServerStateConfig;
import sacred.alliance.magic.area.constant.ServerStateType;
import sacred.alliance.magic.area.domain.Server;
import sacred.alliance.magic.base.OsType;
import sacred.alliance.magic.util.MacUtil;

public class AreaServerNotifyAppImpl implements AreaServerNotifyApp {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private String port ;
	private int appId ;
	private String serverId ;
	private int maxServerIdTimes = 3 ;
	private long loopMillis = 30*1000 ;
	private ServerStateConfig serverStateConfig ;
	private AtomicBoolean start = new AtomicBoolean(false);
	private AtomicBoolean startServerId =  new AtomicBoolean(false);
	private static final int ALL_CHANNEL_ID = -1;

	public void setServerStateConfig(ServerStateConfig serverStateConfig) {
		this.serverStateConfig = serverStateConfig;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public void setAppId(int appId) {
		this.appId = appId;
	}

	
	public void setLoopMillis(long loopMillis) {
		this.loopMillis = loopMillis;
	}

	private int notifyRun() {
		return GameContext.getAreaServerClient().notifyRun(appId, serverId, getServerStateType());
	}
	
	private ServerStateType getServerStateType(){
		//根据服务器时间情况设置负载
		int fullSize = this.getFullSize();
		//用连接数代替在线用户数
		int onlineSize = GameContext.getMinaServer().getAcceptor().getManagedSessionCount();
		//lowLoad, normalLoad, highLoad, ultrahighLoad
		if(onlineSize >= (int)((serverStateConfig.getUltrahighloadRate()/(float)100) * fullSize)){
			return ServerStateType.ultrahighLoad ; 
		}
		if(onlineSize >= (int)((serverStateConfig.getNormalloadRate()/(float)100) * fullSize)){
			return ServerStateType.highLoad ;
		}
		if(onlineSize >= (int)((serverStateConfig.getlowloadRate()/(float)100) * fullSize)){
			return ServerStateType.normalLoad ;
		}
		return ServerStateType.lowLoad ;
	}

	private int notifyStop() {
		return GameContext.getAreaServerClient().notifyStop(appId, serverId);
	}
	
	public void setMaxServerIdTimes(int maxServerIdTimes) {
		this.maxServerIdTimes = maxServerIdTimes;
	}

	@Override
	public void setArgs(Object arg0) {
		
	}

	@Override
	public void start() {
		//启动的时候调用下,可以检测错误
		//serverStateConfig.getFullsize();
		serverStateConfig.getlowloadRate();
		serverStateConfig.getNormalloadRate();
		serverStateConfig.getHighloadRate();
		serverStateConfig.getUltrahighloadRate();
		
		if(startServerId.compareAndSet(false, true)){
			//获得本机所有ip
			List<String> macList = MacUtil.getAllMacAddresses();
			String macs = "" ;
			String cat = "" ;
			for(String mac : macList){
				macs += cat + mac ;
				cat = ";" ;
			}
			for(int i =0 ;i<this.maxServerIdTimes;i++){
				try{
					this.serverId =  GameContext.getAreaServerClient().getServerId(macs, port, appId);
					if(null != serverId && serverId.trim().length() > 0 && logger.isInfoEnabled()){
						Server serverStatus =  GameContext.getAreaServerClient().getServerStatus();
						logger.info("get serverId from areaServer success,serverId=" + serverId + 
								" emulatorFlag=" + 0 + " fullSize=" +
								serverStatus.getFullSize() +" maxLevel=" + serverStatus.getMaxLevel());
						return ;
					}
				}catch(Exception ex){
					logger.error("get serverId from areaServer error,time=" + i,ex);
				}
			}
			//获得serverId失败退出系统
			String errInfo = "get serverId from areaServer error(try " + this.maxServerIdTimes + " times),the system will shutdown" ;
			logger.error(errInfo);
			System.exit(1);
			throw new java.lang.RuntimeException(errInfo);
			
		}
		//判断是否已经获得了serverId
		if(null == serverId || 0 == serverId.trim().length()){
			throw new java.lang.RuntimeException("must get the serverId first");
		}
		if(start.compareAndSet(false, true)){
			new Thread(new Runnable(){
				@Override
				public void run() {
					while(start.get()){
						try{
							int runValue = notifyRun();
							if(logger.isInfoEnabled()){
								/*logger.info("areaServer notifyRun=" + runValue + " emulatorFlag=" 
										+ AreaNotifyClient.getInstance().getEmulatorFlag());*/
								Server serverStatus =  GameContext.getAreaServerClient().getServerStatus();
								logger.info("areaServer notifyRun=" + runValue + " emulatorFlag=" + 0 
										+ " fullSize=" + serverStatus.getFullSize() +" maxLevel=" + serverStatus.getMaxLevel());
							}
							Thread.sleep(loopMillis);
						}catch(Exception ex){
							logger.error("areaServer notify run error",ex);
						}
					}
				}
			}).start();
		}
	}

	@Override
	public void stop() {
		startServerId.set(false);
		if(start.compareAndSet(true, false)){
			int stopValue = this.notifyStop();
			if(logger.isInfoEnabled()){
				logger.info("areaServer notifyStop=" + stopValue);
			}
		}
	}

	@Override
	public String getServerId() {
		return this.serverId;
	}


	private int getFullSize() {
		return  GameContext.getAreaServerClient().getServerStatus().getFullSize();
	}

	@Override
	public int getMaxLevel() {
		return GameContext.getAreaServerClient().getServerStatus().getMaxLevel();
	}

	@Override
	public boolean isAllowLogin(OsType osType) {
		if(OsType.Emulator != osType){
			return true;
		}
		return 1 == GameContext.getAreaServerClient().getServerStatus().getAllowSimulator();
	}
	
	@Override
	public boolean isAllowChannelLogin(int channelId) {
		Set<Integer> set = GameContext.getAreaServerClient().getChannelIdSet();
		return set.contains(ALL_CHANNEL_ID) || set.contains(channelId);
	}
	
}
