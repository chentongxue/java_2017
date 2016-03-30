package sacred.alliance.magic.base;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.util.ProbabilityMachine;
import sacred.alliance.magic.util.Util;

import com.game.draco.GameContext;

public enum AttributeType {
	
	//0.原始属性0-30;
	curHP((byte)0,TextId.Attri_curHP,AttriLevelType.orig),
	curMP((byte)1,TextId.Attri_curMP,AttriLevelType.orig),
	exp((byte)2,TextId.Attri_exp,AttriLevelType.orig),
	bindingGoldMoney((byte)3,TextId.Attri_bindingGoldMoney,AttriLevelType.orig),
	goldMoney((byte)4,TextId.Attri_goldMoney,AttriLevelType.orig),
	silverMoney((byte)5,TextId.Attri_silverMoney,AttriLevelType.orig),
	speed((byte)6,TextId.Attri_speed,AttriLevelType.added),
	state((byte)7,TextId.Attri_state,AttriLevelType.orig),
	
	maxHP((byte)8,TextId.Attri_maxHP,AttriLevelType.added),
	maxMP((byte)9,TextId.Attri_maxMP,AttriLevelType.added),
	
	level((byte)10,TextId.Attri_level,AttriLevelType.orig),
	maxExp((byte)11,TextId.Attri_maxExp,AttriLevelType.added),
	//标识
	expChange((byte)12,TextId.Attri_expChange,AttriLevelType.flag),
	
	honor((byte)13,TextId.Attri_honor,AttriLevelType.orig),
	potential((byte)14,TextId.Attri_potential,AttriLevelType.orig),
	residueGoldMoney((byte)15,TextId.Attri_residueGoldMoney,AttriLevelType.orig),
	residueBindingMoney((byte)16,TextId.Attri_residueBindingMoney,AttriLevelType.orig),
	battleScore((byte)17,TextId.Attri_battleScore,AttriLevelType.flag),
	lq((byte)19,TextId.Attri_lq,AttriLevelType.orig),
	
	
	//1.高级属性
	//career((byte)20,TextId.Attri_career,AttriLevelType.orig),
	sex((byte)21,TextId.Attri_sex,AttriLevelType.orig),
	camp((byte)22,TextId.Attri_camp,AttriLevelType.orig),
	curPower((byte)23,TextId.Attri_curPower,AttriLevelType.orig),
	maxPower((byte)24,TextId.Attri_maxPower,AttriLevelType.added),
	coupon((byte)25,TextId.Attri_coupon,AttriLevelType.orig),
	arean1v1_score((byte)26,TextId.Attri_arean1v1_score,AttriLevelType.orig),
	goddessExpChange((byte)27,TextId.Attri_goddessExpChange,AttriLevelType.flag),
	todayCoupon((byte)28,TextId.Attri_todaycoupon,AttriLevelType.orig),
	campPrestige((byte)29,TextId.Attri_campPrestige,AttriLevelType.orig),
	
	//战斗属性
	atk((byte)50,TextId.Attri_atk,AttriLevelType.added),
	rit((byte)51,TextId.Attri_rit,AttriLevelType.added),
	phyAtk((byte)52,TextId.Attri_phyAtk,AttriLevelType.added),
	iceAtk((byte)53,TextId.Attri_iceAtk,AttriLevelType.added),
	fireAtk((byte)54,TextId.Attri_fireAtk,AttriLevelType.added),
	sacredAtk((byte)55,TextId.Attri_sacredAtk,AttriLevelType.added),
	phyRit((byte)56,TextId.Attri_phyRit,AttriLevelType.added),
	iceRit((byte)57,TextId.Attri_iceRit,AttriLevelType.added),
	fireRit((byte)58,TextId.Attri_fireRit,AttriLevelType.added),
	sacredRit((byte)59,TextId.Attri_sacredRit,AttriLevelType.added),
	hit((byte)60,TextId.Attri_hit,AttriLevelType.added),
	dodge((byte)61,TextId.Attri_dodge,AttriLevelType.added),
	critAtk((byte)62,TextId.Attri_critAtk,AttriLevelType.added),
	critRit((byte)63,TextId.Attri_critRit,AttriLevelType.added),
	critAtkProb((byte)64,TextId.Attri_critAtkProb,AttriLevelType.added,true),
	hurtRemitRate((byte)65,TextId.Attri_hurtRemitRate,AttriLevelType.added,true),
	
