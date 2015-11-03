package a_effective.inheritance;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

//71
/**
 * 与方法调用不同的是，继承打破了封装性。话句话说，子类依赖于其超类中特定功能的实现细节。
 * 超类的实现有可能会随着发行版本的不同而有所变化，如果真的发生变化，子类可能会遭到破坏，
 * 即使它的代码完全没有改变，除非超类是专门为了扩展而设计的，并且有很好的文档说明。
 * 为说明的跟家具体一点儿，我们假设有一个程序使用了HashSet。为了调优改程序的性能，需要查询
 * HashSet，看一看自从它从创建以来曾经添加了多少个元素（不要与它当前的元素混淆起来，元素数目会随着元素的删除而递减）。
 * 为了提供这种功能，我们编写一个HashSet变量，它记录下试图插入的元素数量，并针对该计数值导出一个访问方法。
 * HashSet类包含两个可以增加元素的方法：add和addAll,因此这两个方法都要被覆盖：
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
		/* 我们期望getAddCount方法返回3，但是实际上返回的是6，因为addAll方法是基于add方法实现的，但是这种自用性，是实现细节，不是承诺 */
		System.out.println(s.getAddCount());
	}
}
