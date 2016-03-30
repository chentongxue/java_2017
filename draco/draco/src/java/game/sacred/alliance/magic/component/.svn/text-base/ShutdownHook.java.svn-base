package sacred.alliance.magic.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.game.draco.GameContext;

import sacred.alliance.magic.component.id.IdFactory;
import sacred.alliance.magic.vo.RoleInstance;

public class ShutdownHook extends Thread {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public void init(){
//		ProxoolFacade.disableShutdownHook();
	}
	
	public void run() {
		logger.info("start to run ShutdownHook");
		
		//通知分区服务器
		try{
			GameContext.getAreaServerNotifyApp().stop();
		}catch(Exception ex){
			logger.error("",ex);
		}
		try{
			//拒绝用户请求
			GameContext.getShutDownApplication().setRefuseRequest();
		}catch(Exception ex){
			logger.error("",ex);
		}
		//socket网关已经不存在
		/*try{
			//通知网关清除相关记录
			GameContext.getGateWayNotify().stop() ;
		}catch(Exception ex){
			logger.error("",ex);
		}*/
		
//		try{
//			//公会成员信息入库
//			GameContext.getFactionApp().saveAllFactionRoles();
//		}catch(Exception e){
//			GameContext.getFactionApp().factionMemberLog();
//			logger.error("",e);
//		}
//		try{
//			//公会信息入库
//			GameContext.getFactionApp().saveAllFaction();
//		}catch(Exception e){
//			GameContext.getFactionApp().factionLog();
//			logger.error("",e);
//		}
		
		try {
			// 角色数据入库
			for (RoleInstance role : GameContext.getOnlineCenter()
					.getAllOnlineRole()) {
				//GameContext.getOnlineCenter().offline(role);
				try{
					role.getBehavior().closeNetLink();
				}catch(Exception ex){
					logger.error("",ex);
				}
			}
		} catch (Exception ex) {
			logger.error("", ex);
		}
		try{
			//ID队列中的数据入库
			IdFactory.getInstance().stop();
		}catch(Exception e){
			logger.error("", e);
		}
		
		try{
			//因为closeNetLink是异步操作,这里必须等所有入库操作完成
			while(true){
				int onlineSize = 0 ;
				try{
					onlineSize = GameContext.getOnlineCenter().onlineUserSize();
					logger.info("shutdown hook: onlineSize=" + onlineSize);
					if(onlineSize>0){
						Thread.sleep(3000);
					}else{
						break ;
					}
				}catch(Exception ex){
					logger.error("",ex);
					try {
						Thread.sleep(2000);
					} catch (Exception e) {
					}
				}
			}
		}catch(Exception ex){
			logger.error("",ex);
		}
//		ProxoolFacade.shutdown(0);
		logger.info("end to run ShutdownHook");
	}
}
