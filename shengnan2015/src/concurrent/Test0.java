package concurrent;
/**
 * i++,并不是原子操作
 */
public class Test0 {
	private static  int a = 0;
	public static void main(String[] args){
		System.err.println("LOL");
		int n=10000;
		while(n-->0){
			startAdd();
		}
		System.out.println(a);
	}
	public static void startAdd(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				a++;
			}
		}).start();
	}
}
