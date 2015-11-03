package swich;

public class Test2 {
	public static void main(String args[]) {
		for (int a = 1; a < 10; a++) {
			hello(a);
		}
	}
	public static void hello(int a){
		switch (a) {
		case 1:
		case 2:
		case 3:
			System.out.println("ccc :" + a);
			return;
		case 4:
		case 5:
		case 6:
		case 7:
			System.out.println("a :" + a);
			return;
		}
	}
}
