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
/**
 * HashSet杂乱无序
 * TreeSey有顺序
 * @author mofun030601
 *
 */
public class Luckdraw3 {

	private Luckdraw3(){}
	private static Luckdraw3 instance = null;
    private static final int SHUFFLE_THRESHOLD        =    5;
	public static synchronized Luckdraw3 getInstance(){
		if(instance == null){
			instance = new Luckdraw3();
		}
		return instance;
	}
//	public void  get
	public static void main(String[] args) {
		HashMap map = new HashMap<Integer, Integer>();
		map.put(0, 5);
		map.put(1, 5);
		map.put(2, 5);
		map.put(3, 5);
		map.put(4, 5);
		map.put(5, 5);
		map.put(6, 5);
		map.put(7, 5);
		map.put(8, 5);
		map.put(9, 5);
		map.put(10, 5);
		map.put(11, 5);
//		ArrayList<Integer> list = getLuckyDraw(10,map);
		int cc = 10000;
		ArrayList<Integer> list = new ArrayList<Integer>();
		Random rnd = new Random();
		
		while(cc-->0){
//		   Integer i = getWeightCalct(map);
			Integer i = rnd.nextInt(100);//1到100
		   if(i==null)
			   System.err.println(cc+"HIHI\n");
		   list.add(i);
		}
		Collections.sort(list);
		System.out.println("HI\n"+list.size()+"\n"+list.toString());
//		getWeightCalct(2 ,map);
		
//		System.out.println("HIHI\n"+Math.abs(rand.nextInt()));
		
	}
	/**
	 * 包含边界
	 * @param left
	 * @param right
	 * @return
	 */
	public int getRandom(int left ,int right){
		if(left == right){
			return left ;
		}
		if(right < left){
			return (int) (Math.random() * (left - right + 1)) + right;
		}
		return (int) (Math.random() * (right - left + 1)) + left;
	}

	public static Set<Integer> getWeightCalct(int count,final Map<Integer,Integer> map){
//		Set<Integer> set = new HashSet<Integer>();
//		Collections.sort(map);
		int sum = 0;
        Iterator<Integer> iter = map.keySet().iterator();
        while (iter.hasNext()) {
        	int key = iter.next();
        	int value = map.get(key);
        	sum += value;
        }
        while(count-->=0){
        	int darwNumber = (int) (Math.random() * sum);
        	int anchorNumber = 0;
        	System.out.println("AA"+darwNumber);
        	iter = map.keySet().iterator();
            while (iter.hasNext()) {
            	int key = iter.next();
            	anchorNumber += map.get(key);
            	if(darwNumber >= anchorNumber){
            		
            	}
            }
        }
        System.out.println(sum); 
        return null;
}
		

	
	public static Integer getWeightCalct(final Map<Integer,Integer> factorMap){
		int sumGon = 0;
		//获得属性总值，用作产生随机属性
		for(Integer gon : factorMap.values()){
			sumGon += gon ;
		}
		if(0 == sumGon){
			 System.err.println("0 == sumGon\n");
			return null;
		}
		Random rnd = new Random();
		int random = rnd.nextInt(sumGon)+1;
		int overlapCount = 0; 
		for(Iterator<Integer> it = factorMap.keySet().iterator();it.hasNext();){
			int key = it.next();
			int gonNum = factorMap.get(key);
			if((overlapCount< random) && (random<= (overlapCount+ gonNum))){
				return key;
			}
			overlapCount += gonNum;
		}
		 System.err.println(random+"ended\n");
		return null;
	}
	
	
	
	
	
//	private static SecureRandom rand = new SecureRandom();
	/**
	 * 抽取必有奖励，奖励可重复
	 * @param count 抽奖次数
	 * @param weightMap
	 * @return
	 */
	public static ArrayList getLuckyDraw(int count,Map<Integer,Integer> weightMap){
		ArrayList<Integer> list = new ArrayList<Integer>();
		SecureRandom rand = new SecureRandom();
		Map<Integer,Integer> factorMap = new HashMap<Integer,Integer>();
		factorMap.putAll(weightMap);
		if(null == factorMap || 0 == factorMap.size()){
			return list;
		}
		int sum = 0 ;
		for(Integer value : factorMap.values()){
			sum += value;
		}
		if(0 == sum){ 
			return list;
		}
		for(int i =0; i<count ;i++){
			int random = Math.abs(rand.nextInt()) % (sum);
			int overlapCount = 0; 
			for(Iterator<Map.Entry<Integer, Integer>> it = factorMap.entrySet().iterator();it.hasNext();){
				Map.Entry<Integer, Integer> entry = it.next();
				int key = entry.getKey() ;
				int value = entry.getValue() ;
				if((overlapCount < random) && (random <= (overlapCount+ value))){
					list.add(key);
					sum -= value;
//					it.remove();//可重复
					break;
				}
				overlapCount += value;
			}//for
		}
		return list;
	}



}
