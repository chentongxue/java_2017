package a_effective.concurrent;
//235
/*
  Observerͨ������addObserver����Ԥ��֪ͨ��ͨ������removeObserver����ȡ��Ԥ���� 
  ������������£�����ص��ӿڵ��������ᱻ���ݸ�������
 */
public interface SetObserver<E> {
	//Invoke when an element is added to the observable set
	void added(ObservableSet<E> set, E element);
}
