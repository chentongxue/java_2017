package java8;


/**
 * �ͱ��ر�����ͬ���ǣ�
 * lambda�ڲ�����ʵ�����ֶ��Լ���̬�����Ǽ��ɶ��ֿ�д��
 * ����Ϊ������������һ�µ�
 */
public class Test_07 {

    static int outerStaticNum;
    int outerNum;
    void testScopes() {
        Converter<Integer, String> stringConverter1 = (from) -> {
            outerNum = 23;
            return String.valueOf(from);
        };
        Converter<Integer, String> stringConverter2 = (from) -> {
            outerStaticNum = 72;
            return String.valueOf(from);
        };
    }
	
	public static void main(String[] args) {}
}
