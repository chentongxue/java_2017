package a_shengnan.a_mianshi;
/**��������,�����з���static����̬����飩����Σ� */
public class SingleTon {
	
	private SingleTon(){}//��֤�����ⲿʵ����
	
	private final static SingleTon instance = new SingleTon();
	
	public static SingleTon getInstance(){
		return instance;
	}
}
