package sort;

import java.security.SecureRandom;
import java.util.Random;

public class RandomTest {
	public static void main(String args[]){
		SecureRandom rand = new SecureRandom();  
//		Random rand = new Random();
		int index = rand.nextInt(3);
		int n = 20;
		while(n-->0){
			System.out.println(rand.nextInt(3));
		}
		
		
	}
}
