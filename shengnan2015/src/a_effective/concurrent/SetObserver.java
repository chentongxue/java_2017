package a_effective.concurrent;
//235
/*
  Observer通过调用addObserver方法预定通知，通过调用removeObserver方法取消预定。 
  在这两种情况下，这个回调接口的事例都会被传递给方法：
 */
public interface SetObserver<E> {
	//Invoke when an element is added to the observable set
	void added(ObservableSet<E> set, E element);
}
