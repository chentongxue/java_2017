package sacred.alliance.magic.component.id;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.module.id.IdGenerator;

/**
 * 
 * @author tiefengKuang 
 * @date 2009-10-19 
 * @version 0.0.0.1
 * @changeLog
 *  1) create: 2009-10-19
 */
public  class IdFactory<T>  implements Service{
	private static final Logger logger = LoggerFactory.getLogger(IdFactory.class);
	/**提供单例方法,避免处处注入*/
	private static IdFactory instance;
	public IdFactory(){
		IdFactory.instance = this;
	}
	
	public static IdFactory getInstance(){
		return IdFactory.instance;
	}
	
	private Map<Integer,IdGenerator> idGeneratorMap = new HashMap<Integer,IdGenerator>();
	
	public Map<Integer, IdGenerator> getIdGeneratorMap() {
		return idGeneratorMap;
	}

	public void setIdGeneratorMap(Map<Integer, IdGenerator> idGeneratorMap) {
		this.idGeneratorMap = idGeneratorMap;
	}

	/**
	 * 获得下一个ID
	 * @param idType
	 * @return
	 * @throws Exception
	 */
	public String nextId(IdType idType) throws Exception {
		return this.getIdGenerator(idType).nextId().toString();
	}
	
	
	/**
	 * 获得当前ID(此ID已经被使用)
	 * @param idType
	 * @return
	 * @throws Exception
	 */
	public String currentId(IdType idType) throws Exception{
		return this.getIdGenerator(idType).currentId().toString();
	}
	
	private IdGenerator getIdGenerator(IdType idType){
		if(null == idGeneratorMap || 0 == idGeneratorMap.size()){
			throw new RuntimeException("not config the idGeneratorMap");
		}
		IdGenerator gen = idGeneratorMap.get(idType.getType());
		if(null == gen){
			gen = idGeneratorMap.get(String.valueOf(idType.getType()));
		}
		if(null == gen){
			throw new RuntimeException("not config the idGenerator for " + idType.toString());
		}
		return gen ;
	}
	
	public boolean hasAllowLogin(){
		if(null == idGeneratorMap || 0 == idGeneratorMap.size()){
			return false;
		}
		for(IdGenerator gen : idGeneratorMap.values()){
			if(gen.getStatus() != 0){
				return false;
			}
		}
		return true;
	}

	@Override
	public void setArgs(Object arg0) {
		
	}

	@Override
	public void start(){
		try{
			for(IdGenerator idg : idGeneratorMap.values()){
				idg.start();
			}
		}catch(Exception ex){
			logger.error("id Factory start error",ex);
			System.exit(1);
		}
	}

	@Override
	public void stop() {
		for(IdGenerator idg : idGeneratorMap.values()){
			try {
				idg.stop();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
