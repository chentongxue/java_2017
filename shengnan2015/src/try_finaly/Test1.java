package try_finaly;

public class Test1 {

	public static void main(String[] args) {
		int a = test();
		System.out.println(a);
	}
	public static int test(){
		int x = 1;
		try{
			System.out.println("return x; = "+x);
			return x;
		}finally{
//			return ++x;
			++x;
			System.out.println("final");
		}
	}

}
