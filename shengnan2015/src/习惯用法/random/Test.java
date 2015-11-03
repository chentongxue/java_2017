package œ∞πﬂ”√∑®.random;

import java.util.HashMap;
import java.util.Random;

import com.google.common.collect.Maps;

public class Test {
	public static final Random rand = new Random();
	public static int diceRoll(){
		return rand.nextInt(100) + 1;
	}
	public static void main(String[] args) {
		int n = 5000;
		HashMap<Integer, Integer> map = Maps.newHashMap();
		while(n-- > 0){
			int r = diceRoll();
			if(map.containsKey(r)){
				map.put(r, map.get(r) + 1);
				continue;
			}
			map.put(r, 1);
		}
		System.out.println(map.toString().replace(", ", "\n"));
	}

}
