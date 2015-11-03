package a_effective.enum_;
//133
/**
 * (3)特定于常量的方法实现与特定于常量的数据结合起来。
 */
public enum Operation3 {
	PLUS ("+") {double apply(double x, double y) {return 0;}},
	MINUS ("-") {double apply(double x, double y) {return 0;}},
	TIMES ("*") {double apply(double x, double y) {return 0;}},
	DIVIDE ("/"){double apply(double x, double y) {return 0;}};
	
	private final String symbol;
	Operation3(String symbol) {
		this.symbol = symbol;
	}
	@Override public String toString(){return symbol;}
	
 	abstract double apply(double x,double y);
 	
 	//136 枚举中的switch语句适合于给外部的枚举类型增加特定与常量的行为。例如喜欢有一个实例方法返回每个运算的反运算
 	//switch on an enum to simulate a missing method
 	public static Operation3 inverse(Operation op){
 		switch (op) {
		case PLUS: return Operation3.MINUS;
		case MINUS: return Operation3.PLUS;
		case TIMES: return Operation3.DIVIDE;
		case DIVIDE: return Operation3.TIMES;
		default:
			throw new AssertionError("Unkonwn op: " + op);
		}
 	}
}
