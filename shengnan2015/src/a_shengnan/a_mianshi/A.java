package a_shengnan.a_mianshi;
/**
 * �ڲ���;�̬�ڲ��������
 *
 * Static Nested Class�Ǳ�����Ϊ��̬��static�����ڲ��࣬�����Բ��������ⲿ��ʵ����ʵ������
 * ��ͨ�����ڲ�����Ҫ���ⲿ��ʵ���������ʵ��������Ҫ���staticӦ�����ڲ���ʱ�ĺ��壬��ͱ����ס��
 *	��ͨ���ڲ�����������ر�����һ�����ã�ָ�򴴽�������Χ�����Ȼ�������ڲ�����static��ʱ���Ͳ��������ˡ�
 */
public class A {
	private int a;
	private static class B{
		int b = 0;
		public void method(){
			int c = 0;
			b = 0;
			a = 0;
		}
	}

}
