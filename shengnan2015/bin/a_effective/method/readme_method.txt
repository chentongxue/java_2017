1 �������� ��165-169
CollectionClassifier.java
CollectionClassifier2.java
SetList.java

2 ���ÿɱ���� 170
1.5�����˿ɱ�������ɱ������Ϊprintf����Ƶġ������printf����ɱ�������������
Test2.java

˼������������������������
	public static <T> void  print(T ... args){
		for (T t : args) {
			System.out.println(t);
		}
	}
	public static void  print2(Object ... args){
		for (Object t : args) {
			System.out.println(t);
		}
	}


--------------
throw new ArithmeticException("");//����
 throw new NullPointerException();//��ָ��