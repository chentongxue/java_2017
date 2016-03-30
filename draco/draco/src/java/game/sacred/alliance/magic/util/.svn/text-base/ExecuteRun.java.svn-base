package sacred.alliance.magic.util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class ExecuteRun implements Runnable{
	private CountDownLatch count ;
	private List<Runnable> targets = new ArrayList<Runnable>() ;
	public ExecuteRun(CountDownLatch count,List<Runnable> targets){
		this.count = count ;
		this.targets = targets ;
	}
	
	public ExecuteRun(CountDownLatch count,Runnable task){
		this.count = count ;
		this.targets.add(task);
	}
	
	@Override
	public void run() {
		try {
			if (null != targets) {
				for (Runnable r : targets) {
					try {
						r.run();
					} catch (Exception ex) {
					}
				}
			}
		} finally {
			if (null != count) {
				count.countDown();
			}
		}
	}
}
