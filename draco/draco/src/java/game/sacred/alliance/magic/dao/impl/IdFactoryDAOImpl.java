package sacred.alliance.magic.dao.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.orm.ibatis.SqlMapClientCallback;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import sacred.alliance.magic.dao.IdFactoryDAO;

import com.game.draco.GameContext;
import com.ibatis.sqlmap.client.SqlMapExecutor;

public class IdFactoryDAOImpl extends SqlMapClientDaoSupport implements IdFactoryDAO {
	
	private static final String NAMESPACE = "Idfactory";
	private static final String INSERT_INT = NAMESPACE + ".insertInt";
	private static final String INSERT_STRING = NAMESPACE + ".insertString";
	private static final String SELECT_INT_LIST = NAMESPACE + ".selectIntList";
	private static final String SELECT_STRING_LIST = NAMESPACE + ".selectStringList";
	private static final String DEL_INT = NAMESPACE + ".delInt";
	private static final String DEL_STRING = NAMESPACE + ".delString";
	
	@Override
	public List<Integer> insertBatchInt(final List<Integer> list) {
		if(null == list || 0 == list.size()){
			return new ArrayList<Integer>();
		}
		final int serverId = GameContext.getServerId();
		this.getSqlMapClientTemplate().execute(new SqlMapClientCallback() {
            public Object doInSqlMapClient(SqlMapExecutor executor)
                    throws SQLException {
                executor.startBatch();
                for(int i=0,count=list.size();i<count;i++)
                {    
                	Map map = new HashMap();
            		map.put("id", list.get(i));
            		map.put("serverId", serverId);
                    executor.insert(INSERT_INT, map);
                }
                executor.executeBatch();
                return null;
            }
        });
		return list;
	}
	
	@Override
	public List<String> insertBatchString(final List<String> list) {
		if(null == list || 0 == list.size()){
			return new ArrayList<String>();
		}
		final int serverId = GameContext.getServerId();
		this.getSqlMapClientTemplate().execute(new SqlMapClientCallback() {
            public Object doInSqlMapClient(SqlMapExecutor executor)
                    throws SQLException {
                executor.startBatch();
                for(int i=0,count=list.size();i<count;i++)
                {    
                	Map map = new HashMap();
            		map.put("id", list.get(i));
            		map.put("serverId", serverId);
                    executor.insert(INSERT_STRING, map);
                }
                executor.executeBatch();
                return null;
            }
        });
		return list;
	}
	
	@Override
	public List<Integer> selectIntList() {
		return (List<Integer>)this.getSqlMapClientTemplate().queryForList(SELECT_INT_LIST,GameContext.getServerId());
	}
	
	@Override
	public List<String> selectStringList() {
		return (List<String>)this.getSqlMapClientTemplate().queryForList(SELECT_STRING_LIST,GameContext.getServerId());
	}
	
	@Override
	public void deleteIntId() {
		this.getSqlMapClientTemplate().delete(DEL_INT,GameContext.getServerId());
	}
	
	@Override
	public void deleteStringId() {
		this.getSqlMapClientTemplate().delete(DEL_STRING,GameContext.getServerId());
	}

}
