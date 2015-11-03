package a_effective.enum_;
//132
/**(1)通过启用枚举值来实现
 * 计算器的四大基本操作
 */
public enum Operation {
	PLUS, MINUS, TIMES, DIVIDE;
	//Do the artithmetic op represented by this constant
	double apply(double x, double y){
		switch (this) {
		case PLUS:	return x + y;
		case MINUS: return x - y;
		case TIMES: return x * y;
		case DIVIDE: return x /y;
		}
		throw new AssertionError("Unkown op:" + this);//如果没有这一行，不能编译
	}
}
