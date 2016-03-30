package sacred.alliance.magic.service.exception;

import sacred.alliance.magic.core.exception.NestedCheckedException;


public class NotOnlineException extends NestedCheckedException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NotOnlineException() {
		super("Not Online Exception!");
	}
	
	public NotOnlineException(Throwable e) {
		super("Not Online Exception!",e);
	}
	
	public NotOnlineException(String message,Throwable e) {
		super("Not Online Exception!",e);
	}
	
	public NotOnlineException(String message) {
		super(message);
	}

}
