package game;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Random;
import java.util.RandomAccess;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.collect.*;

/**
 * HashSet杂乱无序 TreeSey有顺序
 * 
 * @author mofun030601
 * 
 */
public class Luckdraw3lishengnan4 {

	private Luckdraw3lishengnan4() {
	}

	private static Luckdraw3lishengnan4 instance = null;
	private static final int SHUFFLE_THRESHOLD = 5;

	public static synchronized Luckdraw3lishengnan4 getInstance() {
		if (instance == null) {
			instance = new Luckdraw3lishengnan4();
		}
		return instance;
	}

	// public void get
	public static void main(String[] args) {
		int cc = 100000;
		List<String> list = new ArrayList<String>();
		// Random rnd = new Random();
		HashMap<Integer, Integer> ma = new HashMap<Integer, Integer>();
		

		while (cc-- > 0) {
			SecureRandom rand = new SecureRandom();
			int random = Math.abs(rand.nextInt()) % (10);
			if (ma.get(random) == null) {
				ma.put(random, 1);
			} else
				ma.put(random, 1 + ma.get(random));

		}
		System.out.println(Arrays.toString(ma.keySet().toArray()));
		System.out.println(Arrays.toString(ma.values().toArray()));
		//

	}

	/**
	 * 抽取必有奖励，奖励可重复
	 * 
	 * @param count
	 *            抽奖次数
	 * @param weightMap
	 * @return
	 */
	public static List<String> getLuckyDraw(int count,
			Map<String, Integer> weightMap) {
		List<String> list = Lists.newArrayList();
		SecureRandom rand = new SecureRandom();
		Map<String, Integer> factorMap = Maps.newHashMap();
		factorMap.putAll(weightMap);
		if (null == factorMap || 0 == factorMap.size()) {
			return list;
		}
		int sum = 0;
		for (Integer value : factorMap.values()) {
			sum += value;
		}
		if (0 == sum) {
			return list;
		}
		for (int i = 0; i < count; i++) {
			int random = Math.abs(rand.nextInt()) % (sum);
			int overlapCount = 0;
			for (Iterator<Map.Entry<String, Integer>> it = factorMap.entrySet()
					.iterator(); it.hasNext();) {
				Map.Entry<String, Integer> entry = it.next();
				String key = entry.getKey();
				int value = entry.getValue();
				if ((overlapCount < random)
						&& (random <= (overlapCount + value))) {
					list.add(key);
					sum -= value;
					// it.remove();//可重复
					break;
				}
				overlapCount += value;
			}// for
		}
		return list;
	}

}
