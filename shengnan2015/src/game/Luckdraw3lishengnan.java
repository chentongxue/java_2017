package game;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Random;
import java.util.RandomAccess;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.collect.*;
/**
 * HashSet��������
 * TreeSey��˳��
 * @author mofun030601
 *
 */
public class Luckdraw3lishengnan {

	private Luckdraw3lishengnan(){}
	private static Luckdraw3lishengnan instance = null;
    private static final int SHUFFLE_THRESHOLD        =    5;
	public static synchronized Luckdraw3lishengnan getInstance(){
		if(instance == null){
			instance = new Luckdraw3lishengnan();
		}
		return instance;
	}
//	public void  get
	public static void main(String[] args) {
		HashMap map = new HashMap<String, Integer>();
		map.put("0", 1);
		map.put("1", 2);
		map.put("2", 1);
		map.put("3", 1);
//		map.put("4", 1);
//		map.put("5", 1);
//		map.put("6", 1);
//		map.put("7", 1);
//		map.put("8", 5);
//		map.put("9", 5);
//		map.put("10", 5);
//		map.put("11", 5);
//		int cc = 1000;
//		List<String> list = new ArrayList<String>();
//		HashMap<String, Integer> ma=  new HashMap<String, Integer>();
//		int count = 0;
//		while(cc-->0){
//			list = getLuckyDrawUnique(3,map);
//			for (int i = 0; i < list.size(); i++) {
//				String key = list.get(i);
//				if(ma.get(key)==null){
//					ma.put(key, 1);
//				}else
//				ma.put(key, 1+ma.get(key));
//			}
//			count += list.size();
//			Collections.sort(list);
//		}
//		System.out.println("key"+Arrays.toString(ma.keySet().toArray()));
//		System.out.println("val"+Arrays.toString(ma.values().toArray()));
//		System.out.println(count);
//		String fc = "0_7:1,normal_98:2,normal_84:2,normal_45:4,normal_7:2,normal_28:5,normal_54:2,normal_101:4";
//		System.out.println(fc.length());
//		
//		LinkedHashMap<String, Integer> lm = new LinkedHashMap<String, Integer>();
//		int b = lm.get("a");
//		System.out.println(b);
		
		Map<String, Integer> lm = new HashMap<String, Integer>();
		Integer b = lm.get("a");
		System.out.println(b);
		
	}
	/**
	 * ÿ��ȡ��Żغ���
	 * @param count �齱����
	 * @param oddsMap Ȩֵ��
	 * @return
	 */
	public static <T> List<T> getLuckyDrawPutBack(int count,Map<T,Integer> oddsMap){
		List<T> list = Lists.newArrayList();
		SecureRandom rand = new SecureRandom();
		Map<T,Integer> factorMap = Maps.newHashMap();
		factorMap.putAll(oddsMap);
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
			int random = rand.nextInt(sum)+1;//���ȵĲ���1��SUM
			int overlapCount = 0; 
			for(Map.Entry<T, Integer> entry:factorMap.entrySet()){
				T key = entry.getKey() ;
				int value = entry.getValue() ;
				if((overlapCount < random) && (random <= (overlapCount+ value))){
					list.add(key);
					break;
				}
				overlapCount += value;
			}//for
		}
		return list;
	}
	/**
	 * С��ȡ��ÿ����ȡ���ļ��ʲ�ͬ�����Żغ���
	 * ����Ȩ��ȡ�ò�ͬ�Ľ���
	 * @param count �齱����
	 * @param oddsMapȨ�ر�
	 * @return
	 */
	public static <T> List<T> getLuckyDrawUnique(int count,Map<T,Integer> oddsMap){
		List<T> list = Lists.newArrayList();
		SecureRandom rand = new SecureRandom();
		Map<T,Integer> factorMap = Maps.newHashMap();
		factorMap.putAll(oddsMap);
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
			int random = rand.nextInt(sum)+1;//���ȵĲ���1��SUM
			int overlapCount = 0; 
			for(Iterator<Map.Entry<T, Integer>> it = factorMap.entrySet().iterator();it.hasNext();){
				Map.Entry<T, Integer> entry = it.next();
				T key = entry.getKey() ;
				int value = entry.getValue() ;
				if((overlapCount < random) && (random <= (overlapCount+ value))){
					list.add(key);
					it.remove();
					sum -= value;
					break;
				}
				overlapCount += value;
			}//for
		}
		return list;
	}
	/**
	 * ÿ��ȡ�겻�Żغ���
	 * ��ȡ���н��������ظ�
	 * @param count �齱����
	 * @param oddsMap
	 * @return
	 */
	public static <T> List<T> getLuckyDrawNobBack(int count,Map<T,Integer> oddsMap){
		List<T> list = Lists.newArrayList();
		SecureRandom rand = new SecureRandom();
		Map<T,Integer> factorMap = Maps.newHashMap();
		factorMap.putAll(oddsMap);
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
			int random = rand.nextInt(sum)+1;//���ȵĲ���1��SUM
			int overlapCount = 0; 
			for(Iterator<Map.Entry<T, Integer>> it = factorMap.entrySet().iterator();it.hasNext();){
				Map.Entry<T, Integer> entry = it.next();
				T key = entry.getKey() ;
				int value = entry.getValue() ;
				if((overlapCount < random) && (random <= (overlapCount+ value))){
					list.add(key);
					/* 
					 * Ҫȡ���ظ�����ķ���
					 * it.remove()
					 * sum -= value;
					 */
					
					sum -= 1; //�Żغ��ڣ�ÿ�λ������
					entry.setValue(value-1);//
					break;
				}
				overlapCount += value;
			}//for
		}
		return list;
	}

}
