package sacred.alliance.magic.app.msgcenter;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MessageSenderImpl extends AbstractMessageSender{
	private ThreadPoolExecutor threadPool = null;
	private BlockingQueue<MessageEntry> messageQueue ;
	private int corePoolSize = 2;
	private int maximumPoolSize = 16;
	private long keepAliveMillisTime = 60 * 1000;
	private int queueCapacity = 500;
	private boolean clearWhenFull = false ;
	//每次清除容量
	private int clearCapacity = queueCapacity ;
	private Object clearLock = new byte[0] ;
	
	public void setCorePoolSize(int corePoolSize) {
		this.corePoolSize = corePoolSize;
	}

	public void setMaximumPoolSize(int maximumPoolSize) {
		this.maximumPoolSize = maximumPoolSize;
	}

	public void setKeepAliveMillisTime(long keepAliveMillisTime) {
		this.keepAliveMillisTime = keepAliveMillisTime;
	}

	public void setQueueCapacity(int queueCapacity) {
		this.queueCapacity = queueCapacity;
	}
	
	
	public void setClearWhenFull(boolean clearWhenFull) {
		this.clearWhenFull = clearWhenFull;
	}

	public void setClearCapacity(int clearCapacity) {
		this.clearCapacity = clearCapacity;
	}

	private void clearWhenFull(){
		if(!clearWhenFull || queueCapacity <= 0 ){
			return ;
		}
		synchronized (clearLock) {
			int size = messageQueue.size() ;
			if( size < queueCapacity){
				//未满
				return ;
			}
			if(logger.isInfoEnabled()){
				logger.info( this.threadName + " is full,will clear" );
			}
			if(this.clearCapacity == this.queueCapacity){
				//清除全部
				this.messageQueue.clear();
				this.msgSize.addAndGet(-size);
				return ;
			}
			//清除部分
			for(int i=0;i<clearCapacity;i++){
				this.messageQueue.poll();
			}
			this.msgSize.addAndGet(-clearCapacity);
		}
	}

	
	@Override
	protected void addMessage(MessageEntry entry) {
		this.clearWhenFull() ;
		this.messageQueue.add(entry);
	}

	private void init() {
		if(queueCapacity > 0){
			messageQueue = new LinkedBlockingQueue<MessageEntry>(queueCapacity);
		}else{
			messageQueue = new LinkedBlockingQueue<MessageEntry>();
		}
		threadPool = new ThreadPoolExecutor(corePoolSize, maximumPoolSize,
				keepAliveMillisTime, TimeUnit.MILLISECONDS,
				new SynchronousQueue<Runnable>(),
				new ThreadPoolExecutor.CallerRunsPolicy());
		
		if(this.clearCapacity > this.queueCapacity || this.clearCapacity <=0 ){
			this.clearCapacity = this.queueCapacity ;
		}
		
		final Thread task = new Thread(new Task());
		task.setName(threadName+ "_allot");
		task.setDaemon(true);
		task.start();
	}

	@Override
	protected void startAction() {
		init();
	}

	@Override
	protected void stopAction() {
		if (null != threadPool) {
			threadPool.shutdown();
		}
		if (null != messageQueue) {
			messageQueue.clear();
		}
	}

	private class Task implements Runnable {
		public void run() {
			while (running()) {
					try {
						final MessageEntry entry = messageQueue.take();
						if(null != entry){
							threadPool.execute(new Runnable(){
								public void run() {
									messageEntryHandle(entry);
								}
							});
						}
					} catch (Exception ex) {
						//ex.printStackTrace();
					}
			}
		}
	}
}
