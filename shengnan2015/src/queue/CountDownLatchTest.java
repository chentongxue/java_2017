package queue;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CountDownLatch;

public class CountDownLatchTest {
	private final static SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
	public static void main(String[] args) throws InterruptedException {
        CountDownLatch latch=new CountDownLatch(2);//两个工人的协作  
        Worker worker1=new Worker("bao", 2000, latch);  
        Worker worker2=new Worker("senan", 4000, latch);  
        worker1.start();  
        worker2.start(); 
        latch.await();	//等待所有worker完成工作  
        System.out.println("all work done at "+sdf.format(new Date()));  	}
	static class Worker extends Thread{
		private String workerName;
		private int workTime;
		private CountDownLatch latch;
		public Worker(String workerName, int workTime, CountDownLatch latch){
			this.workerName = workerName;
			this.workTime = workTime;
			this.latch = latch;
		}
		public void run(){
			System.out.println(workerName + " start at" + sdf.format(new Date()));
			try {
				Thread.sleep(workTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}finally{
				latch.countDown();	//计数器减一
			}
			System.out.println(workerName + " end at" + sdf.format(new Date()));
		}
	}
}
