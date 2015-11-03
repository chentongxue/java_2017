package try_finaly;

public class Test {

	/**
	 * @param args
	 */
	public static int a= 1;
	public static void main(String[] args) {
		try{
			System.err.println(a);
			return;
		}finally{
			a++;
			System.out.println(a);
		}
	}

}
