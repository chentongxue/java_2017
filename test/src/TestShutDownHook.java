public class TestShutDownHook {

	public static void main(String[] args) {
		System.out.println("hello0");
		// ϵͳ�ر�ʱ��������
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				System.out.println("ϵͳ�ر�ʱ��������");
			}
		});
		
		System.out.println("hello");
	}

}
