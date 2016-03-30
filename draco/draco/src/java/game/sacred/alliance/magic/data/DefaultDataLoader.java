package sacred.alliance.magic.data;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import javolution.util.FastMap;

import org.slf4j.Logger;

import com.game.draco.GameContext;

import sacred.alliance.magic.util.Log4jManager;

public abstract class DefaultDataLoader<K, V> implements DataLoader {
	protected Logger logger = Log4jManager.CHECK ;
	private boolean concurrent = false;

	private AtomicBoolean loaded = new AtomicBoolean(false);

	private Map<K, V> dataMap = new ConcurrentHashMap<K, V>();

	protected Map<K, V> fastDataMap = new FastMap<K, V>();

	public Map<K, V> getDataMap() {
		if (!concurrent) {
			return fastDataMap;
		}
		return dataMap;
	}
	
	public String getXlsPath(){
		return GameContext.getPathConfig().getXlsPath();
	}
	
	/*private  void  verifyData(Map<K,V> d){
		// 验证
		StringBuffer buffer = new StringBuffer();
		for (Iterator<V> it = d.values().iterator(); it.hasNext();) {
			V v = it.next();
			if (v instanceof SysVerify) {
				String info = ((SysVerify) v).sysVerify();
				if (null != info) {
					buffer.append(info);
				}
			} else if (v instanceof Map) {
				StringBuffer mapbuffer = new StringBuffer();
				Map m = (Map) v;
				for (Iterator<Object> it2 = m.values().iterator(); it2
						.hasNext();) {
					Object object = it2.next();
					if(object instanceof List){
						List list = (List)(object);
						StringBuffer listbuffer = new StringBuffer();
						for(Object obj : list){
							if (obj instanceof SysVerify) {
								String info = ((SysVerify) obj).sysVerify();
								if (null != info) {
									listbuffer.append(info);
								}

							}
						}
						mapbuffer.append(listbuffer);
					}
					if (object instanceof SysVerify) {
						String info = ((SysVerify) object).sysVerify();
						if (null != info) {
							buffer.append(info);
						}
					}
				}
				buffer.append(mapbuffer);
			}else if (v instanceof List) {
				StringBuffer mapbuffer = new StringBuffer();
				List m = (List) v;
				for (Iterator<Object> it2 = m.iterator(); it2
						.hasNext();) {
					Object object = it2.next();
					if(object instanceof List){
						List list = (List)(object);
						StringBuffer listbuffer = new StringBuffer();
						for(Object obj : list){
							if (obj instanceof SysVerify) {
								String info = ((SysVerify) obj).sysVerify();
								if (null != info) {
									listbuffer.append(info);
								}
							}
						}
						mapbuffer.append(listbuffer);
					}
					if (object instanceof SysVerify) {
						String info = ((SysVerify) object).sysVerify();
						if (null != info) {
							buffer.append(info);
						}
					}
				}
				buffer.append(mapbuffer);
			}

		}
		if (0 < buffer.toString().trim().length()) {
			Log4jManager.checkFail();
			Log4jManager.CHECK.error(buffer.toString());
		}
	}*/

	public boolean load() {
		if (loaded.compareAndSet(false, true)) {
			Map<K, V> d = this.loadData();
			if (null == d) {
				return false;
			}
			if (concurrent) {
				this.dataMap.clear();
				this.dataMap.putAll(d);
			} else {
				this.fastDataMap.clear();
				this.fastDataMap.putAll(d);
			}
			
			/*this.verifyData(d);*/
			
			d.clear();
			d = null;

		}
		return true;
	}

	public boolean reload() {
		loaded.set(false);
		return load();
	}

	public abstract Map<K, V> loadData();

	public void setConcurrent(boolean concurrent) {
		this.concurrent = concurrent;
	}
}
