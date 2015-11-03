package a_effective.inheritance;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

//71
/**
 * �뷽�����ò�ͬ���ǣ��̳д����˷�װ�ԡ����仰˵�������������䳬�����ض����ܵ�ʵ��ϸ�ڡ�
 * �����ʵ���п��ܻ����ŷ��а汾�Ĳ�ͬ�������仯�������ķ����仯��������ܻ��⵽�ƻ���
 * ��ʹ���Ĵ�����ȫû�иı䣬���ǳ�����ר��Ϊ����չ����Ƶģ������кܺõ��ĵ�˵����
 * Ϊ˵���ĸ��Ҿ���һ��������Ǽ�����һ������ʹ����HashSet��Ϊ�˵��Ÿĳ�������ܣ���Ҫ��ѯ
 * HashSet����һ���Դ����Ӵ���������������˶��ٸ�Ԫ�أ���Ҫ������ǰ��Ԫ�ػ���������Ԫ����Ŀ������Ԫ�ص�ɾ�����ݼ�����
 * Ϊ���ṩ���ֹ��ܣ����Ǳ�дһ��HashSet����������¼����ͼ�����Ԫ������������Ըü���ֵ����һ�����ʷ�����
 * HashSet�����������������Ԫ�صķ�����add��addAll,���������������Ҫ�����ǣ�
 */
public class InstrumentedHashSet<E> extends HashSet<E> {
	// The number of attempted element insertions
	private int addCount = 0; 
	
	public InstrumentedHashSet(){
	}
	
	public InstrumentedHashSet(int initCap, float loadFactor){
		super(initCap, loadFactor);
	}
	
	@Override public boolean add(E e){
		addCount ++;
		return super.add(e);
	}
	
	@Override public boolean addAll(Collection<? extends E> c){
		addCount += c.size();
		return super.addAll(c);
	}
	
	int getAddCount(){
		return addCount;
	}
	
	public static void main(String args[]){
		InstrumentedHashSet<String> s = new InstrumentedHashSet<String>();
		s.addAll(Arrays.asList("Snap", "Crackle", "Pop"));
		/* ��������getAddCount��������3������ʵ���Ϸ��ص���6����ΪaddAll�����ǻ���add����ʵ�ֵģ��������������ԣ���ʵ��ϸ�ڣ����ǳ�ŵ */
		System.out.println(s.getAddCount());
	}
}
