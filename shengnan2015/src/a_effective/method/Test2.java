package a_effective.method;

public class Test2 {
//170
	/**
	 * ���ÿɱ����
	 */
	public static void main(String[] args) {
		System.out.println("sum() = "+sum());				//0
		System.out.println("sum(1, 2, 3) = " +sum(1, 2, 3));//6
	}
	//170
	public static int sum(int ... args){
		int sum = 0;
		for (int i : args) {
			sum += i;
		}
		return sum;
	}
	/**
	 * ��ʱ����Ҫ��д��Ҫһ������ĳ�����Ͳ����ķ���������ͻ���û�д������������������Ķ���Ͳ�̫���ˣ�����������ʱ������鳤��
	 * ��������ͻ���ʹ�����������û�д�������������������ʱʧ�ܶ����Ǳ���ʱʧ�ܣ����ҳ��Ǽ�min��ʼ��Ϊ Integer.MAX_VALUE,������ʹ��for each ѭ���������ķ���Ҳ�����ۣ���
	 */
	public static int min(int ... args){
		if(args.length == 0){
			throw new IllegalArgumentException("Too few arguments");
		}
		int min = args[0];
		for (int i = 1; i < args.length; i++) {
			if(args[i] < min){
				min = args[i];
			}
		}
		return min;
	}
	/**
	 * ���ַ����ܺõĸ�����min1�ķ���
	 */
	public static int min2(int firstArg, int ... args){
		int min = firstArg;
		for (int i = 1; i < args.length; i++) {
			if(args[i] < min){
				min = args[i];
			}
		}
		return min;
	}
}
