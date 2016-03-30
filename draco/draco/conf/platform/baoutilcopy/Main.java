package baoutilcopy;

import java.awt.Color;


public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		LogB.i("hello 宝");
		LogB.i("welcome 宝",Color.red);
		new Thread(){
			public void run() {
				int i = 100;
				while((--i)>0){
					try {
						Thread.sleep(50);
						LogB.i("welcome 宝"+i,Color.red);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			
		}.start();
	}

}
