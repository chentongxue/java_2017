package sacred.alliance.magic.constant;

public interface RespTypeStatus {

	public static final byte SUCCESS = (byte) 1;// 成功

	public static final byte FAILURE = (byte) 0;// 失败

	public static final byte KICK_ROLE = (byte) 4;
	// 默认胸部资源ID
	public static final int DEFAULT_CLOTHES_RESID = 30002;
	// 默认装备资源ID
	public static final int DEFAULT_EQUIP_RESID = 0;

	// 默认英雄头像ID
	public static final short DEFAULT_HERO_HEAD_ID = 163;

	public static final int HANDUP_TOP_LEVEL = 0;
	public static final int HANDUP_NOT_CONDITION = 1;
	public static final int HANDUP_EXP_LACK = 2;
	public static final int HANDUP_SUCCESS = 3;
	public static final int HANDUP_FAIL = 4;

	public static final float FULL_RATE = 10000f;
}
