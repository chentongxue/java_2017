package a_effective.concurrent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import a_effective.inheritance.ForwardingSet;
//234
/**
 	过度同步 可能会导致性能降低，死锁，甚至不确定的行为。
 	为了避免活性丧失和安全性失败，在一个被同步的方法或代码块中，永远不要放弃对客户端的控制。
 	换句话说，在一个被同步的区域内部，不要调用设计成要被覆盖的方法，或者是由客户端以函数对象的形式提供的方法。
 	从包含该同步区域的类的角度来看，这样的方法是外来的（alien）。
 	这个类不知道该方法会做什么事情，也无法控制它。根据外来方法的作用，从同步区域中调用它会导致异常、死锁或者数据损坏。
 	
 	下面的例子是观察者模式，它允许客户端在将元素添加到集合中时预定通知

 */
public class ObservableSet<E> extends ForwardingSet<E> {
	public ObservableSet(Set<E> set){ super(set);}
	//疑问？这算不算对直接实例化一个借口？
	private final List<SetObserver<E>> observers = new ArrayList<SetObserver<E>>();
	
	public void addObserver(SetObserver<E> observer){
		synchronized(observers){
			observers.add(observer);
		}
	}
	
	public boolean removeObserver(SetObserver<E> observer){
		synchronized(observers){
			return observers.remove(observer);
		}
	}
	
	private void notifyElementAdded0(E element){
		synchronized(observers){
			for (SetObserver<E> observer:observers){
				observer.added(this, element);//不可以在里边用remove方法，我的理解也就是这个方法不能以接口 的形式让匿名内部类等子类实现
			}
		}
	}
	
	@Override
	public boolean add(E element){
		boolean added = super.add(element);
		if(added){
			notifyElementAdded(element);
		}
		return added;
	}
	
	@Override public boolean addAll(Collection<? extends E> c){
		boolean result = false;
		for(E element : c){
			result |= add(element);//calls notifyElementAdded
		}
		return result;
	}
	/*
	  Observer通过调用addObserver方法预定通知，通过调用removeObserver方法取消预定。
  	在这两种情况下，这个回调接口的事例都会被传递给方法：
		public interface SetObserver<E> {
			//Invoke when an element is added to the observable set
			void added(ObservableSet<E> set, E element);
		}
	   如果只是粗略地检验一下，ObserverSet会显得很正常。比如下面程序打印出0~99的数字
	 */
	public static void main0(String[] args){
		ObservableSet<Integer> set = new ObservableSet<Integer>(new HashSet<Integer>());
		set.addObserver(new SetObserver<Integer>(){
			@Override
			public void added(ObservableSet<Integer> set, Integer e) {
				System.out.println(e);
			}
		});
		for(int i = 0; i < 100; i++){
			set.add(i);
		}
	}
	/**
	 * 尝试一下更复杂点儿的例子。假设我们用一个addObserver调用来代替这个调用，用来替换的那个addObserver调用传递了一个打印Integer值的观察者，
	 * 这个值被添加到该集合中，如果值为23，这个观察者要将自身删除
	 * 这里实际上会抛出ConcurrentModificationException。
	 * 问题在于，当notifyElementAdded调用时观察者的added方法时，它正处于遍历observers列表的过程中。
	 * added方法调用可观察集合的removeObserver方法，从而调用observers.remove。
	 * 现在我们有麻烦了。我们正企图在遍历列表的过程中，将一个元素从列表中删除，这是非法的。
	 * notifyElementAdded方法中的迭代是在同一个同步的块中，可防止并发的修改，但是无法防止迭代线程本身回调到可观察的集合中，也无法防止修改它的observers列表
	 */
	public static void main1(String[] args){
		ObservableSet<Integer> set = new ObservableSet<Integer>(new HashSet<Integer>());
		set.addObserver(new SetObserver<Integer>(){
			@Override
			public void added(ObservableSet<Integer> set, Integer e) {
				System.err.println(e);
				if(e == 23){
					set.removeObserver(this);
				}
			}
		});
		for(int i = 0; i < 100; i++){
			set.add(i);
		}
	}
	//-236
	/**
	 * 现在我们尝试一些比较奇特的例子：我们来编写一个视图取消预定的观察者，但是不是直接调用removeObserver，它用另一个线程的服务来完成。
	 * 这个观察者使用了一个executor service
	 * 这一次，没有遇到异常，而是遇到了死锁。后台线程调用s.removeObserver,他企图锁定observers，但是他无法获得该锁，
	 * 因为主线程已经有锁了。这期间，主线程一直在等待后台线程来完成对观察者的删除，这正是造成死锁的原因。
	 */
	public static void main2(String[] args){
		ObservableSet<Integer> set = new ObservableSet<Integer>(new HashSet<Integer>());
		//Observer that uses a background thread needlessly
		set.addObserver(new SetObserver<Integer>(){
			@Override
			public void added(final ObservableSet<Integer> set, Integer e) {
				System.err.println(e);
				if(e == 23){
					ExecutorService executor = Executors.newSingleThreadExecutor();
					final SetObserver<Integer> observer = this;//这里是final，121行也得是final
					try{
						executor.submit(new Runnable() {
							
							@Override
							public void run() {
								set.removeObserver(observer);
							}
						}).get();
					}catch (ExecutionException ex) {
						throw new AssertionError(ex.getCause());
					}catch (InterruptedException ex){
						throw new AssertionError(ex.getCause());
					}finally{
						executor.shutdown();
					}
				}
			}
		});
		for(int i = 0; i < 100; i++){
			set.add(i);
		}
	}
	/**
	 * <此条未验证，或验证失败>
	 * 通过将外来方法的调用移出同步的代码块来解决这个问题通常并不困难。
	 * 对于notifyElementAdded方法，这还涉及给observers列表拍张“快照”，然后没有锁也可以安全地遍历这个表了。
	 * 经过这一修改，前两个例子运行起来便再也不会出现异常或死锁了。
	 */
	// Alien method moved outside of synchronized block - open calls
	private void notifyElementAdded(E element){
/*		synchronized(observers){
			for (SetObserver<E> observer:observers){
				observer.added(this, element);//不可以在里边用remove方法，我的理解也就是这个方法不能以接口 的形式让匿名内部类等子类实现
			}
		}*/
		List<SetObserver<E>> snapshot = null;
		synchronized (observers) {
			snapshot = new ArrayList<SetObserver<E>>(observers);
		}
		for(SetObserver<E>observer : snapshot){
			observer.added(this, element);
		}
	}
}











