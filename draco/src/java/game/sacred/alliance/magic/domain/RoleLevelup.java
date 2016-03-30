package sacred.alliance.magic.domain;
import lombok.Data;
import sacred.alliance.magic.base.AttributeType;

public @Data class RoleLevelup {
	private int level;
	
	private int maxHP;
	private int breakDefense;
	private int atk;
	private int rit;
	private int critAtk;
	private int critRit;
	private int hit;
	private int dodge;
	//private int maxPower ;

	
	private int upExp;
	private int maxExp;
	private int upMoney;
	//private int baseMaxMp ;
	//private int baseMaxHp ;
	private int hurtRemitFactor;
	
	
	/**
	 * 相对于前一级的baseMaxMp,baseMaxHp是否发生了变化
	 */
	//private boolean baseMaxChanged = true ;

	
	public int getAttriValue(AttributeType attriType){
		if(attriType == AttributeType.level){
			return this.level;
		} else if(attriType == AttributeType.maxHP){
			return this.maxHP;
		} else if(attriType == AttributeType.breakDefense){
			return this.breakDefense;
		} else if(attriType == AttributeType.atk){
			return this.atk;
		} else if(attriType == AttributeType.rit){
			return this.rit;
		} else if(attriType == AttributeType.critAtk){
			return this.critAtk;
		} else if(attriType == AttributeType.critRit){
			return this.critRit;
		} else if(attriType == AttributeType.hit){
			return this.hit;
		} else if(attriType == AttributeType.dodge){
			return this.dodge;
		} 
		return 0;
	}
	
		
	
}
