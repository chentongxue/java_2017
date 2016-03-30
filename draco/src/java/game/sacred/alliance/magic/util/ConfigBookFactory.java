package sacred.alliance.magic.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigBookFactory {
	private static final Logger logger = LoggerFactory.getLogger(ConfigBookFactory.class);
	private static final String CONFIG_FILE_TYPE_ENV_KEY = "_game_config_file_type" ;
	private static int status = -1 ;
	private static int status_txt = 1 ;
	private static int status_xls = 0 ;
	
	
	public static ConfigBook getConfigBook(){
		if(-1 == status){
			String configType = System.getProperty(CONFIG_FILE_TYPE_ENV_KEY);
			if(null != configType && configType.equals("txt")){
				status = status_txt ;
			}else {
				status = status_xls ;
				configType = "xls" ;
			}
			logger.info("**********************************");
			logger.info("*********** _game_config_file_type=" + configType + " *********");
			logger.info("**********************************");
		}
		if(status == status_txt){
			return new TextConfigBook();
		}
		return new ExcelConfigBook();
	}
}
