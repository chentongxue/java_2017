package sacred.alliance.magic.app.notify;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import platform.message.request.C5306_AreaServerStateNotifyReqMessage;
import platform.message.response.C5306_AreaServerStateNotifyRespMessage;
import sacred.alliance.magic.area.constant.NotifyStateCode;
import sacred.alliance.magic.area.constant.ServerStateType;
import sacred.alliance.magic.area.domain.Server;
import sacred.alliance.magic.client.Client;
import sacred.alliance.magic.client.zeroc.DefaultIceClient;
import sacred.alliance.magic.codec.impl.bytes.BytesMessageParser;
import sacred.alliance.magic.conf.Configable;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.constant.RespTypeStatus;
import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.util.FileUtil;
import sacred.alliance.magic.util.Util;

public class AreaServerClient implements Service,Configable{
	private static final Logger logger = LoggerFactory.getLogger(AreaServerClient.class);
	private static final String NOTIFY_START_FAILURE_TIPS = "get serverId from areaServer failure," +
			"please to make sure that the areaServer is running now ," +
			"and the config file  is correct." ;
	private String clientAllFile = "area-ice.client" ;
	private List<Client> clientList = new ArrayList<Client>() ;
	private BytesMessageParser messageParser ;
	private Server serverStatus ;
	private Map<String, Long> lastModifyMap = new HashMap<String, Long>();
	private String lastSyncChannelIdStr;
	private Set<Integer> channelIdSet = new HashSet<Integer>();
	
	public void setClientAllFile(String clientAllFile) {
		this.clientAllFile = clientAllFile;
	}

	public void setMessageParser(BytesMessageParser messageParser) {
		this.messageParser = messageParser;
	}
	
	@Override
	public void start(){
		List<String> clientFileList = FileUtil.readFileToList(clientAllFile);
		for(String clientFile : clientFileList) {
			if(null == clientFile || 0 == clientFile.trim().length()){
				continue ;
			}
			File file = new File(clientFile);
			long lastModifyTime = file.lastModified();
			createClient(clientFile, lastModifyTime);
		}
		logger.info("AreaServerClient init success,client num=" + this.clientList.size());
	}
	
	private void createClient(String clientFile, long lastModifyTime){
		Client client = null ;
		try {
			client = new DefaultIceClient(clientFile,messageParser);
			this.clientList.add(client);
			logger.info("AreaServerClient init app success,appName=" + clientFile);
			lastModifyMap.put(clientFile, lastModifyTime);
		} catch (Exception e) { 
			logger.info("AreaServerClient init app failure,appName=" + clientFile,e);
		}
	}
	
	private C5306_AreaServerStateNotifyRespMessage sendNotifyMessage(C5306_AreaServerStateNotifyReqMessage reqMsg,Client client,int index){
		if(null == client || null == reqMsg){
			return null ;
		}
		try {
			return (C5306_AreaServerStateNotifyRespMessage)client.sendMessage(reqMsg);
		} catch (Exception e) {
			logger.error("areaServerClient notify error,server index=" + index,e);
			return null ;
		}
	}
	
	
	
	private C5306_AreaServerStateNotifyRespMessage sendNotifyMessage(C5306_AreaServerStateNotifyReqMessage reqMsg){
		C5306_AreaServerStateNotifyRespMessage returnMsg = null ;
		int index = -1 ;
		for(Client client : this.clientList){
			index++ ;
			C5306_AreaServerStateNotifyRespMessage respMsg = this.sendNotifyMessage(reqMsg, client,index);
			if(null == returnMsg){
				returnMsg = respMsg ;
			}
			//有成功的,返回最后一个成功的
			if(null != respMsg && respMsg.getState() == RespTypeStatus.SUCCESS){
				returnMsg = respMsg ;
			}
		}
		return returnMsg ;
	}
	
	
	public String getServerId(String mac,String port,int appId){
		C5306_AreaServerStateNotifyReqMessage reqMsg = new C5306_AreaServerStateNotifyReqMessage();
		reqMsg.setMacAddress(mac);
		reqMsg.setPort(port);
		reqMsg.setAppId(appId);
		reqMsg.setState(ServerStateType.starting.getType());
		C5306_AreaServerStateNotifyRespMessage resp = this.sendNotifyMessage(reqMsg);
		if(null == resp || NotifyStateCode.SUCCESS != resp.getState()){
			//失败,直接不运行系统启动
			logger.error(NOTIFY_START_FAILURE_TIPS);
			//调用者决定是否退出系统
			//System.exit(1);
			return null ;
		}
		this.serverStatus = this.buildServerInfo(resp);
		return String.valueOf(this.serverStatus.getServerId());
	}
	
