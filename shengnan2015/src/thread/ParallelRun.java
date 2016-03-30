package thread;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ParallelRun {
	
	public static void execute(int threadSize, List<Runnable> tasks) {
		if(null == tasks || 0 == tasks.size()){
			return ;
		}
		CountDownLatch count = new CountDownLatch(tasks.size());
		ExecutorService fixedThreadPool = Executors.newFixedThreadPool(threadSize);
		for(Runnable task : tasks){
			fixedThreadPool.execute(new ExecuteRun(count,task));
		}
		try {
			count.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
