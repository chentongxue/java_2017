package ac;


public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Object o = new Object();
		/**
		 ActiveBaseItem[ id=101 name=Tom status=ok time=2011/03/02 level=11 ]
		 */
		StringBuilder buffer = new StringBuilder("ActiveBaseItem");
		buffer.append("[ ");
		buffer.append("id=");
		buffer.append(101);
		buffer.append(" ");
		buffer.append("name=");
		buffer.append("Tom");
		buffer.append(" ");
		buffer.append("status=");
		buffer.append("ok");
		buffer.append(" ");
		buffer.append("time=");
		buffer.append("2011/03/02");
		buffer.append(" ");
		buffer.append("level=");
		buffer.append(11);
		buffer.append(" ");
		buffer.append("]");
		System.out.println(buffer.toString());
		//---------------------------------------
		Class clazz = o.getClass();
		System.out.println(clazz.getName());
		
		
	}//main

}
