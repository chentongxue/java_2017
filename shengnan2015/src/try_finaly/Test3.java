package try_finaly;

public class Test3 {

	/**
	 * 2015��12��11��21:06:09 
	 * ���ȣ���Ҫ��final��дreturn
	 * �ڶ���final�з��أ���final���Ϊ׼
	 * ������final���޷��أ����Ƕ����������������final�����Ȼ��������Ƕ����ݵĸ��ĶԷ���ֵ��Ч
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
