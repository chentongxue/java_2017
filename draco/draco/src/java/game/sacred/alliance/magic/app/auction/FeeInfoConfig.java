package sacred.alliance.magic.app.auction;

import sacred.alliance.magic.base.MoneyType;
import lombok.Data;

public @Data class FeeInfoConfig {

	private int fee8 = 500;
	private int fee24 = 1000 ;
	private int fee48 = 2000 ;
	private int minGameMoney = 1000 ;
	private int minGlodMoney  = 2 ;
	private float deductRate = 5;
	private int upGameMoney = 0;
	private int upGoldMoney = 0;
	
	private float percent = 100f;
	
	/**允许上架金币*/
	public boolean hasUpGameMoney(){
		return this.upGameMoney == 1;
	}
	
	/**允许上架元宝*/
	public boolean hasUpGoldMoney(){
		return this.upGoldMoney == 1;
	}
	
	/**得到拍卖成功后的收税百分比*/
	public float getBusinessFeeRate(){
		return this.deductRate/percent;
	}
	
	/**最少出售的钱数*/
	public int getLessMoney(MoneyType mt){
		if(MoneyType.game == mt){
			return (int) (minGameMoney/percent);
		}
		if(MoneyType.rmb == mt){
			return (int) (minGlodMoney-1);
		}
		return 0;
	}

}
