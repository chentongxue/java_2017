package sacred.alliance.magic.app.onlinecenter;

import java.util.concurrent.atomic.AtomicBoolean;

import com.game.draco.GameContext;

import sacred.alliance.magic.core.server.MinaServer;

public class PrintOnlineInfo  {
	private long interval = 5*1000 ;
	private MinaServer minaServer ;
	private OnlineCenter onlineCenter ;
	private AtomicBoolean start = new AtomicBoolean(false);
	public void start(){
		if(start.compareAndSet(false, true)){
			Thread t = new Thread(new Runnable(){
				@Override
				public void run() {
					while(start.get()){
						act();
					}
				}
			});
			t.setName("thread-printonline");
			t.start();
		}
	}
	
	private void act(){
		try {
			try{
				Thread.sleep(interval);
			}catch(Exception e){
			}
			GameContext.getStatLogApp().roleOnlineLog();
		} catch (Exception e) {
		}
	}
	 
	public void stop(){
		start.set(false);
	}

	public void setMinaServer(MinaServer minaServer) {
		this.minaServer = minaServer;
	}


	public void setOnlineCenter(OnlineCenter onlineCenter) {
		this.onlineCenter = onlineCenter;
	}

	public void setInterval(long interval) {
		this.interval = interval;
	}

	
}
