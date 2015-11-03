package a_shengnan.a_mianshi;
/**
 * 不能直接放入，得如下放入才编译通过
 */
public class SingleTon1 {
	
	private SingleTon1(){}//保证不被外部实例化
	
	private final static SingleTon1 instance;
	
	static{
		instance = new SingleTon1();
	}

	public static SingleTon1 getInstance(){
		return instance;
	}
}
