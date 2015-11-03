package exception;

public class MyException extends Exception{

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			throw new MyException();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public MyException(){
		super("format dose not match");
	}
	public MyException(String message){
		super(message);
	}

}
