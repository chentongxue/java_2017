package sacred.alliance.magic.data;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.util.Log4jManager;

public class DefaultDataLoaderManager implements DataLoaderManager{

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private Map<String,DataLoader> loaderMap = new HashMap<String,DataLoader>() ;
	
	public void setLoaderMap(Map<String, DataLoader> loaderMap) {
		this.loaderMap = loaderMap;
	}

	public void start() {

//		logger.info("start to load data ");
		Iterator<String> it = loaderMap.keySet().iterator();
		while(it.hasNext()){
			String key = it.next();
			boolean success = loaderMap.get(key).load();
			if(!success){
				Log4jManager.CHECK.error(key + " execute fail,pls check the system");
				Log4jManager.checkFail();
			}
//			logger.info(key + " load data success") ;
		}
	
//		logger.info("load data success");
	}

	public void stop() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setArgs(Object args) {
		// TODO Auto-generated method stub
		
	}

}
