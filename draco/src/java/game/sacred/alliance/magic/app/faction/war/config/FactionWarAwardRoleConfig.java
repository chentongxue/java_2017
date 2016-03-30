package sacred.alliance.magic.app.faction.war.config;

import lombok.Data;
import sacred.alliance.magic.util.KeySupport;

public @Data class FactionWarAwardRoleConfig implements KeySupport<Integer> {
	private int rounds;
	
	private int winMoneyModulus;
	private int winMoneyBase;
	private int winMoneyMax;
	private int winContributeModulus;
	private int winContributeBase;
	private int winContributeMax;
	private int winGoodsId;
	private int winGoodsNum;
	private byte winGoodsBind;
	
	private int loseMoneyModulus;
	private int loseMoneyBase;
	private int loseMoneyMax;
	private int loseContributeModulus;
	private int loseContributeBase;
	private int loseContributeMax;
	private int loseGoodsId;
	private int loseGoodsNum;
	private byte loseGoodsBind;
	
	private String mailTitle;
	private String winMailContent;
	private String loseMailContent;
	
	@Override
	public Integer getKey() {
		return this.rounds;
	}
	
	public int getWinMoney(int killNum){
		int money = killNum * winMoneyModulus + winMoneyBase;
		if(money > winMoneyMax) {
			money = winMoneyMax;
		}
		return money;
	}
	
	public int getWinContribution(int killNum){
		int contribution = killNum * winContributeModulus + winContributeBase;
		if(contribution > winContributeMax) {
			contribution = winContributeMax;
		}
		return contribution;
	}
	
	public int getLoseMoney(int killNum){
		int money = killNum * loseMoneyModulus + loseMoneyBase;
		if(money > loseMoneyMax) {
			money = loseMoneyMax;
		}
		return money;
	}
	
	public int getLoseContribution(int killNum){
		int contribution = killNum * loseContributeModulus + loseContributeBase;
		if(contribution > loseContributeMax) {
			contribution = loseContributeMax;
		}
		return contribution;
	}
}
