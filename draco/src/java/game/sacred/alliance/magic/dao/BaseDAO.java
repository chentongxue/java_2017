package sacred.alliance.magic.dao;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * 
 * ibatis 1：配置文件的标识键必须与此方法名相同
 *        2：ibatis空间名称必须与pojo的类名相同
 *        3：配置文件的标识键可以比此方法个数少相同
 *        
 * @author arhat 
 * @date 2009-11-26
 * @version 0.0.0.1
 * @changeLog
 *  1) create: 2009-11-26
 */
public interface BaseDAO {

	public <T> int update(T persistentInstance);
	public <T> int updateObject(T persistentInstance);
	public <T> int update(Class<T> clazz, String keyName,Serializable value); 
	public <T> List<T> selectAll(Class<T> clazz);
	public <T> T insert(T transientInstance);
	
	public <T> T replace(T transientInstance);
	/**
	 * 先进行update操作，如果没有就进行insert操作
	 * @param <T>
	 * @param persistentInstance
	 * @return
	 */
	public <T> T saveOrUpdate(T persistentInstance);
	/**
	 * 批量插入
	 * @param <T>
	 * @param list
	 */
	public <T> void insertBatch(final List<T> list);
	
	public <T> T selectEntity(Class<T> clazz,String keyName,Serializable value);
	public <T> T selectEntity(Class<T> clazz,String keyName1,Serializable value1,String keyName2,Serializable value2);
	public <T> List<T> selectList(Class<T> clazz,String keyName,Serializable value);
	public <T> List<T> selectList(Class<T> clazz,String keyName1,Serializable value1,String keyName2,Serializable value2);
	public <T> List<T> selectList(Class<T> clazz,String selectId);
	
	//public <T> List<T> selectMap(Class<T> clazz,Map<String,Serializable> map);
	public <T> List<T> selectListByParms(Class<T> clazz,String keyName,Serializable value1, Serializable value2, Serializable value3,Serializable value4,Serializable value5,Serializable value6);
	
	//public <T> List<T> selectListByParms(Class<T> clazz,String keyName,List<Serializable> list);
	
	
	public <T> int delete(Class<T> clazz,String keyName,Serializable value);
	public <T> int delete(Class<T> clazz,String keyName1,Serializable value1,String keyName2,Serializable value2);
	public <T> int delete(Class<T> clazz,String keyName1,Serializable value1,String keyName2,Serializable value2,String keyName3,Serializable value3);
	public <T> int del2str(Class<T> clazz,String keyName,Serializable value);
	
	public <T> int deleteAll(Class<T> clazz);
	
	//TODO:
	//public <T> List<T> selectByMapName(Map<String,String> keyName,T instance);
	
	public <T> int count(Class<T> clazz,String keyName,Serializable value);
	public <T> int count(Class<T> clazz);
	public <T> int count(Class<T> clazz,String keyName1,Serializable value1,String keyName2,Serializable value2);
	public <T> int updateBatch(final Class<T> clazz,final String type,final Collection<T> list);
	public <T> int count(Class<T> clazz, String keyName1, Serializable value1,
			String keyName2, Serializable value2, String keyName3,
			Serializable value3);
	
	public <T> int sum(Class<T> clazz,String keyName1,Serializable value1,String keyName2,Serializable value2,String keyName3,Serializable value3);
}


