package a_effective.enum_;
//138
/**
 * ���һ��ö�����͵�Ԫ����Ҫ���ڼ����У�һ��ʹ��intö��ģʽ����ÿ2�Ĳ�ͬ��������ÿ��������
 * ���ַ���������OR���㽫���������ϲ���һ�������У�����λ��bit field��
 * test.applyStyles(STYLE_BOLD | STYLE_ITALIC);
 */
public class Test {
	public static final int STYLE_BOLD			= 1 << 0;
	public static final int STYLE_ITALIC		= 1	<< 1;
	public static final int STYLE_UNDERLINE		= 1	<< 2;
	public static final int STYLE_STRIKETHROUCH	= 1 << 3;
	
	//Parameter is bitwise OR of zero or more STYLE_ constants
	public  void applyStyles(int styles){
		
	}
}
