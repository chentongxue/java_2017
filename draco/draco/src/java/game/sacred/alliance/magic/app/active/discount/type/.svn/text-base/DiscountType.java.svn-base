package sacred.alliance.magic.app.active.discount.type;

import sacred.alliance.magic.app.active.discount.type.attris.DiscountTypeEquipMosaic;
import sacred.alliance.magic.app.active.discount.type.attris.DiscountTypeEquipQuality;
import sacred.alliance.magic.app.active.discount.type.attris.DiscountTypeEquipRecasting;
import sacred.alliance.magic.app.active.discount.type.attris.DiscountTypeEquipStrengthen;
import sacred.alliance.magic.app.active.discount.type.attris.DiscountTypeFashionQuality;
import sacred.alliance.magic.app.active.discount.type.attris.DiscountTypeMountLevel;
import sacred.alliance.magic.app.active.discount.type.attris.DiscountTypeWingOpen;


public enum DiscountType {

	PAY_ONCE(0,0,true,false),
	PAY_TOTAL(0,1,true,true),
	BUY_ONCE(0,2,false,false),
	BUY_TOTAL(0,3,false,true),
	PAY_FIRST_ACTIVE(0,4,true,false),
	PAY_FIRST_DAY(0,5,true,false),
	PAY_FIRST_WEEK(0,6,true,false),
	PAY_CONTINUOUS_DAY(0,7,true,false),
	BUY_CONTINUOUS_DAY(0,8,false,false),
	PAY_SUITE(0,9,true,true),
	BUY_SUITE(0,10,false,true),
	MOUNT_PHASE(1,13,false,false), //坐骑转生阶数
	WING_OPEN(1,14,false,false), //翅膀命格到达XX阶段
	MAGIC_REFINE_LEVEL(1,15,false,false), //法宝炼化等级
	MAGIC_HOLES(1,16,false,false), //出战法宝内丹x品x颗
	EQUIP_QUALITY(1,17,false,false), //身上满n件x品质装备
	EQUIP_STRENGTHEN(1,18,false,false), //身上强化n件+18、n件+15
	EQUIP_RECASTING(1,19,false,false), //身上洗炼到XX星
	EQUIP_MOSAIC(1,20,false,false), //身上镶嵌满n颗n级宝石
	MOUNT_LEVEL(1,21,false,false), //坐骑培养级数
	FASHION_QUALITY(1,22,false,false), //时装xx品质xx件
	TAOBAO_BUY(0,23,false,true), //累计淘宝消费
	SECRETSHOP_BUY(0,24,false,true), //累计神秘商店消费
	SHOP_BUY(0,25,false,true), //累计商城消费
	LOGIN_CONTINUOUS(2,26,false,false), //连续登录
	PAY_FIRST_RETURN(0,27,true,true), //首次任意金额,只能充值一次,金额计入total中
	;
	
	private final int type; //折扣活动触发类型 0:充值消费 1:属性 2:登陆
	private final int subType;
	private final boolean pay ;
	private final boolean total ;
	
	public final static int TYPE_MONEY = 0;
	public final static int TYPE_ATTRI = 1;
	public final static int TYPE_LOGIN = 2;
	
	DiscountType(int type, int subType,boolean pay,boolean total){
		this.type = type;
		this.subType = subType;
		this.pay = pay ;
		this.total =  total ;
	}
	
	
	
	public boolean isPay() {
		return pay;
	}

	public int getSubType(){
		return subType;
	}

	public int getType(){
		return type;
	}
	
	
	
	public boolean isTotal() {
		return total;
	}



	public static DiscountType get(int subtype){
		for(DiscountType v : values()){
			if(subtype == v.getSubType()){
				return v;
			}
		}
		return null;
	}
	
	public DiscountTypeLogic createDiscountType(){
		switch(this){
		case BUY_ONCE:
		case PAY_ONCE:
			return new DiscountTypeOnce();
		case BUY_TOTAL:
		case PAY_TOTAL:
			return new DiscountTypeTotal();
		case PAY_FIRST_ACTIVE:
			return new DiscountTypeFirstActive();
		case PAY_FIRST_DAY:
			return new DiscountTypeFirstDay();
		case PAY_FIRST_WEEK:
			return new DiscountTypeFirstWeek();
		case PAY_CONTINUOUS_DAY:
		case BUY_CONTINUOUS_DAY:
			return new DiscountTypeContinuousDay();
		case PAY_SUITE:
		case BUY_SUITE:
			return new DiscountTypeSuite();
		case MOUNT_PHASE:
		case MOUNT_LEVEL:
			return new DiscountTypeMountLevel();
		case WING_OPEN:
			return new DiscountTypeWingOpen();
		case EQUIP_QUALITY:
			return new DiscountTypeEquipQuality();
		case EQUIP_STRENGTHEN:
			return new DiscountTypeEquipStrengthen();
		case EQUIP_RECASTING:
			return new DiscountTypeEquipRecasting();
		case EQUIP_MOSAIC:
			return new DiscountTypeEquipMosaic();
		case FASHION_QUALITY:
			return new DiscountTypeFashionQuality();
		case TAOBAO_BUY:
		case SECRETSHOP_BUY:
		case SHOP_BUY:
			return new DiscountTypeSubBuyTotal();
		case LOGIN_CONTINUOUS:
			return new DiscountTypeContinuousLogin();
		case PAY_FIRST_RETURN:
			return new DiscountTypePayFirst();
		default:
			return null;
		}
	}
	
}