	private Server buildServerInfo(C5306_AreaServerStateNotifyRespMessage message){
		Server server = new Server();
		server.setServerId(message.getServerId());
		server.setFullSize(message.getFullSize());
		server.setMaxLevel(message.getMaxLevel());
		server.setServerName(message.getServerName());
		server.setAllowSimulator(message.getAllowSimulator());
		this.updateChannelId(message.getMark1());
		return server ;
	}
	
	/**
	 * 更新所允许的渠道ID列表
	 * @param syncChannelIdStr
	 */
	private void updateChannelId(String syncChannelIdStr){
		try {
			//如果同步过来的渠道为空，说明需要清空渠道号set
			if(Util.isEmpty(syncChannelIdStr)){
				this.channelIdSet.clear();
				return;
			}
			//判断渠道是否有变化
			if(null != this.lastSyncChannelIdStr && syncChannelIdStr.equals(this.lastSyncChannelIdStr)){
				return;
			}
			Set<Integer> set = new HashSet<Integer>();
			//有变化才做修改
			this.lastSyncChannelIdStr = syncChannelIdStr;
			String[] cids = syncChannelIdStr.split(Cat.comma);
			for(String cid : cids){
				if(Util.isEmpty(cid)){
					continue;
				}
				int id = Integer.valueOf(cid);
				set.add(id);
			}
			this.channelIdSet = set;
		} catch (Exception e) {
			logger.error(this.getClass().getName() + ".updateChannelId error: ", e);
		}
	}
	
	public int notifyStop(int appId,String serverId){
		return this.notifyRun(appId, serverId, ServerStateType.stop);
	}
	
	public int notifyRun(int appId,String serverId,
			ServerStateType state){
		C5306_AreaServerStateNotifyReqMessage reqMsg = new C5306_AreaServerStateNotifyReqMessage();
		reqMsg.setAppId(appId);
		reqMsg.setServerId(serverId);
		reqMsg.setState(state.getType());
		C5306_AreaServerStateNotifyRespMessage resp = this.sendNotifyMessage(reqMsg);
		if(null == resp){
			return 0 ;
		}
		int ret = resp.getState();
		if(RespTypeStatus.SUCCESS == ret){
			this.serverStatus = this.buildServerInfo(resp);
		}
		return ret ;
	}


	public Server getServerStatus() {
		return serverStatus;
	}

	@Override
	public void setArgs(Object arg0) {
		
	}


	@Override
	public void stop() {
		
	}

	@Override
	public boolean hotLoadConfig() {
		try{
			Set<String> fileSet = new HashSet<String>();
			List<String> clientFileList = FileUtil.readFileToList(clientAllFile);
			for(String clientFile : clientFileList) {
				if(null == clientFile || 0 == clientFile.trim().length()){
					continue ;
				}
				File file = new File(clientFile);
				if(null == file) {
					continue;
				}
				fileSet.add(clientFile);
				if(!lastModifyMap.containsKey(clientFile)){
					createClient(clientFile, file.lastModified());
				}
			}
			Set<String> removeSet = new HashSet<String>();
			for(String clientFile : lastModifyMap.keySet()) {
				if(fileSet.contains(clientFile)) {
					continue;
				}
				removeSet.add(clientFile);
			}
			
			for(Iterator<Client> it = this.clientList.iterator();it.hasNext();){
				Client client = it.next();
				String configFile = client.getConfigFile();
				if(null == configFile || 0 == configFile.trim().length()){
					continue ;
				}
				if(removeSet.contains(configFile)) {
					it.remove();
					lastModifyMap.remove(configFile);
					continue;
				}
				File file = new File(configFile);
				if(null == file) {
					continue;
				}
				long lastModifyTime = 0;
				if(lastModifyMap.containsKey(configFile)) {
					lastModifyTime = lastModifyMap.get(configFile);
				}
				if(file.lastModified() != lastModifyTime) {
					boolean flag = client.hotLoadConfig();
					if(flag) {
						lastModifyMap.put(configFile, file.lastModified());
						logger.error("reload sucess:" + client.getConfigFile());
					}else{
						logger.error("reload error:" + client.getConfigFile());
					}
				}
			}
			return true;
		}catch(Exception e){
			logger.error("hotLoadConfig iceclient error" ,e);
			return false;
		}
	}

	@Override
	public String getConfigFile() {
		return clientAllFile;
	}
	
	public Set<Integer> getChannelIdSet() {
		return channelIdSet;
	}
	
}
