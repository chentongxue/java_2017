package game_;

import org.apache.commons.lang3.time.StopWatch;

public class StopWatchTest {
	//ִ�м�ʱ
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
		//ִ��ҵ��
		watch.start();
		test();
		watch.stop();
		
		//��¼ִ��ʱ��
		long timeRunning = watch.getTime();
		System.err.println(timeRunning);
	}
	public static void main(String args[]){
		StopWatchTest t = new StopWatchTest();
		t.hello();
	}
}
