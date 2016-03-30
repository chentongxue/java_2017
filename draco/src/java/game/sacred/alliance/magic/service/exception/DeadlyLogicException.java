package sacred.alliance.magic.service.exception;

import sacred.alliance.magic.core.exception.NestedCheckedException;

public class DeadlyLogicException extends NestedCheckedException{
	public DeadlyLogicException(String msg) {
		super(msg);
	}
	public DeadlyLogicException() {
		super("Code Logic Exception !");
	}
	public DeadlyLogicException(String msg,Throwable e) {
		super(msg,e);
	}
	public DeadlyLogicException(Throwable e) {
		super("Code Logic Exception !",e);
	}
	private static final long serialVersionUID = 1L;

}
