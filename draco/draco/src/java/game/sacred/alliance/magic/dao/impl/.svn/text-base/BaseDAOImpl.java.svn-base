package sacred.alliance.magic.dao.impl;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.orm.ibatis.SqlMapClientCallback;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import sacred.alliance.magic.dao.BaseDAO;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapExecutor;

/**
 * 
 * ibatis 1：键值标识的说明
 * 			 操作类型.对象.参数个数
 * 		     selectListOne 
 *        2：ibtis的文件里面的标识要能在下面匹配到
 *       
 *        
 * @author arhat 
 * @date 2009-11-26
 * @version 0.0.0.1
 * @changeLog
 *  1) create: 2009-11-26  
 */
public class BaseDAOImpl extends SqlMapClientDaoSupport implements BaseDAO {
	
	private static final String DOT = "." ;
	private static final String INSERT = ".insert";
	private static final String REPLACE = ".replace";
	private static final String SELECTALL = ".selectAll";
	private static final String UPDATE = ".update";
	private static final String UPDATEONE = ".updateOne";
	private static final String SELECTLISTONE = ".selectListOne";
	private static final String DELETEONE = ".deleteOne";
	private static final String DELETE = ".delete";
	private static final String DELETETWO = ".deleteTwo";
	private static final String DELETETHREE = ".deleteThree";
	private static final String DEL2STR = ".del2str";
	private static final String COUNTONE = ".countOne";
	private static final String COUNT = ".count";
	private static final String COUNTTWO = ".countTwo";
	private static final String COUNTTHREE = ".countThree";
	private static final String SELECTOBJECTONE = ".selectObjectOne";
	private static final String SELECTLISTTWO = ".selectListTwo";
	private static final String SELECTOBJECTTWO = ".selectObjectTwo";
	private static final String SELECTMAP = ".selectMap";
	private static final String DELETEALL = ".deleteAll";
	private static final String SUMTHREE = ".sumThree";
	private static final String SELECTLISTBYITERATE = ".selectListByIterate";
	
	public <T> T insert(T transientInstance) {
		this.getSqlMapClientTemplate().insert(transientInstance.getClass().getSimpleName()+INSERT, transientInstance);
		return transientInstance;
	}
	
	public <T> T replace(T transientInstance) {
		this.getSqlMapClientTemplate().insert(transientInstance.getClass().getSimpleName()+REPLACE, transientInstance);
		return transientInstance;
	}

	
	@Override
	public <T> void insertBatch(final List<T> list) {
		if(null == list || 0 == list.size()){
			return;
		}
		this.getSqlMapClientTemplate().execute(new SqlMapClientCallback() {
            public Object doInSqlMapClient(SqlMapExecutor executor)
                    throws SQLException {
                executor.startBatch();
                String calzz = list.get(0).getClass().getSimpleName();
                for(int i=0,count=list.size();i<count;i++)
                {    
                    executor.insert(calzz+INSERT, list.get(i));
                }
                executor.executeBatch();
//                calzz = null;
                return null;
            }
        });
	}
	
//	@Override
//	public <T> void insertBatch(final List<T> list) {
//		if(null == list || 0 == list.size()){
//			return ;
//		}
//		SqlMapClient client = getSqlMapClient();
//		Connection con = null;
//		boolean autoCommit = false;
//		try {
//			con = client.getDataSource().getConnection();
//			autoCommit = con.getAutoCommit();
//			con.setAutoCommit(false);
//			client.setUserConnection(con);
//			client.startBatch();
//			int batch = DEFAULT_BATCH_NUM;
//			int i = 0;
//			String calzz = list.get(0).getClass().getSimpleName();
//			for (T t : list) {
//				client.insert(calzz+INSERT, t);
//				i++;
//				if (i % batch == 0) {
//					client.executeBatch();
//				}
//			}
//			client.executeBatch();
//		} catch (SQLException e) {
//			e.printStackTrace();
//		} finally {
//			try {
//				con.setAutoCommit(autoCommit);
//			} catch (SQLException e) {
//				e.printStackTrace();
//			}
//		}
//	}
	
	public <T> List<T> insertBatchSql(final List<T> list) {
		if(null == list || 0 == list.size()){
			return new ArrayList<T>();
		}
		this.getSqlMapClientTemplate().execute(new SqlMapClientCallback() {
            public Object doInSqlMapClient(SqlMapExecutor executor)
                    throws SQLException {
                executor.startBatch();
                String calzz = list.get(0).getClass().getSimpleName();
                for(int i=0,count=list.size();i<count;i++)
                {    
                    executor.insert(calzz+INSERT, list.get(i));
                }
                executor.executeBatch();
//                calzz = null;
                return null;
            }
        });
		return list;
	}
	
