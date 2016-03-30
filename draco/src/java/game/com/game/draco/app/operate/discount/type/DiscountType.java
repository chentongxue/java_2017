package com.game.draco.app.operate.discount.type;

import com.game.draco.app.operate.discount.type.attris.DiscountTypeEquipMosaic;
import com.game.draco.app.operate.discount.type.attris.DiscountTypeEquipQuality;
import com.game.draco.app.operate.discount.type.attris.DiscountTypeEquipStrengthen;

public enum DiscountType {

	PAY_ONCE(0, 1, true, false), // 单笔充值
	BUY_ONCE(0, 2, false, false), // 单笔消费
	PAY_TOTAL(0, 3, true, true), // 累积充值
	BUY_TOTAL(0, 4, false, true), // 累积消费
	DAILY_PAY_ONCE(0, 5, true, false), // 每日单笔充值
	DAILY_BUY_ONCE(0, 6, false, false), // 每日单笔消费
	DAILY_PAY_TOTAL(0, 7, true, false), // 每日累计充值
	DAILY_BUY_TOTAL(0, 8, false, false), // 每日累计消费

	PAY_CONTINUOUS_DAY(0, 9, true, false), // 连续XX天充值XXX元宝
	BUY_CONTINUOUS_DAY(0, 10, false, false), // 连续XX天消费XXX元宝
	PAY_SUITE(0, 11, true, true), // 充值套餐
	BUY_SUITE(0, 12, false, true), // 消费套餐
	TAOBAO_BUY(0, 13, false, true), // 累计淘宝消费
	SHOP_BUY(0, 14, false, true), // 累计商城消费
	SECRETSHOP_BUY(0, 15, false, true), // 累计神秘商店消费

	EQUIP_STRENGTHEN(1, 16, false, false), // 身上强化n件+18
	EQUIP_QUALITY(1, 17, false, false), // 身上满n件x品质装备
	EQUIP_MOSAIC(1, 18, false, false), // 身上镶嵌满n颗n级宝石
	
	LOGIN_CONTINUOUS(2, 19, false, false), // 连续登录
	;

	private final int type;// 折扣活动触发类型
	private final int subType;// 活动类型
	private final boolean pay;// 是否是充值
	private final boolean total;// 是否累计

	public final static int TYPE_MONEY = 0;// 充值消费
	public final static int TYPE_ATTRI = 1;// 属性
	public final static int TYPE_LOGIN = 2;// 登陆

	DiscountType(int type, int subType, boolean pay, boolean total) {
		this.type = type;
		this.subType = subType;
		this.pay = pay;
		this.total = total;
	}

	public boolean isPay() {
		return pay;
	}

	public int getSubType() {
		return subType;
	}

	public int getType() {
		return type;
	}

	public boolean isTotal() {
		return total;
	}

	public static DiscountType get(int subtype) {
		for (DiscountType v : values()) {
			if (subtype == v.getSubType()) {
				return v;
			}
		}
		return null;
	}

	/**
	 * 创建活动逻辑类
	 * @return
	 */
	public DiscountTypeLogic createDiscountType() {
		switch (this) {
		
		case PAY_ONCE:
		case BUY_ONCE:
			return new DiscountTypeOnce();
		
		case PAY_TOTAL:
		case BUY_TOTAL:
			return new DiscountTypeTotal();
		
		case DAILY_PAY_ONCE:
		case DAILY_BUY_ONCE:
			return new DiscountTypeDailyOnce();
		
		case DAILY_PAY_TOTAL:
		case DAILY_BUY_TOTAL:
			return new DiscountTypeDailyTotal();
		
		case PAY_CONTINUOUS_DAY:
		case BUY_CONTINUOUS_DAY:
			return new DiscountTypeContinuousDay();
		
		case PAY_SUITE:
		case BUY_SUITE:
			return new DiscountTypeSuite();
		
		case TAOBAO_BUY:
		case SECRETSHOP_BUY:
		case SHOP_BUY:
			return new DiscountTypeSubBuyTotal();
		
		case EQUIP_STRENGTHEN:
			return new DiscountTypeEquipStrengthen();
		case EQUIP_QUALITY:
			return new DiscountTypeEquipQuality();
		case EQUIP_MOSAIC:
			return new DiscountTypeEquipMosaic();
			
		case LOGIN_CONTINUOUS:
			return new DiscountTypeContinuousLogin();
		default:
			return null;
		}
	}

}
