package classloader;

public class Test {

	public static void main(String[] args) {
		System.out.println("sun.boot.class.path------" + System.getProperty("sun.boot.class.path"));   

        System.out.println("java.ext.dirs------" + System.getProperty("java.ext.dirs"));   

        System.out.println("java.class.path------" +System.getProperty("java.class.path"));

        ClassLoader cl = Thread.currentThread().getContextClassLoader();//ClassLoader.getSystemClassLoader()

        System.out.println("getContextClassLoader------" +cl.toString());

        System.out.println("getContextClassLoader.parent------" +cl.getParent().toString());

        System.out.println("getContextClassLoader.parent2------" +cl.getParent().getParent());

	}

}
