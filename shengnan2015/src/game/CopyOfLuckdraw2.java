package game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Random;
import java.util.RandomAccess;
import java.util.TreeSet;
/**
 * HashSet‘”¬“Œﬁ–Ú
 * TreeSey”–À≥–Ú
 * @author mofun030601
 *
 */
public class CopyOfLuckdraw2 {

//	public void  get
	public static void main(String[] args) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("1", "1");
		map.put("2", "2");
		map.put("3", "3");
		map.put("4", "4");
		 
		Collection<String> valueCollection = map.values();
		final int size = valueCollection.size();
		 
		List<String> valueList = new ArrayList<String>(valueCollection);
		 
		String[] valueArray = new String[size];
		map.values().toArray(valueArray);
		
		TreeSet set = new TreeSet<Double>();
		int n = 100;
		Random rnd = new Random();
		while(n-->=0){
			set.add(rnd.nextDouble());
		}
		System.out.println(Arrays.toString(set.toArray()));
//		TreeSet set = new TreeSet<Integer>();
//		int n = 100;
//		Random rnd = new Random();
//		while(n-->=0){
//			set.add(rnd.nextInt(10));
//		}
//		System.out.println(Arrays.toString(set.toArray()));
	}

}
