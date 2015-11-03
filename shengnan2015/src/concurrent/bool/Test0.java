package concurrent.bool;

import java.util.concurrent.atomic.AtomicBoolean;
/**
 * Ä£°å
 * @author mofun030601
 *
 */
public class Test0 {
	private static AtomicBoolean update = new AtomicBoolean(false);
	public void init()
	{
	   if( this.update.compareAndSet(false, true) )
	   {
		   try{
			// do some thing
		   }
		   finally{
			   this.update.set(false);
		   }
	   }
	}
	public static void main(String[] args) {

	}

}
