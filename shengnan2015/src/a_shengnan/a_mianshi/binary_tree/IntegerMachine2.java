package a_shengnan.a_mianshi.binary_tree;

import java.util.concurrent.atomic.AtomicInteger;

public class IntegerMachine2 {
	public static final IntegerMachine2 instance= new IntegerMachine2();
	
	private volatile int value;
	
	private IntegerMachine2(){}
	
	public synchronized int incrementAndget() {
	        return value++;
	}

}
