package try_finaly;

public class Test3 {

	/**
	 * 2015年12月11日21:06:09 
	 * 首先，不要在final里写return
	 * 第二，final有返回，已final里的为准
	 * 第三，final里无返回，但是对数据有输出处理，则final里的仍然输出，但是对数据的更改对返回值无效
	 */
	public static void main(String[] args) {
			System.out.println(getV());
	}
	private static int getV(){
		String s = "a";
		try{
			Integer.parseInt(s);
		}catch (Exception e) {
			System.out.println(e);
			return 0;
		}finally{
			return 200;
		}
		
	}

}
