package sacred.alliance.magic.app.goods.exception;

import sacred.alliance.magic.core.exception.ServiceException;

public class GoodsCanotUpgradeException extends ServiceException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public GoodsCanotUpgradeException(){
		super("not  Found Goods  Exception !");
		
	}
	public GoodsCanotUpgradeException(Throwable e){
		super("not  Found Goods  Exception !",e);
	}
	
	public GoodsCanotUpgradeException(String message){
		super(message);
		
	}
	public GoodsCanotUpgradeException(String message,Throwable e){
		super(message,e);
	}
}
