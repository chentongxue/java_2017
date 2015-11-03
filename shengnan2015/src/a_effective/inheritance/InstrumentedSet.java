package a_effective.inheritance;

import java.util.Collection;
import java.util.Set;
//74
/**
 * ��һ�ַ������Ա���InstrumentedHashSet�е��������⣬������չ���е��࣬�������µ���������һ��˽����
 * �������������һ��ʵ����������ƽ��������ϣ�composition��������Ϊ���е������������һ�������
 * �����û��ʵ�����������Ե��ñ�������������ʵ���ж�Ӧ�ķ��������������Ľ�����ⱻ��Ϊת����forwarding��,�����еķ�������Ϊת��������forwarding method)��
 * �����õ����ཫ��ǳ��ȹ̣������������������ʵ��ϸ�ڡ���ʱ���е���������µķ�����Ҳ����Ӱ���µ��ࡣ���������ʵ�ַ�Ϊ�������֣��౾��Ϳ����õ�ת���ࣨforwarding class��,
 * ���������еķ�����û������������
 * 
 * ��Ϊÿһ��InstrumentedSet����Setʵ����װ�����ˣ�����InstrumentedSet�౻��Ϊ��װ�ࣨwrapper class����
 *
 * ��װ�༸��û��ʲôȱ�㡣��Ҫע���һ���ǣ���װ�಻�ʺ����ڻص���ܣ�callback framework)�У��ڻص�����У��������������ô��ݸ������Ķ���
 * ���ں����ĵ��ã����ص���������Ϊ����װ�����Ķ��󲢲�֪��������İ�װ��������������һ��ָ����������ã�this����ͬʱ�ܿ�������İ�װ��������
 *	������һ��ָ�����������
 */
public class InstrumentedSet<E> extends ForwardingSet<E>{
	private int addCount = 0;
	public InstrumentedSet(Set<E> s) {
		super(s);
	} 
	
	@Override
	public boolean add(E e){
		addCount ++;
		return super.add(e);
	}
	
	@Override public boolean addAll(Collection<? extends E> c){
		addCount += c.size();
		return super.addAll(c);
	}
	
	public int getAddCount(){
		return addCount;
	}
}
