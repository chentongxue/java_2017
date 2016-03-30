package sacred.alliance.magic.constant;

public interface ParasConstant {
	public static final int REG_USER_MAX_LENGTH = 16;
	public static final int REG_PWD_MAX_LENGTH = 10 ;
	//public static final String REG_USER_GUEST = "guest";
	public static final int REG_PASSWD_NUM = 6;
	
	public static final int SILVER_MONEY = Integer.MAX_VALUE;
	public static final int GOLD_MONEY = Integer.MAX_VALUE;
	
	//角色可装备在身上的装备数量
	//public static final byte ROLE_EQUIP_MAX_NUM = 12 ; //12 + 12 时装 装备位
	//public static final byte ROLE_EQUIP_FASHIONE_INDEX = 12; //前12个为装备
	public static final byte HERO_EQUIP_MAX_NUM = 6 ; //英雄装备个数
	public static final int ROLE_BACKPACK_MAX_NUM = 200;//背包最大格子数
	public static final byte ROLE_BACKPACK_DEF_NUM = 50;//背包默认格子数
	public static final byte ROLE_WAREHOUSE_DEF_NUM = 10;//仓库默认格子数
	//不支持用金条或绑金购买的值
	public static final int Gold_Bind_Nonsupport_Value = -1;
	
	//百分比基数10000=100%
	public static final float PERCENT_BASE_VALUE = 10000f;
	
	//角色身上存放飘字信息的最大数量
	public static final int ATTRFONT_MAX_SIZE = 10;
	
}
