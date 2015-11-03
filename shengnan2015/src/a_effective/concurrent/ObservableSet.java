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
 	����ͬ�� ���ܻᵼ�����ܽ��ͣ�������������ȷ������Ϊ��
 	Ϊ�˱������ɥʧ�Ͱ�ȫ��ʧ�ܣ���һ����ͬ���ķ����������У���Զ��Ҫ�����Կͻ��˵Ŀ��ơ�
 	���仰˵����һ����ͬ���������ڲ�����Ҫ������Ƴ�Ҫ�����ǵķ������������ɿͻ����Ժ����������ʽ�ṩ�ķ�����
 	�Ӱ�����ͬ���������ĽǶ������������ķ����������ģ�alien����
 	����಻֪���÷�������ʲô���飬Ҳ�޷��������������������������ã���ͬ�������е������ᵼ���쳣���������������𻵡�
 	
 	����������ǹ۲���ģʽ��������ͻ����ڽ�Ԫ����ӵ�������ʱԤ��֪ͨ

 */
public class ObservableSet<E> extends ForwardingSet<E> {
	public ObservableSet(Set<E> set){ super(set);}
	//���ʣ����㲻���ֱ��ʵ����һ����ڣ�
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
				observer.added(this, element);//�������������remove�������ҵ����Ҳ����������������Խӿ� ����ʽ�������ڲ��������ʵ��
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
	  Observerͨ������addObserver����Ԥ��֪ͨ��ͨ������removeObserver����ȡ��Ԥ����
  	������������£�����ص��ӿڵ��������ᱻ���ݸ�������
		public interface SetObserver<E> {
			//Invoke when an element is added to the observable set
			void added(ObservableSet<E> set, E element);
		}
	   ���ֻ�Ǵ��Եؼ���һ�£�ObserverSet���Եú�������������������ӡ��0~99������
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
	 * ����һ�¸����ӵ�������ӡ�����������һ��addObserver����������������ã������滻���Ǹ�addObserver���ô�����һ����ӡIntegerֵ�Ĺ۲��ߣ�
	 * ���ֵ����ӵ��ü����У����ֵΪ23������۲���Ҫ������ɾ��
	 * ����ʵ���ϻ��׳�ConcurrentModificationException��
	 * �������ڣ���notifyElementAdded����ʱ�۲��ߵ�added����ʱ���������ڱ���observers�б�Ĺ����С�
	 * added�������ÿɹ۲켯�ϵ�removeObserver�������Ӷ�����observers.remove��
	 * �����������鷳�ˡ���������ͼ�ڱ����б�Ĺ����У���һ��Ԫ�ش��б���ɾ�������ǷǷ��ġ�
	 * notifyElementAdded�����еĵ�������ͬһ��ͬ���Ŀ��У��ɷ�ֹ�������޸ģ������޷���ֹ�����̱߳���ص����ɹ۲�ļ����У�Ҳ�޷���ֹ�޸�����observers�б�
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
	 * �������ǳ���һЩ�Ƚ����ص����ӣ���������дһ����ͼȡ��Ԥ���Ĺ۲��ߣ����ǲ���ֱ�ӵ���removeObserver��������һ���̵߳ķ�������ɡ�
	 * ����۲���ʹ����һ��executor service
	 * ��һ�Σ�û�������쳣��������������������̨�̵߳���s.removeObserver,����ͼ����observers���������޷���ø�����
	 * ��Ϊ���߳��Ѿ������ˡ����ڼ䣬���߳�һֱ�ڵȴ���̨�߳�����ɶԹ۲��ߵ�ɾ�������������������ԭ��
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
					final SetObserver<Integer> observer = this;//������final��121��Ҳ����final
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
	 * <����δ��֤������֤ʧ��>
	 * ͨ�������������ĵ����Ƴ�ͬ���Ĵ����������������ͨ���������ѡ�
	 * ����notifyElementAdded�������⻹�漰��observers�б����š����ա���Ȼ��û����Ҳ���԰�ȫ�ر���������ˡ�
	 * ������һ�޸ģ�ǰ��������������������Ҳ��������쳣�������ˡ�
	 */
	// Alien method moved outside of synchronized block - open calls
	private void notifyElementAdded(E element){
/*		synchronized(observers){
			for (SetObserver<E> observer:observers){
				observer.added(this, element);//�������������remove�������ҵ����Ҳ����������������Խӿ� ����ʽ�������ڲ��������ʵ��
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











