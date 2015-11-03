package a_effective.enum_;
//138
/**
 * 如果一个枚举类型的元素主要用在集合中，一般使用int枚举模式，将每2的不同倍数赋予每个常量：
 * 这种方法让你融OR运算将几个常量合并到一个集合中，称作位域（bit field）
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
