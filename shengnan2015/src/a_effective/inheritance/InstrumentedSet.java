package a_effective.inheritance;

import java.util.Collection;
import java.util.Set;
//74
/**
 * 有一种方法可以避免InstrumentedHashSet中的所有问题，不用扩展现有的类，而是在新的类中增加一个私有域，
 * 它引用现有类的一个实例。这种设计叫做“复合（composition）”，因为现有的类变成了新类的一个组件。
 * 新类的没法实例方法都可以调用被包含的现有类实例中对应的方法，并返回它的结果。这被称为转发（forwarding）,新类中的方法被称为转发方法（forwarding method)。
 * 这样得到的类将会非常稳固，它不依赖于现有类的实现细节。及时现有的类添加了新的方法，也不会影响新的类。下面的例子实现分为两个部分：类本身和可重用的转发类（forwarding class）,
 * 包含了所有的方法，没有其他方法。
 * 
 * 因为每一个InstrumentedSet都把Set实例包装起来了，所以InstrumentedSet类被称为包装类（wrapper class）。
 *
 * 包装类几乎没有什么缺点。需要注意的一点是，包装类不适合用在回调框架（callback framework)中；在回调框架中，对象把自身的引用传递给其他的对象，
 * 用于后续的调用（“回调”）。因为被包装起来的对象并不知道它外面的包装对象，所以它传递一个指向自身的引用（this），同时避开了外面的包装对象。所以
 *	它传递一个指向自身的引用
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
