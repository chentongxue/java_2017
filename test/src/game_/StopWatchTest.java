package game_;

import org.apache.commons.lang3.time.StopWatch;

public class StopWatchTest {
	//执行计时
	private final StopWatch watch;
	public StopWatchTest(){
		this.watch = new StopWatch();
	}
	public void test(){
		int n = 10000;
		while(n-->0){
			System.out.println(n);
		}
	}
	public void hello(){
		//执行业务
		watch.start();
		test();
		watch.stop();
		
		//记录执行时间
		long timeRunning = watch.getTime();
		System.err.println(timeRunning);
	}
	public static void main(String args[]){
		StopWatchTest t = new StopWatchTest();
		t.hello();
	}
}
