package sacred.alliance.magic.app.msgcenter;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.onlinecenter.OnlineCenter;
import sacred.alliance.magic.monitor.MonitorPrint;
import sacred.alliance.magic.vo.RoleInstance;

public abstract class AbstractMessageSender implements MessageSender,MonitorPrint{
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	private AtomicBoolean started = new AtomicBoolean(false);
	protected OnlineCenter onlineCenter ;
	protected AtomicLong msgSize = new AtomicLong(0);
	protected AtomicLong processSize =  new AtomicLong(0);
	protected String threadName = this.getClass().getSimpleName();
	
	public void sendMessage(MessageEntry entry){
		if(null == entry || null == entry.getMessage()){
			return ;
		}
		
		this.msgSize.incrementAndGet();
		this.addMessage(entry);
	}
	
	protected abstract void addMessage(MessageEntry entry);
	
	protected abstract void startAction();
	
	protected abstract void stopAction();
	
	protected boolean running(){
		return started.get();
	}
	
	@Override
	public void monitorPrint(){
		logger.info(threadName  +" monitor: msgSize=" + msgSize.get() + " processSize=" + processSize.get());
	}
	
	
	public void start() {
		if (started.compareAndSet(false, true)) {
			this.startAction();
		}
	}

	public void stop() {
		if (started.compareAndSet(true, false)) {
			this.stopAction();
		}
	}
	
	@Override
	public void messageEntryHandle(MessageEntry entry) {
		processSize.incrementAndGet();
		msgSize.decrementAndGet();
		try {
			if (null == entry || entry.isExpire() || null == entry.getMessage()) {
				return;
			}
			String destUserId = entry.getDestUserId();
			if (!entry.isCustomerService()) {
				RoleInstance role = onlineCenter
						.getRoleInstanceByUserId(destUserId);
				if (null == role) {
					return;
				}
				role.getBehavior().sendMessage(entry.getMessage());
			} else {
				// GM
				
			}
			
		} finally {
			processSize.decrementAndGet();
			if (null != entry) {
				entry.setMessage(null);
				entry = null;
			}
		}
	}

	public void setOnlineCenter(OnlineCenter onlineCenter) {
		this.onlineCenter = onlineCenter;
	}

	public String getThreadName() {
		return threadName;
	}

	public void setThreadName(String threadName) {
		this.threadName = threadName;
	}


	
	
}
