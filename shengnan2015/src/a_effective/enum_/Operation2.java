package a_effective.enum_;
//132
/**
 * (2)���õķ�������ö��������һ������ķ����������ض��ڳ����ĵ��������У��þ���ķ�������ÿ�������ĳ��󷽷�
 * �����ķ���ʵ�֣�constant-specific method implementation��
 */
public enum Operation2 {
	PLUS {double apply(double x, double y) {return 0;}},
	MINUS {double apply(double x, double y) {return 0;}},
	TIMES {double apply(double x, double y) {return 0;}},
	DIVIDE {double apply(double x, double y) {return 0;}};
	
 	abstract double apply(double x,double y);
}