	//概率属性
	//抵抗
	slowRitRate((byte)66,TextId.Attri_slowRitRate,AttriLevelType.added),
	sunderRitRate((byte)67,TextId.Attri_sunderRitRate,AttriLevelType.added),
	weakRitRate((byte)68,TextId.Attri_weakRitRate,AttriLevelType.added),
	fixedRitRate((byte)69,TextId.Attri_fixedRitRate,AttriLevelType.added),
	blowFlyRitRate((byte)70,TextId.Attri_blowFlyRitRate,AttriLevelType.added),
	repelRitRate((byte)71,TextId.Attri_repelRitRate,AttriLevelType.added),
	lullRitRate((byte)72,TextId.Attri_lullRitRate,AttriLevelType.added),
	tiredRitRate((byte)73,TextId.Attri_tiredRitRate,AttriLevelType.added),
	mumRitRate((byte)74,TextId.Attri_mumRitRate,AttriLevelType.added),
	bloodRitRate((byte)75,TextId.Attri_bloodRitRate,AttriLevelType.added),
	charmRitRate((byte)76,TextId.Attri_charmRitRate,AttriLevelType.added),
	comaRitRate((byte)77,TextId.Attri_comaRitRate,AttriLevelType.added),
	mussRitRate((byte)78,TextId.Attri_mussRitRate,AttriLevelType.added),
	agedRitRate((byte)79,TextId.Attri_agedRitRate,AttriLevelType.added),
	
	//加成
	slowAddRate((byte)80,TextId.Attri_slowAddRate,AttriLevelType.added),
	sunderAddRate((byte)81,TextId.Attri_sunderAddRate,AttriLevelType.added),
	weakAddRate((byte)82,TextId.Attri_weakAddRate,AttriLevelType.added),
	fixedAddRate((byte)83,TextId.Attri_fixedAddRate,AttriLevelType.added),
	blowFlyAddRate((byte)84,TextId.Attri_blowFlyAddRate,AttriLevelType.added),
	repelAddRate((byte)85,TextId.Attri_repelAddRate,AttriLevelType.added),
	lullAddRate((byte)86,TextId.Attri_lullAddRate,AttriLevelType.added),
	tiredAddRate((byte)87,TextId.Attri_tiredAddRate,AttriLevelType.added),
	mumAddRate((byte)88,TextId.Attri_mumAddRate,AttriLevelType.added),
	bloodAddRate((byte)89,TextId.Attri_bloodAddRate,AttriLevelType.added),
	charmAddRate((byte)90,TextId.Attri_charmAddRate,AttriLevelType.added),
	comaAddRate((byte)91,TextId.Attri_comaAddRate,AttriLevelType.added),
	mussAddRate((byte)92,TextId.Attri_mussAddRate,AttriLevelType.added),
	agedAddRate((byte)93,TextId.Attri_agedAddRate,AttriLevelType.added),
	lightAddRate((byte)94,TextId.Attri_lightAddRate,AttriLevelType.added),
	lightRitRate((byte)95,TextId.Attri_lightRitRate,AttriLevelType.added),
	
	heroCoin((byte)96,TextId.Attri_HeroCoin,AttriLevelType.orig),
	expAddRate((byte)97,TextId.Attri_expAddRate,AttriLevelType.added),
	expMultRate((byte)98,TextId.Attri_expMultRate,AttriLevelType.added),
	//5.附加属性 96-120
	moneyAddRate((byte)99,TextId.Attri_moneyAddRate,AttriLevelType.added),
	
	hatredRate((byte)117,TextId.Attri_hatredRate,AttriLevelType.added),
	healRate((byte)118,TextId.Attri_healRate,AttriLevelType.added),
	mpConsumeRate((byte)119,TextId.Attri_mpConsumeRate,AttriLevelType.added),
	
    cdRate((byte)121,TextId.Attri_cdRate,AttriLevelType.added),
    normalAtkCdRate((byte)122,TextId.Attri_normalAtkCdRate,AttriLevelType.added),
    
  	//DKP
    dkp((byte)123,TextId.Attri_dkp,AttriLevelType.orig),
    
	//==================================
	//标识属性(不属于人物属性)
	//==================================
	skillATKAdded((byte)-3,TextId.Attri_skillATKAdded,AttriLevelType.flag),
	factionIntegral((byte)-15,TextId.Attri_factionIntegral,AttriLevelType.flag),
	factionMoney((byte)-16,TextId.Attri_factionMoney,AttriLevelType.flag),
	;
	
