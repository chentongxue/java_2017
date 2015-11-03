package a_shengnan.a_mianshi.binary_tree;

import java.util.concurrent.atomic.AtomicInteger;

public class IntegerMachine {
	public static final IntegerMachine instance= new IntegerMachine();
	private IntegerMachine(){}
	private static AtomicInteger index = new AtomicInteger(0);
	public static int getIndex(){
		return index.incrementAndGet();
	}
	public static void main(String[] args) {

	}

}
