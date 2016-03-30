package sacred.alliance.magic.vo;

import lombok.Data;

public @Data class RoleNotifyAttribute {

	private int hpCur;
	private int mpCur;
	private int hpMax;
	private int mpMax;
	private int lv;
	private int exp;

	private int speed;
	private int state;
	/**金币*/
	private int goldMoney;
	/**银条*/
	private int bindMoney;
	/**铜币*/
	private int silverMoney;
	/**升级需要经验*/
	private int maxExp;
	/**阵营声望*/
	private int campPrestige ;
	/**真气*/
	private int potential ;
	/**门派贡献度*/
	//private int contribute;
	/**
	 * 战斗力
	 */
	private int battleScore ;
	
	/**
	 * 今日点券数
	 */
	private int todayCoupon;
	
	/**
	 * 总点券数
	 */
	private int coupon;

	/**
	 * expChange字段特殊,每次同步后需要清除
	 * 没有放在角色上，是担心int溢出
	 */
	private int expChange ;
	//灵气
	private int lq ;
	
	//dkp
	private int dkp;
	
	private int honor;
	private int expHook ;
	private int maxExpHook ;
	
	private int braveSoul;
	private int wildBlood;
	private int talent;
	private int arena3v3Score ;
	/**试炼点*/
	private int heroCoin ;
	
	/**威望*/
	private int prestigePoints ;
	
}
