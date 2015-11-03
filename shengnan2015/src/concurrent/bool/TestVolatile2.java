package concurrent.bool;

import java.util.concurrent.atomic.AtomicBoolean;
/**
 * Ä£°å
 * @author mofun030601
 *
 */
public class TestVolatile2 {
	private volatile int value;
	public int getValue() { return value; }

    public synchronized int increment() {
        return value++;
    }
    
	public static void main(String[] args) {

	}

}
