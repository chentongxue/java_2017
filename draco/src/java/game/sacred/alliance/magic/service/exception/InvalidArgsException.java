package sacred.alliance.magic.service.exception;

import sacred.alliance.magic.core.exception.NestedCheckedException;

public class InvalidArgsException extends NestedCheckedException{

	private static final long serialVersionUID = 1L;

	public InvalidArgsException() {
		super("Invalid Args Exception!");
	}
	
	public InvalidArgsException(Throwable e) {
		super("Invalid Args Exception!",e);
	}
	
	public InvalidArgsException(String message,Throwable e) {
		super("Invalid Args Exception!",e);
	}
	
	public InvalidArgsException(String message) {
		super(message);
	}


}
