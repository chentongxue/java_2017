package static_problem;
/**
 * ���캯���;�̬���ĸ��ȵ��ã�
 * ���Ǿ�̬���ȵ��ã�������Ӿ����Ի���,��private static final TestStatic t = new TestStatic();ɾ�����ܵó���ȷ�𰸣�
 * ��Ϊ��һ���Ǿ�̬��������������ص�ʱ��ͬ�������ڹ��캯��������
 * @author admin
 *
 */
public class TestStatic {
//	private static final TestStatic t = new TestStatic();
	static{
		System.out.println("static");
	}
	public TestStatic(){
		System.out.println("���캯��");//1
	}
	public static void hello(){
		System.out.println("hello");//2
	}
	public static void main(String args[]){
		new TestStatic();
		System.out.println("hi");
	}
}