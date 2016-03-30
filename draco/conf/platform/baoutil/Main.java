package baoutil;

import java.awt.Color;


public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		new Thread(){
			public void run() {
				int i = 100;
				while((--i)>0){
					LogB.ic("welcome 宝"+i);
				}
			}
			
		}.start();
	}

}
