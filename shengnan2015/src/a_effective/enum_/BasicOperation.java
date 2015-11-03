package a_effective.enum_;

import java.util.Arrays;
import java.util.Collection;

//144
/**
 * �ýӿ�ģ���������ö��
 *
 */
public enum BasicOperation implements Operation4{
	/** ���� ö��ע�� */
	PLUS("+"){
		public double apply(double x,double y){return x + y;}
	},
	MINUS("-"){
		public double apply(double x,double y){return x - y;}
	},
	TIMES("*"){
		public double apply(double x,double y){return x * y;}
	},
	DEVIDE("/"){
		public double apply(double x,double y){return x / y;}
	};
	private final String symbol;
	BasicOperation(String symbol){
		this.symbol = symbol;
	}
	@Override
	public String toString() {
		return symbol;
	}
	public static void main(String[] args){
		 double x = 0.5;
		double y = 0.4;
		test(BasicOperation.class, x, y);
		test2(Arrays.asList(BasicOperation.values()), x, y);
	}
	private static <T extends Enum<T> & Operation4> void test(Class<T> opSet,double x,double y){
		for(Operation4 op: opSet.getEnumConstants()){
			System.err.printf("%f %s %f = %f%n",x, op, y, op.apply(x, y) );
		}
	}
	private static void test2(Collection<? extends Operation4>opSet, double x, double y){
		for(Operation4 op :opSet){
			System.out.printf("%f %s %f = %f%n", x,op,y,op.apply(x, y));
		}
	}
}