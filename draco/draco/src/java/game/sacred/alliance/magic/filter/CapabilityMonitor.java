package sacred.alliance.magic.filter;

import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.onlinecenter.OnlineCenter;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.core.filter.Filter;
import sacred.alliance.magic.core.filter.FilterChain;






public class CapabilityMonitor implements Filter{
	private final Logger logger= LoggerFactory.getLogger(this.getClass());
	private Map<String,Tag> map=new ConcurrentHashMap<String, Tag>();
	private Timer timer=new Timer();
	private long interval=5000;
	private OnlineCenter onlineCenter ;
	
	public CapabilityMonitor() {
		super();
	}

	public long getInterval() {
		return interval;
	}

	public void setInterval(long interval) {
		this.interval = interval;
	}


	public void setOnlineCenter(OnlineCenter onlineCenter) {
		this.onlineCenter = onlineCenter;
	}

	public void init() {
		timer.schedule(new Task(map),interval,interval);
	}

	public void doFilter(ActionContext context, FilterChain chain) throws Exception {
		Tag tag=new Tag();
		tag.setMessage(context.getMessage());
		tag.setThreadId(Thread.currentThread().getId());
		map.put(String.valueOf(Thread.currentThread().getId()),tag);
		chain.doFilter(context);
		map.remove(String.valueOf(Thread.currentThread().getId()));
//		logger.debug("Action执行时间："+(System.currentTimeMillis()-tag.getCreateTime())+"毫秒；消息："+tag.getMessage().toString());
	}

	public void destroy() {
		
	}
	
	private class Task extends TimerTask{
		private Map<String,Tag> map;
		public Task(Map<String,Tag> map){
			this.map=map;
		}
		
		public void run() {
			try{
//				logger.debug("Monitor:"+sessionManager.sessionSize()+"/"+onlineCenter.onlineUserSize()+"(session/online user)");
				Iterator<Map.Entry<String, Tag>> it= map.entrySet().iterator();
				while(it.hasNext()){
					Tag tag=it.next().getValue();
					if(System.currentTimeMillis()-tag.getCreateTime()>interval){
						//过期，未清除,报警
						it.remove();
						logger.warn("Monitor:Action执行时间过长，执行时间超过"+interval+"毫秒；消息："+tag.getMessage().toString());
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
	}
	
}
