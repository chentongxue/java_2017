package a_shengnan.a_mianshi;
/**饥汉单例,第六行放在static（静态代码块）中如何？ */
public class SingleTon {
	
	private SingleTon(){}//保证不被外部实例化
	
	private final static SingleTon instance = new SingleTon();
	
	public static SingleTon getInstance(){
		return instance;
	}
}
