package a_effective.enum_;
//133
/**
 * (3)�ض��ڳ����ķ���ʵ�����ض��ڳ��������ݽ��������
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
 	
 	//136 ö���е�switch����ʺ��ڸ��ⲿ��ö�����������ض��볣������Ϊ������ϲ����һ��ʵ����������ÿ������ķ�����
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
