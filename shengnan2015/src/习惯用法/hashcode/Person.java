package ϰ���÷�.hashcode;

import java.util.Arrays;
import java.util.HashMap;

import com.google.common.collect.Maps;
/**
 * 
	hashCode()��򵥵ĺϷ�ʵ�־��Ǽ򵥵�return 0����Ȼ���ʵ������ȷ�ģ�������ᵼ��HashMap��Щ���ݽṹ���еú�����
	ע��������a��bδ��ֵ�����java.lang.NullPointerException
 *
 */
public class Person {
	private String a;
	private Object b;
	private byte c;
	int [] d;
	
	@Override
	public int hashCode(){
		return a.hashCode() + b.hashCode() + c + Arrays.hashCode(d);
	}
	public static void main(String[] args) {
		Person p = new Person();
		Person p1 = new Person();
		HashMap map = Maps.newHashMap();
		map.put(p,"hi");
		map.put(p1,"hi2");
		System.out.println(map);
		System.out.println(map.get(p));
		System.out.println(map.get(p1));
//		System.out.println(p.hashCode());
	}
}
