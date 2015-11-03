package test;
/**
 * 需要日后查看
 * @author Administrator
 *
 */
public class TestNewInstance {
	private int id;
	private String nm;
	
//	public TestNewInstance(){
//		
//	}
	public TestNewInstance(int id, String nm) {
		super();
		this.id = id;
		this.nm = nm;
	}

	public static void main(String[] args) {
		try {
			TestNewInstance.class.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
}
