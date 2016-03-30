package sacred.alliance.magic.base;

import lombok.Data;
import sacred.alliance.magic.vo.RoleInstance;

public @Data class Money {

	public Money(){
		
	}
	
	public Money(MoneyType moneyType,int num){
		this.moneyType = moneyType ;
		this.num = num ;
	}
	
	public Money(byte moneyType,int num){
		this.moneyType = MoneyType.get(moneyType) ;
		this.num = num ;
	}

	private MoneyType moneyType ;
	private int num ;
	
	public String getName(){
		return this.moneyType.getAttributeType().getName() + "*" + this.num ;
	}
	
	public boolean isCorrect(){
		return null != this.moneyType && this.num >0 ;
	}
	
	public boolean canExchange (){
		return this.isCorrect() && this.moneyType.isExchange() ;
	}
	
	public boolean isEnough(RoleInstance role){
		if(!this.isCorrect()){
			return false ;
		}
		return num <= role.get(moneyType.getAttributeType());
	}
}