	@Override
	public <T> List<T> selectAll(Class<T> clazz) {
		return (List<T>)this.getSqlMapClientTemplate().queryForList(clazz.getSimpleName()+SELECTALL);
	}
	@Override
	public <T> int update(T persistentInstance) {
		return this.getSqlMapClientTemplate().update(persistentInstance.getClass().getSimpleName()+UPDATE, persistentInstance);
	}
	
	@Override
	public <T> int updateObject(T persistentInstance) {
		return this.getSqlMapClientTemplate().update(persistentInstance.getClass().getSimpleName()+UPDATE, persistentInstance);
	}
	@Override
	public <T> T saveOrUpdate(T persistentInstance){
		int cnt = this.getSqlMapClientTemplate().update(persistentInstance.getClass().getSimpleName()+UPDATE, persistentInstance);
		if(cnt != 1){
			this.insert(persistentInstance);
		}
		return persistentInstance;
	}
	
	@Override
	public <T> int update(Class<T> clazz, String keyName,Serializable value) {
		Map map = new HashMap();
		map.put("key", keyName);
		map.put("value", value);
		return this.getSqlMapClientTemplate().update(clazz.getSimpleName()+UPDATEONE, map);

	}
	
	@Override
	public <T> List<T> selectList(Class<T> clazz, String keyName,
			Serializable value) {
		Map map = new HashMap();
		map.put("key", keyName);
		map.put("value", value);
		return (List<T>)this.getSqlMapClientTemplate().queryForList(clazz.getSimpleName()+SELECTLISTONE, map);
	}
	@Override
	public <T> List<T> selectListByParms(Class<T> clazz,String keyName,
			Serializable value1, Serializable value2, Serializable value3,Serializable value4,Serializable value5,Serializable value6) {
		Map map = new HashMap();
		map.put("key1", keyName);
		map.put("value1", value1);
		map.put("value2", value2);
		map.put("value3", value3);
		map.put("value4", value4);
		map.put("value5", value5);
		map.put("value6", value6);
		return (List<T>)this.getSqlMapClientTemplate().queryForList(clazz.getSimpleName()+SELECTLISTBYITERATE, map);
	}
	
	/*public <T> List<T> selectListByParms(Class<T> clazz,String keyName,List<Serializable> list){
		Map map = new HashMap();
		map.put("key1", keyName);
		int index =1; 
		for(Iterator<Serializable> it = list.iterator();it.hasNext();){
			map.put("value"+index, it.next());
			index ++ ;
		}
		return (List<T>)this.getSqlMapClientTemplate().queryForList(clazz.getSimpleName()+SELECTLISTBYITERATE, map);
	}*/

	@Override
	public <T> int delete(Class<T> clazz, String keyName,
			Serializable value) {
		Map map = new HashMap();
		map.put("key", keyName);
		map.put("value", value);
		return this.getSqlMapClientTemplate().delete(clazz.getSimpleName()+DELETEONE, map);

	}

	@Override
	public <T> int delete(Class<T> clazz, String keyName1,
			Serializable value1, String keyName2, Serializable value2) {
		Map map = new HashMap();
		map.put("key1", keyName1);
		map.put("value1", value1);
		map.put("key2", keyName2);
		map.put("value2", value2);
		return this.getSqlMapClientTemplate().delete(clazz.getSimpleName()+DELETETWO, map);
	}
	
	
	
	
	@Override
	public <T> int delete(Class<T> clazz, String keyName1,
			Serializable value1, String keyName2, Serializable value2,String keyName3, Serializable value3) {
		Map map = new HashMap();
		map.put("key1", keyName1);
		map.put("value1", value1);
		map.put("key2", keyName2);
		map.put("value2", value2);
		map.put("key3", keyName3);
		map.put("value3", value3);
		return this.getSqlMapClientTemplate().delete(clazz.getSimpleName()+DELETETHREE, map);
	}


	@Override
	public <T> int del2str(Class<T> clazz, String keyName, Serializable value) {
		Map map = new HashMap();
		map.put("key", keyName);
		map.put("value", value);
		return this.getSqlMapClientTemplate().delete(clazz.getSimpleName()+DEL2STR, map);

	}

	@Override
	public <T> int count(Class<T> clazz, String keyName, Serializable value) {
		Map map = new HashMap();
		map.put("key", keyName);
		map.put("value", value);
		return (Integer)this.getSqlMapClientTemplate().queryForObject(clazz.getSimpleName()+COUNTONE,map);
	}

	@Override
	public <T> int count(Class<T> clazz) {
		return (Integer)this.getSqlMapClientTemplate().queryForObject(clazz.getSimpleName()+COUNT);
	}
	
