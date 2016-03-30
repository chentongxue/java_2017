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
