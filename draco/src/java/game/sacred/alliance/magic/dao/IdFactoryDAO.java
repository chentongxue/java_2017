package sacred.alliance.magic.dao;

import java.util.List;


public interface IdFactoryDAO {

	void deleteStringId();

	void deleteIntId();

	List<String> selectStringList();

	List<Integer> selectIntList();

	List<String> insertBatchString(List<String> list);

	List<Integer> insertBatchInt(List<Integer> list);

}