	private final static Map<Byte,AttributeType> allMap = new HashMap<Byte,AttributeType>();
	private final static Map<Byte,AttributeType> maxAbleMap = new HashMap<Byte,AttributeType>();
	//需要重算列表
	private final static List<AttributeType> addedList = new ArrayList<AttributeType>();
	
	static {
		for (AttributeType attriType : AttributeType.values()) {
			byte value = attriType.getType();
			allMap.put(value, attriType);
			if(attriType.getAttriLevelType() == AttriLevelType.added){
				addedList.add(attriType);
			}
		}
		maxAbleMap.put(AttributeType.maxHP.getType(), AttributeType.curHP);
		maxAbleMap.put(AttributeType.maxMP.getType(), AttributeType.curMP);
	}
	
	public static List<AttributeType> getAddedList(){
		return addedList ;
	}
	
	public static Map<Byte,AttributeType> getMaxAbleAttributes(){
		return maxAbleMap ;
	}
	
	/**属于付费类型(金条,绑金)*/
	public static boolean isPayType(int payType){
		if(payType == goldMoney.getType() || payType == bindingGoldMoney.getType()){
			return true;
		}
		return false;
	}
	
	
	/**判断是否是buff begin修改属性时需要存储,以便于buff去掉时需要恢复的属性*/
	public static boolean isBuffResumeAttribute(AttributeType attriType){
		if(null ==attriType){
			return false ;
		}
		if(AttriLevelType.orig == attriType.getAttriLevelType()
				|| AttriLevelType.flag ==attriType.getAttriLevelType()){
			return false ;
		}
		return true ;
	}
	
	public static boolean isBuffResumeAttribute(byte attriType){
		return isBuffResumeAttribute(get(attriType));
	}
	
	//是否允许为负
	public static boolean isNegative(AttributeType attriType) {
		return false;
	}
	
	
	/**特殊的orig*/
	public static boolean isSpecialOrig(AttributeType attriType){
		if(curHP == attriType
				|| curMP == attriType
				|| curPower == attriType){
			return true ;
		}
		return false ;
	}
	/**
	 * 是否为钱
	 * @param attriType
	 * @return
	 */
	public static boolean isMoney(AttributeType attriType){
		if(bindingGoldMoney == attriType
				|| goldMoney == attriType
				|| silverMoney == attriType){
			return true ;
		}
		return false ;
	}
	
	
	public static AttributeType get(byte type){
		return allMap.get(type);
	}
	
	
	public boolean isEffectBattleScore(){
		return this == AttributeType.maxHP 
		 || this == AttributeType.maxMP
		 || this == AttributeType.atk
		 || this == AttributeType.rit
		 || this == AttributeType.phyAtk
		 || this == AttributeType.iceAtk
		 || this == AttributeType.fireAtk
		 || this == AttributeType.sacredAtk
		 || this == AttributeType.phyRit
		 || this == AttributeType.iceRit
		 || this == AttributeType.fireRit
		 || this == AttributeType.hit
		 || this == AttributeType.dodge
		 || this == AttributeType.critAtk
		 || this == AttributeType.critRit;
	}
	
	
	private final byte type;
	private final String name;
	private final AttriLevelType attriLevelType;
	private final boolean percentage ;
	
	
	AttributeType(byte type,String name,AttriLevelType attriLevelType,boolean percentage){
		this.type = type;
		this.name = name;
		this.attriLevelType = attriLevelType;
		this.percentage = percentage ;
	}	
	
	AttributeType(byte type,String name,AttriLevelType attriLevelType){
		this(type,name,attriLevelType,false);
	}	
	
	public final byte  getType(){
		return type;
	}
	
	public String getName(){
		return GameContext.getI18n().getText(name);
	}
	
	public AttriLevelType getAttriLevelType() {
		return attriLevelType;
	}
	
	public boolean isFlagAttrType(){
		return AttriLevelType.flag == this.attriLevelType;
	}

	public boolean isMoney(){
		return this == AttributeType.goldMoney
		|| this == AttributeType.bindingGoldMoney
		|| this == AttributeType.silverMoney ;
	}

	public boolean isPercentage() {
		return percentage;
	}
	
	public static String formatValue(byte attriType,float value){
		AttributeType at = get(attriType);
		if(null == at || !at.isPercentage()){
			return String.valueOf((int)value);
		}
		double precValue = (double)(value /ProbabilityMachine.RATE_PERCENT_MODULUS);
		if(precValue <= 0){
			precValue = 0.00 ;
		}
		return Util.doubleFormat(precValue)+ "%" ;
		
	}
 }

