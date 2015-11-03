package try_finaly;

public class CopyOfTest {

	/**
	 * @param args
	 */
	public static int a= 1;
	public static void main(String[] args) {
			System.out.println(getV());
	}
	private static int getV(){
		String s = "1";
		try{
			return Integer.parseInt(s);
		}catch (Exception e) {
			return 0;
		}finally{
			return 100;
		}
	}

}
