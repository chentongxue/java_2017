package sacred.alliance.magic.app.config;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.base.Result;

public class ConfigCollection {

	private static List<Configurable> configList = new ArrayList<Configurable>();
	private static final Logger logger = LoggerFactory.getLogger(ConfigCollection.class);
	public static void add(Configurable config){
		if(null == config){
			return  ;
		}
		configList.add(config);
	}
	
	public static Result reload(){
		Result result = new Result();
		if(null == configList){
			result.success();
			return result ;
		}
		boolean ok = true ;
		StringBuffer buffer = new StringBuffer("");
		for(Configurable c : configList){
			try {
				c.reLoad();
			} catch (Exception e) {
				ok = false ;
				buffer.append(c.getName() + " load error\n");
				logger.error("load config error: " + c.getName() ,e);
			}
		}
		if(ok){
			result.success();
			return result ;
		}
		return result.setInfo(buffer.toString());
	}
}
