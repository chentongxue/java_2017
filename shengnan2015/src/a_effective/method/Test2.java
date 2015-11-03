package a_effective.method;

public class Test2 {
//170
	/**
	 * 慎用可变参数
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
	 * 有时候，需要编写需要一个或多个某种类型参数的方法，如果客户端没有传入参数，那这个方法的定义就不太好了，可以在运行时检查数组长度
	 * 但是如果客户端使用这个方法并没有传进参数，方法会运行时失败而不是编译时失败，而且除非见min初始化为 Integer.MAX_VALUE,否则不能使用for each 循环，这样的方法也不美观，。
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
	 * 这种方法很好的改善了min1的方法
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
