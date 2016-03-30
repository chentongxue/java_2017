package sacred.alliance.magic.app.active.siege;

import lombok.Data;

public @Data class SiegeFailAwardRate {
	private int bossHp;//BOSS剩余血量百分比(上限)
	private float bindAddRate;//绑金加成
	private float moneyAddRate;//金钱加成
	private float expAddRate;//经验加成
	
	private int minBossHp;//血量百分比下限
	
	//为起始值赋值
	public void init(int minValue){
		this.minBossHp = minValue;
	}

	/**
	 * 满足百分比
	 * @param percent
	 * @return
	 */
	public boolean isBoosHpPercent(int min , int max){
		int percent =  (int) (((float)min/max)*100);
		if(percent >= minBossHp && percent <= bossHp){
			return true;
		}
		return false;
	}
}
