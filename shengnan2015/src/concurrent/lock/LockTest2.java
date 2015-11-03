package concurrent.lock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LockTest2 {
	private Lock lock = new ReentrantLock();
	public void m1(){
		this.lock.lock(); 
		try{
			//do some thing
		}finally{
			lock.unlock();
		}
	}

}
