package a_effective.enum_;
//132
/**
 * (2)更好的方法，在枚举中声明一个抽象的方法，并在特定于常量的的类主体中，用具体的方法覆盖每个常量的抽象方法
 * 常量的方法实现（constant-specific method implementation）
 */
public enum Operation2 {
	PLUS {double apply(double x, double y) {return 0;}},
	MINUS {double apply(double x, double y) {return 0;}},
	TIMES {double apply(double x, double y) {return 0;}},
	DIVIDE {double apply(double x, double y) {return 0;}};
	
 	abstract double apply(double x,double y);
}
