package breakcontinue;

public class TestContinueIf {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int a = 1;
		if(true){
			System.out.println("aaaaaaaaaaaa");
			if(a==1){
				System.out.println("bbbbbbbbbb");
//				continue;    //±àÒë²»Í¨¹ý
//				System.out.println("ccccccccccc");
			}
			System.out.println("dddddddddddddddddddd");
		}
		System.out.println("end");
	}

}
