package sacred.alliance.magic.admin;

import org.slf4j.Logger;

import sacred.alliance.magic.util.Log4jManager;

public abstract class AbstractControl implements Control{
	protected final Logger logger = Log4jManager.ADMIN_LOG ;
	@Override
	public String execute(String[] args) {
		String result = "" ;
		try {
			logger.info("admin control {} recv args:{}",this.getName(),args);
			result = this.action(args);
		} catch (Exception ex) {
			logger.error("admin control {} exec error",this.getName(),ex);
			result = "ERROR" ;
		}
		logger.info("admin control {} exec result:{}",this.getName(),result);
		return result;
	}

	protected abstract String action(String[] args) ;
	
	
	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}
}
