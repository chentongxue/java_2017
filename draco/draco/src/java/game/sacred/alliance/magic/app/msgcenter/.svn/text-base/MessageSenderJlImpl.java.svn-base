package sacred.alliance.magic.app.msgcenter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.jetlang.channels.Channel;
import org.jetlang.channels.MemoryChannel;
import org.jetlang.fibers.Fiber;
import org.jetlang.fibers.PoolFiberFactory;

import sacred.alliance.magic.actor.AbstractActor;


public class MessageSenderJlImpl extends AbstractMessageSender{
	private MessageActor actor ;
	private int corePoolSize = 2;
	private int maximumPoolSize = 16;
	private long keepAliveMillisTime = 60 * 1000;
	
	private class MessageActor extends AbstractActor<MessageEntry>{
		private MessageSender sender ;
		public MessageActor(Channel<MessageEntry> inChannel, Fiber fiber,MessageSender sender) {
			this.inChannel = inChannel ;
			this.fiber = fiber ;
			this.sender = sender ;
		}

		@Override
		public void action(MessageEntry entry) {
			sender.messageEntryHandle(entry);
		}
	}


	@Override
	protected void addMessage(MessageEntry entry) {
		actor.publish(entry);
	}

	@Override
	protected void startAction() {
		// ExecutorService serv =
		// Executors.newFixedThreadPool(actorThreadPoolSize);
		ExecutorService serv = new ThreadPoolExecutor(corePoolSize,
				maximumPoolSize, keepAliveMillisTime, TimeUnit.MILLISECONDS,
				new SynchronousQueue<Runnable>(),
				new ThreadPoolExecutor.CallerRunsPolicy());
		PoolFiberFactory fact = new PoolFiberFactory(serv);
		Fiber fiber = fact.create();
		MemoryChannel<MessageEntry> channel = new MemoryChannel<MessageEntry>();
		actor = new MessageActor(channel, fiber, this);
		actor.start();
	}

	@Override
	protected void stopAction() {
		if (null != actor) {
			actor.stop();
		}
	}
	

	public void setCorePoolSize(int corePoolSize) {
		this.corePoolSize = corePoolSize;
	}

	public void setMaximumPoolSize(int maximumPoolSize) {
		this.maximumPoolSize = maximumPoolSize;
	}

	public void setKeepAliveMillisTime(long keepAliveMillisTime) {
		this.keepAliveMillisTime = keepAliveMillisTime;
	}

	

}
