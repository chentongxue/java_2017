package sacred.alliance.magic.component;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.game.draco.GameContext;

import sacred.alliance.magic.app.goods.Util;
import sacred.alliance.magic.component.id.IdFactory;
import sacred.alliance.magic.core.Service;

import java.util.List;

public class ShutdownHook extends Thread {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private List<Service> appList = Lists.newArrayList() ;
	
	public void init(){

	}

	private void initAppList(){
		//通知分区服务器
		this.appList.add(GameContext.getAreaServerNotifyApp());
		//拒绝用户请求
		this.appList.add(GameContext.getShutDownApp());
		// 排位赛排行榜入库
		this.appList.add(GameContext.getQualifyApp());
		//乐翻天整体数据
		this.appList.add(GameContext.getDonateApp());
		//在线中心
		this.appList.add(GameContext.getOnlineCenter());
		//ID队列中的数据入库(最后)
		this.appList.add(IdFactory.getInstance());
	}
	
	public void run() {
		logger.info("ShutdownHook: start to run ShutdownHook");
		if(Util.isEmpty(appList)){
			logger.info("ShutdownHook: init appList");
			this.initAppList();
			logger.info("ShutdownHook: appList size=" + this.appList.size());
		}
		for(Service service : appList){
			long start = System.currentTimeMillis();
			try {
				service.stop();
			}catch (Exception ex){
				logger.error("ShutdownHook: " + service.getClass().getName() + " stop error ",ex);
			}
			long end = System.currentTimeMillis() ;
			logger.info("ShutdownHook: " + service.getClass().getName() + " stop ,times=" + (end-start));
		}
		logger.info("ShutdownHook: end to run ShutdownHook");
	}
}
