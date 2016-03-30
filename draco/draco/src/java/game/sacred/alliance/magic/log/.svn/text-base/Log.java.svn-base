package sacred.alliance.magic.log;

import org.slf4j.Logger;

import lombok.Data;

public @Data abstract class Log{
	private String userId;
	private String charId;
	private String userIp;
	private String userName;
	private long time;
	protected String productId;
	protected String regionId;
	protected String cat = "\t";
	private Logger logger = null;  
	
	public abstract String createLog();
}
