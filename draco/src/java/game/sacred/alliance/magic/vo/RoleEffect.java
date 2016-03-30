package sacred.alliance.magic.vo;

import lombok.Data;
import sacred.alliance.magic.base.AttributeType;



public @Data class RoleEffect{
	
	public int get(AttributeType attriType){
		switch(attriType){
			//附加
			case expAddRate:return this.getExpAddRate();
			case expMultRate:return this.getExpMultRate();
			case moneyAddRate:return this.getMoneyAddRate();
			case hatredRate:return this.getHatredRate();
			case healRate:return this.getHealRate();
			case mpConsumeRate:return this.getMpConsumeRate();
			case cdRate:return this.getCdRate();
			case normalAtkCdRate : return this.getSkillCdRate();
			
			//战斗属性
			case petAtk:return this.getPetAtk();
			case atk:return this.getAtk();
			case rit:return this.getRit();
			case sacredAtk:return this.getSacredAtk();
			case sacredRit :return this.getSacredRit(); 
			case hit:return this.getHit();
			case dodge:return this.getDodge();
			case critAtk:return this.getCritAtk();
			case critRit : return this.getCritRit();
			case critAtkProb: return this.getCritAtkProb();
			case hurtRemitRate :return this.getHurtRemitRate(); 
			
			//概率属性
			case slowRitRate : return this.getSlowRitRate(); 
			case sunderRitRate :return this.getSunderRitRate(); 
			case weakRitRate : return this.getWeakRitRate(); 
			case fixedRitRate :return this.getFixedRitRate(); 
			case blowFlyRitRate :return this.getBlowFlyRitRate(); 
			case repelRitRate :return this.getRepelRitRate(); 
			case lullRitRate :return this.getLullRitRate(); 
			case tiredRitRate :return this.getTiredRitRate(); 
			case mumRitRate :return this.getSilenceRitRate(); 
			case bloodRitRate :return this.getBloodRitRate(); 
			case charmRitRate :return this.getCharmRitRate(); 
			case comaRitRate :return this.getComaRitRate(); 
			case mussRitRate :return this.getMussRitRate(); 
			case poisionRitRate :return this.getPoisionRitRate(); 
			case lightRitRate :return this.getLightRitRate(); 
			case paralysisRitRate:return this.getParalysisRitRate();
			
			case slowAddRate : return this.getSlowAddRate(); 
			case sunderAddRate :return this.getSunderAddRate(); 
			case weakAddRate : return this.getWeakAddRate(); 
			case fixedAddRate :return this.getFixedAddRate(); 
			case blowFlyAddRate :return this.getBlowFlyAddRate(); 
			case repelAddRate :return this.getRepelAddRate(); 
			case lullAddRate :return this.getLullAddRate(); 
			case tiredAddRate :return this.getTiredAddRate(); 
			case mumAddRate :return this.getSilenceAddRate(); 
			case bloodAddRate :return this.getBloodAddRate(); 
			case charmAddRate :return this.getCharmAddRate(); 
			case comaAddRate :return this.getComaAddRate(); 
			case mussAddRate :return this.getMussAddRate(); 
			case poisionAddRate :return this.getPoisionAddRate(); 
			case lightAddRate : return this.getLightAddRate();
			case dkp : return this.getDkp();
			case paralysisAddRate:return this.getParalysisAddRate();
			case breakDefense : return this.getBreakDefense() ;
			
			default:  throw new java.lang.RuntimeException("attriType = "+attriType.getType());
		}
	}
	
	public boolean set(byte enumValue,int value){
		AttributeType attriType = AttributeType.get(enumValue);
		switch(attriType){
			//衍生
			//二级
			
			case expAddRate:this.setExpAddRate(value);return true;
			case expMultRate:this.setExpMultRate(value);return true;
			case moneyAddRate:this.setMoneyAddRate(value);return true;
			case hatredRate:this.setHatredRate(value);return true ;
			case healRate:this.setHealRate(value);return true ;
			case mpConsumeRate:this.setMpConsumeRate(value);return true ;
			case cdRate:this.setCdRate(value);return true ;
			case normalAtkCdRate : this.setSkillCdRate(value); return true;
			
			
			//战斗属性
			case petAtk:this.setPetAtk(value);return true;
			case atk:this.setAtk(value);return true;
			case rit:this.setRit(value);return true;
			case sacredAtk : this.setSacredAtk(value);return true ;
			case sacredRit : this.setSacredRit(value); return true;
			case hit:this.setHit(value);return true;
			case dodge:this.setDodge(value);return true;
			case critAtk:this.setCritAtk(value);return true;
			case critRit:this.setCritRit(value);return true ;
			case critAtkProb:this.setCritAtkProb(value);return true;
			case hurtRemitRate : this.setHurtRemitRate(value); return true;
			
			//概率属性
			case slowRitRate :  this.setSlowRitRate(value); return true;
			case sunderRitRate : this.setSunderRitRate(value); return true;
			case weakRitRate :  this.setWeakRitRate(value); return true;
			case fixedRitRate : this.setFixedRitRate(value); return true;
			case blowFlyRitRate : this.setBlowFlyRitRate(value); return true;
			case repelRitRate : this.setRepelRitRate(value); return true;
			case lullRitRate : this.setLullRitRate(value); return true;
			case tiredRitRate : this.setTiredRitRate(value); return true;
			case mumRitRate : this.setSilenceRitRate(value); return true;
			case bloodRitRate : this.setBloodRitRate(value); return true;
			case charmRitRate : this.setCharmRitRate(value); return true;
			case comaRitRate : this.setComaRitRate(value); return true;
			case mussRitRate : this.setMussRitRate(value); return true;
			case poisionRitRate : this.setPoisionRitRate(value); return true;
			case lightRitRate : this.setLightRitRate(value); return true;
			case paralysisRitRate:this.setParalysisRitRate(value);return true;
			
			case slowAddRate :  this.setSlowAddRate(value); return true;
			case sunderAddRate : this.setSunderAddRate(value); return true;
			case weakAddRate :  this.setWeakAddRate(value); return true;
			case fixedAddRate : this.setFixedAddRate(value); return true;
			case blowFlyAddRate : this.setBlowFlyAddRate(value); return true;
			case repelAddRate : this.setRepelAddRate(value); return true;
			case lullAddRate : this.setLullAddRate(value); return true;
			case tiredAddRate : this.setTiredAddRate(value); return true;
			case mumAddRate : this.setSilenceAddRate(value); return true;
			case bloodAddRate : this.setBloodAddRate(value); return true;
			case charmAddRate : this.setCharmAddRate(value); return true;
			case comaAddRate : this.setComaAddRate(value); return true;
			case mussAddRate : this.setMussAddRate(value); return true;
			case poisionAddRate : this.setPoisionAddRate(value); return true;
			case lightAddRate : this.setLightAddRate(value); return true;
			case heroCoin : this.setLightAddRate(value); return true;
			case dkp : this.setDkp(value); return true;
			case paralysisAddRate : this.setParalysisAddRate(value); return true;
			case breakDefense : this.setBreakDefense(value); return true ;
			default: throw new java.lang.RuntimeException("enumValue = "+enumValue+"     value = "+value);
		}
	}
	
	private int expAddRate = 0;
	private int expMultRate = 0;
	private int onlineExpRate = 0;
	private int moneyAddRate;
	
	private int hatredRate = 10000; 
	private int healRate = 10000;
	private int mpConsumeRate = 10000;
	private int danderValue;
	private int cdRate = 10000; //普攻cd时间
	private int skillCdRate = 10000; //技能cd时间
	
	//战斗属性
	private int petAtk = 0;
	private int atk;
	private int rit;
	private int sacredAtk ;
	private int sacredRit;
	private int hit;
	private int dodge;
	private int critAtk;
	private int critRit;
	private int critAtkProb;
	private int hurtRemitRate;
	
	//概率属性
	private int slowRitRate; //抵抗减速概率
	private int sunderRitRate; //抵抗破甲概率
	private int weakRitRate; //抵抗虚弱概率
	private int fixedRitRate; //抵抗定身概率
	private int blowFlyRitRate; //抵抗击飞概率
	private int repelRitRate; //抵抗击退概率
	private int lullRitRate; //抵抗麻痹概率
	private int tiredRitRate; //抵抗疲劳概率
	private int silenceRitRate; //抵抗沉默概率
	private int bloodRitRate; //抵抗流血概率
	private int charmRitRate; //抵抗魅惑概率
	private int comaRitRate; //抵抗昏迷概率
	private int mussRitRate; //抵抗混乱概率
	private int poisionRitRate; //抵抗中毒概率
	private int lightRitRate; //抵抗点燃概率
	private int paralysisRitRate;//抵抗瘫痪概率

	private int slowAddRate; //加成减速概率
	private int sunderAddRate; //加成破甲概率
	private int weakAddRate; //加成虚弱概率
	private int fixedAddRate; //加成定身概率
	private int blowFlyAddRate; //加成击飞概率
	private int repelAddRate; //加成击退概率
	private int lullAddRate; //加成麻痹概率
	private int tiredAddRate; //加成疲劳概率
	private int silenceAddRate; //加成沉默概率
	private int bloodAddRate; //加成流血概率
	private int charmAddRate; //加成魅惑概率
	private int comaAddRate; //加成昏迷概率
	private int mussAddRate; //加成混乱概率
	private int poisionAddRate; //加成中毒概率
	private int lightAddRate; //加成点燃概率
	private int paralysisAddRate;//加成瘫痪概率
	
	private int breakDefense ; //破防
	
	private int dkp;
}
