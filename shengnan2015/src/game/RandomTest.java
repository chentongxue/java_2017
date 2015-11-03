package game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

public class RandomTest {

	/**
	 *最简单的测试随机数
	 */
	public static void main(String[] args) {
		int cc = 10000;
		TreeSet<Integer> set = new TreeSet<Integer>();
		Random rnd = new Random();
		
		while(cc-->0){
//		   Integer i = getWeightCalct(map);
			Integer i = rnd.nextInt(100);//0到99
		   if(i==null)
			   System.err.println(cc+"HIHI\n");
		   set.add(i);
		}
		System.out.println(Arrays.toString(set.toArray()));
		System.out.println("abc".replace("a", "A"));
	}

}
