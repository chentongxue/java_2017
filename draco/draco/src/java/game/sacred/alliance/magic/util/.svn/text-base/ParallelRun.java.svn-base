package sacred.alliance.magic.util;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ParallelRun {
	
	public static void execute(int threadSize, List<Runnable> tasks) {
		if(null == tasks || 0 == tasks.size()){
			return ;
		}
		if(threadSize <=1 || 1== tasks.size()){
			for(Runnable r:tasks){
				r.run();
			}
			return ;
		}
		/*List<List<Runnable>> runnableList = new ArrayList<List<Runnable>>();
		int taskSize = tasks.size();
		int realThreadSize = Math.min(taskSize, threadSize);
		//每个线程执行数量
		int taskSizeInThread = taskSize/realThreadSize;
		
		//封装为每个线程执行的任务数
		for( int i = 1 ; i <= realThreadSize ; i++ ){
			List<Runnable> tempList = new ArrayList<Runnable>();
			int beginSize = (i-1)*taskSizeInThread;
			int endSize = beginSize + taskSizeInThread;
			if( i == realThreadSize){//最后一个线程执行余留任务
				tempList = tasks.subList(beginSize, taskSize);
			}else{				
				tempList = tasks.subList(beginSize, endSize);
			}
			if(tempList.size()>0){
				runnableList.add(tempList);
			}
		}
		CountDownLatch count = new CountDownLatch(runnableList.size());
		//执行任务
		for(List<Runnable> rList : runnableList){
			new Thread(new ExecuteRun(count,rList)).start();
		}
		*/
		
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
