package a_shengnan.a_mianshi;
/**
 * �ڲ���;�̬�ڲ��������
 * 21�У������ڲ���Ҫ���ʾֲ��������ֲ��������붨���final���͡�
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
