package baoutil;

import java.awt.Color;


public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		LogB.i("hello ");
		LogB.i("welcome darling",Color.red);
		new Thread(){
			public void run() {
				int i = 100;
				while((--i)>0){
					try {
						Thread.sleep(50);
						LogB.i("welcome"+i,Color.red);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			
		}.start();
	}

}
