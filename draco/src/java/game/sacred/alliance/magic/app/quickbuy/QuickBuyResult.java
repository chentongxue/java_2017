package sacred.alliance.magic.app.quickbuy;

import sacred.alliance.magic.base.Result;

public class QuickBuyResult extends Result{
	
	private QuickBuyResultType quickBuyResultType = QuickBuyResultType.Not_Support;
	
	public void setResult(){
		//快速购买外部不允许使用此方法
	}
	
	/**
	 * 不支持快速购买
	 * @return
	 */
	public QuickBuyResult setNotSupport(){
		this.quickBuyResultType = QuickBuyResultType.Not_Support;
		return this;
	}
	
	public boolean isNotSupport(){
		return this.quickBuyResultType == QuickBuyResultType.Not_Support;
	}
	
	/**
	 * 道具数量足够，可以扣道具
	 * @return
	 */
	public QuickBuyResult setGoodsEnough(){
		this.quickBuyResultType = QuickBuyResultType.Goods_Enough;
		return this;
	}
	
	public boolean isGoodsEnough(){
		return this.quickBuyResultType == QuickBuyResultType.Goods_Enough;
	}
	
	/**
	 * 道具不足，发送了快速购买的确认消息
	 * @return
	 */
	public QuickBuyResult setSendBuyMessage(){
		//!!!!设置为忽略
		this.ignore = true ;
		this.info = null;
		this.quickBuyResultType = QuickBuyResultType.Send_Buy_Message;
		return this;
	}
	
	public boolean isSendBuyMessage(){
		return this.quickBuyResultType == QuickBuyResultType.Send_Buy_Message;
	}
	
	/**
	 * 快速购买时，不符合条件的提示信息
	 * @param info
	 * @return
	 */
	public QuickBuyResult setPayFailure(String info){
		this.quickBuyResultType = QuickBuyResultType.Pay_Failure;
		this.info = info;
		return this;
	}
	
	public boolean isPayFailure(){
		return this.quickBuyResultType == QuickBuyResultType.Pay_Failure;
	}
	
	/**
	 * 快速购买时，满足条件购买成功
	 * @return
	 */
	public QuickBuyResult setPaySuccess(){
		this.result = SUCCESS;
		this.ignore = false;
		this.quickBuyResultType = QuickBuyResultType.Pay_Success;
		return this;
	}
	
	public boolean isPaySuccess(){
		return this.quickBuyResultType == QuickBuyResultType.Pay_Success;
	}
	
}
