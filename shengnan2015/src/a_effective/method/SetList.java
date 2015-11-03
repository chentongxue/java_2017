package a_effective.method;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
/**
 * ��������
 *
 */
public class SetList {
//167
	/**
	 * �ٶ���Set set.remove(i)���õ�ѡ�����ط���remove(E),�����E�Ǽ��ϣ�Integer����Ԫ�����ͣ���i�Զ�װ�䵽Integer�С�
	 * �ڶ���List��list.remove(i)����ѡ�����ط���remove(int i);�����б�ָ��λ����ȥ��Ԫ�ء�Ҫ����������Ҫô�������ΪInteger��Ҫô����Integer.valueOf(i)
	 * ������ΪList<E>�ӿ����������ص�remove������remove��E������remove��int������Ӧ�Ĳ�������Object��int���������ͬ�������Դ����˷��ͺ��Զ�װ��������ֲ������;Ͳ��ڸ�����ͬ�ˡ�
	 * ���Java��������˷��ͺ��Զ�װ����ƻ���List�ӿڡ�
	 * 	int a = Integer.parseInt("1");
		Integer b= Integer.valueOf("2");
		Integer c = Integer.getInteger("3");//ע�ⲻҪʹ���������Ҫ��ǰ���������
		
		��Ȼ1.4�汾String���Ѿ���һ��contentEquals(StringBuffer)��������1.5��������CharSequence�ӿڣ�����String���������ص�contentEquals������contentEquals(CharSequence)
		��ֻҪ������������ͬ���Ĳ����ϱ�����ʱ������ִ����ͬ�Ĺ��ܣ����ؾͲ������Σ����
		public boolean contentEquals(StringBuffer sb) {
        synchronized(sb) {
            return contentEquals((CharSequence)sb);
        }
    }
	 */
	public static void main(String[] args) {
		Set<Integer> set = new TreeSet<Integer>();
		List<Integer> list= new ArrayList<Integer>();
		for(int i = -3; i< 3; i++){
			set.add(i);
			list.add(i);
		}
		for(int i = 0; i< 3; i++){
			set.remove(i);
			list.remove(i);
		}
		System.out.println("set = " + set);//set = [-3, -2, -1]
		System.out.println("list = " + list);//list = [-2, 0, 2]
		test1();
		test2();
	}
	public static void test1(){
		List<Integer> list= new ArrayList<Integer>();
		for(int i = -3; i< 3; i++){
			list.add(i);
		}
		for(int i = 0; i< 3; i++){
			list.remove((Integer)i);
		}
		System.out.println("list = " + list);//[-3, -2, -1]
	}
	public static void test2(){
		List<Integer> list= new ArrayList<Integer>();
		for(int i = -3; i< 3; i++){
			list.add(i);
		}
		for(int i = 0; i< 3; i++){
			list.remove((Integer.valueOf(i)));
		}
		System.out.println("list = " + list);//[-3, -2, -1]
	}
	public static void test3(){
		LinkedHashMap<Integer,Integer> map = new LinkedHashMap<>();
		
		List<Integer> list= new ArrayList<Integer>();
		for(int i = -3; i< 3; i++){
			list.add(i);
		}
		for(int i = 0; i< 3; i++){
			list.remove((Integer.valueOf(i)));
		}
		System.out.println("list = " + list);//[-3, -2, -1]
	}
}
