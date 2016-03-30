package sacred.alliance.magic.service.exception;
import sacred.alliance.magic.core.exception.NestedCheckedException;
public class NegativeValueException extends NestedCheckedException{

	private static final long serialVersionUID = 1L;

	public NegativeValueException() {
		super("Negative Value Exception!");
	}
	
	public NegativeValueException(Throwable e) {
		super("Negative Value Exception!",e);
	}
	
	public NegativeValueException(String message,Throwable e) {
		super("Negative Value Exception!",e);
	}
	
	public NegativeValueException(String message) {
		super(message);
	}

}
