package game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

public class RandomTest2 {

	/**
	 *��򵥵Ĳ���������Ƿ�ֲ�����
	 */
	public static void main(String[] args) {
		int cc = 10000;
		LinkedHashMap<Integer, Integer> map = new LinkedHashMap<Integer, Integer>();
		Random rnd = new Random();
		
		while(cc-->0){
//		   Integer i = getWeightCalct(map);
			Integer i = rnd.nextInt(100);//0��99
		   if(i==null)
			   System.err.println(cc+"HIHI\n");
		   if(map.get(i)==null){
			   map.put(i, 1);
		   }
		   else{
			   map.put(i, map.get(i)+1);
		   }
		}
		
		System.out.println(Arrays.toString(map.values().toArray()));
		
	}

}
