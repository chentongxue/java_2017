package a_shengnan.a_mianshi;
/**
 * ����ֱ�ӷ��룬�����·���ű���ͨ��
 */
public class SingleTon1 {
	
	private SingleTon1(){}//��֤�����ⲿʵ����
	
	private final static SingleTon1 instance;
	
	static{
		instance = new SingleTon1();
	}

	public static SingleTon1 getInstance(){
		return instance;
	}
}
