package sacred.alliance.magic.app.goods.exception;

import sacred.alliance.magic.core.exception.ServiceException;

public class GoodsNotFoundException extends ServiceException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public GoodsNotFoundException(){
		super("not  Found Goods  Exception !");
		
	}
	public GoodsNotFoundException(Throwable e){
		super("not  Found Goods  Exception !",e);
	}
	
	public GoodsNotFoundException(String message){
		super(message);
		
	}
	public GoodsNotFoundException(String message,Throwable e){
		super(message,e);
		
	}
}
