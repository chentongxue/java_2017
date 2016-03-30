package sacred.alliance.magic.domain;

import java.util.Date;

import lombok.Data;

public @Data class RolePayRecord {
	public static final String ROLE_ID = "roleId" ;
	private String roleId;//角色ID
	private int currMoney;//当前金条数
	private int totalMoney;//累计金条数
	private int consumeMoney;//累计消耗金条数
	private int payGold;//真实充值的金条（充值获得的金条数）
	private Date lastUpTime;//更新日期
	
	//以下字段不入库
	private boolean insert=false;//insert到数据库
		
	/**是付费玩家*/
	public boolean isPayUser(){
		return this.payGold > 0 ;
	}
	
	
	/**玩家金条的变化(充值得到的金条数,充值的钱数)*/
	public void addCurrMoney(int addValue,int payGold){
		this.currMoney += addValue;
		this.totalMoney += addValue;
		this.payGold += payGold;
		this.lastUpTime = new Date();
	}
	public void addConsumeMoney(int addValue){
		this.consumeMoney += addValue;
		this.lastUpTime = new Date();
	}
	public void addPayGold(int addValue){
		this.payGold += addValue;
		this.lastUpTime = new Date();
	}
}
