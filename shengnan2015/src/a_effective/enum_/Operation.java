package a_effective.enum_;
//132
/**(1)ͨ������ö��ֵ��ʵ��
 * ���������Ĵ��������
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
		throw new AssertionError("Unkown op:" + this);//���û����һ�У����ܱ���
	}
}
