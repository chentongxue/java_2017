public class TestShutDownHook {

	public static void main(String[] args) {
		System.out.println("hello0");
		// 系统关闭时进行清理
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				System.out.println("系统关闭时进行清理");
			}
		});
		
		System.out.println("hello");
	}

}
