package a_shengnan.a_mianshi;
/**
 * 内部类和静态内部类的区别
 * 21行，匿名内部类要访问局部变量，局部变量必须定义成final类型。
 */
public class A2 {
	private int a  = 0;
	private int b  = 0;
	public void method(){
		new Thread(){
			public void run(){
				a = 0;
			}
			
		}.start();
		final int c= 0;
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				b = 0;
				c = 0;
			}
		}).start();
	}

}