	@Override
	public <T> int count(Class<T> clazz,String keyName1,Serializable value1,String keyName2,Serializable value2) {
		Map map = new HashMap();
		map.put("key1", keyName1);
		map.put("value1", value1);
		map.put("key2", keyName2);
		map.put("value2", value2);
		return (Integer)this.getSqlMapClientTemplate().queryForObject(clazz.getSimpleName()+COUNTTWO,map);
	}
	@Override
	public <T> int count(Class<T> clazz,String keyName1,Serializable value1,String keyName2,Serializable value2,String keyName3,Serializable value3) {
		Map map = new HashMap();
		map.put("key1", keyName1);
		map.put("value1", value1);
		map.put("key2", keyName2);
		map.put("value2", value2);
		map.put("key3", keyName3);
		map.put("value3", value3);
		return (Integer)this.getSqlMapClientTemplate().queryForObject(clazz.getSimpleName()+COUNTTHREE,map);
	}
	
	@Override
	public <T> T selectEntity(Class<T> clazz, String keyName, Serializable value) {
		Map map = new HashMap();
		map.put("key", keyName);
		map.put("value", value);
		return (T)this.getSqlMapClientTemplate().queryForObject(clazz.getSimpleName()+SELECTOBJECTONE, map);
	}

	@Override
	public <T> List<T> selectList(Class<T> clazz, String keyName1,
			Serializable value1, String keyName2, Serializable value2) {
		Map map = new HashMap();
		map.put("key1", keyName1);
		map.put("value1", value1);
		map.put("key2", keyName2);
		map.put("value2", value2);
		return (List<T>)this.getSqlMapClientTemplate().queryForList(clazz.getSimpleName()+SELECTLISTTWO, map);
	}
	
	@Override
	public <T> List<T> selectList(Class<T> clazz,String selectId){
		return (List<T>)this.getSqlMapClientTemplate().queryForList(clazz.getSimpleName()+ DOT + selectId);
	}

	@Override
	public <T> T selectEntity(Class<T> clazz, String keyName1,
			Serializable value1, String keyName2, Serializable value2) {
		Map map = new HashMap();
		map.put("key1", keyName1);
		map.put("value1", value1);
		map.put("key2", keyName2);
		map.put("value2", value2);
		return (T)this.getSqlMapClientTemplate().queryForObject(clazz.getSimpleName()+SELECTOBJECTTWO, map);
	}


	/*@Override
	public <T> List<T> selectMap(Class<T> clazz, Map<String, Serializable> map) {
		return (List<T>)this.getSqlMapClientTemplate().queryForList(clazz.getSimpleName()+SELECTMAP, map);
	}*/


	@Override
	public <T> int deleteAll(Class<T> clazz) {
		return this.getSqlMapClientTemplate().delete(clazz.getSimpleName()+DELETEALL);
	}


	/*@Override
	public <T> List<T> selectMap(Class<T> clazz, Map<String, Serializable> map) {
		// TODO Auto-generated method stub
		return null;
	}*/
	@Override
	public <T> int updateBatch(final Class<T> clazz,final String type,final Collection<T> list) {
		Object result = this.getSqlMapClientTemplate().execute(new SqlMapClientCallback() {
            public Object doInSqlMapClient(SqlMapExecutor executor){
            	try{
            		try{
            		executor.startBatch();
                	// do some iBatis operations here
                	for(Iterator<T> it = list.iterator(); it.hasNext();) {
            			T obj = it.next();
                		if(type.equals("update")){
                			executor.update(clazz.getSimpleName()+UPDATE, obj);
                		}else if(type.equals("insert")){
                			executor.insert(clazz.getSimpleName()+INSERT, obj);
                		}else if(type.equals("delete")){
                			executor.delete(clazz.getSimpleName()+DELETE, obj);
                		}
                		
                	}
                	executor.executeBatch();
            		}catch(Exception e){
            			e.printStackTrace();
            		}
                	return 1;
            	}catch(Exception e){
            		return 0;
            	}
            }
		});
		return Integer.parseInt(result.toString());
	}

	@Override
	public <T> int sum(Class<T> clazz, String keyName1, Serializable value1,
			String keyName2, Serializable value2, String keyName3,
			Serializable value3) {
		Map map = new HashMap();
		map.put("key1", keyName1);
		map.put("value1", value1);
		map.put("key2", keyName2);
		map.put("value2", value2);
		map.put("key3", keyName3);
		map.put("value3", value3);
		Object obj = this.getSqlMapClientTemplate().queryForObject(clazz.getSimpleName()+SUMTHREE,map);
		if(null == obj){
			return 0;
		}
		return (Integer)obj;
	}
	
}
