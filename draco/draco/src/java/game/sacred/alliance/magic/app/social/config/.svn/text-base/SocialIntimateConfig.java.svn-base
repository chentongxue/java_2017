package sacred.alliance.magic.app.social.config;

import lombok.Data;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.vo.AttributeOperateBean;

public @Data class SocialIntimateConfig {
	
	private int level;//亲密度等级
	private int minIntimate;//最小亲密度
	private int maxIntimate;//最大亲密度
	private byte attr;//属性
	private int value;//属性值
	private float percent;//百分比
	
	private AttributeOperateBean attributeBean;
	
	public void init(){
		String info = "load SocialIntimateConfig error: level=" + this.level + ".";
		if(this.level < 0){
			this.checkFail(info + "level is config error.");
		}
		if(0 == this.level && 0 != this.minIntimate){
			this.checkFail(info + "level is 0, the minIntimate must be 0");
		}
		if(this.minIntimate < 0 || this.maxIntimate < 0 || this.minIntimate >= this.maxIntimate){
			this.checkFail(info + "minIntimate or maxIntimate is config error.");
		}
		this.buldAttribute(this.attr, this.value, this.percent);
	}
	
	private void buldAttribute(byte type, int value, float precValue){
		if(value <= 0 && precValue <= 0){
			return;
		}
		AttributeType attrType = AttributeType.get(type);
		if(null == attrType){
			this.checkFail("load SocialIntimateConfig error: attr=" + type + ",it's not exist!");
			return;
		}
		if(null == IntimateAttrType.get(attrType)){
			this.checkFail("load SocialIntimateConfig error: attr=" + type + ",not support!");
			return;
		}
		this.attributeBean = new AttributeOperateBean(attrType, value, precValue);
	}
	
	private void checkFail(String info){
		Log4jManager.CHECK.error(info);
		Log4jManager.checkFail();
	}
	
	/**
	 * 亲密度是否在区间之内
	 * @param intimate
	 * @return
	 */
	public boolean isWithin(int intimate){
		return intimate >= this.minIntimate && intimate <= this.maxIntimate;
	}
	
	enum IntimateAttrType{
		
		curHP(AttributeType.curHP),//0生命
		curMP(AttributeType.curMP),//1法力
		maxHP(AttributeType.maxHP),//8生命值上限
		maxMP(AttributeType.maxMP),//9法力值上限
		atk(AttributeType.atk),//50=攻击力
		rit(AttributeType.rit),//51=防御力
		hitValue(AttributeType.hit),//52=命中值
		dodgeValue(AttributeType.dodge),//53=闪避值
		critATKValue(AttributeType.critAtk),//54=暴击值
		critRITValue(AttributeType.critRit),//55=暴击抵抗
		
		;
		
		private final AttributeType attrType;
		
		IntimateAttrType(AttributeType attrType){
			this.attrType = attrType;
		}
		
		public AttributeType getAttrType() {
			return attrType;
		}
		
		public static IntimateAttrType get(AttributeType attrType){
			for(IntimateAttrType item : IntimateAttrType.values()){
				if(item.getAttrType() == attrType){
					return item;
				}
			}
			return null;
		}
		
	}
	
}
