package sacred.alliance.magic.app.ai.config;

import lombok.Data;
import sacred.alliance.magic.constant.ParasConstant;

public @Data class AutoMaxHp {
	public static final int MIN_NUM = 0 ;
	public static final int MAX_NUM = 100 ;
	
	private String npcId ;
	private int minAttackerNum ;
	private int maxAttackerNum ;
	private float maxHpRate ;
	
	public void init(){
		this.maxHpRate = (this.maxHpRate / ParasConstant.PERCENT_BASE_VALUE) ;
	}
}
