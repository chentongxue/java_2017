1 慎用重载 ，165-169
CollectionClassifier.java
CollectionClassifier2.java
SetList.java

2 慎用可变参数 170
1.5加入了可变参数，可变参数是为printf而设计的。反射和printf都因可变参数而大大受益
Test2.java

思考：下面两个方法有区别吗
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
throw new ArithmeticException("");//算数
 throw new NullPointerException();//空指针