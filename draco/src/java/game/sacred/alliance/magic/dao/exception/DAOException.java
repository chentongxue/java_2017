package sacred.alliance.magic.dao.exception;

import sacred.alliance.magic.core.exception.NestedCheckedException;


public class DAOException extends NestedCheckedException {

	private static final long serialVersionUID = 1L;

	public DAOException(String msg, Throwable ex) {
		super(msg, ex);

	}

	public DAOException() {
		super("DAO Exception");
	}

	public DAOException(String msg) {
		super(msg);
	}

}
