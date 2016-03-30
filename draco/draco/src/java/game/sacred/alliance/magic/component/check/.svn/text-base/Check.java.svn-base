package sacred.alliance.magic.component.check;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.game.draco.GameContext;

import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.util.Log4jManager;

public class Check implements Service{

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Override
	public void setArgs(Object arg0) {
		
	}
	
	private void print(String info){
		logger.error("*******************************************");
		logger.error("");
		logger.error(info);
		logger.error("");
		logger.error("*******************************************");
	}

	@Override
	public void start() {
		if(Log4jManager.canStart()){
			return ;
		}
		if(!GameContext.isTestServer()){
			//测试服务器才做此操作,根据打包流程,只要保证了测试服务器正式服务器肯定没有问题
			this.print("server check failure,but the server will start ,view the logfile:check.log for details");
			return ;
		}
		//服务器启动自检失败
		this.print("server check failure,the system shutdown now,view the logfile:check.log for details");
		//关闭服务器
		System.exit(1);
	}

	@Override
	public void stop() {
		
	}

}
