package sacred.alliance.magic.app.goods.exception;

import sacred.alliance.magic.core.exception.ServiceException;

public class OutOfGoodsBagException extends ServiceException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public OutOfGoodsBagException(){
		super("Out Of Goods Bag Error !");
		
	}
	public OutOfGoodsBagException(Throwable e){
		super("Out Of Goods Bag Error !",e);
	}
	
	public OutOfGoodsBagException(String message){
		super(message);
		
	}
	public OutOfGoodsBagException(String message,Throwable e){
		super(message,e);
		
	}
}
